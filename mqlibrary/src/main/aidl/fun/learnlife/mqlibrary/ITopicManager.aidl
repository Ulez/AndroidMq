// ITopicManager.aidl
package fun.learnlife.mqlibrary;

// Declare any non-default types here with import statements

import fun.learnlife.mqlibrary.IOnNewTopicArrivedListener;
import fun.learnlife.mqlibrary.IAddTopic;

interface ITopicManager {
    void publishToClient(in String topic, in String[] extras);
    void publishToVoice(in String topic, in String[] extras);

    void registerClientListener(in String[] topics, IOnNewTopicArrivedListener listener); // 注册voice发布到client接口
    void addClientNeedTopicListener(in IAddTopic l); // 注册client需要的topic
}
