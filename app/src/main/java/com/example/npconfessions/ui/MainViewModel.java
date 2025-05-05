package com.example.npconfessions.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.npconfessions.data.Post;
import com.example.npconfessions.data.Repository;

import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final Repository repo;
    public final LiveData<List<Post>> posts;

    public MainViewModel(@NonNull Application app) {
        super(app);
        repo  = new Repository(app);
        posts = repo.getAllPosts();
    }
    public void delete(Post p){ repo.delete(p); }
    public void toggleFav(Post p){ repo.toggleFavorite(p.id); }
}
