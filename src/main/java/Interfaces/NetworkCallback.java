package Interfaces;

import Model.Message;

public interface NetworkCallback {
    void onMessageReceived(Message msg);
}
