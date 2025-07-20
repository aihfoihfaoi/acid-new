package hood.manager.acid.module.modules.misc;

import com.google.common.collect.Lists;
import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.module.ModuleManager;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glDisable;

public class Model extends Module {
    Setting scale = new Setting("Scale", this, 30, 1, 100, true);

    public Model() {
        super("Model", "Displays your player model on your HUD.", Category.MISC);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            int y = Main.INSTANCE.moduleManager.isModuleEnabled("Watermark") ? 85 : 70;

            Point pos = new Point(25, Main.INSTANCE.moduleManager.isModuleEnabled("Watermark") ? 85 : 70);

            int health = (int) (mc.player.getHealth() / mc.player.getMaxHealth() * 100);

            int red = (int) (255 * (100 - health) / 100.0);
            int green = (int) (255 * health / 100.0);

            Color healthColor = new Color(red, green, 0);

            int bgTop = Main.INSTANCE.moduleManager.isModuleEnabled("Watermark") ? 23 : 10;

            String state = "Idle";

            if (mc.player.isSneaking()) state = "Sneaking";
            if (mc.player.isSprinting()) state = "Sprinting";
            if (mc.player.isElytraFlying()) state = "Flying";
            if (mc.player.isRiding()) state = "Riding";
            if (mc.player.isInWater()) state = "Swimming";
            if (mc.player.isBurning()) state = "Burning";
            if (mc.player.isRiding()
                    && mc.player.getRidingEntity() instanceof EntityBoat
                    && mc.player.getRidingEntity().isInWater()) state = "Sailing";

            Gui.drawRect(0, bgTop, 205, 90, new Color(25, 25, 25).getRGB());
            Gui.drawRect(49, bgTop, 50, 90, new Color(0, 150, 150).getRGB());

            int offset = mc.fontRenderer.FONT_HEIGHT;

            mc.fontRenderer.drawStringWithShadow("Health: " + health + "%", 60, bgTop + 7, healthColor.getRGB());
            mc.fontRenderer.drawStringWithShadow("State: " + state, 60, bgTop + 7 + offset, -1);

            String formattedXP = String.format("%.1f", mc.player.experience);

            int armourCoverage = (int) ((mc.player.getTotalArmorValue() / 20.0f) * 100);

            mc.fontRenderer.drawStringWithShadow("XP: " + formattedXP, 60, bgTop + 7 + offset * 2, -1);
            mc.fontRenderer.drawStringWithShadow(mc.player.dimension == -1 ? "Nether" : "Overworld", 60, bgTop + 7 + offset * 3, -1);
            mc.fontRenderer.drawStringWithShadow("Resilience: " + armourCoverage + "%", 60, bgTop + 7 + offset * 4, -1);

            Gui.drawRect(150, bgTop, 151, 90, new Color(0, 150, 150).getRGB());

            int initial = 7;
            int off = initial;
            for (ItemStack stack : Lists.reverse(new ArrayList<>(mc.player.inventory.armorInventory))) {
                if (stack.isEmpty()) continue;

                int durability = stack.getMaxDamage() - stack.getItemDamage();
                int dp = (int) ((double) durability / stack.getMaxDamage() * 100);

                int durRed = (int) (255 * (100 - dp) / 100.0);
                int durGreen = (int) (255 * dp / 100.0);

                Color durColor = new Color(durRed, durGreen, 0);

                mc.getRenderItem().renderItemAndEffectIntoGUI(stack, 155, (bgTop + off) - 5);
                mc.fontRenderer.drawStringWithShadow(dp + "%", 175, (bgTop + off) - 1, durColor.getRGB());

                off += 15;
            }

            renderModel(mc.player, pos, 30);
        }
    }

    public static void renderModel(EntityLivingBase entity, Point pos, int scale) {
        GlStateManager.enableTexture2D();
        GlStateManager.depthMask(true);
        GL11.glPushAttrib(GL11.GL_SCISSOR_BIT);
        GL11.glDisable(GL11.GL_SCISSOR_TEST);
        GlStateManager.clear(GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glPopAttrib();
        GlStateManager.enableDepth();
        GlStateManager.disableAlpha();
        GlStateManager.pushMatrix();
        GlStateManager.color(1,1,1,1);

        float yaw = Math.max(-35, Math.min(35, entity.rotationYaw));
        float pitch = Math.max(-35, Math.min(35, -entity.rotationPitch));

        GuiInventory.drawEntityOnScreen(pos.x,pos.y,scale, yaw, pitch,entity);
        GlStateManager.popMatrix();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
    }
}
