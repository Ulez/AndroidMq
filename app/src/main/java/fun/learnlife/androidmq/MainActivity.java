package fun.learnlife.androidmq;

import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import fun.learnlife.ThreadUtil;
import fun.learnlife.androidmq.engine.AsrEngine;
import fun.learnlife.androidmq.engine.DMEngine;
import fun.learnlife.androidmq.engine.FrontEngine;
import fun.learnlife.androidmq.engine.TtsEngine;
import fun.learnlife.androidmq.engine.VoiceUi;
import fun.learnlife.mqlibrary.Agent;

public class MainActivity extends AppCompatActivity {
    AsrEngine asrEngine = new AsrEngine(Agent.getInstance());
    DMEngine dmEngine = new DMEngine(Agent.getInstance());
    FrontEngine frontEngine = new FrontEngine(Agent.getInstance());
    TtsEngine ttsEngine = new TtsEngine(Agent.getInstance());
    VoiceUi voiceUi = new VoiceUi(Agent.getInstance());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ThreadUtil.init();
    }

    public void startVoice(View view) {
        frontEngine.sendAwake();
        SystemClock.sleep(100);

        frontEngine.sendVadStart();
        SystemClock.sleep(1000);
        asrEngine.sendAsr();
        SystemClock.sleep(1000);

        frontEngine.sendVadEnd();
        SystemClock.sleep(1000);

        asrEngine.sendNlu();
        SystemClock.sleep(200);

        ttsEngine.sendTtsStart();
        SystemClock.sleep(2000);
        ttsEngine.sendTtsEnd();
        SystemClock.sleep(100);

        dmEngine.sendSessionEnd();
    }
}