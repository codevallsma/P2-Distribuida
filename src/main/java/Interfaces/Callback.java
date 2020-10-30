package Interfaces;

import ClassesToSend.Message;

public interface Callback {
    void handleMsg(Message msg);
}
