package com.hardlove.library.ctoolbar;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;

import com.bumptech.glide.Glide;
import com.carlos.camerax.CustomCameraView;
import com.carlos.camerax.ImageLoader;
import com.carlos.permissionhelper.PermissionHelper;

import java.util.Arrays;
import java.util.Collections;

public class TestActivtiy extends AppCompatActivity {

    private CustomCameraView customCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activtiy_test);

        initCameraXView();


        PermissionHelper.builder()
                .addPermission(new String[]{Manifest.permission.CAMERA})
                .callback(new PermissionHelper.SimpleCallback() {
                    @Override
                    public void onGranted() {
                    }

                    @Override
                    public void onDenied() {

                    }
                }).request();

    }

    private void initCameraXView() {
        customCameraView = findViewById(R.id.customCameraView);
        ImageLoader imageloader = new ImageLoader() {
            @Override
            public void load(@NonNull ImageView iv, @NonNull Uri uri) {
                Glide.with(iv).load(uri).into(iv);

            }
        };
        customCameraView.setImageLoader(imageloader);
    }

    private boolean falsh = false;

    public void flash(View view) {
        falsh = !falsh;
        if (falsh) {
            customCameraView.setFlashMode(ImageCapture.FLASH_MODE_ON);
        } else {
            customCameraView.setFlashMode(ImageCapture.FLASH_MODE_OFF);
        }

    }

    private boolean flag;

    public void changeAspectRatio(View view) {
        if (flag) {
            customCameraView.setAspectRatio(AspectRatio.RATIO_16_9);
        } else {
            customCameraView.setAspectRatio(AspectRatio.RATIO_4_3);
        }
        flag = !flag;


    }

    public void takePicture(View view) {
        customCameraView.takePicture(new ImageCapture.OnImageSavedCallback() {

            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {

            }
        });

    }


    public void startIntent(View view) {
        startActivity(new Intent(this, SecondActivity.class));
    }

    public void cancel(View view) {
        customCameraView.cancel();
    }
}
