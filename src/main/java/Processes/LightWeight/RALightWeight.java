package Processes.LightWeight;

import DataParser.HeavyWeight;
import DataParser.LightWeight;
import Mutex.MutexType;

public class RALightWeight extends LightWeightPrc {

    public RALightWeight(int id, LightWeight nodeInfo, HeavyWeight parentInfo, MutexType mutexType) {
        super(id, nodeInfo, parentInfo, mutexType);
    }
}
