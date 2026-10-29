package dev.amble.brightestday.client.screens;

import dev.amble.brightestday.client.networking.UpdateSuitPayload;
import dev.amble.brightestday.client.screens.buttons.ButtonScrollList;
import dev.amble.brightestday.core.BrightestDayAttachments;
import dev.amble.brightestday.core.BrightestDayRegistries;
import dev.amble.brightestday.platform.Services;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class SuitSelectorScreen extends Screen {

    private ButtonScrollList suitScrollList;

    public SuitSelectorScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        int listWidth = 200;
        int listHeight = 160;
        int topY = 40;
        int rowHeight = 24;

        this.suitScrollList = new ButtonScrollList(this.minecraft, listWidth, listHeight, topY, rowHeight);

        this.suitScrollList.setX((this.width - listWidth) / 2);

        Player player = this.minecraft.player;
        if (player == null) return;

        player.registryAccess().lookupOrThrow(BrightestDayRegistries.SUIT).stream().forEach(suit -> {
            Button btn = Button.builder(Component.literal(suit.id().toShortString()), button -> {

                if (!BrightestDayAttachments.SUIT.get(player).equals(suit.id())) {
                    Services.PLATFORM.sendToServer(new UpdateSuitPayload(suit));
                }
            }).bounds(0, 0, listWidth, 20).build();

            this.suitScrollList.addButtonNode(new ButtonScrollList.ButtonEntry(btn));
        });

        this.addRenderableWidget(suitScrollList);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        graphics.text(this.font, "Special Button", 40, 40 - this.font.lineHeight - 10, 0xFFFFFFFF, true);
    }
}
