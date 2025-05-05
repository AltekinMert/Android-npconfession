package com.example.npconfessions.worker;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.npconfessions.data.Post;
import com.example.npconfessions.data.Repository;
import com.example.npconfessions.ui.MainActivity;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.UUID;

import retrofit2.Response;

/** Injects trending tracks every 6 h; always stores Deezer links. */
public class PostSyncWorker extends Worker {

    public PostSyncWorker(@NonNull Context ctx, @NonNull WorkerParameters p){ super(ctx,p); }

    @SuppressLint("MissingPermission")
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
                p.cloudId = "trend_" + UUID.randomUUID();
                p.message     = "🎶 Trending track!";
                p.songTitle   = o.get("title").getAsString();
                p.songArtist  = o.getAsJsonObject("artist").get("name").getAsString();
                p.deezerUrl   = o.get("link").getAsString();
                p.timestamp   = System.currentTimeMillis();
                p.coverUrl = o.getAsJsonObject("album").get("cover_medium").getAsString();
                repo.insert(p);
            }
            // Launch intent (taps go to MainActivity)
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pi = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

// Show notification
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "sync_channel")
                    .setSmallIcon(android.R.drawable.btn_star)  // use any small icon you have
                    .setContentTitle("🎶 New trending tracks")
                    .setContentText("5 new songs added to your feed")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(pi)
                    .setAutoCancel(true);

            NotificationManagerCompat.from(getApplicationContext()).notify(101, builder.build());
            return Result.success();

        }catch(Exception e){
            e.printStackTrace();
            return Result.retry();
        }
    }
}
