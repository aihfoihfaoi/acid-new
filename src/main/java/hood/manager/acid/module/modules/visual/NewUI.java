package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;

public class NewUI extends Module {
    Setting blur = new Setting("Blur", this, true);

    public NewUI() {
        super("NewUI", "Displays the UI (new).", Category.VISUAL);
        setKey(Keyboard.KEY_RSHIFT);

        Main.INSTANCE.settingManager.addSetting(blur);
    }

    @Override
    protected void onEnable() {
        super.onEnable();
        mc.displayGuiScreen(Main.INSTANCE.newUI);

        if (blur.getValBoolean())
            mc.entityRenderer.loadShader(new ResourceLocation("minecraft", "shaders/post/blur" + ".json"));
    }

    @Override
    public void onUpdate() {
        if (Keyboard.isKeyDown(Keyboard.KEY_ESCAPE)) disable();
    }

    @Override
    protected void onDisable() {
        if(mc.entityRenderer.getShaderGroup() != null) mc.entityRenderer.getShaderGroup().deleteShaderGroup();
    }
}
