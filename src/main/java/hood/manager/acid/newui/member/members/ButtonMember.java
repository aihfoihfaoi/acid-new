package hood.manager.acid.newui.member.members;

import hood.manager.acid.Main;
import hood.manager.acid.module.Module;
import hood.manager.acid.newui.Context;
import hood.manager.acid.newui.member.Member;
import hood.manager.acid.newui.member.members.sub.CheckboxMember;
import hood.manager.acid.newui.member.members.sub.KeybindMember;
import hood.manager.acid.newui.member.members.sub.ModeMember;
import hood.manager.acid.newui.member.members.sub.SliderMember;
import hood.manager.acid.util.RenderUtil;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author hood manager
 */
public class ButtonMember extends Member {
    public List<Member> members = new ArrayList<>();
    public Module module;
    public Context ctx;

    public boolean expanded, isHovered;
    public int offset, height;

    public ButtonMember(Module module, Context ctx, int offset) {
        this.module = module;
        this.ctx = ctx;
        this.offset = offset;
        this.expanded = false;

        height = 18;
        AtomicInteger settingY = new AtomicInteger(offset + 18);
        if (Main.INSTANCE.settingManager.getSettingsByMod(module) != null) {
            Main.INSTANCE.settingManager.getSettingsByMod(module).forEach(setting -> {
                if (setting.isSlider()) {
                    members.add(new SliderMember(setting, this, settingY.get()));
                    settingY.addAndGet(18);
                }
                if (setting.isCheck()) {
                    members.add(new CheckboxMember(setting, this, settingY.get()));
                    settingY.addAndGet(18);
                }
                if (setting.isCombo()) {
                    members.add(new ModeMember(setting, this, module, settingY.get()));
                    settingY.addAndGet(18);
                }
            });
        }
        members.add(new KeybindMember(this, settingY.get()));
    }

    @Override
    public void drawMember() {
        Minecraft mc = Minecraft.getMinecraft();

        Color buttonColor = isHovered
                ? (module.isToggled() ? new Color(0, 150, 150).brighter() : new Color(25, 25, 25).brighter())
                : (module.isToggled() ? new Color(0, 150, 150) : new Color(25, 25, 25));

        float halfY = ctx.getY() + offset + 6;

        RenderUtil.drawRoundedRect(
                ctx.getX() + 2,
                ctx.getY() + offset,
                ctx.getWidth() - 4,
                height,
                5,
                buttonColor
        );
        mc.fontRenderer.drawStringWithShadow(module.getName(), ctx.getX() + 4, halfY, -1);
        if (expanded && !members.isEmpty()) {
            RenderUtil.drawRoundedRect(ctx.getX() + 2, (ctx.getY() - 2) + offset + (height + 2), ctx.getWidth() - 4, (height + 2) * members.size(), 5, new Color(30, 30, 30));
            members.forEach(Member::drawMember);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (isOver(mouseX, mouseY) && button == 0) module.toggle();
        if (isOver(mouseX, mouseY) && button == 1) {
            expanded = !expanded;
            ctx.refresh();
        }
        for (Member member : members) {
            member.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        for (Member member : members) {
            member.mouseReleased(mouseX, mouseY, mouseButton);
        }
    }

    @Override
    public int getHeight() {
        if (expanded) {
            return (19 * (members.size() + 1));
        }
        return 19;
    }

    @Override
    public void setOff(int newOff) {
        offset = newOff;
        int settingY = offset + 19;
        for(Member member : members) {
            member.setOff(settingY);
            settingY += 19;
        }
    }

    @Override
    public void updateMember(int mouseX, int mouseY) {
        isHovered = isOver(mouseX, mouseY);
        if (!members.isEmpty()) {
            members.forEach(m -> m.updateMember(mouseX, mouseY));
        }
    }

    @Override
    public void keyTyped(char typedChar, int key) {
        for (Member member : members) {
            member.keyTyped(typedChar, key);
        }
    }

    public boolean isOver(int x, int y) {
        return x > ctx.getX() && x < ctx.getX() + ctx.getWidth() && y > this.ctx.getY() + this.offset && y < this.ctx.getY() + 16 + this.offset;
    }
}
