package hood.manager.acid.newui.member.members.sub;

import hood.manager.acid.module.Module;
import hood.manager.acid.newui.member.Member;
import hood.manager.acid.newui.member.members.ButtonMember;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class ModeMember extends Member {
    private Module module;
    private ButtonMember parent;
    private Setting setting;

    private boolean isHovered;
    private int offset, x, y, index;

    public ModeMember(Setting setting, ButtonMember button, Module module, int offset) {
        this.setting = setting;
        this.parent = button;
        this.module = module;
        this.x = button.ctx.getX() + button.ctx.getWidth();
        this.y = button.ctx.getY() + button.offset;
        this.offset = offset;
        this.index = 0;
    }

    @Override
    public void drawMember() {
        Minecraft mc = Minecraft.getMinecraft();

        int x = parent.ctx.getX() + 4;
        int y = parent.ctx.getY() + offset + 1;
        int width = parent.ctx.getWidth() - 8;
        int height = 16;

        float maxX = parent.ctx.getX() + parent.ctx.getWidth() - mc.fontRenderer.getStringWidth(setting.getValString());

        RenderUtil.drawRoundedRect(x, y, width, height, 5, new Color(35, 35, 35));

        mc.fontRenderer.drawStringWithShadow(setting.getName(), x + 2, y + 4, -1);
        mc.fontRenderer.drawStringWithShadow(setting.getValString(), maxX - 5, y + 4, -1);
    }

    @Override
    public void updateMember(int mouseX, int mouseY) {
        this.isHovered = isOver(mouseX, mouseY);
        this.y = parent.ctx.getY() + offset;
        this.x = parent.ctx.getX();
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if(isOver(mouseX, mouseY) && button == 0 && parent.expanded) {
            int maxIndex = setting.getOptions().length;

            if(index + 1 >= maxIndex)
                index = 0;
            else
                index++;

            setting.setValString(setting.getOptions()[index]);
        }
    }

    public boolean isOver(int x, int y) {
        return x > this.x
                && x < this.x + 90
                && y > this.y
                && y < this.y + 16;
    }
}
