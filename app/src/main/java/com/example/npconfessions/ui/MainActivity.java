package com.example.npconfessions.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.npconfessions.R;
import com.example.npconfessions.data.Post;

import com.google.android.material.appbar.MaterialToolbar;

public class MainActivity extends AppCompatActivity {

    private MainViewModel vm;
    private PostAdapter   adapter;

    @Override protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_main);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        RecyclerView rv = findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(new PostAdapter.Callbacks() {
            @Override public void onSwipeDelete(Post p)   { vm.delete(p); }
            @Override public void onLongPressFavorite(Post p){ vm.toggleFav(p); }
        });
        rv.setAdapter(adapter);

        // Swipe-to-delete helper
        new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder a, RecyclerView.ViewHolder b){ return false; }
                    @Override public void onSwiped(RecyclerView.ViewHolder vh, int dir){
                        adapter.cb.onSwipeDelete(adapter.getAt(vh.getAdapterPosition()));
                    }
                }).attachToRecyclerView(rv);

        vm = new ViewModelProvider(this).get(MainViewModel.class);
        vm.posts.observe(this, adapter::submit);          // LiveData hook

        findViewById(R.id.fabSettings).setOnClickListener(v ->
                startActivity(new Intent(this, AddPostActivity.class)));
    }

    @Override public boolean onCreateOptionsMenu(Menu m){
        getMenuInflater().inflate(R.menu.menu_main, m);
        return true;
    }
    @Override public boolean onOptionsItemSelected(MenuItem i){
        if(i.getItemId()==R.id.menu_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(i);
    }
}
