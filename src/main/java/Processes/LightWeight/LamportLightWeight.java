package Processes.LightWeight;

import Mutex.LamportMutex;
import Mutex.MutexType;

public class LamportLightWeight extends LightWeight {


    public LamportLightWeight(MutexType mutexType) {
        super(mutexType);
    }

    public void start() {
        this.networkManager.start();
    }

    public boolean isReady() {
        return ((LamportMutex)mutex).isReady();
    }

}
