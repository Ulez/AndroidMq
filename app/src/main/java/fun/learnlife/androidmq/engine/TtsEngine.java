package fun.learnlife.androidmq.engine;

import android.util.Log;

import java.util.Arrays;

import fun.learnlife.mqlibrary.Agent;
import fun.learnlife.mqlibrary.HCallBack;
import fun.learnlife.mqlibrary.Protocol;

public class TtsEngine {
    private static final String TAG = "TtsEngine";
    private Agent agent;

    public TtsEngine(Agent agent) {
        this.agent = agent;
        agent.subscribe(new HCallBack.ISubscriber() {
            @Override
            public void onReceive(String topic, Object... extras) {
                Log.e(TAG, "receive,topic = " + topic + ", extras = " + Arrays.toString(extras));
            }
        }, new String[]{Protocol.session_end});
    }

    public void sendTtsStart() {
        agent.publish(Protocol.tts_start, "tts start");
    }

    public void sendTtsEnd() {
        agent.publish(Protocol.tts_end, "tts end");
    }
}
