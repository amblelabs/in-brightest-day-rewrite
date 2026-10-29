package dev.amble.brightestday.client.screens.buttons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public class ButtonScrollList extends ObjectSelectionList<ButtonScrollList.ButtonEntry> {
    public ButtonScrollList(Minecraft minecraft, int width, int height, int y, int itemHeight) {
        super(minecraft, width, height, y, itemHeight);
    }

    public void addButtonNode(ButtonEntry entry) {
        this.addEntry(entry);
    }

    public static class ButtonEntry extends ObjectSelectionList.Entry<ButtonEntry> {

        private final Button button;

        public ButtonEntry(Button button) {
            this.button = button;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor graphics, int mouseX, int mouseY, boolean hovered, float a) {
            this.button.setPosition(this.getContentX(), this.getContentY());
            this.button.setWidth(this.getContentWidth());
            this.button.extractRenderState(graphics, mouseX, mouseY, a);
        }

        @Override
        public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
            return this.button.mouseClicked(event, doubleClick);
        }

        @Override
        public Component getNarration() {
            return this.button.getMessage();
        }
    }
}
