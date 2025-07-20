package hood.manager.acid.module.modules.movement;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;

public class FastFall extends Module {
    Setting height = new Setting("Height", this, 2.0, 0.0, 10.0, false);

    public FastFall() {
        super("FastFall", "Makes you fall faster.", Category.MOVEMENT);

        Main.INSTANCE.settingManager.addSetting(height);
    }

    @Override
    public void onUpdate() {
        if (mc.world == null || mc.player == null || mc.player.isInWater() || mc.player.isInLava()
        || mc.player.isOnLadder() || mc.gameSettings.keyBindJump.isKeyDown()) return;

        if (mc.player != null && mc.player.onGround && !mc.player.isInWater() && !mc.player.isOnLadder()) {
            for (double y = 0.0; y < this.height.getValDouble() + 0.5; y += 0.01) {
                if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -y, 0.0)).isEmpty()) {
                    mc.player.motionY = -10.0;
                    break;
                }
            }
        }
    }
}
