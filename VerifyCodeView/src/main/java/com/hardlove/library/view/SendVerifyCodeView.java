package com.hardlove.library.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

/**
 * 发送验证码View
 */
public class SendVerifyCodeView extends AppCompatTextView implements View.OnClickListener {
    private final int normalTextColor;//倒计时结束时的颜色（可点击时的颜色）
    private final int unableTextColor;//倒计时过程中的颜色（不可点击时的颜色）
    private int total = 30;//默认30秒
    private Handler mHandler;
    private int i;
    private String normalTips;
    private String resendTips;
    private OnSendCodeClickListener onSendCodeClickListener;

    public SendVerifyCodeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        //默认值
        normalTips = getContext().getString(R.string.send_verify_code);
        resendTips = getContext().getString(R.string.resend_verify_code);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SendVerifyCodeView);
        total = typedArray.getInt(R.styleable.SendVerifyCodeView_totalCountDownTime, total);
        normalTextColor = typedArray.getColor(R.styleable.SendVerifyCodeView_normalTextColor, getCurrentTextColor());
        unableTextColor = typedArray.getColor(R.styleable.SendVerifyCodeView_unableTextColor, getCurrentTextColor());
        normalTips = typedArray.getString(R.styleable.SendVerifyCodeView_normalTips);
        resendTips = typedArray.getString(R.styleable.SendVerifyCodeView_resendTips);

        typedArray.recycle();

        setGravity(Gravity.CENTER);
        setText(normalTips);
        setTextColor(normalTextColor);
        setOnClickListener(this);
    }

    public void setOnSendCodeClickListener(OnSendCodeClickListener onSendCodeClickListener) {
        this.onSendCodeClickListener = onSendCodeClickListener;
    }


    @Override
    public void onClick(View v) {
        if (onSendCodeClickListener != null) {
            onSendCodeClickListener.onSendClick();
        }
    }

    /**
     * 设置倒计时时长 单位 秒
     *
     * @param second
     */
    public void setCountDownTime(int second) {
        this.total = second;
    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                i++;
                refreshTime();
                if (i >= total) {
                    stop();
                    if (onSendCodeClickListener != null) {
                        onSendCodeClickListener.onCountDownFinish();
                    }
                } else {
                    mHandler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        };

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
    }


    public void start() {
        i = 0;
        setEnabled(false);
        setTextColor(unableTextColor);
        refreshTime();
        mHandler.sendEmptyMessageDelayed(1, 1000);
    }

    public void stop() {
        i = 0;
        mHandler.removeCallbacksAndMessages(null);
        setEnabled(true);
        setTextColor(normalTextColor);
        setText(resendTips);
    }

    public void setNormalTips(String normalTips) {
        this.normalTips = normalTips;
        setText(normalTips);
    }

    public void setResendTips(String resendTips) {
        this.resendTips = resendTips;
    }

    private void refreshTime() {
        setText(String.format("%s s", (total - i)));
    }

    public interface OnSendCodeClickListener {
        /**
         * 发送验证码
         **/
        void onSendClick();

        /**
         * 倒计时结束
         */
        void onCountDownFinish();
    }

}
