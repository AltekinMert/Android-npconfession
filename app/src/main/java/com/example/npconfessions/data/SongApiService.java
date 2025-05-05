package com.example.npconfessions.data;

import com.google.gson.JsonObject;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SongApiService {

    /* Deezer keyword search – no API key required */
    @GET("search")
    Call<JsonObject> searchTrack(@Query("q") String query,
                                 @Query("limit") int limit);

    /* Deezer global top-tracks chart (for WorkManager) */
    @GET("chart/0/tracks")
    Call<JsonObject> getTrending(@Query("limit") int limit);
}
