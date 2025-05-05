package com.example.npconfessions.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface PostDao {
    @Query("SELECT * FROM posts ORDER BY timestamp DESC")
    LiveData<List<Post>> getAll();

    @Insert
    void insert(Post post);

    @Delete
    void delete(Post post);

    @Update
    void update(Post post);

    @Query("UPDATE posts SET favorite = NOT favorite WHERE id = :postId")
    void toggleFavorite(long postId);

    @Query("SELECT * FROM posts WHERE favorite = 1 ORDER BY timestamp DESC")
    LiveData<List<Post>> getFavorites();
}
