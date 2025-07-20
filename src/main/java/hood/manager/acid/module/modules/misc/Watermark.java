package hood.manager.acid.module.modules.misc;

import hood.manager.acid.Main;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.Module;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.Random;

public class Watermark extends Module {
    String[] watermarks = {
            "jew",
            "kike",
            "fart",
            "crystal",
            "russian",
            "roblox",
            "nigeria",
            "femboy",
            "fortnite",
    };

    String[] endings = {
            ".pub", ".ru", ".cc", "sense", ".us",
            ".wtf", ".lol", ".gg", ".org", "hack",
            "+", "++", "+2", "+3", ".net", ".com"
    };

    int wIndex = new Random().nextInt(watermarks.length);
    int eIndex = new Random().nextInt(endings.length);

    Setting random = new Setting("Random", this, true);
    Setting basic = new Setting("Basic", this, false);

    public Watermark() {
        super("Watermark", "Displays a watermark on your screen.", Category.CLIENT);

        Main.INSTANCE.settingManager.addSetting(random);
        Main.INSTANCE.settingManager.addSetting(basic);
    }

    @SubscribeEvent
    public void onRender2D(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.TEXT) {
            String watermark = watermarks[wIndex];
            String ending = endings[eIndex];

            int ping = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.player.getUniqueID()).getResponseTime();

            String ip = (mc.getCurrentServerData() != null) ? mc.getCurrentServerData().serverIP.toLowerCase() : "Singleplayer";
            LocalTime currentTime = LocalTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");

            String formattedTime = currentTime.format(formatter);

            String product = random.getValBoolean() ? watermark + TextFormatting.AQUA + ending + TextFormatting.RESET + " | " + ping + "ms" + " | " + ip + " | " + formattedTime : "acid" + TextFormatting.AQUA + "sense" + TextFormatting.RESET + " | " + ping + "ms" + " | " + ip + " | " + formattedTime;

            if (!basic.getValBoolean()) {
                RenderUtil.drawRoundedRect(2, 4, mc.fontRenderer.getStringWidth(product) + 3, mc.fontRenderer.FONT_HEIGHT + 5, 4, new Color(35, 35, 35, 135));
                Gui.drawRect(2, 5, mc.fontRenderer.getStringWidth(product) + 5, 3, new Color(0, 150, 150).getRGB());
                mc.fontRenderer.drawStringWithShadow(product, 4, 8, new Color(255, 255, 255).getRGB());

            } else {
                mc.fontRenderer.drawStringWithShadow(product, 4, 8, new Color(255, 255, 255).getRGB());
            }
        }
    }
}
