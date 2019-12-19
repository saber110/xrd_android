package com.example.collectdata_01;

import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;

import androidx.appcompat.app.AppCompatActivity;

import com.github.chrisbanes.photoview.PhotoView;


public class ImageActivity extends AppCompatActivity {
    private PhotoView imageView;
    private String imageName;
    private String imageFile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        imageView = (PhotoView) findViewById(R.id.imageView);
        imageName = getIntent().getStringExtra("image");
        imageFile = (Environment.getExternalStorageDirectory()+ "/"+ getResources().getString(R.string.picturePath) + "/" + imageName);

        imageView.setImageURI(Uri.parse("file://" + imageFile));
    }

}
