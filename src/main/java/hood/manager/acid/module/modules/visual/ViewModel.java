package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.RenderHeldItemEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ViewModel extends Module {
    Setting mainX = new Setting("Main X", this, 0.0, -2.0, 2.0, false);
    Setting mainY = new Setting("Main Y", this, 0.0, -2.0, 2.0, false);
    Setting mainZ = new Setting("Main Z", this, 0.0, -2.0, 2.0, false);
    Setting offX = new Setting("Off X", this, 0.0, -2.0, 2.0, false);
    Setting offY = new Setting("Off Y", this, 0.0, -2.0, 2.0, false);
    Setting offZ = new Setting("Off Z", this, 0.0, -2.0, 2.0, false);

    public ViewModel() {
        super("ViewModel", "Changes your view model.", Category.VISUAL);

        Main.INSTANCE.settingManager.addSetting(mainX);
        Main.INSTANCE.settingManager.addSetting(mainY);
        Main.INSTANCE.settingManager.addSetting(mainZ);
        Main.INSTANCE.settingManager.addSetting(offX);
        Main.INSTANCE.settingManager.addSetting(offY);
        Main.INSTANCE.settingManager.addSetting(offZ);
    }

    @SubscribeEvent
    public void onRenderItem(RenderHeldItemEvent event) {
        switch (event.getSide()) {
            case LEFT: {
                GlStateManager.translate(offX.getValDouble(), offY.getValDouble(), offZ.getValDouble());
                break;
            }
            case RIGHT: {
                GlStateManager.translate(mainX.getValDouble(), mainY.getValDouble(), mainZ.getValDouble());
                break;
            }
        }
    }
}
