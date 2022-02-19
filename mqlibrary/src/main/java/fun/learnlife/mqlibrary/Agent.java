package fun.learnlife.mqlibrary;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import java.util.Arrays;

public class Agent {
    private static final String TAG = "Agent";

    private Handler handler;

    private static class LazyHolder {
        private static Agent sInstance = new Agent();
    }

    public static final Agent getInstance() {
        return LazyHolder.sInstance;
    }

    private Agent() {
        HandlerThread handlerThread = new HandlerThread("publish-thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new HCallBack());
    }

    public void publish(String topic, Object... extras) {
        Log.i(TAG, "publish,topic = " + topic + ", extras = " + Arrays.toString(extras));
        Message.obtain(handler, HCallBack.WHAT_PUBLISH, new Op(topic, extras)).sendToTarget();
    }

    public void subscribe(HCallBack.ISubscriber listener, String[] topics) {
        Log.i(TAG, "subscribe,topics = " + Arrays.toString(topics) + ", listener = " + listener);
        Message.obtain(handler, HCallBack.WHAT_SUBSCRIBE, new Op(topics, listener)).sendToTarget();
    }

    public void unsubscribe(String[] topics, HCallBack.ISubscriber listener) {
        Log.i(TAG, "unsubscribe,topics = " + Arrays.toString(topics) + ", listener = " + listener);
        Message.obtain(handler, HCallBack.WHAT_UNSUBSCRIBE, new Op(topics, listener)).sendToTarget();
    }

}
