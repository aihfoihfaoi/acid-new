package hood.manager.acid.util;

public class FlyTimer {
    private long time;

    public FlyTimer() {
        this.time = -1L;
    }

    public boolean passed(final double ms) {
        return System.currentTimeMillis() - this.time >= ms;
    }

    public void reset() {
        this.time = System.currentTimeMillis();
    }

    public void resetTimeSkipTo(final long p_MS) {
        this.time = System.currentTimeMillis() + p_MS;
    }

    public long getTime() {
        return this.time;
    }

    public void setTime(final long time) {
        this.time = time;
    }
}
