package hood.manager.acid.module.modules.combat;

import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.util.*;
import hood.manager.acid.util.enums.Side;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;

/**
 * @author hood manager
 * @since 2024/02/11
 */

public class HoleFill extends Module {
    public Timer timer = new Timer();
    public BlockPos currentPos = null;

    public HoleFill() {
        super("HoleFill", "Fills nearby holes.", Category.COMBAT);
    }

    @Override
    public void onUpdate() {
        if (mc.player != null || mc.world != null) {
            int obbySlot = InventoryUtil.getHotbarIndex(Item.getItemFromBlock(Blocks.OBSIDIAN));

            if (obbySlot == -1) {
                ChatUtil.sendMessage(Side.CLIENT, "No obsidian found in hotbar!");
                toggle();
                return;
            }

            WorldUtil.getSphere(mc.player.getPosition(), 4.0f, false).stream()
                    .filter(this::isHole)
                    .forEach(pos -> {
                EnumFacing[] facings;

                if (mc.player.inventory.currentItem != obbySlot) InventoryUtil.indexSwap(obbySlot);

                Arrays.stream(EnumFacing.values()).forEach(side -> {
                    BlockPos adjacent = pos.offset(side);
                    EnumFacing opposite = side.getOpposite();

                    Vec3d hitVec = new Vec3d(adjacent).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));

                    if (timer.hasPassed(50L)) {
                        currentPos = pos;

                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        mc.playerController.processRightClickBlock(mc.player, mc.world, pos, opposite, hitVec, EnumHand.MAIN_HAND);
                        timer.reset();
                    }
                });
            });
        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (currentPos != null) {
            double x = currentPos.getX();
            double y = currentPos.getY();
            double z = currentPos.getZ();

            double distance = mc.player.getDistance(x, y, z);

            float red = 76.0f / 255.0f;
            float green = 92.0f / 255.0f;
            float blue = 173.0f / 255.0f;
            float alpha = 1.0f;

            GlStateManager.pushMatrix();
            GlStateManager.enableAlpha();
            GlStateManager.disableDepth();

            GL11.glLineWidth(0.1f);

            AxisAlignedBB bb = mc.world.getBlockState(currentPos)
                    .getSelectedBoundingBox(mc.world, currentPos)
                    .offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
            RenderGlobal.drawBoundingBox(bb.minX, bb.minY + 0.9, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);

            GlStateManager.enableDepth();
            GlStateManager.disableAlpha();
            GlStateManager.popMatrix();

            String formattedDistance = String.format("%.1f", distance);
            RenderUtil.drawNametag(currentPos, 2.0F, formattedDistance);
        }
    }

    public boolean isHole(BlockPos pos) {
        return !(HoleFill.mc.world.getBlockState(pos).getBlock() != Blocks.AIR
                || HoleFill.mc.world.getBlockState(pos.down()).getBlock() != Blocks.BEDROCK
                && HoleFill.mc.world.getBlockState(pos.down()).getBlock() != Blocks.OBSIDIAN
                || HoleFill.mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR
                || HoleFill.mc.world.getBlockState(pos.up().up()).getBlock() != Blocks.AIR
                || HoleFill.mc.world.getBlockState(pos.north()).getBlock() != Blocks.BEDROCK
                && HoleFill.mc.world.getBlockState(pos.north()).getBlock() != Blocks.OBSIDIAN
                || HoleFill.mc.world.getBlockState(pos.south()).getBlock() != Blocks.BEDROCK
                && HoleFill.mc.world.getBlockState(pos.south()).getBlock() != Blocks.OBSIDIAN
                || HoleFill.mc.world.getBlockState(pos.west()).getBlock() != Blocks.BEDROCK
                && HoleFill.mc.world.getBlockState(pos.west()).getBlock() != Blocks.OBSIDIAN
                || HoleFill.mc.world.getBlockState(pos.east()).getBlock() != Blocks.BEDROCK
                && HoleFill.mc.world.getBlockState(pos.east()).getBlock() != Blocks.OBSIDIAN);
    }
}
