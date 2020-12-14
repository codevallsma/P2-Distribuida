package Interfaces;

import Model.Message;

public interface LamportInterface {
    boolean okCS();
    boolean isGreater(int index1, int index2, int value1, int value2);
}
