package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.tileentity.*;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class TileESP extends Module {
    Setting chests;
    Setting enderChests;
    Setting shulkerBoxes;
    Setting hoppers;
    Setting furnaces;
    Setting red;
    Setting green;
    Setting blue;
    Setting lineWidth;
    Setting height;

    public TileESP() {
        super("TileESP", "Highlights tile entities.", Category.VISUAL);

        this.chests = new Setting("Chests", (Module)this, true);
        this.enderChests = new Setting("Ender chests", (Module)this, true);
        this.shulkerBoxes = new Setting("Shulker boxes", (Module)this, true);
        this.hoppers = new Setting("Hoppers", (Module)this, false);
        this.furnaces = new Setting("Furnaces", (Module)this, false);
        this.red = new Setting("R", (Module)this, 255.0, 0.0, 255.0, false);
        this.green = new Setting("G", (Module)this, 255.0, 0.0, 255.0, false);
        this.blue = new Setting("B", (Module)this, 255.0, 0.0, 255.0, false);
        this.lineWidth = new Setting("Line width", (Module)this, 1.0, 1.0, 8.0, false);
        this.height = new Setting("Height", (Module)this, 1.0, 0.01, 1.0, false);

        Main.INSTANCE.settingManager.addSetting(chests);
        Main.INSTANCE.settingManager.addSetting(enderChests);
        Main.INSTANCE.settingManager.addSetting(shulkerBoxes);
        Main.INSTANCE.settingManager.addSetting(hoppers);
        Main.INSTANCE.settingManager.addSetting(furnaces);
        Main.INSTANCE.settingManager.addSetting(red);
        Main.INSTANCE.settingManager.addSetting(green);
        Main.INSTANCE.settingManager.addSetting(blue);
        Main.INSTANCE.settingManager.addSetting(lineWidth);
        Main.INSTANCE.settingManager.addSetting(height);
    }

    @SubscribeEvent
    public void onRender3D(final RenderWorldLastEvent event) {
        if (TileESP.mc.player != null || TileESP.mc.world != null) {
            for (final TileEntity tileEntity : TileESP.mc.world.loadedTileEntityList) {
                if (!this.chests.getValBoolean() && tileEntity instanceof TileEntityChest) {
                    continue;
                }
                if (!this.enderChests.getValBoolean() && tileEntity instanceof TileEntityEnderChest) {
                    continue;
                }
                if (!this.shulkerBoxes.getValBoolean() && tileEntity instanceof TileEntityShulkerBox) {
                    continue;
                }
                if (!this.hoppers.getValBoolean() && tileEntity instanceof TileEntityHopper) {
                    continue;
                }
                if (!this.furnaces.getValBoolean() && tileEntity instanceof TileEntityFurnace) {
                    continue;
                }
                final BlockPos tileEntityPos = tileEntity.getPos();
                if (tileEntity instanceof TileEntityChest) {
                    RenderUtil.drawBoxBlockPos(tileEntityPos, new Color(255, 170, 0), new Color(255, 170, 0), (float)this.lineWidth.getValDouble(), true, true, 100, 255, (float)this.height.getValDouble());
                    RenderUtil.drawNametag(tileEntityPos, 0.5f, "Chest");
                }
                if (tileEntity instanceof TileEntityShulkerBox) {
                    RenderUtil.drawBoxBlockPos(tileEntityPos, new Color(255, 0, 100), new Color(255, 0, 100), (float)this.lineWidth.getValDouble(), true, true, 100, 255, (float)this.height.getValDouble());
                    RenderUtil.drawNametag(tileEntityPos, 0.5f, "Shulker box");
                }
                if (tileEntity instanceof TileEntityEnderChest) {
                    RenderUtil.drawBoxBlockPos(tileEntityPos, new Color(100, 100, 255), new Color(100, 100, 255), (float)this.lineWidth.getValDouble(), true, true, 100, 255, (float)this.height.getValDouble());
                    RenderUtil.drawNametag(tileEntityPos, 0.5f, "Ender chest");
                }
                if (tileEntity instanceof TileEntityFurnace) {
                    RenderUtil.drawBoxBlockPos(tileEntityPos, new Color(150, 150, 150), new Color(150, 150, 150), (float)this.lineWidth.getValDouble(), true, true, 100, 255, (float)this.height.getValDouble());
                    RenderUtil.drawNametag(tileEntityPos, 0.5f, "Furnace");
                }
                if (!(tileEntity instanceof TileEntityHopper)) {
                    continue;
                }
                RenderUtil.drawBoxBlockPos(tileEntityPos, new Color(200, 0, 0), new Color(200, 0, 0), (float)this.lineWidth.getValDouble(), true, true, 100, 255, (float)this.height.getValDouble());
            }
        }
    }
}
