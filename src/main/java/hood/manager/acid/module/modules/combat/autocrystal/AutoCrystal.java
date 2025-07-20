package hood.manager.acid.module.modules.combat.autocrystal;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.PacketEvent;
import hood.manager.acid.mixin.mixins.network.IMixinCPacketUseEntity;
import hood.manager.acid.mixin.mixins.player.IMixinPlayerControllerMP;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.*;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemTool;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author hood manager
 * @since 2024/02/11
 */

public class AutoCrystal extends Module {
    static float range = 5.0f;
    static float wallRange = 6.0f;

    public static AutoCrystal INSTANCE;

    Setting allowConsumption = new Setting("Consumption", this, false);
    Setting allowMining = new Setting("Mining", this, false);
    Setting multiTask = new Setting("Multi-task", this, true);
    Setting distance = new Setting("Distance", this, true);
    Setting minDmg = new Setting("Min damage", this, 5.0f, 1.0f, 20.0f, false);
    Setting maxSelfDmg = new Setting("Max self", this, 9.0f, 5.0f, 20.0f, false);
    Setting safety = new Setting("Safety", this, true);
    Setting facePlace = new Setting("Face place", this, 5.0f, 5.0f, 20.0f, false);
    Setting antiDesync = new Setting("Anti de-sync", this, true);
    Setting multiPlace = new Setting("Multi-place", this, false);

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and destroys end crystals.", Category.COMBAT);

        Main.INSTANCE.settingManager.addSetting(allowConsumption);
        Main.INSTANCE.settingManager.addSetting(allowMining);
        Main.INSTANCE.settingManager.addSetting(multiTask);
        Main.INSTANCE.settingManager.addSetting(distance);
        Main.INSTANCE.settingManager.addSetting(minDmg);
        Main.INSTANCE.settingManager.addSetting(maxSelfDmg);
        Main.INSTANCE.settingManager.addSetting(safety);
        Main.INSTANCE.settingManager.addSetting(facePlace);
        Main.INSTANCE.settingManager.addSetting(antiDesync);
        Main.INSTANCE.settingManager.addSetting(multiPlace);

