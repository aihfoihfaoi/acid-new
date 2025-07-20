package hood.manager.acid.newui;

import hood.manager.acid.clickgui.component.Component;
import hood.manager.acid.module.Category;
import hood.manager.acid.module.ModuleManager;
import hood.manager.acid.newui.member.Member;
import hood.manager.acid.newui.member.members.ButtonMember;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class Context {
    public List<Member> members = new ArrayList<>();
    public Category category;
    private boolean expanded;
    private int width, x, y, barHeight;

    protected static final Minecraft mc = Minecraft.getMinecraft();

    public Context(Category category) {
        this.category = category;
        this.width = 90;
        this.x = 5;
        this.y = 5;
        this.barHeight = 20;
        this.expanded = false;

        AtomicInteger tY = new AtomicInteger(this.barHeight);
        ModuleManager.getModulesInCategory(category).forEach(module -> {
            ButtonMember btn = new ButtonMember(module, this, tY.get());
            members.add(btn);
            tY.addAndGet(20);
        });
    }

    public void drawContext() {
        if (Mouse.getEventDWheel() > 0) {
            y += 1;
        } else if (Mouse.getEventDWheel() < 0) {
            y -= 1;
        }

        float halfX = x + width / 2.0f - mc.fontRenderer.getStringWidth(category.name()) / 2.0f;
        float halfY = y + barHeight / 2.0f - mc.fontRenderer.FONT_HEIGHT / 2.0f;

        RenderUtil.drawRoundedRect(x, y, width, barHeight, 6, new Color(35, 35, 35));
        mc.fontRenderer.drawStringWithShadow(category.name(), halfX, halfY, -1);

        if (isExpanded() && !members.isEmpty()) {
            RenderUtil.drawRoundedRect(getX(), getY() + 17, getWidth(), (members.size() * 20) + 4, 5, new Color(35, 35, 35));
            RenderUtil.drawRoundedRect(x, y + barHeight - 3, width, 1, 0, new Color(0, 150, 150));
            members.forEach(Member::drawMember);
        }
    }

    public void refresh() {
        int off = this.barHeight;
        for (Member member : members) {
            member.setOff(off);
            off += member.getHeight() + 1;
        }
    }

    public List<Member> getMembers() {
        return members;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public int getWidth() {
        return width;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isOver(int x, int y) {
        return x >= this.x
                && x <= this.x + this.width
                && y >= this.y
                && y <= this.y + this.barHeight;
    }
}
