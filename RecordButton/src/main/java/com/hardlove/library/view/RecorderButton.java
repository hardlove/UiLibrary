package com.hardlove.library.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.hardlove.library.view.recordbutton.R;

import java.io.File;

/**
 * 仿微信录音
 *
 * @author lizheng
 * created at 2018/8/21 下午1:45
 */
public class RecorderButton extends AppCompatButton {

    /**
     * 录音时是否显示录音状态弹框
     */
    private boolean dialogEnable = true;
    private AudioDialogManager mAudioDialogManager;
    private String mFileName = null;
    private String mSaveDirPath = null;//录音文件保存的目录
    private OnFinishedRecordListener recordListener;
    /**
     * 最短录音时间
     */
    private static final int MIN_INTERVAL_TIME = 2000;
    /**
     * 最大录音时长
     */
    private static final int maxRecordTime = Integer.MAX_VALUE;
    private long startTime = 0;
    /**
     * 录音时长
     */
    private long mLong;
    private MediaRecorder recorder;
    private ObtainDecibelThread thread;
    private Handler volumeHandler;
    /**
     * 两次点击录音按钮的间隔
     */
    private long doubleTime = 0;
    //是否应用于IM
    private boolean usedInIm = false;
    private Context mContext;
    private CheckRecordPermissionListener checkRecordPermissionListener;


    //检查录音时长
    private static int FLAG_CHECK_TIME = 0x01;
    private static final int FLAG_DISMISS_DIALOG = 0x02;
    private static final int FLAG_UPDATE_VOICE_LEVEL = 0x03;

    public RecorderButton(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public RecorderButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    public RecorderButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void setDialogEnable(boolean dialogEnable) {
        this.dialogEnable = dialogEnable;
    }

    public void setUsedInIm(boolean usedInIm) {
        this.usedInIm = usedInIm;
    }

    /**
     * 设置保存录音文件的目录
     *
     * @param saveDirPath
     */
    public void setSaveDirPath(String saveDirPath) {
        mSaveDirPath = saveDirPath;
    }

    /**
     * 获取录音文件保持目录
     *
     * @return
     */
    public String getSaveDirPath() {
        if (TextUtils.isEmpty(mSaveDirPath)) {
            return getDefaultSavaDirPath().getAbsolutePath();
        } else {
            return mSaveDirPath;
        }
    }

    /**
     * 获取录音文件默认保持目录
     *
     * @return
     */
    public File getDefaultSavaDirPath() {
        return new File(getContext().getExternalFilesDir(null), "voices");
    }


    public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
        recordListener = listener;
    }

    private void init() {
        volumeHandler = new ShowVolumeHandler();
        mAudioDialogManager = new AudioDialogManager(getContext());
    }

    private void prepareStartRecording() {
        initDialogAndStartRecord();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (dialogEnable) {
            mAudioDialogManager.dismissDialog();
        }
        volumeHandler.removeCallbacksAndMessages(null);
    }

    private long exitTime = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 屏蔽父控件拦截onTouch事件
        getParent().requestDisallowInterceptTouchEvent(true);

