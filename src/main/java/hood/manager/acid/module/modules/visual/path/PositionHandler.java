package hood.manager.acid.module.modules.visual.path;

import net.minecraft.util.math.Vec3d;

public class PositionHandler {
    private final Vec3d vec;
    private final long time;

    public PositionHandler(Vec3d vec, long time) {
        this.vec = vec;
        this.time = time;
    }

    public Vec3d getVec() {
        return vec;
    }

    public long getTime() {
        return time;
    }
}
