package hood.manager.acid.mixin.mixins.render;

import hood.manager.acid.event.events.RenderHeldItemEvent;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public class MixinRenderItem {
    @Inject(method = { "transformFirstPerson" }, at = { @At("HEAD") })
    public void onTransformFirstPerson(EnumHandSide handSide, float partialTicks, CallbackInfo info) {
        RenderHeldItemEvent event = new RenderHeldItemEvent(handSide);
        MinecraftForge.EVENT_BUS.post(event);
    }
}
