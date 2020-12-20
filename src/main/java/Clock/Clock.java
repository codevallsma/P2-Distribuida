package Clock;

public interface Clock {
    public int getValue(int index);
    public void tick();
    public void tick(int sender, int value);
    public int requestAction();
    public void receiveAction(int sender, int sentValue);
}
