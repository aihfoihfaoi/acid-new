package hood.manager.acid.mixin.mixins.world;

import hood.manager.acid.event.events.RenderSkyEvent;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(World.class)
public class MixinWorld {
    @Inject(method = { "getSkyColor" }, at = { @At("HEAD") }, cancellable = true)
    public void getSkyColor(Entity entity, float partialTicks, CallbackInfoReturnable<Vec3d> ciReturnable) {
        RenderSkyEvent event = new RenderSkyEvent();
        MinecraftForge.EVENT_BUS.post(event);

        if (event.isCanceled()) {
            ciReturnable.cancel();
            ciReturnable.setReturnValue(new Vec3d(event.getColor().getRed() / 255.0, event.getColor().getGreen() / 255.0, event.getColor().getBlue() / 255.0));
        }
    }
}
