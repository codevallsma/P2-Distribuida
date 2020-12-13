package Clock;

public class LamportClock implements Clock{
    int ticks;

    public LamportClock() {
        this.ticks = 0;
    }

    public int getValue(int index) {
        return ticks;
    }

    public void tick(){
        ticks++;
    }

    public int requestAction() {
        ticks++;
        return ticks;
    }

    public void receiveAction(int src, int receivedValue) {
        ticks = Integer.max(ticks, receivedValue) + 1;
    }
}
