package fun.learnlife.androidmq.engine;

import android.util.Log;

import java.util.Arrays;

import fun.learnlife.mqlibrary.Agent;
import fun.learnlife.mqlibrary.HCallBack;
import fun.learnlife.mqlibrary.Protocol;

public class DMEngine {
    private static final String TAG = "DMEngine";
    private Agent agent;

    public DMEngine(Agent agent) {
        this.agent = agent;
        agent.subscribe(new HCallBack.ISubscriber() {
            @Override
            public void onReceive(String topic, Object... extras) {
                Log.e(TAG, "receive,topic = " + topic + ", extras = " + Arrays.toString(extras));
            }
        }, new String[]{Protocol.wake, Protocol.nlu});
    }

    public void sendSessionEnd() {
        agent.publish(Protocol.session_end, "一轮对话结束了。。。");
    }
}
