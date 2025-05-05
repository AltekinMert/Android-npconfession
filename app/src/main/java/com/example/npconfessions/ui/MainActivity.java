package com.example.npconfessions.ui;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.npconfessions.R;
import com.example.npconfessions.data.Post;

import com.example.npconfessions.data.Repository;
import com.example.npconfessions.worker.PostSyncWorker;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;
    private PostAdapter   adapter;

    @Override protected void onCreate(Bundle saved) {
        FirebaseAuth.getInstance().signInAnonymously();
        super.onCreate(saved);
        setContentView(R.layout.activity_main);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(new PostAdapter.Callbacks() {
            @Override public void onSwipeDelete(Post p) {
                String me = FirebaseAuth.getInstance().getUid();

                if (p.ownerUid != null && p.ownerUid.equals(me)) {
                    // 👉 You created this post — remove everywhere
                    vm.delete(p);                                // delete from Room
                    if (p.cloudId != null && !p.cloudId.isEmpty()) {
                        FirebaseFirestore.getInstance()          // and from Firestore
                                .collection("posts")
                                .document(p.cloudId)
                                .delete();
                    }
                } else {
                    // 👉 Someone else’s post — hide locally only
                    vm.hide(p);                                  // insert into hidden table
                }
            }
            @Override public void onLongPressFavorite(Post p){ vm.toggleFav(p); }
        });
        rv.setAdapter(adapter);

        // Swipe-to-delete helper
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder a, RecyclerView.ViewHolder b){ return false; }
                    @Override public void onSwiped(RecyclerView.ViewHolder vh, int dir){
                        adapter.cb.onSwipeDelete(adapter.getAt(vh.getAdapterPosition()));
                    }
                }).attachToRecyclerView(rv);

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        Repository repo = vm.getRepository();

        // -------- Firestore snapshot listener ----------
        FirebaseFirestore.getInstance()
                .collection("posts")
                .orderBy("timestamp")
                .addSnapshotListener((snap, e) -> {
                    if (e != null || snap == null) return;

                    for (DocumentChange dc : snap.getDocumentChanges()) {
                        if (dc.getType() == DocumentChange.Type.ADDED) {
                            DocumentSnapshot d = dc.getDocument();

                            Post p = new Post();
                            p.cloudId    = d.getId();
                            p.ownerUid   = d.getString("uid");
                            p.message    = d.getString("message");
                            p.songTitle  = d.getString("songTitle");
                            p.songArtist = d.getString("songArtist");
                            p.deezerUrl  = d.getString("deezerUrl");
                            p.coverUrl   = d.getString("coverUrl");
                            Long ts      = d.getLong("timestamp");
                            p.timestamp  = ts != null ? ts : System.currentTimeMillis();

                            repo.insertOrIgnore(p);      // duplicates ignored because cloudId is UNIQUE
                        }
                    }
                });

        vm.feed.observe(this, adapter::submit);
        findViewById(R.id.fabSettings).setOnClickListener(v ->
                startActivity(new Intent(this, AddPostActivity.class)));

        //Periodic req
        PeriodicWorkRequest req = new PeriodicWorkRequest.Builder(
                PostSyncWorker.class, 6, TimeUnit.HOURS
        ).build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "post_sync",
                ExistingPeriodicWorkPolicy.KEEP,
                req
        );




        createNotificationChannel();
    }

    @Override public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.menu_main, m);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem i){
        if(i.getItemId()==R.id.menu_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(i);
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel ch = new NotificationChannel(
                    "sync_channel",
                    "Background Sync",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            ch.setDescription("Notifications after trending songs are synced");
            NotificationManager nm = getSystemService(NotificationManager.class);
            nm.createNotificationChannel(ch);
        }
    }
}
