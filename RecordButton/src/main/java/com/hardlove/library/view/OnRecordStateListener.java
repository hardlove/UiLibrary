package com.hardlove.library.view;

/**
 * Auth:CL
 * Description：录音状态监听
 **/
public interface OnRecordStateListener {
    void onPrepared();

    void onStartRecording();

    void onRecordCompleted(String audioPath, int length);

    void onTooShort();

    void onWantToCancel();

    void onCancel();

    void onRecording();

    void onError(String msg);

    void updateVoiceLevel(int level);

    void onInitState();
}
