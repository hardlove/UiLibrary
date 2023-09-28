package com.carlos.widgets;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;


public class CompassView extends ImageView {
    private Bitmap backgroundBitmap;
    private Bitmap pointerBitmap;
    private float mDirection = 0;
    private float mTargetDirection = 0;

    private int centerX;
    private int centerY;
    private int radius;
    private final Matrix matrix = new Matrix();
    private final Rect src = new Rect();
    private final RectF dst = new RectF();
    private SensorManager sensorManager;

    private boolean isLocked;
    private final float MAX_ROATE_DEGREE = 1f;
    private final DecelerateInterpolator mInterpolator = new DecelerateInterpolator();


    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    public CompassView(Context context) {
        this(context, null);
    }

    public CompassView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CompassView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // 解析自定义属性
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CompassView);
        int backgroundResId = a.getResourceId(R.styleable.CompassView_background, 0);
        int pointerResId = a.getResourceId(R.styleable.CompassView_pointer, 0);
        if (backgroundResId != 0) {
            setBackground(BitmapFactory.decodeResource(getResources(), backgroundResId));
        }
        if (pointerResId != 0) {
            setPointer(BitmapFactory.decodeResource(getResources(), pointerResId));
        }
        a.recycle();


        registerListener();
    }

    public void setBackground(Bitmap bitmap) {
        backgroundBitmap = bitmap;
        invalidate();
    }

    public void setPointer(Bitmap bitmap) {
        pointerBitmap = bitmap;

        invalidate();
    }

    public void setAngle(float angle) {
        mDirection = angle;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int width = getMeasuredWidth() - getPaddingStart() - getPaddingEnd();
        int height = getMeasuredHeight() - getPaddingStart() - getPaddingEnd();
        centerX = width / 2;
        centerY = height / 2;
        radius = Math.min(centerX, centerY);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.save();

        matrix.reset();
        matrix.postRotate(mDirection, centerX, centerY);
        canvas.setMatrix(matrix);

        if (backgroundBitmap != null) {
            src.left = 0;
            src.right = backgroundBitmap.getWidth();
            src.top = 0;
            src.bottom = backgroundBitmap.getHeight();

            dst.left = centerX - radius;
            dst.right = centerX + radius;
            dst.top = centerY - radius;
            dst.bottom = centerY + radius;
            canvas.drawBitmap(backgroundBitmap, src, dst, null);
        }
        canvas.restore();

        if (pointerBitmap != null) {
            src.left = 0;
            src.right = pointerBitmap.getWidth();
            src.top = 0;
            src.bottom = pointerBitmap.getHeight();

            dst.left = centerX - (pointerBitmap.getWidth() >> 1);
            dst.right = centerX + (pointerBitmap.getWidth() >> 1);
            dst.top = centerY - (pointerBitmap.getHeight() >> 1);
            dst.bottom = centerY + (pointerBitmap.getHeight() >> 1);


            canvas.drawBitmap(pointerBitmap, src, dst, null);
        }

    }


    public void registerListener() {
        if (isSupport()) {
            // 获取传感器管理器
            sensorManager = (SensorManager) getContext().getSystemService(Context.SENSOR_SERVICE);

            // 获取磁力传感器和加速度传感器
            Sensor orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
            Sensor orientationVectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            Sensor magneticSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            Sensor accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

            boolean flag = false;
            // 注册传感器监听器
            if (orientationSensor != null && !flag) {
                sensorManager.registerListener(sensorEventListener, orientationSensor, SensorManager.SENSOR_DELAY_GAME);
                flag = true;
            }
            if (orientationVectorSensor != null && !flag) {
                sensorManager.registerListener(sensorEventListener, orientationVectorSensor, SensorManager.SENSOR_DELAY_GAME);
                flag = true;

            }

            if (magneticSensor != null && accelerometerSensor != null && !flag) {
                sensorManager.registerListener(sensorEventListener, magneticSensor, SensorManager.SENSOR_DELAY_GAME);
                sensorManager.registerListener(sensorEventListener, accelerometerSensor, SensorManager.SENSOR_DELAY_GAME);
                flag = true;
            }
            if (!flag) {
                Log.e("Carlos", "抱歉,你的设备不支持");
                Toast.makeText(getContext(), "抱歉,你的设备不支持", Toast.LENGTH_SHORT).show();
            }

            if (flag) {
                mHandler.postDelayed(updateRunnable, 20);
            }

        } else {
            Log.e("Carlos", "抱歉,你的设备不支持");
            Toast.makeText(getContext(), "抱歉,你的设备不支持", Toast.LENGTH_SHORT).show();

        }


    }

    private boolean isSupport() {
        return getContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);

    }


    private final Handler mHandler = new Handler();
    private final Runnable updateRunnable = new Runnable() {
        @Override
        public void run() {
            if (!isLocked) {

                if (mDirection != mTargetDirection) {
                    // calculate the short routine
                    float to = mTargetDirection;
                    if (to - mDirection > 180) {
                        to -= 360;
                    } else if (to - mDirection < -180) {
                        to += 360;
                    }

                    // limit the max speed to MAX_ROTATE_DEGREE
                    float distance = to - mDirection;
                    if (Math.abs(distance) > MAX_ROATE_DEGREE) {
                        distance = distance > 0 ? MAX_ROATE_DEGREE : (-1.0f * MAX_ROATE_DEGREE);
                    }

                    // need to slow down if the distance is short
                    mDirection = normalizeDegree(mDirection + ((to - mDirection) * mInterpolator.getInterpolation(Math.abs(distance) > MAX_ROATE_DEGREE ? 0.4f : 0.3f)));
                    setAngle(mDirection);
                }
            }

            mHandler.postDelayed(updateRunnable, 20);
        }
    };

    private float normalizeDegree(float degree) {
        return (degree + 720) % 360;
    }


    private final float[] mAccelerometerValues = new float[3];
    private final float[] mMagneticFieldValues = new float[3];
    private final float[] mRotationMatrix = new float[9];
    private final float[] mOrientationValues = new float[3];

    private boolean flag1 = false;
    private boolean flag2 = false;
    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            float direction;
            Log.d("Carlos", "onSensorChanged~~~~~~~~~~~ type:" + sensorEvent.sensor.getType() + "  name:" + sensorEvent.sensor.getName());
            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    direction = sensorEvent.values[0] * -1.0f;
                    mTargetDirection = normalizeDegree(direction);

                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    // 处理旋转矢量传感器数据
                    SensorManager.getRotationMatrixFromVector(mRotationMatrix, sensorEvent.values);
                    SensorManager.getOrientation(mRotationMatrix, mOrientationValues);
                    direction = (float) Math.toDegrees(mOrientationValues[0]) * -1.0f;
                    mTargetDirection = normalizeDegree(direction);
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    if (!flag1) {
                        flag1 = true;
                    }
                    // 处理磁场传感器数据
                    System.arraycopy(sensorEvent.values, 0, mMagneticFieldValues, 0, 3);
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    if (!flag2) {
                        flag2 = true;
                    }
                    // 处理加速度传感器数据
                    System.arraycopy(sensorEvent.values, 0, mAccelerometerValues, 0, 3);
                    break;
            }

            // 获取当前方向
            if (flag1 && flag2) {
                SensorManager.getRotationMatrix(mRotationMatrix, null, mAccelerometerValues, mMagneticFieldValues);
                SensorManager.getOrientation(mRotationMatrix, mOrientationValues);
                float azimuth = (float) Math.toDegrees(mOrientationValues[0]) * -1.0f;
                mTargetDirection = normalizeDegree(azimuth);
            }


        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };


    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (sensorManager != null) {
            sensorManager.unregisterListener(sensorEventListener);
        }
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}