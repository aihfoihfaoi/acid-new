package hood.manager.acid.util;

import hood.manager.acid.Main;
import hood.manager.acid.module.modules.misc.CustomFont;
import net.minecraft.client.Minecraft;

public class FontUtil {
    protected static Minecraft mc = Minecraft.getMinecraft();

    /**
     * Renders a given text
     * @param text The given text
     * @param x The x position
     * @param y The y position
     * @param color The color of the text
     * @return The color of the text
     */
    public static int drawString(String text, float x, float y, int color) {
        return Main.INSTANCE.moduleManager.isModuleEnabled("CustomFont") ? Main.INSTANCE.fontManager.getFontRenderer().drawString(text, x, y, color, false, true) : mc.fontRenderer.drawString(text, (int) x, (int) y, color);
    }

    /**
     * Renders a given text with a shadow
     * @param text The given text
     * @param x The x position
     * @param y The y position
     * @param color The color of the text
     * @return The color of the text
     */
    public static int drawStringWithShadow(String text, float x, float y, int color) {
        return Main.INSTANCE.moduleManager.isModuleEnabled("CustomFont") ? Main.INSTANCE.fontManager.getFontRenderer().drawStringWithShadow(text, x, y, color, true) : mc.fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    /**
     * Gets a given text's width
     * @param text The given text
     * @return The given text's width
     */
    public static int getStringWidth(String text) {
        return Main.INSTANCE.moduleManager.isModuleEnabled("CustomFont") ? Main.INSTANCE.fontManager.getFontRenderer().getStringWidth(text) : mc.fontRenderer.getStringWidth(text);
    }

    /**
     * Gets the current font's height
     * @return The current font's height
     */
    public static float getFontHeight() {
        return mc.fontRenderer.FONT_HEIGHT;
    }
}
