package fun.learnlife.androidmq.engine;

import android.util.Log;

import java.util.Arrays;

import fun.learnlife.mqlibrary.Agent;
import fun.learnlife.mqlibrary.HCallBack;
import fun.learnlife.mqlibrary.Protocol;

public class FrontEngine {
    private static final String TAG = "FrontEngine";
    private Agent agent;

    public FrontEngine(Agent agent) {
        this.agent = agent;
        agent.subscribe(new HCallBack.ISubscriber() {
            @Override
            public void onReceive(String topic, Object... extras) {
                Log.e(TAG, "receive,topic = " + topic + ", extras = " + Arrays.toString(extras));
            }
        }, new String[]{Protocol.session_end, Protocol.nlu});
    }

    public void sendAwake() {
        agent.publish(Protocol.wake, "唤醒了。。。");
    }

    public void sendVadStart() {
        agent.publish(Protocol.vad_start, "vad start。。。");
    }

    public void sendVadEnd() {
        agent.publish(Protocol.vad_end, "vad end。。。");
    }
}
