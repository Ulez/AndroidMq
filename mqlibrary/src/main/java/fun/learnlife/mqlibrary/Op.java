package fun.learnlife.mqlibrary;

public class Op {
    public String topic;
    public String[] topics;
    public HCallBack.ISubscriber listener;
    public Object[] extra;

    public Op(String topic, Object[] extras) {
        this.topic = topic;
        this.extra = extras;
    }

    public Op(String topic, HCallBack.ISubscriber listener) {
        this.topic = topic;
        this.listener = listener;
    }

    public Op(String[] topics, HCallBack.ISubscriber listener) {
        this.topics = topics;
        this.listener = listener;
    }
}
