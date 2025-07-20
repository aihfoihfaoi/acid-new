package hood.manager.acid.module.modules.misc;

import hood.manager.acid.event.events.RenderFontEvent;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomFont extends Module {
    public CustomFont() {
        super("CustomFont", "Renders a custom font on everything.", Category.MISC);
    }

    @SubscribeEvent
    public void onFontRender(RenderFontEvent event) {
        event.setCanceled(true);
    }
}
