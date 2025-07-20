package hood.manager.acid.newui.member.members.sub;

import hood.manager.acid.clickgui.component.components.Button;
import hood.manager.acid.newui.member.Member;
import hood.manager.acid.newui.member.members.ButtonMember;
import hood.manager.acid.setting.Setting;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderMember extends Member {
    private Setting setting;
    private ButtonMember parent;

    private int offset, x, y;
    private double length;
    private boolean isHovered, sliding;

    public SliderMember(Setting setting, ButtonMember button, int offset) {
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

        RenderUtil.drawRoundedRect(x, y, width, height, 5, isHovered ? new Color(35, 35, 35).brighter() : new Color(35, 35, 35));
        RenderUtil.drawRoundedRect(
                parent.ctx.getX() + 5,
                parent.ctx.getY() + offset + 13,
                (int) length,
                2,
                2, new Color(0, 150, 150)
        );
        float maxX = parent.ctx.getX() + parent.ctx.getWidth() - mc.fontRenderer.getStringWidth(String.valueOf(setting.getValDouble())) - 5;

        RenderUtil.drawRoundedRect(parent.ctx.getX() + (int) length + 2, parent.ctx.getY() + offset + 12, 3.0, 4.0, 2.0, new Color(255, 255, 255));

        mc.fontRenderer.drawStringWithShadow(setting.getName(), parent.ctx.getX() + 5, parent.ctx.getY() + offset + 4, -1);
        mc.fontRenderer.drawStringWithShadow(String.valueOf(setting.getValDouble()), maxX, parent.ctx.getY() + offset + 4, -1);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        sliding = false;
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if( isOverDouble(mouseX, mouseY) && button == 0 && parent.expanded) sliding = true;
        if (isOverInteger(mouseX, mouseY) && button == 0 && parent.expanded) sliding = true;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
    }

    @Override
    public void updateMember(int mouseX, int mouseY) {
        this.isHovered = isOverDouble(mouseX, mouseY) || isOverInteger(mouseX, mouseY);

        this.y = parent.ctx.getY() + offset;
        this.x = parent.ctx.getX();

        double diff = Math.min(82, Math.max(0, mouseX - this.x));

        double min = setting.getMin();
        double max = setting.getMax();

        length = 82 * (setting.getValDouble() - min) / (max - min);

        if (sliding) {
            if (diff == 0) setting.setValDouble(setting.getMin());
            else {
                double rounded = round(((diff / 82) * (max - min) + min), 2);
                setting.setValDouble(rounded);
            }
        }
    }

    private static double round(double value, int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public boolean isOverDouble(int x, int y) {
        return x > this.x && x < this.x + (parent.ctx.getWidth() / 2 + 1) && y > this.y && y < this.y + 18;
    }

    public boolean isOverInteger(int x, int y) {
        return x > this.x + parent.ctx.getWidth() / 2 && x < this.x + parent.ctx.getWidth() && y > this.y && y < this.y + 18;
    }
}
