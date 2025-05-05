package com.example.npconfessions.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/** Keeps cloudId of posts the user wants to hide locally. */
@Entity(tableName = "hidden")
public class HiddenPost {
    @PrimaryKey
    @NonNull
    public String cloudId;   // same ID used in Firestore docs
}