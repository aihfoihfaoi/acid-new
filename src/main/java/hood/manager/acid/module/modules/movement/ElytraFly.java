package hood.manager.acid.module.modules.movement;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.PlayerTravelEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.FlyTimer;
import hood.manager.acid.util.MathUtil;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Collections;

public class ElytraFly extends Module {

    Setting mode = new Setting("Mode", this, "Control", "Control", "Advanced", "Packet");
    Setting speed = new Setting("Speed", this, 2.0, 0.0, 10.0, false);
    Setting glideSpeed = new Setting("Glide speed", this, 1.0, 0.0, 10.0, false);
    Setting dropSpeed = new Setting("Drop speed", this, 2.0, 0.0, 10.0, false);
    Setting acceleration = new Setting("Acceleration", this, false);
    Setting timer = new Setting("Timer", this, 1000.0, 0.0, 10000.0, false);
    Setting rotationPitch = new Setting("Face pitch", this, 0.0, -90.0, 90.0, false);
    Setting sourceCancel = new Setting("Source cancel", this, true);
    Setting instant = new Setting("Instant", this, false);
    Setting cancelHeight = new Setting("Cancel height", this, 5.0, 0.0, 10.0, false);
    Setting factor = new Setting("Face factor", this, 0.3, 0.1, 1.0, false);

    private FlyTimer accelerationTimer = new FlyTimer();
    private FlyTimer accelerationResetTimer = new FlyTimer();
    private FlyTimer instantFlightTimer = new FlyTimer();

    public ElytraFly() {
        super("ElytraFly", "Improves elytra-flying.", Category.MOVEMENT);

        Main.INSTANCE.settingManager.addSetting(mode);
        Main.INSTANCE.settingManager.addSetting(speed);
        Main.INSTANCE.settingManager.addSetting(glideSpeed);
        Main.INSTANCE.settingManager.addSetting(dropSpeed);
        Main.INSTANCE.settingManager.addSetting(acceleration);
        Main.INSTANCE.settingManager.addSetting(timer);
        Main.INSTANCE.settingManager.addSetting(rotationPitch);
        Main.INSTANCE.settingManager.addSetting(sourceCancel);
        Main.INSTANCE.settingManager.addSetting(instant);
        Main.INSTANCE.settingManager.addSetting(cancelHeight);
        Main.INSTANCE.settingManager.addSetting(factor);
    }

