package com.example.npconfessions.ui;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.npconfessions.R;
import com.example.npconfessions.data.Post;
import com.example.npconfessions.utils.PreferencesManager;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import android.text.format.DateUtils;
/** Binds Post objects to item_post.xml cards. */
public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostVH> {

    public interface Callbacks {
        void onSwipeDelete(Post post);
        void onLongPressFavorite(Post p);
    }

    final Callbacks cb;
    private final List<Post> data = new ArrayList<>();

    public PostAdapter(Callbacks cb) { this.cb = cb; }

    /* ---------- Lifecycle ---------- */

    @NonNull @Override
    public PostVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_post, parent, false);
        return new PostVH(v);
    }

    @Override public int getItemCount() { return data.size(); }

    @Override public void onBindViewHolder(@NonNull PostVH h, int pos) {
        Post p = data.get(pos);

        h.tvMsg .setText(p.message);
        h.tvSong.setText(p.songTitle + " • " + p.songArtist);
        CharSequence relTime = DateUtils.getRelativeTimeSpanString(
                p.timestamp,
                System.currentTimeMillis(),
                DateUtils.SECOND_IN_MILLIS
        );
        h.tvTime.setText(relTime);

        Glide.with(h.itemView)
                .load(p.coverUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(h.imgCover);

        h.itemView.setOnClickListener(v -> openSong(v.getContext(), p));
        h.itemView.setOnLongClickListener(v -> { cb.onLongPressFavorite(p); return true; });
    }

    public void submit(List<Post> posts) { data.clear(); data.addAll(posts); notifyDataSetChanged(); }
    public Post getAt(int pos)           { return data.get(pos); }

    static class PostVH extends RecyclerView.ViewHolder {
        TextView tvMsg, tvSong, tvTime;
        ImageView imgCover;
        PostVH(View v){
            super(v);
            tvMsg  = v.findViewById(R.id.tvMessage);
            tvSong = v.findViewById(R.id.tvSong);
            imgCover = v.findViewById(R.id.imgCover);
            tvTime = v.findViewById(R.id.tvTime);
        }
    }

    /* ---------- Helpers ---------- */

    private void openSong(Context ctx, Post p) {
        PreferencesManager pm = new PreferencesManager(ctx);
        boolean useSpotify = pm.getProvider().equals("spotify");

        String url;

        if (useSpotify) {
            /* ---- SPOTIFY: always build search URL on the fly ---- */
            String q = Uri.encode(p.songTitle + " " + p.songArtist);
            url = "https://open.spotify.com/search/" + q;

        } else {
            /* ---- DEEZER branch ---- */
            if (p.deezerUrl != null && !p.deezerUrl.isEmpty()) {
                url = p.deezerUrl;               // we stored a preview link
            } else {
                /* post was created before we stored deezerUrl: build fallback */
                String q = Uri.encode(p.songTitle + " " + p.songArtist);
                url = "https://www.deezer.com/search/" + q;
            }
        }

        if (url == null || url.isEmpty()) {
            Toast.makeText(ctx, "No link available", Toast.LENGTH_SHORT).show();
            return;
        }
        ctx.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /* ---------- View-holder ---------- */


}
