package hood.manager.acid.module.modules.movement;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.PlayerMoveEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import io.netty.util.internal.ConcurrentSet;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Set;

// thanks to phobos for this
public class PacketFly extends Module {
    Setting mode = new Setting("Mode", this, "Setback", "Setback", "Normal");
    Setting additional = new Setting("Additional", this, true);
    Setting offset = new Setting("Offset", this, 1337, -1337, 1337, true);
    Setting fall = new Setting("Fall add", this, true);
    Setting tp = new Setting("TP", this, true);
    Setting tpVerify = new Setting("TP verify", this, 2, 0, 4, true);
    Setting setMove = new Setting("Set move", this, false);
    Setting noClip = new Setting("No clip", this, false);
    Setting cancel = new Setting("Cancel", this, 20, 0, 20, false);
    Setting xSpeed = new Setting("X speed", this, 500, 1, 1000, true);
    Setting ySpeed = new Setting("Y speed", this, 500, 1, 1000, true);

    private final Set<CPacketPlayer> packets = new ConcurrentSet();
    private boolean teleport = true;
    private int teleportIds = 0;
    private int posLookPackets;

    public PacketFly() {
        super("PacketFly", "Attempts to use packets to fly.", Category.MOVEMENT);

        Main.INSTANCE.settingManager.addSetting(mode);
        Main.INSTANCE.settingManager.addSetting(additional);
        Main.INSTANCE.settingManager.addSetting(offset);
        Main.INSTANCE.settingManager.addSetting(fall);
        Main.INSTANCE.settingManager.addSetting(tp);
        Main.INSTANCE.settingManager.addSetting(tpVerify);
        Main.INSTANCE.settingManager.addSetting(setMove);
        Main.INSTANCE.settingManager.addSetting(noClip);
        Main.INSTANCE.settingManager.addSetting(cancel);
        Main.INSTANCE.settingManager.addSetting(xSpeed);
        Main.INSTANCE.settingManager.addSetting(ySpeed);
    }

    @SubscribeEvent
    public void onMove(PlayerMoveEvent event) {
        event.setX(mc.player.motionX);
        event.setY(mc.player.motionY);
        event.setZ(mc.player.motionZ);
        if (noClip.getValBoolean()) {
            mc.player.noClip = true;
        }
        begin(event);
    }

    private void begin(PlayerMoveEvent event) {
        if (mode.getValString().equals("Setback")) {
            double[] dirSpeed = this.getMotion(this.teleport ? (double) ySpeed.getValDouble() / 10000.0 : (double) (ySpeed.getValDouble() - 1) / 10000.0);
            double posX = mc.player.posX + dirSpeed[0];
            double posY = mc.player.posY + (mc.gameSettings.keyBindJump.isKeyDown() ? (this.teleport ? (double) ySpeed.getValDouble() / 10000.0 : (double) (ySpeed.getValDouble() - 1) / 10000.0) : 1.0E-8) - (mc.gameSettings.keyBindSneak.isKeyDown() ? (this.teleport ? (double) ySpeed.getValDouble() / 10000.0 : (double) (ySpeed.getValDouble() - 1) / 10000.0) : 2.0E-8);
            double posZ = mc.player.posZ + dirSpeed[1];
            CPacketPlayer.PositionRotation packetPlayer = new CPacketPlayer.PositionRotation(posX, posY, posZ, mc.player.rotationYaw, mc.player.rotationPitch, false);
            this.packets.add(packetPlayer);
            mc.player.connection.sendPacket(packetPlayer);
            if (tpVerify.getValDouble() != 3) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportIds - 1));
                ++this.teleportIds;
            }
            if (additional.getValBoolean()) {
                CPacketPlayer.PositionRotation packet = new CPacketPlayer.PositionRotation(mc.player.posX, (double) offset.getValDouble() + mc.player.posY, mc.player.posZ, mc.player.rotationYaw, mc.player.rotationPitch, true);
                this.packets.add(packet);
                mc.player.connection.sendPacket(packet);
            }
            if (tpVerify.getValDouble() != 1) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportIds + 1));
                ++this.teleportIds;
            }
            if (tpVerify.getValDouble() == 4) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportIds));
                ++this.teleportIds;
            }
            if (this.fall.getValBoolean()) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_FALL_FLYING));
            }
            mc.player.setPosition(posX, posY, posZ);
            boolean bl = this.teleport = !tp.getValBoolean() || !this.teleport;
            if (event != null) {
                event.setX(0.0);
                event.setY(0.0);
                event.setX(0.0);
            } else {
                mc.player.motionX = 0.0;
                mc.player.motionY = 0.0;
                mc.player.motionZ = 0.0;
            }
        }
    }

    private double[] getMotion(double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f) {
                rotationYaw += (float) (moveForward > 0.0f ? -45 : 45);
            } else if (moveStrafe < 0.0f) {
                rotationYaw += (float) (moveForward > 0.0f ? 45 : -45);
            }
            moveStrafe = 0.0f;
            if (moveForward > 0.0f) {
                moveForward = 1.0f;
            } else if (moveForward < 0.0f) {
                moveForward = -1.0f;
            }
        }
        double posX = (double) moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double) moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double) moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double) moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    @Override
    public void onDisable() {
        this.packets.clear();
        this.posLookPackets = 0;
        if (mc.player != null) mc.player.noClip = false;
    }
}
