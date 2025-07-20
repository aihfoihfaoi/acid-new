package hood.manager.acid.module.modules.movement;

import com.google.common.collect.Lists;
import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import org.lwjgl.input.Keyboard;

public class Sprint extends Module {
    Setting mode = new Setting("Mode", this, "Legit", "Legit", "Rage");

    public Sprint() {
        super("Sprint", "Automatically sprints.", Category.MOVEMENT);

        Main.INSTANCE.settingManager.addSetting(mode);
    }

    @Override
    public void onUpdate() {
        if (mode.getValString().equals("Legit")) {
            if (mc.player.movementInput.moveForward > 0.0
                    && !mc.player.isSprinting()
                    && !mc.player.isSneaking()
                    && !mc.player.collidedHorizontally)
                mc.player.setSprinting(true);
        }
        if (mode.getValString().equals("Rage")) mc.player.setSprinting(true);
    }
}
