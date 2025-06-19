package n1luik.KAllFix.forge.RenderOptimizer.ui;

import n1luik.KAllFix.forge.RenderOptimizer.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.client.gui.widget.ExtendedButton;

import java.awt.*;

public class ConfigScreen extends Screen {

    protected EditBox EntityStopTickDistance;
    protected EditBox EntityStartStopTickDistance;
    protected EditBox EntityTickScaling;
    public ConfigScreen() {
        super(Component.empty());
    }

    @Override
    public void render(GuiGraphics p_281549_, int p_281550_, int p_282878_, float p_282465_) {
        super.render(p_281549_, p_281550_, p_282878_, p_282465_);
        //p_281549_.pose().pushPose();
        //p_281549_.pose().scale(3.3f, 3.3f, 3.3f);
        Minecraft m = Minecraft.getInstance();
        int ets;
        int bets;
        if (m.level == null) {
            ets = -1;
            bets = -1;
        }else {
            if (m.level.tickingEntities.active == null) {
                ets = 0;
            }else {
                ets = m.level.tickingEntities.active.size();
            }
            bets = m.level.blockEntityTickers.size();
        }
        int len = (int)(font.lineHeight * 1.2);
        p_281549_.drawString(
                font,
                Component.translatable("k_all_fix.RenderOptimizer.renderTickInfo",
                        ets, bets),
                3, 5, 0xFFFFFFFF,
                false);
        //p_281549_.pose().popPose();
        int pos = len + 6 + 3 + 20 + 3;
        p_281549_.drawString(
                font,
                Component.translatable("k_all_fix.RenderOptimizer.EntityStopTickDistance"),
                3, pos, 0xFFFFFFFF,
                false);
        pos += len + 20 + 3 + 3;
        p_281549_.drawString(
                font,
                Component.translatable("k_all_fix.RenderOptimizer.EntityStartStopTickDistance"),
                3, pos, 0xFFFFFFFF,
                false);
        pos += 3 + len + 20 + 3;
        p_281549_.drawString(
                font,
                Component.translatable("k_all_fix.RenderOptimizer.EntityTickScaling"),
                3, pos, 0xFFFFFFFF,
                false);
    }

    @Override
    protected void init() {
        super.init();
        int len = (int)(font.lineHeight * 1.2);
        int pos = len + 6 + 3;
        addRenderableWidget(new ExtendedButton(3, pos, 150, 20, Component.translatable("k_all_fix.RenderOptimizer.Enable", Config.Enable),v -> {
            Config.Enable = !Config.Enable;
            v.setMessage(Component.translatable("k_all_fix.RenderOptimizer.Enable", Config.Enable));
        }));
        pos += 20 + 3 + 3 + len;
        addRenderableWidget(EntityStopTickDistance = new EditBox(Minecraft.getInstance().font, 3, pos, 150, 20, Component.empty()));
        EntityStopTickDistance.setValue(String.valueOf(Config.EntityStopTickDistance));
        pos += 20 + 3 + 3 + len;
        addRenderableWidget(EntityStartStopTickDistance = new EditBox(Minecraft.getInstance().font, 3, pos, 150, 20, Component.empty()));
        EntityStartStopTickDistance.setValue(String.valueOf(Config.EntityStartStopTickDistance));
        pos += 20 + 3 + len;
        addRenderableWidget(EntityTickScaling = new EditBox(Minecraft.getInstance().font, 3, pos, 150, 20, Component.empty()));
        EntityTickScaling.setValue(String.valueOf(Config.EntityTickScaling));
        pos += 20 + 3 + len;


        int pos2 = 3;
        addRenderableWidget(new ExtendedButton(width - 153, pos2, 150, 20, Component.translatable("k_all_fix.RenderOptimizer.FixLivingEntity", Config.FixLivingEntity),v -> {
            Config.FixLivingEntity = !Config.FixLivingEntity;
            v.setMessage(Component.translatable("k_all_fix.RenderOptimizer.FixLivingEntity", Config.FixLivingEntity));
        }));
        pos2 += 20;

        

    }

    @Override
    public void tick() {
        super.tick();
        int rgb = new Color(255, 0, 0, 255).getRGB();
        if (EntityStopTickDistance.getValue() != null) {
            try {
                Config.EntityStopTickDistance = Float.parseFloat(EntityStopTickDistance.getValue());
                EntityStopTickDistance.setTextColor(0xFFFFFFFF);
            }catch (Exception e){
                EntityStopTickDistance.setTextColor(rgb);
            }
        }
        if (EntityStartStopTickDistance.getValue() != null) {
            try {
                Config.EntityStartStopTickDistance = Float.parseFloat(EntityStartStopTickDistance.getValue());
                EntityStartStopTickDistance.setTextColor(0xFFFFFFFF);
            }catch (Exception e){
                EntityStartStopTickDistance.setTextColor(rgb);
            }
        }
        if (EntityTickScaling.getValue() != null) {
            try {
                Config.EntityTickScaling = Float.parseFloat(EntityTickScaling.getValue());
                EntityTickScaling.setTextColor(0xFFFFFFFF);
            }catch (Exception e){
                EntityTickScaling.setTextColor(rgb);
            }
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
