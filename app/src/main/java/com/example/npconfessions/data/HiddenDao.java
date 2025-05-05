package com.example.npconfessions.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

/**
 * DAO for the "hidden" table.
 * The table stores cloudId values of posts the current user
 * has chosen to hide locally.  A post is hidden simply by
 * inserting its cloudId; no un-hide method is needed unless
 * you decide to add one later.
 */
@Dao
public interface HiddenDao {

    /** Insert a cloudId to hide it (ignore if it already exists). */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void hide(HiddenPost hidden);

    /** Get all cloudIds the user has hidden, as LiveData list. */
    @Query("SELECT cloudId FROM hidden")
    LiveData<List<String>> getHiddenIds();
}