        INSTANCE = this;
    }

    private static BlockPos renderBlock = null;
    private static Set<BlockPos> placed = new HashSet<>();

    public static boolean canPlaceCrystal(final BlockPos pos, final boolean check) {
        final BlockPos boost = pos.add(0, 1, 0);
        final BlockPos boostTwo = pos.add(0, 2, 0);
        try {
            if (mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN) {
                return false;
            }
            if (mc.world.getBlockState(boost).getBlock() != Blocks.AIR || mc.world.getBlockState(boostTwo).getBlock() != Blocks.AIR) {
                return false;
            }
            if (check) {
                return mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost)).isEmpty() && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostTwo)).isEmpty();
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boost))) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return false;
            }
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(boostTwo))) {
                if (entity instanceof EntityEnderCrystal) {
                    continue;
                }
                return false;
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
            return false;
        }
        return true;
    }

    private List<BlockPos> findCrystalBlocks() {
        BlockPos playerPos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));

        NonNullList<BlockPos> positions = NonNullList.create();
        positions.addAll(WorldUtil.getSphere(playerPos, range, (int)range, false, true, 0).stream()
                .filter(pos -> canPlaceCrystal(pos, multiPlace.getValBoolean()))
                .collect(Collectors.toList()));
        return positions;
    }

    static boolean angleBreak;
    static float yaw;
    static float pitch;

    public static double[] calculateLookAt(double px, double py, double pz, EntityPlayer me) {
        double dirx = me.posX - px;
        double diry = me.posY - py;
        double dirz = me.posZ - pz;

        double len = Math.sqrt(dirx * dirx + diry * diry + dirz * dirz);

        dirx /= len;
        diry /= len;
        dirz /= len;

        double pitch = Math.asin(diry);
        double yaw = Math.atan2(dirz, dirx);

        pitch = pitch * 180.0d / Math.PI;
        yaw = yaw * 180.0d / Math.PI;

        yaw += 90f;

        return new double[]{yaw, pitch};
    }

    private static void setYawAndPitch(float yaw1, float pitch1) {
        yaw = yaw1;
        pitch = pitch1;
        angleBreak = true;
    }

    private void lookAtPacket(double px, double py, double pz, EntityPlayer me) {
        double[] v = calculateLookAt(px, py, pz, me);
        setYawAndPitch((float) v[0], (float) v[1]);
    }

    private static void resetRotation() {
        if (angleBreak) {
            yaw = mc.player.rotationYaw;
            pitch = mc.player.rotationPitch;
            angleBreak = false;
        }
    }

    public static boolean isMining() {
        return mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemTool
                && mc.playerController.getIsHittingBlock()
                && BlockUtil.getBlock(mc.objectMouseOver.getBlockPos()) == Blocks.BEDROCK
                && !mc.world.isAirBlock(mc.objectMouseOver.getBlockPos());
    }

    public static boolean isConsuming() {
        if (mc.player.isHandActive()) {
            return mc.player.getActiveItemStack().getItemUseAction().equals(EnumAction.EAT)
                    || mc.player.getActiveItemStack().getItemUseAction().equals(EnumAction.DRINK);
        }

        return false;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) return;

        if (multiTask.getValBoolean()) {
            if (!allowConsumption.getValBoolean() && isConsuming()) return;
            if (!allowMining.getValBoolean() && isMining()) return;
        }

        EntityEnderCrystal crystal = mc.world.loadedEntityList.parallelStream()
                .filter(e -> e instanceof EntityEnderCrystal)
                .filter(e -> mc.player.getDistance(e) <= range)
                .filter(Decisions::shouldBreak)
                .map(e -> (EntityEnderCrystal)e)
                .min(Comparator.comparing(e -> mc.player.getDistance(e)))
                .orElse(null);

        if (crystal != null) {
            if (!mc.player.canEntityBeSeen(crystal)
                     && mc.player.getDistance(crystal) > wallRange) return;

            lookAtPacket(crystal.posX, crystal.posY, crystal.posZ, mc.player);

            //mc.player.swingArm(EnumHand.MAIN_HAND);
            //mc.playerController.attackEntity(mc.player, crystal);
        } else resetRotation();

        List<BlockPos> blocks = findCrystalBlocks();

        BlockPos blockPos1 = null;
        double damage = 0.5D;

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal) continue;
            if (!(entity instanceof EntityAnimal || entity instanceof EntityMob || entity instanceof EntityOtherPlayerMP)) continue;
            if (entity.equals(mc.player) || ((EntityLivingBase)entity).getHealth() <= 0) continue;

            for (BlockPos pos : blocks) {
                double b = entity.getDistanceSq(pos);

                if(b >= 169) continue;

                double d = Calculations.calculateDamage(
                        pos.getX() + 0.5D,
                        pos.getY() + 1,
                        pos.getZ() + 0.5D,
                        entity
                );

                if(d <= minDmg.getValDouble() && ((EntityLivingBase)entity).getHealth() + ((EntityLivingBase)entity).getAbsorptionAmount() > facePlace.getValDouble()) continue;

                if (d > damage) {
                    double self = Calculations.calculateDamage(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, mc.player);

                    if ((self > d && !(d < ((EntityLivingBase)entity).getHealth())) || self - 0.5D > mc.player.getHealth() && safety.getValBoolean()) continue;
                    if (safety.getValBoolean() && self > maxSelfDmg.getValDouble()) continue;

                    damage = d;
                    blockPos1 = pos;
                    //renderEnt = entity;
                }
            }
        }

        if (damage == 0.5D) {
            resetRotation();
            return;
        }
        renderBlock = blockPos1;

        lookAtPacket(blockPos1.getX() + 0.5D, blockPos1.getY() - 0.5D, blockPos1.getZ() + 0.5D, mc.player);
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(blockPos1.getX() + 0.5D, blockPos1.getY() - 0.5D, blockPos1.getZ() + 0.5D));

        EnumFacing enumFacing;

        if (result == null || result.sideHit == null) {
            enumFacing = null;
            resetRotation();
            return;
        } else {
            enumFacing = result.sideHit;
        }

        EnumHand resolvedHand;

        if (mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) resolvedHand = EnumHand.MAIN_HAND;
        else if (mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) resolvedHand = EnumHand.OFF_HAND;
        else return;

        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(blockPos1, enumFacing, resolvedHand, 0, 0, 0));
        placed.add(blockPos1);
    }

    static class Calculations {
        public static float calculateDamage(double posX, double posY, double posZ, Entity entity) {
            float doubleExplosionSize = 12.0F;
            double distancedSize = entity.getDistance(posX, posY, posZ) / (double) doubleExplosionSize;
            Vec3d vec3d = new Vec3d(posX, posY, posZ);
            double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
            double v = (1.0D - distancedSize) * blockDensity;
            float damage = (float) ((int) ((v * v + v) / 2.0D * 7.0D * (double) doubleExplosionSize + 1.0D));
            double finald = 1.0D;

            if (entity instanceof EntityLivingBase) {
                finald = getBlastReduction((EntityLivingBase) entity, getDamageScaled(damage), new Explosion(mc.world, null, posX, posY, posZ, 6F, false, true));
            }
            return (float) finald;
        }

        public static float getBlastReduction(EntityLivingBase entity, float damage, Explosion explosion) {
            if (entity instanceof EntityPlayer) {
                EntityPlayer ep = (EntityPlayer) entity;
                DamageSource ds = DamageSource.causeExplosionDamage(explosion);
                damage = CombatRules.getDamageAfterAbsorb(damage, (float) ep.getTotalArmorValue(), (float) ep.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

                int k = EnchantmentHelper.getEnchantmentModifierDamage(ep.getArmorInventoryList(), ds);
                float f = MathHelper.clamp(k, 0.0F, 20.0F);
                damage *= 1.0F - f / 25.0F;

                if (entity.isPotionActive(Potion.getPotionById(11))) {
                    damage = damage - (damage / 4);
                }
                damage = Math.max(damage, 0.0F);
                return damage;
            }
            damage = CombatRules.getDamageAfterAbsorb(damage, (float) entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());
            return damage;
        }

        private static float getDamageScaled(float damage) {
            int diff = mc.world.getDifficulty().getDifficultyId();
            return damage * (diff == 0 ? 0 : (diff == 2 ? 1 : (diff == 1 ? 0.5f : 1.5f)));
        }
    }

    static class Decisions {
        public static boolean shouldBreak(Entity entity) {
            if (!(entity instanceof EntityEnderCrystal)) return false;
            return true;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            BlockPos pos = new BlockPos(packet.getX(), packet.getY() - 1.0, packet.getZ());

            if (packet.getType() == 51 && placed.contains(pos)) {
                CPacketUseEntity usePacket = new CPacketUseEntity();
                ((IMixinCPacketUseEntity) usePacket).setEntityId(packet.getEntityID());
                ((IMixinCPacketUseEntity) usePacket).setAction(CPacketUseEntity.Action.ATTACK);

                mc.player.connection.sendPacket(usePacket);
                mc.player.swingArm(EnumHand.OFF_HAND);
            }
        }
        if (event.getPacket() instanceof SPacketSoundEffect && antiDesync.getValBoolean()) {
            SPacketSoundEffect packet2 = (SPacketSoundEffect) event.getPacket();
            if (packet2.getCategory() == SoundCategory.BLOCKS && packet2.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet2.getX(), packet2.getY(), packet2.getZ()) <= MathUtil.square(6.0))
                        entity.setDead();
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        if (renderBlock != null) {
            float red = 76.0f / 255.0f;
            float green = 92.0f / 255.0f;
            float blue = 173.0f / 255.0f;
            float alpha = 1.0f;

            GlStateManager.pushMatrix();
            GlStateManager.disableDepth();
            GlStateManager.enableAlpha();
            GlStateManager.disableTexture2D();

            GL11.glLineWidth(0.1f);

            AxisAlignedBB bb = mc.world.getBlockState(renderBlock)
                    .getSelectedBoundingBox(mc.world, renderBlock)
                    .offset(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);
            RenderGlobal.drawBoundingBox(bb.minX, bb.minY + 0.9, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);

            GlStateManager.disableAlpha();
            GlStateManager.enableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.popMatrix();

            if (distance.getValBoolean()) {
                double dst = mc.player.getDistance(renderBlock.getX(), renderBlock.getY(), renderBlock.getZ());
                String formattedDst = String.format("%.1f", dst);
                RenderUtil.drawNametag(renderBlock, 0.5f, formattedDst);
            }
        }
    }
}
