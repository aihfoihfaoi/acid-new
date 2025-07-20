package hood.manager.acid.module.modules.movement;

import hood.manager.acid.event.events.PlayerMoveEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Strafe extends Module {
    double moveSpeed;

    public Strafe() {
        super("Strafe", "Improves movement.", Category.MOVEMENT);
    }

    @Override
    public void onDisable() {
        this.moveSpeed = 0.0;
        mc.player.setSprinting(false);
    }

    @SubscribeEvent
    public void onMotion(final PlayerMoveEvent event) {
        if (mc.player != null || mc.world != null) {
            float forward = mc.player.movementInput.moveForward;
            float strafe = mc.player.movementInput.moveStrafe;
            float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
            double baseSpeed = 0.2873;
            if (mc.player.isPotionActive(MobEffects.SPEED)) {
                final double amplifier = mc.player.getActivePotionEffect(MobEffects.SPEED).getAmplifier();
                baseSpeed *= 1.0 + 0.2 * (amplifier + 1.0);
            }
            if (mc.player.isPotionActive(MobEffects.SLOWNESS)) {
                final double amplifier = mc.player.getActivePotionEffect(MobEffects.SLOWNESS).getAmplifier();
                baseSpeed /= 1.0 + 0.2 * (amplifier + 1.0);
            }
            this.moveSpeed = 1.15 * baseSpeed - 0.001;
            this.moveSpeed = Math.max(this.moveSpeed, baseSpeed);
            if (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f) {
                event.setX(0.0);
                event.setZ(0.0);
            }
            else if (forward != 0.0f) {
                if (strafe > 0.0f) {
                    yaw += ((forward > 0.0f) ? -45.0f : 45.0f);
                }
                else if (strafe < 0.0f) {
                    yaw += ((forward > 0.0f) ? 45.0f : -45.0f);
                }
                strafe = 0.0f;
                if (forward > 0.0f) {
                    forward = 1.0f;
                }
                else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            if (forward != 0.0f) {
                if (strafe > 0.0f) {
                    yaw += ((forward > 0.0f) ? -45.0f : 45.0f);
                }
                else if (strafe < 0.0f) {
                    yaw += ((forward > 0.0f) ? 45.0f : -45.0f);
                }
                strafe = 0.0f;
                if (forward > 0.0f) {
                    forward = 1.0f;
                }
                else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            final double cos = Math.cos(Math.toRadians(yaw));
            final double sin = -Math.sin(Math.toRadians(yaw));
            event.setX(forward * this.moveSpeed * sin + strafe * this.moveSpeed * cos);
            event.setZ(forward * this.moveSpeed * cos - strafe * this.moveSpeed * sin);

            mc.player.setSprinting(true);
            if (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) {
                if (!mc.player.onGround) {
                    return;
                }
                event.setY(mc.player.motionY = 0.4);
            }
        }
    }
}
