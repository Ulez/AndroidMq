package fun.learnlife.androidmq.engine;

import android.util.Log;

import java.util.Arrays;

import fun.learnlife.mqlibrary.Agent;
import fun.learnlife.mqlibrary.HCallBack;
import fun.learnlife.mqlibrary.Protocol;

public class AsrEngine {
    private static final String TAG = "AsrEngine";
    private Agent agent;
    HCallBack.ISubscriber subscriber= new HCallBack.ISubscriber() {
        @Override
        public void onReceive(String topic, Object... extras) {
            Log.e(TAG, "receive,topic = " + topic + ", extras = " + Arrays.toString(extras));
        }
    };

    public AsrEngine(Agent agent) {
        this.agent = agent;
        agent.subscribe(subscriber, new String[]{Protocol.wake});

        agent.addInterceptor(new HCallBack.IInterceptor() {
            @Override
            public Object[] beforeReceive(String topic, Object... extras) {
                extras[0] = "加工后的呀:" + extras[0];
                return extras;
            }
        }, Protocol.nlu);
    }

    public void removeInterceptor(){
        agent.removeInterceptor(Protocol.nlu);
    }

    public void removeTopic(){
        agent.unsubscribe(new String[]{Protocol.wake},subscriber);
    }

    public void sendAsr() {
        agent.publish(Protocol.asr, "asr:我是大聪明", false);
    }

    public void sendNlu() {
        agent.publish(Protocol.nlu, "domain:我是大聪明", true);
    }
}
