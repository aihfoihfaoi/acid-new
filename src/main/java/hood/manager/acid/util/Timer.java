package hood.manager.acid.util;

public class Timer {
    private long time = -1L;

    long startTime = System.currentTimeMillis();
    long delay = 0L;

    boolean paused = false;

    public boolean hasPassed(long ms) {
        return this.getMs(System.nanoTime() - this.time) >= ms;
    }

    public long getPassed() {
        return this.getMs(System.nanoTime() - this.time);
    }

    public void reset() {
        this.time = System.nanoTime();
    }

    public long getMs(long time) {
        return time / 1000000L;
    }

    public long getTime() {
        return this.time;
    }
}
