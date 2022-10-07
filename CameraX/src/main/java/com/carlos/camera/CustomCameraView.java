package com.carlos.camera;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
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
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

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

    public void setImageLoader(ImageLoader imageLoader) {
        this.imageLoader = imageLoader;
    }

    private ImageLoader imageLoader;

    public CustomCameraView(@NonNull Context context) {
        super(context);
        bindToLifeCycle((LifecycleOwner) context);

    }

    public CustomCameraView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        bindToLifeCycle((LifecycleOwner) context);
    }


    int flashMode = ImageCapture.FLASH_MODE_AUTO;
    private int aspectRatio = AspectRatio.RATIO_16_9;
    /*是否是后置摄像头*/
    private boolean isBackFacing = true;
    private LifecycleOwner lifecycleOwner;
    private boolean isCameraOpen;

    public void bindToLifeCycle(LifecycleOwner lifecycleOwner) {
        this.lifecycleOwner = lifecycleOwner;
        lifecycleOwner.getLifecycle().addObserver(this);

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        startCamera(getContext());
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (processCameraProvider != null) {
            processCameraProvider.unbindAll();
        }

    }


    private void startCamera(Context context) {
        ListenableFuture<ProcessCameraProvider> cameraProviderListenableFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderListenableFuture.addListener(() -> {
            try {
                processCameraProvider = cameraProviderListenableFuture.get();

                bindUseCase(processCameraProvider);
            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(getContext()));

    }

    /**
     * 设置拍照时闪光灯模式：
     * ImageCapture.FLASH_MODE_AUTO  自动模式
     * ImageCapture.FLASH_MODE_ON    拍照时打开
     * ImageCapture.FLASH_MODE_OFF   拍照是关闭
     *
     * @param flashMode
     */
    public void setFlashMode(@ImageCapture.FlashMode int flashMode) {
        this.flashMode = flashMode;
        imageCapture.setFlashMode(flashMode);
    }

    /**
     * 手电筒
     *
     * @param torch true:打开  false:关闭
     */
    public void enableTorch(boolean torch) {
        if (camera != null) {
            camera.getCameraControl().enableTorch(torch);
        }
    }

    public void setAspectRatio(@AspectRatio.Ratio int aspectRatio) {
        this.aspectRatio = aspectRatio;
        bindUseCase(processCameraProvider);
    }

    /**
     * 切换前置后置摄像头
     *
     * @param isBack 是否是后置摄像头
     */
    public void changeLensFacingModel(boolean isBack) {
        if (isBackFacing != isBack) {
            isBackFacing = isBack;
            bindUseCase(processCameraProvider);
        }
    }


    private void bindUseCase(ProcessCameraProvider cameraProvider) {
        if (cameraProvider == null) return;
        if (previewView == null) {
            previewView = new PreviewView(getContext());
            previewView.setScaleType(PreviewView.ScaleType.FIT_CENTER);
            addView(previewView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
        }
        Display display = previewView.getDisplay();
        if (display == null) {
            return;
        }
        int rotation = display.getRotation();
        preview = new Preview.Builder()
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .build();

        imageCapture = new ImageCapture.Builder()
                .setFlashMode(flashMode)
                .setTargetAspectRatio(aspectRatio)
                .setTargetRotation(rotation)
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                .build();

        cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(isBackFacing ? CameraSelector.LENS_FACING_BACK : CameraSelector.LENS_FACING_FRONT)
                .build();


        // Unbind use cases before rebinding
        cameraProvider.unbindAll();
        camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview, imageCapture);

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

    }

    /*图片保存路径*/
    private String saveDirPath;

    public void setSaveDirPath(String saveDirPath) {
        this.saveDirPath = saveDirPath;
    }

    private String getImageSavePath() {
        if (TextUtils.isEmpty(saveDirPath)) {
            return getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();
        } else {
            return saveDirPath;
        }
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
                if (imageView.getVisibility() != VISIBLE) {
                    imageView.setVisibility(VISIBLE);
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


    /**
     * 取消拍照
     */
    public void cancel() {
        if (imageView != null) {
            imageView.setVisibility(INVISIBLE);
        }
    }
}


