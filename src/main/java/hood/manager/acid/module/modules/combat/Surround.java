package hood.manager.acid.module.modules.combat;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.BlockUtil;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class Surround extends Module {
    Setting jumpToggle = new Setting("Jump toggle", this, true);
    Setting center = new Setting("Center", this, true);
    Setting trigger = new Setting("Trigger", this, true);
    Setting timeOutTicks = new Setting("Timeout", this, 10.0, 0.0, 40.0, false);

    private boolean isSneaking;
    private boolean firstRun;
    private int oldSlot;
    public boolean noObby;
    private int blocksPlaced;
    private int runTimeTicks;
    private int delayTimeTicks;
    private int offsetSteps;
    private Vec3d centeredBlock;

    public Surround() {
        super("Surround", "Surrounds you with obsidian.", Category.COMBAT);

        this.isSneaking = false;
        this.firstRun = false;
        this.oldSlot = -1;
        this.runTimeTicks = 0;
        this.delayTimeTicks = 0;
        this.offsetSteps = 0;
        this.centeredBlock = Vec3d.ZERO;

        Main.INSTANCE.settingManager.addSetting(jumpToggle);
        Main.INSTANCE.settingManager.addSetting(center);
        Main.INSTANCE.settingManager.addSetting(trigger);
        Main.INSTANCE.settingManager.addSetting(timeOutTicks);
    }

    public static Vec3d getInterpolatedPos(final Entity entity, final float ticks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(getInterpolatedAmount(entity, ticks));
    }

    public static Vec3d getInterpolatedAmount(final Entity entity, final double ticks) {
        return getInterpolatedAmount(entity, ticks, ticks, ticks);
    }

    public static Vec3d getInterpolatedAmount(final Entity entity, final double x, final double y, final double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    @Override
    public void onUpdate() {
        if (Surround.mc.player == null) {
            this.disable();
            return;
        }
        if (Surround.mc.player.posY <= 0.0) {
            return;
        }
        if (this.firstRun) {
            this.firstRun = false;
            if (this.findObsidianSlot() == -1) {
                this.noObby = true;
                this.disable();
            }
        }
        else {
            if (this.delayTimeTicks < 0) {
                ++this.delayTimeTicks;
                return;
            }
            this.delayTimeTicks = 0;
        }
        if (jumpToggle.getValBoolean() && !Surround.mc.player.onGround && !Surround.mc.player.isInWater()) {
            return;
        }
        if (this.center.getValBoolean() && this.centeredBlock != Vec3d.ZERO && Surround.mc.player.onGround) {
            final double xDeviation = Math.abs(this.centeredBlock.x - Surround.mc.player.posX);
            final double zDeviation = Math.abs(this.centeredBlock.z - Surround.mc.player.posZ);
            if (xDeviation <= 0.1 && zDeviation <= 0.1) {
                this.centeredBlock = Vec3d.ZERO;
            }
            else {
                double newX;
                if (Surround.mc.player.posX > Math.round(Surround.mc.player.posX)) {
                    newX = Math.round(Surround.mc.player.posX) + 0.5;
                }
                else if (Surround.mc.player.posX < Math.round(Surround.mc.player.posX)) {
                    newX = Math.round(Surround.mc.player.posX) - 0.5;
                }
                else {
                    newX = Surround.mc.player.posX;
                }
                double newZ;
                if (Surround.mc.player.posZ > Math.round(Surround.mc.player.posZ)) {
                    newZ = Math.round(Surround.mc.player.posZ) + 0.5;
                }
                else if (Surround.mc.player.posZ < Math.round(Surround.mc.player.posZ)) {
                    newZ = Math.round(Surround.mc.player.posZ) - 0.5;
                }
                else {
                    newZ = Surround.mc.player.posZ;
                }
                Surround.mc.player.connection.sendPacket(new CPacketPlayer.Position(newX, Surround.mc.player.posY, newZ, true));
                Surround.mc.player.setPosition(newX, Surround.mc.player.posY, newZ);
            }
        }
        if (this.trigger.getValBoolean() && this.runTimeTicks >= this.timeOutTicks.getValDouble()) {
            this.runTimeTicks = 0;
            this.disable();
            return;
        }
        this.blocksPlaced = 0;
        while (this.blocksPlaced <= 4) {
            final Vec3d[] offsetPattern = Offsets.SURROUND;
            final int maxSteps = Offsets.SURROUND.length;
            if (this.offsetSteps >= maxSteps) {
                this.offsetSteps = 0;
                break;
            }
            final BlockPos offsetPos = new BlockPos(offsetPattern[this.offsetSteps]);
            BlockPos targetPos = new BlockPos(Surround.mc.player.getPositionVector()).add(offsetPos.getX(), offsetPos.getY(), offsetPos.getZ());
            boolean tryPlacing = true;
            if (!Surround.mc.world.getBlockState(targetPos).getMaterial().isReplaceable()) {
                tryPlacing = false;
            }
            for (final Entity entity : Surround.mc.world.getEntitiesWithinAABBExcludingEntity((Entity)null, new AxisAlignedBB(targetPos))) {
                if (entity instanceof EntityPlayer) {
                    tryPlacing = false;
                    break;
                }
            }
            if (tryPlacing && this.placeBlock(targetPos)) {
                ++this.blocksPlaced;
            }
            ++this.offsetSteps;
            if (!this.isSneaking) {
                continue;
            }
            Surround.mc.player.connection.sendPacket(new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
            this.isSneaking = false;
        }
        ++this.runTimeTicks;
    }

    @Override
    public void onEnable() {
        if (Surround.mc.player == null) {
            return;
        }
        if (this.center.getValBoolean() && Surround.mc.player.onGround) {
            Surround.mc.player.motionX = 0.0;
            Surround.mc.player.motionZ = 0.0;
        }
        this.centeredBlock = this.getCenterOfBlock(Surround.mc.player.posX, Surround.mc.player.posY, Surround.mc.player.posY);
        this.oldSlot = Surround.mc.player.inventory.currentItem;
        if (this.findObsidianSlot() != -1) {
            Surround.mc.player.inventory.currentItem = this.findObsidianSlot();
        }
    }

    private int findObsidianSlot() {
        int slot = -1;
        for (int i = 0; i < 9; ++i) {
            final ItemStack stack = Surround.mc.player.inventory.getStackInSlot(i);
            if (stack != ItemStack.EMPTY) {
                if (stack.getItem() instanceof ItemBlock) {
                    final Block block = ((ItemBlock)stack.getItem()).getBlock();
                    if (block instanceof BlockObsidian) {
                        slot = i;
                        break;
                    }
                }
            }
        }
        return slot;
    }

    private Vec3d getCenterOfBlock(final double playerX, final double playerY, final double playerZ) {
        final double newX = Math.floor(playerX) + 0.5;
        final double newY = Math.floor(playerY);
        final double newZ = Math.floor(playerZ) + 0.5;
        return new Vec3d(newX, newY, newZ);
    }

    private boolean placeBlock(final BlockPos pos) {
        final Block block = Surround.mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid)) {
            return false;
        }
        final EnumFacing side = BlockUtil.getPlaceableSide(pos);
        if (side == null) {
            return false;
        }
        final BlockPos neighbour = pos.offset(side);
        final EnumFacing opposite = side.getOpposite();
        if (!BlockUtil.canBeClicked(neighbour)) {
            return false;
        }
        final Vec3d hitVec = new Vec3d(neighbour).add(new Vec3d(0.5, 0.5, 0.5)).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        final Block neighbourBlock = Surround.mc.world.getBlockState(neighbour).getBlock();
        final int obsidianSlot = this.findObsidianSlot();
        if (Surround.mc.player.inventory.currentItem != obsidianSlot && obsidianSlot != -1) {
            Surround.mc.player.inventory.currentItem = obsidianSlot;
        }
        if ((!this.isSneaking && BlockUtil.blackList.contains(neighbourBlock)) || BlockUtil.shulkerList.contains(neighbourBlock)) {
            Surround.mc.player.connection.sendPacket(new CPacketEntityAction((Entity)Surround.mc.player, CPacketEntityAction.Action.START_SNEAKING));
            this.isSneaking = true;
        }
        if (obsidianSlot == -1) {
            this.noObby = true;
            return false;
        }
        BlockUtil.faceVectorPacketInstant(hitVec);
        Surround.mc.playerController.processRightClickBlock(Surround.mc.player, Surround.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        Surround.mc.player.swingArm(EnumHand.MAIN_HAND);
        return true;
    }

    private static class Offsets
    {
        private static final Vec3d[] SURROUND;

        static {
            SURROUND = new Vec3d[] { new Vec3d(1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, 1.0), new Vec3d(-1.0, 0.0, 0.0), new Vec3d(0.0, 0.0, -1.0), new Vec3d(1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, 1.0), new Vec3d(-1.0, -1.0, 0.0), new Vec3d(0.0, -1.0, -1.0) };
        }
    }
}
