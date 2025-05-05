package com.example.npconfessions.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import com.example.npconfessions.data.Post;
import com.example.npconfessions.data.Repository;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends AndroidViewModel {
    private final Repository repo;
    public  final LiveData<List<Post>> feed;

    public MainViewModel(@NonNull Application app) {
        super(app);
        repo  = new Repository(app);

        MediatorLiveData<List<Post>> mediator = new MediatorLiveData<>();
        mediator.addSource(repo.getAllPosts(),   p -> combine(mediator));
        mediator.addSource(repo.getHiddenIds(),  h -> combine(mediator));
        feed = mediator;
    }
    public void toggleFav(Post p){ repo.toggleFavorite(p.id); }

    private void combine(MediatorLiveData<List<Post>> med){
        List<Post> posts  = repo.getAllPosts().getValue();
        List<String> hide = repo.getHiddenIds().getValue();
        if (posts == null) return;

        List<Post> visible = new ArrayList<>();
        for (Post p : posts)
            if (p.cloudId == null || hide == null || !hide.contains(p.cloudId))
                visible.add(p);

        med.setValue(visible);
    }
    public void hide(Post p){ repo.hide(p.cloudId); }      // call from UI
    public void delete(Post p){ repo.delete(p); }
    // for your own posts

    public Repository getRepository() { return repo; }

}
