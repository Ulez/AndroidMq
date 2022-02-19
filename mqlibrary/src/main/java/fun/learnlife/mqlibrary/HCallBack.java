package fun.learnlife.mqlibrary;

import android.os.Handler;
import android.os.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import fun.learnlife.ThreadUtil;

public class HCallBack implements Handler.Callback {
    private HashMap<String, Set<ISubscriber>> hashMap = new HashMap();
    public static final int WHAT_SUBSCRIBE = 1001;
    public static final int WHAT_UNSUBSCRIBE = 1002;
    public static final int WHAT_PUBLISH = 2001;

    @Override
    public boolean handleMessage(Message msg) {
        Op op = (Op) msg.obj;
        switch (msg.what) {
            case WHAT_SUBSCRIBE:
                for (String topic : op.topics) {
                    if (hashMap.containsKey(topic)) {
                        hashMap.get(topic).add(op.listener);
                    } else {
                        hashMap.put(topic, new HashSet<ISubscriber>() {
                            {
                                add(op.listener);
                            }
                        });
                    }
                }
                break;
            case WHAT_UNSUBSCRIBE:
                for (String topic : op.topics) {
                    Set<ISubscriber> iSubscribers = hashMap.get(topic);
                    if (iSubscribers != null && iSubscribers.size() > 0) {
                        iSubscribers.remove(op.listener);
                        if (iSubscribers.size() < 1) {
                            hashMap.remove(topic);
                        }
                    }
                }
                break;
            case WHAT_PUBLISH:
                publish(op.topic, op.extra);
                break;
        }
        return false;
    }

    private void publish(String topic, Object... extras) {
        Set<ISubscriber> listeners = hashMap.get(topic);
        if (listeners != null && listeners.size() > 0) {
            Iterator<ISubscriber> iterator = listeners.iterator();
            ArrayList<Task> tasks = new ArrayList<>();
            while (iterator.hasNext()) {
                tasks.add(new Task(iterator.next(), topic, extras));
            }
            try {
                ThreadUtil.publishInvokeAll(tasks);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public interface ISubscriber {
        void onReceive(String topic, Object... extras);
    }
}
