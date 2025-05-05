package com.example.npconfessions.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private final PostDao postDao;
    private final LiveData<List<Post>> allPosts;
    private final LiveData<List<Post>> favorites;
    private final SongApiService apiService;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    public Repository(Application application) {
        AppDatabase db = AppDatabase.getInstance(application);
        postDao = db.postDao();
        allPosts = postDao.getAll();
        favorites = postDao.getFavorites();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.deezer.com/")   // <<— Deezer, no key
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(SongApiService.class);

    }

    public LiveData<List<Post>> getAllPosts() { return allPosts; }
    public LiveData<List<Post>> getFavorites() { return favorites; }

    public void insert(Post post) { io.execute(() -> postDao.insert(post)); }
    public void delete(Post post) { io.execute(() -> postDao.delete(post)); }
    public void toggleFavorite(long id) { io.execute(() -> postDao.toggleFavorite(id)); }

    public SongApiService getApiService() { return apiService; }
}
