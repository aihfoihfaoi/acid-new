package hood.manager.acid.module.modules.movement;

import hood.manager.acid.event.events.PacketEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity", "Removes knockback.", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityVelocity
            || event.getPacket() instanceof SPacketExplosion)
            event.setCanceled(true);
    }
}
