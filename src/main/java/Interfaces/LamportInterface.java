package Interfaces;

public interface LamportInterface<T> {
    boolean isGreater(int index1, int index2, int value2);
    boolean okCS();
    void startListeningThread(Runnable connectToServersRunnable);
    void releaseCS();
    void requestCS();
    void accessCriticalZone();
    void startServer();
}