    @SubscribeEvent
    public void onPlayerTravel(final PlayerTravelEvent event) {
        if ((ElytraFly.mc.player != null || ElytraFly.mc.world != null) && ElytraFly.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.ELYTRA) {
            if (!ElytraFly.mc.player.isElytraFlying()) {
                if (!ElytraFly.mc.player.onGround && this.instant.getValBoolean()) {
                    if (!this.instantFlightTimer.passed(1000.0)) {
                        return;
                    }
                    this.instantFlightTimer.reset();
                    ElytraFly.mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)ElytraFly.mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
                }
                return;
            }
            final String valString = this.mode.getValString();
            switch (valString) {
                case "Control": {
                    this.onControlElytra(event);
                    break;
                }
                case "Packet": {
                    this.onPacketElytra(event);
                    break;
                }
                case "Advanced": {
                    this.onAdvancedElytra(event);
                    break;
                }
            }
        }
    }

    public void onControlElytra(final PlayerTravelEvent event) {
        final double[] dir = MathUtil.directionSpeed(this.speed.getValDouble());
        final double accelerationFactor = this.factor.getValDouble();
        if (ElytraFly.mc.player.movementInput.moveStrafe != 0.0f || ElytraFly.mc.player.movementInput.moveForward != 0.0f) {
            if ((this.sourceCancel.getValBoolean() && ElytraFly.mc.player.isInWater()) || ElytraFly.mc.player.isInLava()) {
                return;
            }
            final double targetMotionX = dir[0];
            final double targetMotionZ = dir[1];
            final EntityPlayerSP player = ElytraFly.mc.player;
            player.motionX += (targetMotionX - ElytraFly.mc.player.motionX) * accelerationFactor;
            final EntityPlayerSP player2 = ElytraFly.mc.player;
            player2.motionZ += (targetMotionZ - ElytraFly.mc.player.motionZ) * accelerationFactor;
            final EntityPlayerSP player3 = ElytraFly.mc.player;
            player3.motionX -= ElytraFly.mc.player.motionX * (Math.abs(ElytraFly.mc.player.rotationPitch) + 90.0f) / 90.0 - ElytraFly.mc.player.motionX;
            final EntityPlayerSP player4 = ElytraFly.mc.player;
            player4.motionZ -= ElytraFly.mc.player.motionZ * (Math.abs(ElytraFly.mc.player.rotationPitch) + 90.0f) / 90.0 - ElytraFly.mc.player.motionZ;
        }
        else {
            final double decelerationFactor = this.factor.getValDouble();
            final EntityPlayerSP player5 = ElytraFly.mc.player;
            player5.motionX *= decelerationFactor;
            final EntityPlayerSP player6 = ElytraFly.mc.player;
            player6.motionZ *= decelerationFactor;
        }
        ElytraFly.mc.player.motionY = -MathUtil.degToRad((double)ElytraFly.mc.player.rotationPitch) * ElytraFly.mc.player.movementInput.moveForward;
        ElytraFly.mc.player.prevLimbSwingAmount = 0.0f;
        ElytraFly.mc.player.limbSwingAmount = 0.0f;
        ElytraFly.mc.player.limbSwing = 0.0f;
        event.setCanceled(true);
    }

    public void onAdvancedElytra(final PlayerTravelEvent event) {
        if (!ElytraFly.mc.player.movementInput.jump) {
            ElytraFly.mc.player.setVelocity(0.0, 0.0, 0.0);
            event.setCanceled(true);
            final double[] dir = MathUtil.directionSpeed(this.speed.getValDouble());
            if (ElytraFly.mc.player.movementInput.moveStrafe != 0.0f || ElytraFly.mc.player.movementInput.moveForward != 0.0f) {
                ElytraFly.mc.player.motionX = dir[0];
                ElytraFly.mc.player.motionY = -(this.glideSpeed.getValDouble() / 10000.0);
                ElytraFly.mc.player.motionZ = dir[1];
            }
            if (ElytraFly.mc.player.movementInput.sneak) {
                ElytraFly.mc.player.motionY = -this.dropSpeed.getValDouble();
            }
            ElytraFly.mc.player.prevLimbSwingAmount = 0.0f;
            ElytraFly.mc.player.limbSwingAmount = 0.0f;
            ElytraFly.mc.player.limbSwing = 0.0f;
            return;
        }
        if ((this.sourceCancel.getValBoolean() && ElytraFly.mc.player.isInWater()) || ElytraFly.mc.player.isInLava()) {
            return;
        }
        final double motionSq = Math.sqrt(ElytraFly.mc.player.motionX * ElytraFly.mc.player.motionX + ElytraFly.mc.player.motionZ * ElytraFly.mc.player.motionZ);
        if (motionSq > 1.0) {
            return;
        }
        final double[] dir2 = MathUtil.directionSpeedAlt(this.speed.getValDouble());
        ElytraFly.mc.player.motionX = dir2[0];
        ElytraFly.mc.player.motionY = -(this.glideSpeed.getValDouble() / 10000.0);
        ElytraFly.mc.player.motionZ = dir2[1];
        event.setCanceled(true);
    }

    public void onPacketElytra(final PlayerTravelEvent event) {
        final double yHeight = ElytraFly.mc.player.posY;
        if (yHeight <= this.cancelHeight.getValDouble()) {
            return;
        }
        final boolean isMoveKeyDown = ElytraFly.mc.player.movementInput.moveForward > 0.0f || ElytraFly.mc.player.movementInput.moveStrafe > 0.0f;
        if ((this.sourceCancel.getValBoolean() && ElytraFly.mc.player.isInWater()) || ElytraFly.mc.player.isInLava()) {
            return;
        }
        if (ElytraFly.mc.player.movementInput.jump) {
            event.setCanceled(true);
            this.accelerate();
            return;
        }
        if (isMoveKeyDown) {
            this.accelerationTimer.resetTimeSkipTo((long)(-this.timer.getValDouble()));
        }
        else if (ElytraFly.mc.player.rotationPitch <= this.rotationPitch.getValDouble() && this.sourceCancel.getValBoolean()) {
            if (this.acceleration.getValBoolean() && this.accelerationTimer.passed((double)(long)this.timer.getValDouble())) {
                this.accelerate();
            }
            return;
        }
        event.setCanceled(true);
        this.accelerate();
    }

    public void accelerate() {
        if (this.accelerationResetTimer.passed(this.timer.getValDouble())) {
            this.accelerationResetTimer.reset();
            this.accelerationTimer.reset();
        }
        final float aSpeed = (float)this.speed.getValDouble();
        final double[] dir = MathUtil.directionSpeed((double)aSpeed);
        ElytraFly.mc.player.motionY = -(this.glideSpeed.getValDouble() / 10000.0);
        if (ElytraFly.mc.player.movementInput.moveStrafe != 0.0f || ElytraFly.mc.player.movementInput.moveForward != 0.0f) {
            ElytraFly.mc.player.motionX = dir[0];
            ElytraFly.mc.player.motionZ = dir[1];
        }
        else {
            ElytraFly.mc.player.motionX = 0.0;
            ElytraFly.mc.player.motionZ = 0.0;
        }
        if (ElytraFly.mc.player.movementInput.sneak) {
            ElytraFly.mc.player.motionY = -this.dropSpeed.getValDouble();
        }
        ElytraFly.mc.player.prevLimbSwingAmount = 0.0f;
        ElytraFly.mc.player.limbSwingAmount = 0.0f;
        ElytraFly.mc.player.limbSwing = 0.0f;
    }
}
