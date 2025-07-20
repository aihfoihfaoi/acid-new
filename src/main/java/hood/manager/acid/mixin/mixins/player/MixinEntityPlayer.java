package hood.manager.acid.mixin.mixins.player;

import hood.manager.acid.event.events.PlayerTravelEvent;
import hood.manager.acid.mixin.mixins.entity.MixinEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntity {
    @Inject(method = { "travel" }, at = { @At("HEAD") }, cancellable = true)
    public void travel(final float strafe, final float vertical, final float forward, final CallbackInfo ci) {
        final PlayerTravelEvent event = new PlayerTravelEvent(strafe, vertical, forward);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            ci.cancel();
        }
    }
}
