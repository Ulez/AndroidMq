package fun.learnlife.androidmq.engine;

import android.util.Log;

import java.util.Arrays;

import fun.learnlife.mqlibrary.Agent;
import fun.learnlife.mqlibrary.HCallBack;
import fun.learnlife.mqlibrary.Protocol;

public class VoiceUi {
    private static final String TAG = "VoiceUi";
    private Agent agent;

    public VoiceUi(Agent agent) {
        this.agent = agent;
        agent.subscribe(new HCallBack.ISubscriber() {
            @Override
            public void onReceive(String topic, Object... extras) {
                Log.e(TAG, "receive,topic = " + topic + ", extras = " + Arrays.toString(extras));
            }
        }, new String[]{
                Protocol.wake,
                Protocol.vad_start,
                Protocol.vad_end,
                Protocol.tts_start,
                Protocol.tts_end,
                Protocol.asr,
                Protocol.nlu,
                Protocol.session_end
        });
    }
}
