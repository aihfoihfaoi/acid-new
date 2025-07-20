package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.RenderSkyEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class Skybox extends Module {
    Setting r = new Setting("Sky R", this, 255, 0, 255, true);
    Setting g = new Setting("Sky G", this, 0, 0, 255, true);
    Setting b = new Setting("Sky B", this, 0, 0, 255, true);

    public Skybox() {
        super("Skybox", "Changes the skybox.", Category.VISUAL);

        Main.INSTANCE.settingManager.addSetting(r);
        Main.INSTANCE.settingManager.addSetting(g);
        Main.INSTANCE.settingManager.addSetting(b);
    }

    @SubscribeEvent
    public void onRenderSky(RenderSkyEvent event) {
        event.setColor(new Color((int) r.getValDouble(), (int) g.getValDouble(), (int) b.getValDouble()));
        event.setCanceled(true);
    }
}
