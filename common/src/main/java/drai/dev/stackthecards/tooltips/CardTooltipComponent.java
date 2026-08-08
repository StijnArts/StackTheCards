package drai.dev.stackthecards.tooltips;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.renderers.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;
import org.joml.*;

import java.lang.Math;

public class CardTooltipComponent implements ClientTooltipComponent, TooltipComponent {
    private CardTooltipData cardTooltipData;
    private CardTooltipRenderer cardTooltipRenderer;

    public CardTooltipComponent(CardTooltipData cardTooltipData) {
        this.cardTooltipData = cardTooltipData;
        cardTooltipRenderer = StackTheCardsClient.CARD_TOOLTIP_RENDERER;
    }

    @Override
    public int getHeight() {
//        return Math.max(cardPreviewSize, 0);
        return 0;
    }

    @Override
    public int getWidth(Font font) {
        //        return Math.max(cardPreviewSize, 0);
        return 0;
    }

    @Override
    public void renderText(Font textRenderer, int x, int y, Matrix4f matrix, MultiBufferSource.BufferSource vertexConsumers) {
        ClientTooltipComponent.super.renderText(textRenderer, x, y, matrix, vertexConsumers);
//        drawAt(cardTooltipData.getCard(),x, y, context, );
    }

    @Override
    public void renderImage(Font textRenderer, int x, int y, GuiGraphics context) {
        //TooltipComponent.super.drawItems(textRenderer, x, y, context);
        var stack = cardTooltipData.getStack();
        var cardData = Card.getCardData(stack);
        double offsetScale = getCardPreviewSize() / (double) cardData.getMaxSide();
        drawAt(stack, x, y, context, offsetScale);
    }

    public void drawItemsWithTooltipPosition(Font textRenderer, int x, int y, GuiGraphics context,
                                             int tooltipTopY, int tooltipBottomY, int mouseX, int mouseY) {
        var stack = cardTooltipData.getStack();
        var cardData = Card.getCardData(stack);
        cardData.getHeight();
        double offsetScale = getCardPreviewSize() / (double) cardData.getMaxSide();
        int h = (int) ((cardData.getHeight()) * offsetScale)+3;
        int w = (int) ((cardData.getWidth()) * offsetScale);
        int screenW = context.guiWidth();
        int screenH = context.guiHeight();

        x = Math.min(x - 4, screenW - w)+4;
        y = tooltipBottomY+4;
        if(y + h > screenH) {
            y = tooltipTopY - h - 2;
            if(y < 0){
                y = tooltipTopY + 3;
                x = x - w -8;
            }
        }
        drawAt(stack, x, Math.max(y,5), context, offsetScale);
    }

    private double getCardPreviewSize() {
        double cardPreviewSizeSmall = 58;
        double cardPreviewSizeLarge = 80;
        return StackTheCardsClient.cardLoreKeyPressed ? cardPreviewSizeLarge : cardPreviewSizeSmall;
    }



    public double getCardScale() {
        return 0.007413793103 * getCardPreviewSize();
    }

    private void drawAt(ItemStack stack, int x, int y, GuiGraphics context, double offsetScale) {
        this.cardTooltipRenderer.draw(stack, x, y, context, getCardPreviewSize(), offsetScale, getCardScale());
    }

    public ClientTooltipComponent toClientToolTipComponent() {
        return this;
    }
}
