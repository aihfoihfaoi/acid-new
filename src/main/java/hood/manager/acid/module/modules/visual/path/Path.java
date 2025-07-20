package hood.manager.acid.module.modules.visual.path;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;

public class Path extends Module {
    Setting r = new Setting("Line R", this, 255, 0, 255, true);
    Setting g = new Setting("Line G", this, 255, 0, 255, true);
    Setting b = new Setting("Line B", this, 255, 0, 255, true);
    Setting fade = new Setting("Fade", this, 2.0, 1.0, 10.0, false);
    Setting lineWidth = new Setting("Line width", this, 1.0, 1.0, 8.0, false);
    Setting infinite = new Setting("Infinite", this, false);

    private final LinkedList<PositionHandler> positions = new LinkedList<>();

    public Path() {
        super("Path", "Shows the player's previous path.", Category.VISUAL);

        Main.INSTANCE.settingManager.addSetting(r);
        Main.INSTANCE.settingManager.addSetting(g);
        Main.INSTANCE.settingManager.addSetting(b);
        Main.INSTANCE.settingManager.addSetting(fade);
        Main.INSTANCE.settingManager.addSetting(lineWidth);
        Main.INSTANCE.settingManager.addSetting(infinite);
    }

    @Override
    protected void onDisable() {
        positions.clear();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null || mc.player.ticksExisted <= 20) {
            this.positions.clear();
            return;
        }

        this.positions.add(new PositionHandler(new Vec3d(mc.player.lastTickPosX, mc.player.lastTickPosY, mc.player.lastTickPosZ), System.currentTimeMillis()));
        this.positions.removeIf(position -> System.currentTimeMillis() - position.getTime() >= fade.getValDouble() * 1000.0 && !infinite.getValBoolean());
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        GL11.glPushMatrix();
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glEnable(3042);
        GL11.glDisable(2929);
        GL11.glLineWidth((float) lineWidth.getValDouble());
        mc.entityRenderer.disableLightmap();
        GL11.glBegin(3);
        this.positions.forEach(pos -> {
            GL11.glColor3f((float) r.getValDouble() / 255.0f, (float) g.getValDouble() / 255.0f, (float) b.getValDouble() / 255.0f);
            GL11.glVertex3d(pos.getVec().x - mc.getRenderManager().viewerPosX, pos.getVec().y - mc.getRenderManager().viewerPosY, pos.getVec().z - mc.getRenderManager().viewerPosZ);
            return;
        });
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        GL11.glEnd();
        GL11.glEnable(2929);
        GL11.glDisable(2848);
        GL11.glDisable(3042);
        GL11.glEnable(3553);
        GL11.glPopMatrix();
    }
}
