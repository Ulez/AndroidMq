// IOnNewTopicArrivedListener.aidl
package fun.learnlife.mqlibrary;

// Declare any non-default types here with import statements

interface IOnNewTopicArrivedListener {
    void onClientTopicArrived(in String topic, in String[] extras);
}
