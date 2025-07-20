package hood.manager.acid.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {
    protected static Minecraft mc = Minecraft.getMinecraft();

    public static void prepare() {
        GlStateManager.pushMatrix();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.disableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.disableCull();
        GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);
        GL11.glEnable(2848);
        GL11.glEnable(34383);
        GL11.glHint(3154, 4354);
    }

    public static void release() {
        GL11.glDisable(2848);
        GL11.glDisable(34383);
        GlStateManager.depthMask(true);
        GlStateManager.enableCull();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    public static void drawBoxBlockPos(final BlockPos pos, final Color color, final Color line, final float lineWidth, final boolean outline, final boolean box, final int boxAlpha, final int outlineAlpha, final float height) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos.getX() - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() - RenderUtil.mc.getRenderManager().viewerPosZ, pos.getX() + 1 - RenderUtil.mc.getRenderManager().viewerPosX, pos.getY() + height - RenderUtil.mc.getRenderManager().viewerPosY, pos.getZ() + 1 - RenderUtil.mc.getRenderManager().viewerPosZ);
        final ICamera camera = new Frustum();
        camera.setPosition(Objects.requireNonNull(RenderUtil.mc.getRenderViewEntity()).posX, RenderUtil.mc.getRenderViewEntity().posY, RenderUtil.mc.getRenderViewEntity().posZ);
        if (camera.isBoundingBoxInFrustum(new AxisAlignedBB(pos))) {
            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableDepth();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableTexture2D();
            GlStateManager.depthMask(false);
            GL11.glEnable(2848);
            GL11.glHint(3154, 4354);
            GL11.glLineWidth(lineWidth);
            if (box) {
                RenderGlobal.renderFilledBox(bb, color.getRed() / 255.0f, color.getGreen() / 255.0f, color.getBlue() / 255.0f, boxAlpha / 255.0f);
            }
            if (outline) {
                RenderGlobal.drawBoundingBox(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, line.getRed() / 255.0f, line.getGreen() / 255.0f, line.getBlue() / 255.0f, outlineAlpha / 255.0f);
            }
            GL11.glDisable(2848);
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GlStateManager.popMatrix();
        }
    }

    public static void drawRoundedRect(double x, double y, double width, double height, double radius, Color color) {
        glPushAttrib(GL_POINTS);

        glScaled(0.5, 0.5, 0.5); {
            x *= 2;
            y *= 2;
            width *= 2;
            height *= 2;

            width += x;
            height += y;

            glEnable(GL_BLEND);
            glDisable(GL_TEXTURE_2D);
            glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
            glEnable(GL_LINE_SMOOTH);
            glBegin(GL_POLYGON);

            int i;
            for (i = 0; i <= 90; i++) {
                glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, y + radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
            }

            for (i = 90; i <= 180; i++) {
                glVertex2d(x + radius + Math.sin(i * Math.PI / 180.0D) * radius * -1.0D, height - radius + Math.cos(i * Math.PI / 180.0D) * radius * -1.0D);
            }

            for (i = 0; i <= 90; i++) {
                glVertex2d(width - radius + Math.sin(i * Math.PI / 180.0D) * radius, height - radius + Math.cos(i * Math.PI / 180.0D) * radius);
            }

            for (i = 90; i <= 180; i++) {
                glVertex2d(width - radius + Math.sin(i * Math.PI / 180.0D) * radius, y + radius + Math.cos(i * Math.PI / 180.0D) * radius);
            }

            glEnd();
            glEnable(GL_TEXTURE_2D);
            glDisable(GL_BLEND);
            glDisable(GL_LINE_SMOOTH);
            glDisable(GL_BLEND);
            glEnable(GL_TEXTURE_2D);
        }

        glScaled(2, 2, 2);
        glPopAttrib();
    }

    public static void drawNametag(BlockPos blockPos, float height, String text) {
        GlStateManager.pushMatrix();
        glBillboardDistanceScaled(blockPos.getX() + 0.5f, blockPos.getY() + height, blockPos.getZ() + 0.5f, mc.player, 1);
        GlStateManager.disableDepth();
        GlStateManager.translate(-(mc.fontRenderer.getStringWidth(text) / 2.0), 0.0, 0.0);
        drawRoundedRect(-8, -8, mc.fontRenderer.getStringWidth(text) + 15, mc.fontRenderer.FONT_HEIGHT + 15, 12, new Color(25, 25, 25, 125));
        mc.fontRenderer.drawStringWithShadow(text, 0, 0, -1);
        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    public static void drawLine3D(Vec3d from, Vec3d to, Color color, double lineWidth) {
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glDisable(GL_ALPHA_TEST);
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
        glLineWidth(0.1F);
        glColor4f(color.getRed() / 255F, color.getGreen() / 255F, color.getBlue() / 255F, color.getAlpha() / 255F);
        glLineWidth((float) lineWidth);
        glBegin(GL_CURRENT_BIT);
        glVertex3d(from.x, from.y, from.z);
        glVertex3d(to.x, to.y, to.z);
        glEnd();
        glDepthMask(true);
        glEnable(GL_DEPTH_TEST);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
        glEnable(GL_ALPHA_TEST);
        glDisable(GL_LINE_SMOOTH);
        glColor4f(1, 1, 1, 1);
    }


    public static void drawRoundedBorder(float x, float y, float width, float height, float radius, Color color) {
        RenderUtil.drawRoundedRect(x - 0.5F, y - 0.5F, 0.5F, height + 1, radius, color);
        RenderUtil.drawRoundedRect(x + width, y - 0.5F, 0.5F, height + 1, radius, color);
        RenderUtil.drawRoundedRect(x, y - 0.5F, width, 0.5F, radius, color);
        RenderUtil.drawRoundedRect(x, y + height, width, 0.5F, radius, color);
    }

    public static void glBillboardDistanceScaled(float x, float y, float z, EntityPlayer player, float scale) {
        glBillboard(x, y, z);
        int distance = (int) player.getDistance(x, y, z);
        float scaleDistance = distance / 2F / (2 + (2 - scale));

        if (scaleDistance < 1)
            scaleDistance = 1;

        GlStateManager.scale(scaleDistance, scaleDistance, scaleDistance);
    }

    public static void glBillboard(float x, float y, float z) {
        float scale = 0.02666667f;

        GlStateManager.translate(x - mc.getRenderManager().viewerPosX, y - mc.getRenderManager().viewerPosY, z - mc.getRenderManager().viewerPosZ);
        GlStateManager.glNormal3f(0, 1, 0);
        GlStateManager.rotate(-mc.player.rotationYaw, 0, 1, 0);
        GlStateManager.rotate(mc.player.rotationPitch, (mc.gameSettings.thirdPersonView == 2) ? -1 : 1, 0, 0);
        GlStateManager.scale(-scale, -scale, scale);
    }
}
