package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class ClickGUI extends Module {
    public ClickGUI() {
        super("ClickGUI", "Shows the UI. (old)", Category.VISUAL);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Main.INSTANCE.ui);
        disable();
    }
}
