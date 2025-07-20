package hood.manager.acid.event;

import hood.manager.acid.Main;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class PrimaryListener {
    private static Minecraft mc = Minecraft.getMinecraft();

    public PrimaryListener() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (mc.player != null) {
            Main.INSTANCE.moduleManager.onUpdate();
        }
    }
}
