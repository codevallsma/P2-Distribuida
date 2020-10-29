package Interfaces;

import ClassesToSend.Message;

public interface Callback<T> {
    void handleMsg(Message msg);
    void onNewNode(Message msg);
}
