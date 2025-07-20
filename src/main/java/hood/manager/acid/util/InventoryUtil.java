package hood.manager.acid.util;

import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class InventoryUtil {
    protected static Minecraft mc = Minecraft.getMinecraft();

    public static int getHotbarIndex(Item item) {
        for (int i = 0; i < 9; ++i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() != item) continue;
            return i;
        }
        return -1;
    }

    public static void indexSwap(int slot) {
        mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
        mc.player.inventory.currentItem = slot;
        mc.playerController.updateController();
    }
}
