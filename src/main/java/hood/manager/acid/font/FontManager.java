package hood.manager.acid.font;

import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.FileSystemUtils;

import java.awt.*;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FontManager {
    private FontRenderer font;
    private int fontType;

    /**
     * Loads a given font
     * @param in The given font
     * @param type Font type (bold, italicized, etc.)
     */
    public void loadFont(String in, int type) {
        font = new FontRenderer(loadFont(in, 40, type));
        fontType = type;
    }

    /**
     * Attempts to load a given font
     * @param in The given font
     * @param size The size of the font
     * @return The loaded font
     */
    private Font loadFont(String in, int size, int type) {
        fontType = type;
        try {

            // font stream
            InputStream fontStream = null;

            // if the client font exists
            if (fontStream != null) {

                // creates and derives the font
                Font clientFont = Font.createFont(Font.TRUETYPE_FONT, fontStream);
                clientFont = clientFont.deriveFont(type, size);

                // close stream
                fontStream.close();
                return clientFont;
            }

            // default
            return new Font("Verdana", type, size);

        } catch (Exception exception) {

            // notify
            //if (font != null) {

                // unrecognized gamemode exception
                //Cosmos.INSTANCE.getChatManager().sendHoverableMessage(ChatFormatting.RED + "Unrecognized Font!", ChatFormatting.RED + "Please enter a valid font.");
            //}

            // load default
            return new Font("Verdana", type, size);
        }
    }

    /**
     * Gets the current font
     * @return The current font
     */
    public FontRenderer getFontRenderer() {
        return font != null ? font : new FontRenderer(new Font("Verdana", Font.PLAIN, 40));
    }

    /**
     * Gets the current font
     * @return The current font
     */
    public String getFont() {
        return font.getName();
    }

    /**
     * Gets the current font type
     * @return The current font type
     */
    public int getFontType() {
        return fontType;
    }
}
