package com.example.npconfessions.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.npconfessions.R;
import com.example.npconfessions.data.Post;
import com.example.npconfessions.data.Repository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** “Write a thought + attach song” screen. */
public class AddPostActivity extends AppCompatActivity {

    private EditText editMsg, editSong;
    private Repository repo;

    /* Always store Deezer link (Spotify decided later) */
    private String deezerUrl = "";

    private String coverUrl = "";

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_add_post);

        repo     = new Repository(getApplication());
        editMsg  = findViewById(R.id.editMessage);
        editSong = findViewById(R.id.editSong);

        findViewById(R.id.btnAttach).setOnClickListener(v -> searchSong());
        findViewById(R.id.btnSave).setOnClickListener(v -> savePost());
    }

    /* ----- Deezer keyword search (no API key) ----- */
    private void searchSong() {
        String q = editSong.getText().toString().trim();
        if (q.isEmpty()) { editSong.setError("Type a song"); return; }

        repo.getApiService().searchTrack(q, 10).enqueue(new Callback<JsonObject>() {
            @Override public void onResponse(@NonNull Call<JsonObject> c, @NonNull Response<JsonObject> r) {
                if (!r.isSuccessful() || r.body()==null) { toast("Search failed"); return; }

                JsonArray arr = r.body().getAsJsonArray("data");
                if (arr==null || arr.size()==0) { toast("No results"); return; }

                List<String> titles = new ArrayList<>();
                List<String> links  = new ArrayList<>();
                List<String> covers = new ArrayList<>();

                for (JsonElement el: arr){
                    JsonObject o = el.getAsJsonObject();
                    String title  = o.get("title").getAsString();
                    String artist = o.getAsJsonObject("artist").get("name").getAsString();
                    String cover = o.getAsJsonObject("album").get("cover_medium").getAsString();
                    titles.add(title + " • " + artist);
                    links .add(o.get("link").getAsString());
                    covers.add(cover);
                }

                new AlertDialog.Builder(AddPostActivity.this)
                        .setTitle("Pick a song")
                        .setItems(titles.toArray(new String[0]), (d, idx) -> {
                            editSong.setText(titles.get(idx));
                            deezerUrl = links.get(idx);
                            coverUrl = covers.get(idx);
                        })
                        .show();
            }
            @Override public void onFailure(@NonNull Call<JsonObject> c, @NonNull Throwable t){
                toast("Error: "+t.getMessage());
            }
        });
    }

    /* ----- Validate + insert into Room ----- */
    private void savePost() {
        String msg  = editMsg.getText().toString().trim();
        String song = editSong.getText().toString().trim();
        if (msg.isEmpty()){ editMsg.setError("Say something…"); return; }

        /* If user typed a title but never pressed 'Attach', get link quickly */
        if (song.length()>0 && deezerUrl.isEmpty()){
            try {
                Response<JsonObject> r = repo.getApiService().searchTrack(song, 1).execute();
                if (r.isSuccessful() && r.body()!=null){
                    JsonArray arr = r.body().getAsJsonArray("data");
                    if (arr!=null && arr.size()>0)
                        deezerUrl = arr.get(0).getAsJsonObject().get("link").getAsString();
                }
            } catch (IOException ignored) {}
        }

        Post p = new Post();
        p.message = msg;
        if (!song.isEmpty()){
            String[] s = song.split(" • ");
            p.songTitle  = s[0];
            p.songArtist = (s.length>1) ? s[1] : "";
        }
        p.deezerUrl = deezerUrl;
        p.timestamp = System.currentTimeMillis();
        p.coverUrl = coverUrl;

        repo.insert(p);
        toast("Posted!");
        finish();
    }

    private void toast(String m){ Toast.makeText(this, m, Toast.LENGTH_SHORT).show(); }
}
