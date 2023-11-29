package com.carlos.permissionhelper;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import java.util.HashMap;
import java.util.UUID;

public class TranslucentActivity extends FragmentActivity {
    private static final HashMap<String, TransActivityDelegate> CALLBACK_MAP = new HashMap<>();
    private TransActivityDelegate delegate;
    private static final String KEY = "uuid";
    private String uuid;

    public static void start(Context context, TransActivityDelegate delegate) {
        String uuid = UUID.randomUUID().toString();
        CALLBACK_MAP.put(uuid, delegate);
        Intent intent = new Intent(context, TranslucentActivity.class);
        intent.putExtra(KEY, uuid);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        overridePendingTransition(0, 0);
        super.onCreate(savedInstanceState);
        uuid = getIntent().getStringExtra(KEY);
        delegate = CALLBACK_MAP.get(uuid);
        assert delegate != null;
        delegate.onCreated(this, savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        delegate.onStarted(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        delegate.onResumed(this);
    }

    @Override
    protected void onPause() {
        overridePendingTransition(0, 0);
        super.onPause();
        delegate.onPaused(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        delegate.onStopped(this);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        delegate.onSaveInstanceState(this, outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        delegate.onDestroy(this);

        CALLBACK_MAP.remove(uuid);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        delegate.onRequestPermissionsResult(this, requestCode, permissions, grantResults);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        delegate.onActivityResult(this, requestCode, resultCode, data);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return delegate.dispatchTouchEvent(this, ev) || super.dispatchTouchEvent(ev);

    }

    public abstract static class TransActivityDelegate {
        public void onCreateBefore(@NonNull TranslucentActivity activity, @Nullable Bundle savedInstanceState) {/**/}

        public void onCreated(@NonNull TranslucentActivity activity, @Nullable Bundle savedInstanceState) {/**/}

        public void onStarted(@NonNull TranslucentActivity activity) {/**/}

        public void onDestroy(@NonNull TranslucentActivity activity) {/**/}

        public void onResumed(@NonNull TranslucentActivity activity) {/**/}

        public void onPaused(@NonNull TranslucentActivity activity) {/**/}

        public void onStopped(@NonNull TranslucentActivity activity) {/**/}

        public void onSaveInstanceState(@NonNull TranslucentActivity activity, Bundle outState) {/**/}

        public void onRequestPermissionsResult(@NonNull TranslucentActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {/**/}

        public void onActivityResult(@NonNull TranslucentActivity activity, int requestCode, int resultCode, Intent data) {/**/}

        public boolean dispatchTouchEvent(@NonNull TranslucentActivity activity, MotionEvent ev) {
            return false;
        }

    }
}
