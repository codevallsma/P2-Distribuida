package Interfaces;

import Model.Message;

public interface Callback {
    void handleMsg(Message msg);
}
