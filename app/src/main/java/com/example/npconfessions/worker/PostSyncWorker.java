package com.example.npconfessions.worker;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.npconfessions.data.Post;
import com.example.npconfessions.data.Repository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import retrofit2.Response;

/** Injects trending tracks every 6 h; always stores Deezer links. */
public class PostSyncWorker extends Worker {

    public PostSyncWorker(@NonNull Context ctx, @NonNull WorkerParameters p){ super(ctx,p); }

    @NonNull @Override
    public Result doWork() {
        Repository repo = new Repository((Application) getApplicationContext());

        try {
            Response<JsonObject> r = repo.getApiService().getTrending(5).execute();
            if (!r.isSuccessful() || r.body()==null) return Result.retry();

            JsonArray arr = r.body().getAsJsonArray("data");
            for (JsonElement el: arr){
                JsonObject o = el.getAsJsonObject();
                Post p = new Post();
                p.message     = "🎶 Trending track!";
                p.songTitle   = o.get("title").getAsString();
                p.songArtist  = o.getAsJsonObject("artist").get("name").getAsString();
                p.deezerUrl   = o.get("link").getAsString();
                p.timestamp   = System.currentTimeMillis();
                p.coverUrl = o.getAsJsonObject("album").get("cover_medium").getAsString();
                repo.insert(p);
            }
            return Result.success();

        }catch(Exception e){
            e.printStackTrace();
            return Result.retry();
        }
    }
}