        int action = event.getAction();
        int x = (int) event.getX();
        int y = (int) event.getY();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                //验证录音权限
                if (checkRecordPermission()) {
                    if (checkExternalStoragePermission()) {
                        // 判断两次点击时间少于2秒不执行操作
                        doubleTime = System.currentTimeMillis() - exitTime;
                        if (doubleTime > 2000) {
                            prepareStartRecording();
                            exitTime = System.currentTimeMillis();
                        }
                    } else {
                        if (checkRecordPermissionListener != null) {
                            checkRecordPermissionListener.checkExternalStoragePermission();
                        }
                    }
                } else {
                    if (checkRecordPermissionListener != null) {
                        checkRecordPermissionListener.checkRecordPermission();
                    }
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (isRecording) {
                    // 根据XY坐标判断是否想要取消
                    if (wantToCancel(x, y)) {
                        changeState(STATE_WANT_TO_CANCEL);
                    } else {
                        changeState(STATE_RECORDING);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (isRecording) {
                    if (mCurState == STATE_WANT_TO_CANCEL) {
                        cancelRecord();
                    } else if (mCurState == STATE_RECORDING) {
                        finishRecord();
                    }
                    if (usedInIm) {
                        setBackgroundResource(R.drawable.button_recorder_normal);
                        setText(R.string.str_recorder_normal);
                    }
                }
                break;
            default:
                break;
        }

        return true;
    }

    private boolean checkExternalStoragePermission() {
        String saveDirPath = getSaveDirPath();
        String packageName = getContext().getPackageName();
        String temp = getContext().getFilesDir().getAbsolutePath();
        int end = temp.indexOf(packageName) + packageName.length();
        String prefix1 = temp.substring(0, end);
        temp = getContext().getExternalFilesDir(null).getAbsolutePath();
        end = temp.indexOf(packageName) + packageName.length();
        String prefix2 = temp.substring(0, end);
        if (!saveDirPath.startsWith(prefix1) && !saveDirPath.startsWith(prefix2)) {
            //需要存储权限
            return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private void initDialogAndStartRecord() {
        //防止没释放
        stopRecording();
        startRecording();
        if (isRecording) {
            if (onRecordStateListener != null) {
                onRecordStateListener.onStartRecording();
            }
            if (dialogEnable) {
                mAudioDialogManager.showRecordingDialog();
            }
            changeState(STATE_RECORDING);
        }
    }


    private boolean checkRecordPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkRecordPermission(Manifest.permission.RECORD_AUDIO)) {
                //录音
                return true;
            } else {
                //验证权限
                return false;
            }
        } else {
            //录音
            return true;
        }
    }

    public boolean checkRecordPermission(@NonNull String permission) {
        return ActivityCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void finishRecord() {
        stopRecording();
        if (doubleTime <= 2000) {
            return;
        }
        mLong = System.currentTimeMillis() - startTime;
        if (mLong < MIN_INTERVAL_TIME) {
            if (onRecordStateListener != null) {
                onRecordStateListener.onTooShort();
            }
            if (dialogEnable) {
                mAudioDialogManager.tooShort();
            }
            // 1/2秒后关闭提示
            volumeHandler.sendEmptyMessageDelayed(FLAG_DISMISS_DIALOG, 500);
            File file = new File(mFileName);
            file.delete();
            return;
        }
        if (dialogEnable) {
            mAudioDialogManager.dismissDialog();
        }
        if (recordListener != null) {
            int length = (int) (mLong / 1000);
            File file = new File(mFileName);
            if (file != null && file.length() == 0) {
                if (onRecordStateListener != null) {
                    onRecordStateListener.onError("录音失败，请确保录音和存储权限开启");
                }
                showDefaultAlert(getContext(), "录音失败，请确保录音和存储权限开启");
            } else {
                if (onRecordStateListener != null) {
                    onRecordStateListener.onRecordCompleted(mFileName, length);
                }
                recordListener.onCompleteRecord(mFileName, length);
            }
        }
    }


    public void showDefaultAlert(Context context, String msg) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setMessage(msg);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();//显示对话框
    }

    private void cancelRecord() {
        stopRecording();
        if (onRecordStateListener != null) {
            onRecordStateListener.onCancel();
        }
        if (dialogEnable) {
            mAudioDialogManager.dismissDialog();
        }
        // 删除文件
        File file = new File(mFileName);
        file.delete();

    }

