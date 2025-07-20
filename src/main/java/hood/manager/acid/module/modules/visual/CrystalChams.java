package hood.manager.acid.module.modules.visual;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.RenderCrystalModelEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class CrystalChams extends Module {
    public Setting chamsColorR;
    public Setting chamsColorG;
    public Setting chamsColorB;
    public Setting scale;
    public Setting mode;
    public static CrystalChams INSTANCE;

    public CrystalChams() {
        super("CrystalChams", "Renders crystals in a more appealing way.", Category.VISUAL);
        this.chamsColorR = new Setting("R", (Module)this, 255.0, 0.0, 255.0, false);
        this.chamsColorG = new Setting("G", (Module)this, 255.0, 0.0, 255.0, false);
        this.chamsColorB = new Setting("B", (Module)this, 255.0, 0.0, 255.0, false);
        this.scale = new Setting("Scale", (Module)this, 1.0, 0.1, 3.0, false);
        this.mode = new Setting("Mode", (Module)this, "Model", "Model", "Wireframe");
        CrystalChams.INSTANCE = this;

        Main.INSTANCE.settingManager.addSetting(chamsColorR);
        Main.INSTANCE.settingManager.addSetting(chamsColorG);
        Main.INSTANCE.settingManager.addSetting(chamsColorB);
        Main.INSTANCE.settingManager.addSetting(scale);
        Main.INSTANCE.settingManager.addSetting(mode);
    }

    @SubscribeEvent
    public void onRenderCrystal(final RenderCrystalModelEvent event) {
        if (this.mode.getValString().equals("Chams")) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            GL11.glDisable(3008);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glDisable(2848);
            GL11.glScaled(this.scale.getValDouble(), this.scale.getValDouble(), this.scale.getValDouble());
            GL11.glColor4f((int)this.chamsColorR.getValDouble() / 255.0f, (int)this.chamsColorG.getValDouble() / 255.0f, (int)this.chamsColorB.getValDouble() / 255.0f, 0.3f);
            event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glEnable(3042);
            GL11.glEnable(2896);
            GL11.glEnable(3553);
            GL11.glEnable(3008);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
        else if (this.mode.getValString().equals("Wireframe")) {
            GL11.glPushMatrix();
            GL11.glPushAttrib(1048575);
            GL11.glPolygonMode(1032, 6913);
            GL11.glDisable(3553);
            GL11.glDisable(2896);
            GL11.glDisable(2929);
            GL11.glEnable(2848);
            GL11.glEnable(3042);
            GL11.glBlendFunc(770, 771);
            GL11.glColor3f((int)this.chamsColorR.getValDouble() / 255.0f, (int)this.chamsColorG.getValDouble() / 255.0f, (int)this.chamsColorB.getValDouble() / 255.0f);
            GL11.glLineWidth(1.0f);
            event.modelBase.render(event.entity, event.limbSwing, event.limbSwingAmount, event.ageInTicks, event.netHeadYaw, event.headPitch, event.scale);
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
