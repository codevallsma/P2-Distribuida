package Processes.LightWeight;

import DataParser.HeavyWeight;
import DataParser.LightWeight;
import Interfaces.NetworkCallback;
import Model.Message;
import Mutex.LamportMutex;
import Mutex.MutexType;

public class LamportLightWeight extends LightWeightPrc {

    public LamportLightWeight(int id, LightWeight nodeInfo, HeavyWeight parentInfo, MutexType mutexType) {
        super(id, nodeInfo, parentInfo, mutexType);
    }

    public void start() {
        this.networkManager.start();
    }

    public boolean isReady() {
        return ((LamportMutex)mutex).isReady();
    }

}
