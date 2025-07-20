package hood.manager.acid.newui;

import hood.manager.acid.module.Category;
import hood.manager.acid.newui.member.Member;
import net.minecraft.client.gui.GuiScreen;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class UI extends GuiScreen {
    public static List<Context> contexts = new ArrayList<>();

    public UI() {
        // 5 pixels distance between each context (spread)
        AtomicInteger ctxSpread = new AtomicInteger(5);

        Arrays.stream(Category.values()).forEach(c -> {
            Context ctx = new Context(c);
            ctx.setX(ctxSpread.get());
            contexts.add(ctx);

            ctxSpread.getAndAdd(ctx.getWidth() + 1);
        });
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        contexts.forEach(ctx -> {
            ctx.drawContext();
            for (Member member : ctx.getMembers()) {
                member.updateMember(mouseX, mouseY);
            }
        });
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        contexts.forEach(ctx -> {
            if (ctx.isOver(mouseX, mouseY) && mouseButton == 1) {
                ctx.setExpanded(!ctx.isExpanded());
            }
            if (ctx.isExpanded() && !ctx.getMembers().isEmpty()) {
                ctx.getMembers().forEach(m -> m.mouseClicked(mouseX, mouseY, mouseButton));
            }
        });
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        contexts.forEach(ctx -> {
            if (ctx.isExpanded()) {
                if (!ctx.getMembers().isEmpty()) {
                    for (Member member : ctx.getMembers()) {
                        member.mouseReleased(mouseX, mouseY, state);
                    }
                }
            }
        });
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (Context ctx : contexts) {
            if (ctx.isExpanded() && keyCode != 1) {
                if (!ctx.getMembers().isEmpty()) {
                    for (Member member : ctx.getMembers()) {
                        member.keyTyped(typedChar, keyCode);
                    }
                }
            }
        }
        if (keyCode == Keyboard.KEY_ESCAPE) mc.displayGuiScreen(null);
    }
}
