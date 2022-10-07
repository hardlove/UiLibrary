package com.carlos.camera;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.core.impl.ImageOutputConfig;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;

public class CustomCameraView extends FrameLayout implements LifecycleObserver {
    private ImageCapture imageCapture;
    private Camera camera;
    private PreviewView previewView;
    private ImageView imageView;
    private CameraSelector cameraSelector;
    private Preview preview;
    private ProcessCameraProvider processCameraProvider;

    public CustomCameraView(@NonNull Context context) {
        super(context);
        bindToLifeCycle((LifecycleOwner) context);

    }

    public CustomCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bindToLifeCycle((LifecycleOwner) context);
    }


    private @ImageCapture.FlashMode
    int flashMode = ImageCapture.FLASH_MODE_AUTO;
    private @ImageOutputConfig.RotationValue
    int rotation = Surface.ROTATION_90;


    public void setFlashMode(int flashMode) {
        this.flashMode = flashMode;
        bindPreview(processCameraProvider);
    }

    public void setAspectRatio(int aspectRatio) {
        this.aspectRatio = aspectRatio;
        bindPreview(processCameraProvider);
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
        bindPreview(processCameraProvider);
    }

    private @AspectRatio.Ratio
    int aspectRatio = AspectRatio.RATIO_16_9;


    private LifecycleOwner lifecycleOwner;

    public void bindToLifeCycle(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver(this);

        init(getContext());
    }

    private void init(Context context) {
        imageCapture = new ImageCapture.Builder()
                .setFlashMode(flashMode)
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .build();

        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderListenableFuture.get();

                bindPreview(processCameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));

    }

    private void bindPreview(ProcessCameraProvider cameraProvider) {
        if (cameraProvider == null) return;
        if (previewView == null) {
            previewView = new PreviewView(getContext());
            previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
            addView(previewView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        preview = new Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview);
        preview.setSurfaceProvider(previewView.getSurfaceProvider());
    }

    private String getImageSavePath() {
//        return getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
    }

    public void takePicture(ImageCapture.OnImageSavedCallback onImageSavedCallback) {
        File parent = new File(getImageSavePath());
        File file = new File(parent, System.currentTimeMillis() + ".png");
        ImageCapture.OutputFileOptions outputFileOptions = new ImageCapture.OutputFileOptions.Builder(file).build();

        ImageCapture.OnImageSavedCallback imageSavedCallback = new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                if (imageView == null) {
                    imageView = new ImageView(getContext());
                    imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                    addView(imageView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

                }
                if (imageLoader != null) {
                    imageLoader.load(imageView, outputFileResults.getSavedUri());
                }
                if (onImageSavedCallback != null) {
                    onImageSavedCallback.onImageSaved(outputFileResults);
                }

            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("Carlos", "拍照失败：" + exception.getLocalizedMessage());
                if (onImageSavedCallback != null) {
                    onImageSavedCallback.onError(exception);
                }
            }
        };
        imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(getContext()), imageSavedCallback);
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_CREATE)
    public void onCreate(LifecycleOwner owner) {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        } else {
            Log.d("Carlos", "没有照相机权限");
        }
    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_RESUME)
    public void onResume(LifecycleOwner owner) {


    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_PAUSE)
    public void onPause(LifecycleOwner owner) {

    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_STOP)
    public void onStop(LifecycleOwner owner) {

    }

    @OnLifecycleEvent(value = Lifecycle.Event.ON_DESTROY)
    public void onDestroy(LifecycleOwner owner) {

    }

    /**
     * 手电筒
     *
     * @param torch
     */
    public void flashEnable(boolean torch) {
        if (camera != null) {
            camera.getCameraControl().enableTorch(torch);
        }
    }

    private ImageLoader imageLoader;

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }
}
