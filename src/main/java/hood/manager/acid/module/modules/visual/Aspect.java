package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.PerspectiveEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Aspect extends Module {
    Setting ratio = new Setting("Ratio", this, (double) mc.displayWidth / mc.displayHeight + 0.0, 0.0, 3.0, false);

    public Aspect() {
        super("Aspect", "Changes your aspect ratio.", Category.VISUAL);
        Main.INSTANCE.settingManager.addSetting(ratio);
    }

    @SubscribeEvent
    public void onPerspective(PerspectiveEvent event) {
        event.setAspect((float) ratio.getValDouble());
    }
}
