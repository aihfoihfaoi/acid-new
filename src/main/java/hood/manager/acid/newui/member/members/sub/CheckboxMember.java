package hood.manager.acid.newui.member.members.sub;

import hood.manager.acid.newui.member.Member;
import hood.manager.acid.newui.member.members.ButtonMember;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class CheckboxMember extends Member {
    private Setting setting;
    private ButtonMember parent;

    private int offset, x, y;
    private boolean isHovered;

    public CheckboxMember(Setting setting, ButtonMember button, int offset) {
        this.setting = setting;
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
        mc.fontRenderer.drawStringWithShadow(setting.getName(), x + 2, y + 4, -1);

        RenderUtil.drawRoundedRect(x + width - 13, y + 5, 10, 5, 3, new Color(25, 25, 25));
        RenderUtil.drawRoundedRect(setting.getValBoolean() ? x + width - 8  : x + width - 13, y + 5, 5, 5, 3, new Color(0, 150, 150));
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isOver(mouseX, mouseY) && button == 0 && parent.expanded) {
            setting.setValBoolean(!setting.getValBoolean());
        }
    }

    @Override
    public void updateMember(int mouseX, int mouseY) {
        this.isHovered = isOver(mouseX, mouseY);
        this.x = parent.ctx.getX();
        this.y = parent.ctx.getY() + offset;
    }

    public boolean isOver(int x, int y) {
        return x > this.x
                && x < this.x + 90
                && y > this.y
                && y < this.y + 16;
    }

    public static class ModeMember {
    }
}
