package hood.manager.acid.mixin.mixins.ui;

import hood.manager.acid.Main;
import hood.manager.acid.event.events.RenderFontEvent;
import hood.manager.acid.util.FontUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {
    @Inject(method = "drawString(Ljava/lang/String;FFIZ)I", at = @At("HEAD"), cancellable = true)
    public void renderString(String text, float x, float y, int color, boolean dropShadow, CallbackInfoReturnable<Integer> info) {
        RenderFontEvent renderFontEvent = new RenderFontEvent();
        MinecraftForge.EVENT_BUS.post(renderFontEvent);

        if (renderFontEvent.isCanceled()) {
            info.setReturnValue(FontUtil.drawStringWithShadow(text, x, y, color));
        }
    }

    @Inject(method = "getStringWidth", at = @At("HEAD"), cancellable = true)
    public void getWidth(String text, CallbackInfoReturnable<Integer> info) {
        RenderFontEvent renderFontEvent = new RenderFontEvent();
        MinecraftForge.EVENT_BUS.post(renderFontEvent);

        if (renderFontEvent.isCanceled()) {
            info.setReturnValue(FontUtil.getStringWidth(text));
        }
    }
}
