package hood.manager.acid.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import hood.manager.acid.util.enums.Side;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil {
    protected static Minecraft mc = Minecraft.getMinecraft();

    public static final String prefix = ChatFormatting.RED + "(acid) > " + ChatFormatting.RESET;

    public static void sendMessage(Side side, String msg) {
        switch (side) {
            case SERVER: {
                mc.player.sendChatMessage(msg);
                break;
            }
            case CLIENT: {
                mc.player.sendMessage(new TextComponentString(prefix + msg));
                break;
            }
        }
    }
}
