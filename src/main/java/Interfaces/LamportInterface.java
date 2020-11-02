package Interfaces;

import Model.Message;

public interface LamportInterface {
    void requestCS();
    void releaseCS();
    boolean okCS();
    boolean isGreater(int index1, int index2, int value2);
    void accessCriticalZone();
    void handleMsg(Message msg);
}
