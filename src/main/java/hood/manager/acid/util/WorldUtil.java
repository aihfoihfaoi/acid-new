package hood.manager.acid.util;

import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class WorldUtil {
    public static List<BlockPos> getSphere(BlockPos pos, float r, float h, boolean hollow, boolean sphere, int plusY) {
        ArrayList<BlockPos> blocks = new ArrayList<BlockPos>();
        int x = pos.getX() - (int)r;
        while ((float)x <= (float)pos.getX() + r) {
            int y = sphere ? pos.getY() - (int)r : pos.getY();
            while (true) {
                float f = y;
                float f2 = sphere ? (float)pos.getY() + r : (float)pos.getY() + h;
                if (!(f < f2)) break;
                int z = pos.getZ() - (int)r;
                while ((float)z <= (float)pos.getZ() + r) {
                    double dist = (pos.getX() - x) * (pos.getX() - x) + (pos.getZ() - z) * (pos.getZ() - z) + (sphere ? (pos.getY() - y) * (pos.getY() - y) : 0);
                    if (!(!(dist < (double)(r * r)) || hollow && dist < (double)((r - 1.0f) * (r - 1.0f)))) {
                        blocks.add(new BlockPos(x, y + plusY, z));
                    }
                    ++z;
                }
                ++y;
            }
            ++x;
        }
        return blocks;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, boolean hollow) {
        return WorldUtil.getSphere(pos, r, (int)r, hollow, true, 0);
    }
}
