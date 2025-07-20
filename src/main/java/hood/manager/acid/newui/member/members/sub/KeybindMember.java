package hood.manager.acid.newui.member.members.sub;

import hood.manager.acid.newui.member.Member;
import hood.manager.acid.newui.member.members.ButtonMember;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class KeybindMember extends Member {
    private ButtonMember parent;

    private boolean isHovered, isBinding;
    private int offset, x, y;

    public KeybindMember(ButtonMember button, int offset) {
        this.parent = button;

        this.x = button.ctx.getX() + button.ctx.getWidth();
        this.y = button.ctx.getY() + button.offset;
        this.offset = offset;
    }

    @Override
    public void drawMember() {
        Minecraft mc = Minecraft.getMinecraft();

        int x = parent.ctx.getX() + 4;
        int y = parent.ctx.getY() + offset + 1;
        int width = parent.ctx.getWidth() - 8;
        int height = 16;

        RenderUtil.drawRoundedRect(x, y, width, height, 5, new Color(35, 35, 35));
        mc.fontRenderer.drawStringWithShadow(isBinding ? "Listening..." : "Key", x + 2, (parent.ctx.getY() + offset + 5), -1);
        mc.fontRenderer.drawStringWithShadow(isBinding ? "" : (Keyboard.getKeyName(parent.module.getKey())), (parent.ctx.getX() + parent.ctx.getWidth() - mc.fontRenderer.getStringWidth(Keyboard.getKeyName(parent.module.getKey()))) - 6, y + 4, -1);
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateMember(int mouseX, int mouseY) {
        this.isHovered = isOver(mouseX, mouseY);
        this.y = parent.ctx.getY() + offset;
        this.x = parent.ctx.getX();
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isOver(mouseX, mouseY) && button == 0 && parent.expanded) {
            isBinding = !isBinding;
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        if (isBinding) {
            if (key == Keyboard.KEY_BACK) {
                parent.module.setKey(0);
                isBinding = false;
            } else {
                parent.module.setKey(key);
                isBinding = false;
            }
        }
    }

    public boolean isOver(int x, int y) {
        return x > this.x && x < this.x + 88 && y > this.y && y < this.y + 18;
    }
}
