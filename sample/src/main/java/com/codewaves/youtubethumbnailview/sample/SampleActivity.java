package com.codewaves.youtubethumbnailview.sample;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.codewaves.youtubethumbnailview.ImageLoader;
import com.codewaves.youtubethumbnailview.ThumbnailView;
import com.codewaves.youtubethumbnailview.listener.ThumbnailLoadingListener;
import com.squareup.picasso.Picasso;

/**
 * Created by Sergej Kravcenko on 4/14/2017.
 * Copyright (c) 2017 Sergej Kravcenko
 */

public class SampleActivity extends AppCompatActivity {
   private static final String TAG = "SampleActivity";
   @Override
   protected void onCreate(@Nullable Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_sample);

      final ThumbnailView thumb = (ThumbnailView)findViewById(R.id.thumbnail);

      final Button clear = (Button)findViewById(R.id.clear);
      clear.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            thumb.clearThumbnail();
         }
      });

      final Button fetch = (Button)findViewById(R.id.fetch);
      fetch.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            thumb.loadThumbnail("https://www.youtube.com/watch?v=iCkYw3cRwLo", new ThumbnailLoadingListener() {
               @Override
               public void onLoadingStarted(@NonNull String url, @NonNull View view) {
                  Log.i(TAG, "Thumbnail load started.");
               }

               @Override
               public void onLoadingComplete(@NonNull String url, @NonNull View view) {
                  Log.i(TAG, "Thumbnail load finished.");
               }

               @Override
               public void onLoadingFailed(@NonNull String url, @NonNull View view, Throwable error) {
                  Log.e(TAG, "Thumbnail load failed. " + error.getMessage());
               }
            });
         }
      });

      final Button picasso = (Button)findViewById(R.id.picasso);
      picasso.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            thumb.loadThumbnail("https://www.youtube.com/watch?v=H7jtC8vjXw8", new ThumbnailLoadingListener() {
               @Override
               public void onLoadingStarted(@NonNull String url, @NonNull View view) {
                  Log.i(TAG, "Thumbnail load started.");
               }

               @Override
               public void onLoadingComplete(@NonNull String url, @NonNull View view) {
                  Log.i(TAG, "Thumbnail load finished.");
               }

               @Override
               public void onLoadingFailed(@NonNull String url, @NonNull View view, Throwable error) {
                  Log.e(TAG, "Thumbnail load failed. " + error.getMessage());
               }
            }, new ImageLoader() {
               @Override
               public void load(String url, ImageView imageView) {
                  Picasso.with(SampleActivity.this).load(url).into(imageView);
               }
            });
         }
      });

      final Button display = (Button)findViewById(R.id.display);
      display.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            thumb.displayThumbnail("YouTube video test title", 12345, getResources().getDrawable(R.mipmap.ic_launcher));
         }
      });


   }
}
