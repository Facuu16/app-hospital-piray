package com.facuu16.hp.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskUtil {

    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();

    private TaskUtil() {
        throw new UnsupportedOperationException();
    }

    public static void runAsyncTask(Runnable callback) {
        EXECUTOR.execute(new RunnableTask(callback));
    }

    private static class RunnableTask implements Runnable {
        private final Runnable callback;

        public RunnableTask(Runnable callback) {
            this.callback = callback;
        }

        @Override
        public void run() {
            callback.run();
        }
    }
}
