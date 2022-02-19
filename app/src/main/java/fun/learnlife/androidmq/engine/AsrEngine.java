package fun.learnlife.androidmq.engine;

import android.util.Log;

import java.util.Arrays;

import fun.learnlife.mqlibrary.Agent;
import fun.learnlife.mqlibrary.HCallBack;
import fun.learnlife.mqlibrary.Protocol;

public class AsrEngine {
    private static final String TAG = "AsrEngine";
    private Agent agent;

    public AsrEngine(Agent agent) {
        this.agent = agent;
        agent.subscribe(new HCallBack.ISubscriber() {
            @Override
            public void onReceive(String topic, Object... extras) {
                Log.e(TAG, "receive,topic = " + topic + ", extras = " + Arrays.toString(extras));
            }
        }, new String[]{Protocol.wake});
    }

    public void sendAsr() {
        agent.publish(Protocol.asr, "asr:我是大聪明", false);
    }

    public void sendNlu() {
        agent.publish(Protocol.nlu, "domain:我是大聪明", true);
    }
}
