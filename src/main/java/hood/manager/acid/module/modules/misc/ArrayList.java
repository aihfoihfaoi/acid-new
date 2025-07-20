package hood.manager.acid.module.modules.misc;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class ArrayList extends Module {
    public ArrayList() {
        super("ArrayList", "Display all enabled modules.", Category.CLIENT);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            List<Module> modules = Main.INSTANCE.moduleManager.getModules().parallelStream()
                    .filter(Module::isToggled)
                    .sorted((Comparator.comparing(m -> mc.fontRenderer.getStringWidth(((Module)m).getName()))).reversed())
                    .collect(Collectors.toList());

            ScaledResolution sr = new ScaledResolution(mc);
            int offset = sr.getScaledHeight();
            for (Module m : modules) {
                mc.fontRenderer.drawStringWithShadow(m.getName(), sr.getScaledWidth() - mc.fontRenderer.getStringWidth(m.getName()) - 2, offset - 10, getStaticRainbow(3).getRGB());
                offset -= mc.fontRenderer.FONT_HEIGHT;
            }
        }
    }

    public static Color getStaticRainbow(int offset) {
        return alphaCycle(new Color(0, 150, 150), (offset * 2) + 10);
    }

    public static Color alphaCycle(Color color, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), hsb);
        float brightness = Math.abs(((float) (System.currentTimeMillis() % 2000L) / 1000 + 50F / (float) count * 2) % 2 - 1);
        brightness = 0.5F + 0.5F * brightness;
        hsb[2] = brightness % 2;
        return new Color(Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]));
    }
}
