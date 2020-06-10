package com.aossie.imageuploaded;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.target.DrawableImageViewTarget;
import com.bumptech.glide.request.transition.Transition;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private CompressUtils compressUtils;
    private String clickedPhotoPath;
    private File file;
    private  TextView actualSize;
    private TextView compressedTextView;
    private ImageView compressedImage;
    private String actualPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Calendar calendar = Calendar.getInstance();
        Log.i("offset",calendar.getTimeZone().getRawOffset()+"") ;
        imageView =findViewById(R.id.imageView);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent(getApplicationContext(),ImageViewerActivity.class);
                intent.putExtra("image_url",clickedPhotoPath);
                startActivity(intent);
            }
        });
        compressUtils =new CompressUtils(getContentResolver());
        File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);

        actualSize =(TextView)findViewById(R.id.actualSize);
        compressedTextView =(TextView)findViewById(R.id.compressedSize);
        compressedImage =findViewById(R.id.imageViewComp);
        compressedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(getApplicationContext(),ImageViewerActivity.class);
                intent.putExtra("image_url",actualPath);
                startActivity(intent);
            }
        });
        Button captureBtn  =findViewById(R.id.captureBtn);
        //takki hume acccess mil jaaaye
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        captureBtn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs();
                    File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                    file = new File(dir, System.currentTimeMillis()+".jpg");
                    clickedPhotoPath = file.getAbsolutePath();
                    try {
                        file.createNewFile();
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                        startActivityForResult(intent, CAMERA_REQUEST);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).mkdirs();
                File dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS);
                File file = new File(dir, "tmp11111.png");
                clickedPhotoPath = file.getAbsolutePath();
                try {
                    file.createNewFile();
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
                    startActivityForResult(intent, CAMERA_REQUEST);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            else
            {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {

            Log.i("hello Main",clickedPhotoPath);
            File file  =new File(clickedPhotoPath);
            actualSize.setText("ACTUAL SIZE-----"+String.valueOf((Integer.parseInt(String.valueOf(file.length()/1024))))+"Kb");
            Log.i("Size 1 ", String.valueOf((Integer.parseInt(String.valueOf(file.length()/1024)+""))));

            Glide.with(this)
                    .asDrawable()
                    .load(clickedPhotoPath)
                    .into(new DrawableImageViewTarget(imageView) {
                              @Override
                              public void onResourceReady(Drawable resource, @Nullable Transition<? super Drawable> transition) {
                                  if (resource instanceof GifDrawable) {
                                      ((GifDrawable) resource).setLoopCount(1);
                                  }
                                  super.onResourceReady(resource, transition);
                              }
                          }
                    );
            String path  = null;
            try {
                path = compressUtils.compressImage(clickedPhotoPath);
                actualPath =path;
            } catch (IOException e) {
                e.printStackTrace();
            }

            File fileB  =new File(path);
            Log.i("Size 2", String.valueOf((Integer.parseInt(String.valueOf(fileB.length()/1024)+""))));
            compressedTextView.setText("COMPRESSED SIZE----"+String.valueOf((Integer.parseInt(String.valueOf(fileB.length()/1024)+"")))+"Kb");


            Log.i("hello Main Second ",path);

            Glide.with(this)
                    .asDrawable()
                    .load(path)
                    .into(new DrawableImageViewTarget(compressedImage) {
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

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Hello", null);
        return Uri.parse(path);
    }
}

