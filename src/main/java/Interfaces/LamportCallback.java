package Interfaces;

import ClassesToSend.Message;

public interface LamportCallback<T> {
    void handleMsg(Message msg);
    void onNewNode(Message msg);
    void onDeleteNode(Message msg);
}
