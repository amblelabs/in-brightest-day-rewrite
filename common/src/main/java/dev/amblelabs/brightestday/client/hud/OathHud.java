package dev.amblelabs.brightestday.client.hud;

import dev.amblelabs.brightestday.BrightestDay;
import dev.amblelabs.brightestday.client.networking.OathS2CPayload;
import dev.amblelabs.brightestday.platform.ClientServices;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.util.ARGB;
import net.minecraft.util.Mth;

// TODO make more ornate and not like an ugly childrens show animatic
public final class OathHud {
    private static final float FADE_SPEED = 0.15F;
    private static final float FILL_SPEED = 0.25F;
    private static final int METER_WIDTH = 182;
    private static final int METER_HEIGHT = 5;
    private static final int METER_BOTTOM_OFFSET = 29;
    private static final int GUIDE_OFFSET = 10;
    private static final int LINE_HEIGHT = 10;
    private static final int UNSPOKEN = 0xFFB0B0B0;
    private static final int METER_BACKGROUND = 0xFF101010;

    public static boolean showOathText = true;

    private static String[] lines = new String[0];
    private static int color;
    private static int reached;
    private static float target;
    private static float fill;
    private static float oFill;
    private static boolean active;
    private static float fade;
    private static float oFade;

    public static void init() {
        ClientServices.EVENTS.onClientDisconnect(() -> {
            active = false;
            fade = oFade = 0.0F;
        });
        ClientServices.EVENTS.onClientTick(_ -> {
            oFade = fade;
            fade = Mth.approach(fade, active ? 1.0F : 0.0F, FADE_SPEED);
            oFill = fill;
            fill += (target - fill) * FILL_SPEED;
        });
        ClientServices.EVENTS.registerHudAfterInfoBar(BrightestDay.id("oath"), OathHud::extract);
    }

    public static void handle(OathS2CPayload payload) {
        active = payload.active();
        if (!active) return;
        if (payload.reached() == 0 && payload.progress() <= 0.0F) fill = oFill = 0.0F;
        lines = payload.oath().split("\n");
        color = ARGB.opaque(payload.color());
        reached = payload.reached();
        target = payload.progress();
    }

    private static void extract(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker) {
        float partialTicks = deltaTracker.getGameTimeDeltaPartialTick(false);
        float alpha = Mth.lerp(partialTicks, oFade, fade);
        if (alpha <= 0.02F || lines.length == 0) return;

        if (showOathText) guide(graphics, alpha);
        else meter(graphics, Mth.lerp(partialTicks, oFill, fill), alpha);
    }

    private static void meter(GuiGraphicsExtractor graphics, float progress, float alpha) {
        int left = graphics.guiWidth() / 2 - METER_WIDTH / 2;
        int top = graphics.guiHeight() - METER_BOTTOM_OFFSET;
        int filled = Math.round(METER_WIDTH * Mth.clamp(progress, 0.0F, 1.0F));
        graphics.fill(left - 1, top - 1, left + METER_WIDTH + 1, top + METER_HEIGHT + 1, ARGB.multiplyAlpha(METER_BACKGROUND, alpha));
        graphics.fill(left, top, left + filled, top + METER_HEIGHT, ARGB.multiplyAlpha(color, alpha));
        graphics.fill(left, top, left + filled, top + 1, ARGB.multiplyAlpha(ARGB.srgbLerp(0.5F, color, 0xFFFFFFFF), alpha));
    }

    private static void guide(GuiGraphicsExtractor graphics, float alpha) {
        Font font = Minecraft.getInstance().font;
        int y = graphics.guiHeight() / 2 + GUIDE_OFFSET;
        int spoken = reached;
        for (String line : lines) {
            String trimmed = line.trim();
            int x = graphics.guiWidth() / 2 - font.width(trimmed) / 2;
            for (String word : trimmed.split("\\s+")) {
                int tint = spoken-- > 0 ? color : UNSPOKEN;
                graphics.text(font, word, x, y, ARGB.multiplyAlpha(tint, alpha), true);
                x += font.width(word + " ");
            }
            y += LINE_HEIGHT;
        }
    }

    private OathHud() {}
}
