package com.aossie.imageuploaded;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.chrisbanes.photoview.PhotoView;

public class ImageViewerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        String imgPath =getIntent().getStringExtra("image_url");
        Glide.with(this)
                .asDrawable()
                .load(imgPath)
                .into(new DrawableImageViewTarget((PhotoView)findViewById(R.id.photo_view)) {
                          @Override
                          public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                              if (resource instanceof GifDrawable) {
                                  ((GifDrawable) resource).setLoopCount(1);
                              }
                              super.onResourceReady(resource, transition);
                          }
                      }
                );
    }
}
