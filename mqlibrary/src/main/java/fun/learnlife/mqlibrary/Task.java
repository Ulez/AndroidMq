package fun.learnlife.mqlibrary;

import java.util.concurrent.Callable;

public class Task implements Callable<Boolean> {
    HCallBack.ISubscriber subscriber;
    private String topic;
    private Object[] objects;

    public Task(HCallBack.ISubscriber subscriber, String topic, Object[] objects) {
        this.subscriber = subscriber;
        this.topic = topic;
        this.objects = objects;
    }

    @Override
    public Boolean call() throws Exception {
        subscriber.onReceive(topic, objects);
        return null;
    }
}
