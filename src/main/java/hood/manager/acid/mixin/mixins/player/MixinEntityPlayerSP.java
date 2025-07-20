package hood.manager.acid.mixin.mixins.player;

import com.mojang.authlib.GameProfile;
import hood.manager.acid.event.events.PlayerMoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EntityPlayerSP.class)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    Minecraft mc;
    @Shadow
    private int positionUpdateTicks;

    public MixinEntityPlayerSP() {
        super((World)null, (GameProfile)null);
        this.mc = Minecraft.getMinecraft();
    }

    @Redirect(method = { "move" }, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(final AbstractClientPlayer player, final MoverType type, final double x, final double y, final double z) {
        final PlayerMoveEvent moveEvent = new PlayerMoveEvent(type, x, y, z);
        MinecraftForge.EVENT_BUS.post((Event)moveEvent);
        super.move(type, moveEvent.x, moveEvent.y, moveEvent.z);
    }
}
