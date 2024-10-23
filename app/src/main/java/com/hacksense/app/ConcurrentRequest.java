package com.hacksense.app;

import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public /*utility*/ class ConcurrentRequest {
    private static final Executor ioThread = Executors.newSingleThreadExecutor();
    private static final AtomicBoolean isOngoingRequest = new AtomicBoolean(false);

    public static void startRequest(@NotNull Consumer<State> onSuccess, @Nullable Runnable onFail) {
        if (isOngoingRequest.compareAndSet(false, true)) {
            Log.d(HACKSense.TAG, "Launching request on thread");
            ioThread.execute(() -> {
                try {
                    var result = RequestSender.execute();
                    if (result != null) {
                        Log.d(HACKSense.TAG ,"Request successful, calling back");
                        State state = State.fromJson(result);
                        onSuccess.accept(state);
                    }

                    else if (onFail != null) {
                        onFail.run();
                    }
                } finally {
                    isOngoingRequest.set(false);
                }
            });
        } else {
            Log.i(HACKSense.TAG, "Refresh requested while another request is ongoing");
        }
    }

    public static void startRequest(@NotNull Consumer<State> onSuccess) {
        startRequest(onSuccess, null);
    }
}
