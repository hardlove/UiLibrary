package com.hardlove.library.view;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.hardlove.library.view.recordbutton.R;


/**
 * 录音提示dialog
 *
 * @author lizheng
 */
public class AudioDialogManager {

    private Dialog mDialog;
    private ImageView mIcon;
    private ImageView mVoice;
    private TextView mLable;
    private Context mContext;

    public AudioDialogManager(Context context) {
        mContext = context;
    }

    public void showRecordingDialog() {
        if (mDialog == null) {
            mDialog = new Dialog(mContext, R.style.record_button_toast_dialog_style);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.dialog_recorder, null);
            mDialog.setContentView(view);


            mIcon = (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_icon);
            mVoice = (ImageView) mDialog.findViewById(R.id.id_recorder_dialog_voice);
            mLable = (TextView) mDialog.findViewById(R.id.id_recorder_dialog_label);
            mLable.setText(R.string.record_button_toCancel);
        }

        mDialog.show();
    }

    public void recording() {
        // 正在录音
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.VISIBLE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.recorder);
            mLable.setText(R.string.record_button_toCancel);
        }
    }

    public void wantToCancel() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.cancel);
            mLable.setText(R.string.record_button_releaseToCancel);
        }
    }

    public void tooShort() {
        if (mDialog != null && mDialog.isShowing()) {
            mIcon.setVisibility(View.VISIBLE);
            mVoice.setVisibility(View.GONE);
            mLable.setVisibility(View.VISIBLE);

            mIcon.setImageResource(R.drawable.voice_to_short);
            mLable.setText(R.string.record_button_pleaseSayMore);
        }
    }

    public void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    public void hideDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.hide();
        }
    }
    /**
     * 用level更新voice图片
     *
     * @param level
     */
    public void updateVoiceLevel(int level) {
        if (mDialog != null && mDialog.isShowing()) {

            int resId = mContext.getResources().getIdentifier(
                    "voice_" + level, "drawable",
                    mContext.getPackageName());
            mVoice.setImageResource(resId);
        }
    }
}
