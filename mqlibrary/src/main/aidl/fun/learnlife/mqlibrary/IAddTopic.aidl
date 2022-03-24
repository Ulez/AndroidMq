// IAddTopic.aidl
package fun.learnlife.mqlibrary;

// Declare any non-default types here with import statements

interface IAddTopic {
    void addClientNeedTopics(in String topic); // 注册client需要的topic
    void publishToVoice(in String topic, in String[] extras);
}