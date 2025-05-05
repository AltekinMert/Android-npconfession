package com.example.npconfessions.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** Single feed item (anonymous thought + music). */
@Entity(tableName = "posts")
public class Post {

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String message = "";

    public String coverUrl = "";

    /* Song meta (never null, but may be empty) */
    public String songTitle  = "";
    public String songArtist = "";

    /* Deezer preview URL ALWAYS stored; Spotify URL is built on-the-fly */
    public String deezerUrl  = "";

    public long    timestamp;
    public boolean favorite;
}
