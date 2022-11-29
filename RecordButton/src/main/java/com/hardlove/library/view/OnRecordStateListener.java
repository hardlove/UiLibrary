package com.hardlove.library.view;

/**
 * Auth:CL
 * Description：录音状态监听
 **/
public interface OnRecordStateListener {
    void onPrepared();

    void onStartRecoding();

    void onRecordCompleted();

    void onTooShort();

    void onWantCancel();

    void onCancel();

    void onRecording();

    void onError(String msg);

    void updateVoiceLevel(int level);

    void onInitState();
}
