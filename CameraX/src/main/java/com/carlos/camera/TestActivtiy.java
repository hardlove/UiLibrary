package com.carlos.camera;

import android.Manifest;
import android.os.Bundle;
import android.view.Surface;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;

import com.carlos.permissionhelper.PermissionHelper;

public class TestActivtiy extends AppCompatActivity {

    private CustomCameraView customCameraView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activtiy_test);
        PermissionHelper.builder()
                .addPermission(Manifest.permission.CAMERA,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .callback(new PermissionHelper.SimpleCallback() {
                    @Override
                    public void onGranted() {
                        initCameraXView();
                    }

                    @Override
                    public void onDenied() {

                    }
                }).request();

    }

    private void initCameraXView() {
        customCameraView = findViewById(R.id.customCameraView);
    }

    private boolean falsh = false;
    public void flash(View view) {
        falsh = !falsh;
        customCameraView.flashEnable(falsh);

    }

    public void changeAspectRatio(View view) {
        customCameraView.setAspectRatio(AspectRatio.RATIO_16_9);
    }

    public void takePicture(View view) {
        customCameraView.takePicture();
    }

    private int rotatin = 0;
    public void changeRational(View view) {

        customCameraView.setRotation(rotatin++%3);
    }
}