    private void startRecording() {
        mFileName = generateRecordPathByCurrentTime();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        // 设置输出文件
        recorder.setOutputFile(mFileName);

        try {
            //防止第三方权限拦截软件拒绝录音权限出错
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        isRecording = true;
        thread = new ObtainDecibelThread();
        thread.start();

        startTime = System.currentTimeMillis();
    }

    private void stopRecording() {
        if (thread != null) {
            thread.exit();
            thread = null;
        }

        if (recorder != null) {
            try {
                //防止第三方权限拦截软件拒绝录音权限出错
                recorder.stop();
                recorder.release();
            } catch (Exception e) {
            }
            recorder = null;
        }

        isRecording = false;
    }

    /**
     * 获取录音保存路径
     *
     * @return
     */
    private String generateRecordPathByCurrentTime() {
        File filesDir = new File(getSaveDirPath());
        checkAndMkdirs(filesDir);
        return new File(filesDir, "record_" + System.currentTimeMillis()).getAbsolutePath() + ".amr";
    }


    private File checkAndMkdirs(File file) {
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public void setCheckRecordPermissionListener(CheckRecordPermissionListener checkRecordPermissionListener) {
        this.checkRecordPermissionListener = checkRecordPermissionListener;
    }

    private class ObtainDecibelThread extends Thread {

        private volatile boolean running = true;

        public void exit() {
            running = false;
        }

        @Override
        public void run() {
            while (running) {
                try {
                    Thread.sleep(100);
                    mLong += 0.1f;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (recorder == null || !running) {
                    break;
                }
                Message obtain = Message.obtain();
                obtain.what = FLAG_UPDATE_VOICE_LEVEL;
                obtain.arg1 = 7 * recorder.getMaxAmplitude() / 32768 + 1;
                //更新音量
                volumeHandler.sendMessage(obtain);
                //检查录音时长
                volumeHandler.sendEmptyMessage(FLAG_CHECK_TIME);
            }
        }

    }

    class ShowVolumeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == FLAG_CHECK_TIME) {
                long l = System.currentTimeMillis() - startTime;
                if (l >= maxRecordTime) {//超过最大录音限制时长
                    finishRecord();
                    if (usedInIm) {
                        setBackgroundResource(R.drawable.button_recorder_normal);
                        setText(R.string.str_recorder_normal);
                    }
                }
            } else if (msg.what == FLAG_DISMISS_DIALOG) {
                if (dialogEnable) {
                    mAudioDialogManager.dismissDialog();
                }
            } else if (msg.what == FLAG_UPDATE_VOICE_LEVEL){
                if (dialogEnable) {
                    mAudioDialogManager.updateVoiceLevel(msg.arg1);
                }
                if (onRecordStateListener != null) {
                    onRecordStateListener.updateVoiceLevel(msg.arg1);
                }
            }
        }
    }

    /**
     * 录音回调监听器
     */
    public interface OnFinishedRecordListener {
        void onCompleteRecord(String audioPath, int length);

    }

    /**
     * 改变dialog状态
     *
     * @param state
     */
    private static final int STATE_NORMAL = 1;
    private static final int STATE_RECORDING = 2;
    private static final int STATE_WANT_TO_CANCEL = 3;
    private int mCurState = STATE_NORMAL;
    private boolean isRecording = false;

    private OnRecordStateListener onRecordStateListener;

    public void setOnRecordStateListener(OnRecordStateListener onRecordStateListener) {
        this.onRecordStateListener = onRecordStateListener;
    }

    private void changeState(int state) {
        if (mCurState != state) {
            mCurState = state;
            switch (state) {
                case STATE_NORMAL:
                    if (usedInIm) {
                        setBackgroundResource(R.drawable.button_recorder_normal);
                        setText(R.string.str_recorder_normal);
                    }
                    if (onRecordStateListener != null) {
                        onRecordStateListener.onInitState();
                    }
                    if (dialogEnable) {
                        mAudioDialogManager.dismissDialog();
                    }
                    break;
                case STATE_RECORDING:
                    if (isRecording) {
                        if (usedInIm) {
                            setBackgroundResource(R.drawable.button_recorder_recording);
                            setText(R.string.str_recorder_recording);
                        }
                        if (onRecordStateListener != null) {
                            onRecordStateListener.onRecording();
                        }
                        if (dialogEnable) {
                            mAudioDialogManager.recording();
                        }
                    }
                    break;
                case STATE_WANT_TO_CANCEL:
                    if (isRecording) {
                        if (usedInIm) {
                            setBackgroundResource(R.drawable.button_recorder_recording);
                        }
                        if (onRecordStateListener != null) {
                            onRecordStateListener.onWantToCancel();
                        }
                        if (dialogEnable) {
                            mAudioDialogManager.wantToCancel();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 判断手指是否滑出按钮
     *
     * @param x
     * @param y
     * @return
     */
    private boolean wantToCancel(int x, int y) {
        if (x < 0 || x > getWidth()) {
            return true;
        }
        if (y < -50 || y > getHeight() + 50) {
            return true;
        }
        return false;
    }
}
