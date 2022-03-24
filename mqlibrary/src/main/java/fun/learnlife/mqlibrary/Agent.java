package fun.learnlife.mqlibrary;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;
import java.util.HashSet;

import fun.learnlife.mqlibrary.aidl.MqService;

public class Agent {
    private static final String TAG = "Agent";

    private Handler handler;
    private static Agent sInstance;
    private static ITopicManager iTopicManager;
    private HashSet<String> clientTopics = new HashSet<>();

    public static Agent getInstance() {
        if (sInstance == null) {
            synchronized (Agent.class) {
                if (sInstance == null) {
                    sInstance = new Agent();
                }
            }
        }
        return sInstance;
    }


    private IBinder.DeathRecipient mDeathRecipient = new IBinder.DeathRecipient() {
        @Override
        public void binderDied() {
            Log.d(TAG, "binder died. tname:" + Thread.currentThread().getName());
            if (iTopicManager == null)
                return;
            iTopicManager.asBinder().unlinkToDeath(mDeathRecipient, 0);
            iTopicManager = null;
            ContextHolder.getContext().bindService(new Intent(ContextHolder.getContext(), MqService.class), serviceConnection, Context.BIND_AUTO_CREATE);
        }
    };

    private IAddTopic ll = new IAddTopic.Stub() {
        @Override
        public void addClientNeedTopics(String topic) throws RemoteException {
            Log.e(TAG, "voice know client need topic = " + topic);
            clientTopics.add(topic);
        }

        @Override
        public void publishToVoice(String topic, String[] extras) throws RemoteException {
            Log.e(TAG, "voice receive client data, topic = " + topic + ", extras = " + Arrays.toString(extras));
            publishFromClient(topic, (Object[]) extras);
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ITopicManager topicManager = ITopicManager.Stub.asInterface(service);
            iTopicManager = topicManager;
            try {
                iTopicManager.asBinder().linkToDeath(mDeathRecipient, 0);
                iTopicManager.addClientNeedTopicListener(ll);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "onServiceDisconnected");
            iTopicManager = null;
        }
    };

    private Agent() {
        HandlerThread handlerThread = new HandlerThread("publish-thread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new HCallBack());
        Intent intent = new Intent(ContextHolder.getContext(), MqService.class);
        ContextHolder.getContext().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * client发过来的消息，只需要发给voice内部就够了
     *
     * @param topic
     * @param extras
     */
    private void publishFromClient(String topic, Object... extras) {
        Log.i(TAG, "publish,topic = " + topic + ", extras = " + Arrays.toString(extras));
        Message.obtain(handler, HCallBack.WHAT_PUBLISH, new Op(topic, extras)).sendToTarget();
    }

    /**
     * voice发送的内部消息，同时要判断是不是需要发给client.
     *
     * @param topic
     * @param extras
     */
    public void publish(String topic, Object... extras) {
        Log.i(TAG, "publish, topic = " + topic + ", extras = " + Arrays.toString(extras));
        Message.obtain(handler, HCallBack.WHAT_PUBLISH, new Op(topic, extras)).sendToTarget();
        try {
            if (clientTopics.contains(topic)) {
                if (extras instanceof String[]) {
                    iTopicManager.publishToClient(topic, (String[]) extras);
                } else {
                    Log.e(TAG, "extras are not  (String[]) type");
                }
            } else {
                Log.e(TAG, "no client need topic = " + topic);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(HCallBack.ISubscriber listener, String[] topics) {
        Log.i(TAG, "subscribe,topics = " + Arrays.toString(topics) + ", listener = " + listener);
        Message.obtain(handler, HCallBack.WHAT_SUBSCRIBE, new Op(topics, listener)).sendToTarget();
    }

    public void unsubscribe(String[] topics, HCallBack.ISubscriber listener) {
        Log.i(TAG, "unsubscribe,topics = " + Arrays.toString(topics) + ", listener = " + listener);
        Message.obtain(handler, HCallBack.WHAT_UNSUBSCRIBE, new Op(topics, listener)).sendToTarget();
    }

    public void addInterceptor(HCallBack.IInterceptor interceptor, String topic) {
        Log.i(TAG, "addInterceptor,topic = " + interceptor + ", interceptor = " + interceptor);
        Message.obtain(handler, HCallBack.WHAT_SUBSCRIBE_INTERCEPTOR, new Op(topic, interceptor)).sendToTarget();
    }

    public void removeInterceptor(String topic) {
        Log.i(TAG, "removeInterceptor,topic = " + topic);
        Message.obtain(handler, HCallBack.WHAT_UNSUBSCRIBE_INTERCEPTOR, new Op(topic)).sendToTarget();
    }

}
