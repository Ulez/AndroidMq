package fun.learnlife;

import android.os.Handler;
import android.os.Looper;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadUtil {
    private static final String TAG = "ThreadUtil";
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int CORE_POOL_SIZE = Math.max(2, CPU_COUNT - 1);
    //    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    private static final long KEEP_ALIVE_TIME = 20L;
    private static Handler sUIHandler;
    private static ExecutorService mExecutor;

    /**
     * 专用于消息的publish，会在短时间内多次调用，需要限制最大线程数。
     */
    private static ExecutorService mPublishExecutor;

    private static ScheduledExecutorService mScheduledExecutor;
    private static volatile boolean mInited = false;

    public static void init() {
        sUIHandler = new Handler(Looper.getMainLooper());
        mExecutor = new ThreadPoolExecutor(CORE_POOL_SIZE, Integer.MAX_VALUE, KEEP_ALIVE_TIME, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable r) {
                        return new Thread(r, "TU-Executor");
                    }
                });
        mScheduledExecutor = Executors.newScheduledThreadPool(0, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "TU-Scheduled");
            }
        });

        mPublishExecutor = new ThreadPoolExecutor(0, CORE_POOL_SIZE,
                5L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
                new ThreadFactory() {
                    @Override
                    public Thread newThread(Runnable runnable) {
                        return new Thread(runnable, "TU-publish");
                    }
                });
        mInited = true;
    }

    /**
     * 在UI线程执行
     *
     * @param runnable
     */
    public static void runOnUIThread(Runnable runnable) {
        checkInit();
        runOnUIThread(runnable, 0);
    }

    private static void checkInit() {
        if (!mInited) {
            throw new RuntimeException("ThreadUtil need to init!");
        }
    }

    public static void runOnUIThread(Runnable runnable, long delayMills) {
        checkInit();
        if (runnable != null) {
            if (Thread.currentThread() == Looper.getMainLooper().getThread() && delayMills == 0) {
                runnable.run();
            } else {
                sUIHandler.postDelayed(runnable, delayMills);
            }
        }
    }

    public static void execute(Runnable runnable) {
        checkInit();
        mExecutor.execute(runnable);
    }

    public static Future submit(Callable callable) {
        checkInit();
        return mExecutor.submit(callable);
    }

    public static void executeScheduled(Runnable runnable) {
        checkInit();
        mScheduledExecutor.execute(runnable);
    }

    public static <T> Future<T> submitScheduled(Callable<T> task) {
        checkInit();
        return mScheduledExecutor.submit(task);
    }

    public static <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
        checkInit();
        return mScheduledExecutor.schedule(callable, delay, unit);
    }

    public static ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
        checkInit();
        return mScheduledExecutor.schedule(command, delay, unit);
    }

    public static <T> List<Future<T>> publishInvokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        checkInit();
        return mPublishExecutor.invokeAll(tasks);
    }

    public static <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
        checkInit();
        return mExecutor.invokeAll(tasks);
    }

    /**
     * 周期性的执行任务
     *
     * @param command
     * @param initialDelay 初始化延迟开始执行
     * @param period       间隔时间
     * @param unit
     * @return
     */
    public static ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
        checkInit();
        return mScheduledExecutor.scheduleAtFixedRate(command, initialDelay, period, unit);
    }
}