package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import hood.manager.acid.util.WorldUtil;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class HoleESP extends Module {
    Setting safeR = new Setting("Safe R", this, 0, 0, 255, true);
    Setting safeG = new Setting("Safe G", this, 0, 0, 255, true);
    Setting safeB = new Setting("Safe R", this, 255, 0, 255, true);
    Setting unsafeR = new Setting("Unsafe R", this, 100, 0, 255, true);
    Setting unsafeG = new Setting("Unsafe G", this, 0, 0, 255, true);
    Setting unsafeB = new Setting("Unsafe B", this, 255, 0, 255, true);
    Setting range = new Setting("Range", this, 12.0, 1.0, 25.0, false);
    Setting height = new Setting("Height", this, 0.01, 0.01, 1.0, false);
    Setting lineWidth = new Setting("Line width", this,  1.0, 1.0, 8.0, false);

    public HoleESP() {
        super("HoleESP", "Highlights holes in the world.", Category.VISUAL);

        Main.INSTANCE.settingManager.addSetting(safeR);
        Main.INSTANCE.settingManager.addSetting(safeG);
        Main.INSTANCE.settingManager.addSetting(safeB);
        Main.INSTANCE.settingManager.addSetting(unsafeR);
        Main.INSTANCE.settingManager.addSetting(unsafeG);
        Main.INSTANCE.settingManager.addSetting(unsafeB);
        Main.INSTANCE.settingManager.addSetting(range);
        Main.INSTANCE.settingManager.addSetting(height);
        Main.INSTANCE.settingManager.addSetting(lineWidth);
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        for (final BlockPos blockPos : WorldUtil.getSphere(mc.player.getPosition(), (float)this.range.getValDouble(), false)) {
            RenderUtil.prepare();
            if (isSafe(blockPos))
                RenderUtil.drawBoxBlockPos(blockPos, new Color((int) safeR.getValDouble(), (int) safeG.getValDouble(), (int) safeB.getValDouble()), new Color((int) safeR.getValDouble(), (int) safeG.getValDouble(), (int) this.safeB.getValDouble()), (float) lineWidth.getValDouble(), true, true, 50, 255, (float) height.getValDouble());
            else if (isUnsafe(blockPos))
                RenderUtil.drawBoxBlockPos(blockPos, new Color((int) unsafeR.getValDouble(), (int) unsafeG.getValDouble(), (int) unsafeB.getValDouble()), new Color((int) unsafeR.getValDouble(), (int) unsafeG.getValDouble(), (int) unsafeB.getValDouble()), (float) lineWidth.getValDouble(), true, true, 50, 255, (float)this.height.getValDouble());
            RenderUtil.release();
        }
    }

    public boolean isSafe(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK;
    }

    public boolean isUnsafe(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN) && (mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN);
    }
}
