package fun.learnlife.mqlibrary;

public class Op {
    public String topic;
    public String[] topics;
    public HCallBack.ISubscriber listener;
    public HCallBack.IInterceptor interceptor;
    public Object[] extra;

    public Op(String topic, Object[] extras) {
        this.topic = topic;
        this.extra = extras;
    }
    public Op(String topic) {
        this.topic = topic;
    }

    public Op(String topic, HCallBack.ISubscriber listener) {
        this.topic = topic;
        this.listener = listener;
    }

    public Op(String topic, HCallBack.IInterceptor interceptor) {
        this.topic = topic;
        this.interceptor = interceptor;
    }

    public Op(String[] topics, HCallBack.ISubscriber listener) {
        this.topics = topics;
        this.listener = listener;
    }
}
