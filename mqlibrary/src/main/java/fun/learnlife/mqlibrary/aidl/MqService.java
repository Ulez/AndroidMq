package fun.learnlife.mqlibrary.aidl;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import java.util.Arrays;
import java.util.HashMap;

import fun.learnlife.mqlibrary.IAddTopic;
import fun.learnlife.mqlibrary.IOnNewTopicArrivedListener;
import fun.learnlife.mqlibrary.ITopicManager;

public class MqService extends Service {
    private static final String TAG = "MqService";
    private IAddTopic addTopicListener;
    private HashMap<String, RemoteCallbackList<IOnNewTopicArrivedListener>> mCallClient = new HashMap();


    private Binder mBinder = new ITopicManager.Stub() {

        //voice -> client
        @Override
        public void publishToClient(String topic, String[] extras) throws RemoteException {
            int N = 0;
            if (mCallClient.get(topic) == null || (N = mCallClient.get(topic).beginBroadcast()) < 1) {
                Log.e(TAG, "no client need topic = " + topic);
                return;
            }
            for (int i = 0; i < N; i++) {
                IOnNewTopicArrivedListener arrivedListener = mCallClient.get(topic).getBroadcastItem(i);
                if (arrivedListener != null) {
                    try {
                        arrivedListener.onClientTopicArrived(topic, extras);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
            mCallClient.get(topic).finishBroadcast();
        }

        @Override
        public void publishToVoice(String topic, String[] extras) throws RemoteException {
            Log.e(TAG, "client publish to voice topic = " + topic + ", extras = " + Arrays.toString(extras));
            addTopicListener.publishToVoice(topic, extras);
        }

        @Override
        public void registerClientListener(String[] topics, IOnNewTopicArrivedListener listener) throws RemoteException {
            if (topics != null && topics.length > 0) {
                for (String topic : topics) {
                    if (mCallClient.get(topic) == null) {
                        mCallClient.put(topic, new RemoteCallbackList<>());
                        Log.i(TAG, "client need topic = " + topic);
                        addTopicListener.addClientNeedTopics(topic);
                    }
                    mCallClient.get(topic).register(listener);
                    final int N = mCallClient.get(topic).beginBroadcast();
                    mCallClient.get(topic).finishBroadcast();
                    Log.i(TAG, "registerListener, current size:" + N);
                }
            }
        }

        @Override
        public void addClientNeedTopicListener(IAddTopic l) throws RemoteException {
            addTopicListener = l;
        }

    };

    public MqService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
}