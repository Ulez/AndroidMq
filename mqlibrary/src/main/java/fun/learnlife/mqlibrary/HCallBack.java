package fun.learnlife.mqlibrary;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import fun.learnlife.ThreadUtil;

public class HCallBack implements Handler.Callback {
    private static final String TAG = "HCallBack";
    private HashMap<String, Set<ISubscriber>> subscribers = new HashMap();
    private HashMap<String, IInterceptor> interceptors = new HashMap();
    public static final int WHAT_SUBSCRIBE = 1001;
    public static final int WHAT_UNSUBSCRIBE = 1002;
    public static final int WHAT_PUBLISH = 1003;
    public static final int WHAT_SUBSCRIBE_INTERCEPTOR = 2001;
    public static final int WHAT_UNSUBSCRIBE_INTERCEPTOR = 2002;


    @Override
    public boolean handleMessage(Message msg) {
        Op op = (Op) msg.obj;
        switch (msg.what) {
            case WHAT_SUBSCRIBE_INTERCEPTOR:
                interceptors.put(op.topic, op.interceptor);
                break;
            case WHAT_UNSUBSCRIBE_INTERCEPTOR:
                interceptors.remove(op.topic);
                break;
            case WHAT_SUBSCRIBE:
                for (String topic : op.topics) {
                    if (subscribers.containsKey(topic)) {
                        subscribers.get(topic).add(op.listener);
                    } else {
                        subscribers.put(topic, new HashSet<ISubscriber>() {
                            {
                                add(op.listener);
                            }
                        });
                    }
                }
                break;
            case WHAT_UNSUBSCRIBE:
                for (String topic : op.topics) {
                    Set<ISubscriber> iSubscribers = subscribers.get(topic);
                    if (iSubscribers != null && iSubscribers.size() > 0) {
                        iSubscribers.remove(op.listener);
                        if (iSubscribers.size() < 1) {
                            subscribers.remove(topic);
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

        if (interceptors.get(topic) != null) {
            extras = interceptors.get(topic).beforeReceive(topic, extras);
        }

        Set<ISubscriber> listeners = subscribers.get(topic);
        if (listeners != null && listeners.size() > 0) {
            Iterator<ISubscriber> iterator = listeners.iterator();
            ArrayList<Task> tasks = new ArrayList<>();
            while (iterator.hasNext()) {
                tasks.add(new Task(iterator.next(), topic, extras));
            }
            try {
                List<Future<Boolean>> futures = ThreadUtil.publishInvokeAll(tasks);
                for (Future f : futures) {
                    f.get();
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "InterruptedException = " + e.getMessage());
            } catch (ExecutionException e) {
                Log.e(TAG, "error = " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public interface ISubscriber {
        void onReceive(String topic, Object... extras);
    }

    public interface IInterceptor {
        Object[] beforeReceive(String topic, Object... extras);
    }
}
