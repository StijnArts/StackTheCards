package drai.dev.stackthecards.tooltips;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.renderers.*;
import net.minecraft.client.font.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.client.render.*;
import net.minecraft.item.*;
import org.joml.*;

import java.lang.*;
import java.lang.Math;

public class CardTooltipComponent implements TooltipComponent {
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
    public int getWidth(TextRenderer textRenderer) {
        //        return Math.max(cardPreviewSize, 0);
        return 0;
    }

    @Override
    public void drawText(TextRenderer textRenderer, int x, int y, Matrix4f matrix, VertexConsumerProvider.Immediate vertexConsumers) {
        TooltipComponent.super.drawText(textRenderer, x, y, matrix, vertexConsumers);
//        drawAt(cardTooltipData.getCard(),x, y, context, );
    }

    @Override
    public void drawItems(TextRenderer textRenderer, int x, int y, DrawContext context) {
        //TooltipComponent.super.drawItems(textRenderer, x, y, context);
        var stack = cardTooltipData.getStack();
        var cardData = Card.getCardData(stack);
        double offsetScale = getCardPreviewSize() / (double) cardData.getMaxSide();
        drawAt(stack, x, y, context, textRenderer, 0, 0, offsetScale);
    }

    public void drawItemsWithTooltipPosition(TextRenderer textRenderer, int x, int y, DrawContext context,
                                             int tooltipTopY, int tooltipBottomY, int mouseX, int mouseY) {
        var stack = cardTooltipData.getStack();
        var cardData = Card.getCardData(stack);
        cardData.getHeight();
        double offsetScale = getCardPreviewSize() / (double) cardData.getMaxSide();
        int h = (int) ((cardData.getHeight()) * offsetScale)+3;
        int w = (int) ((cardData.getWidth()) * offsetScale);
        int screenW = context.getScaledWindowWidth();
        int screenH = context.getScaledWindowHeight();

        x = Math.min(x - 4, screenW - w)+4;
        y = tooltipBottomY+4;
        if(y + h > screenH) {
            y = tooltipTopY - h - 2;
            if(y < 0){
                y = tooltipTopY + 3;
                x = x - w -7;
            }
        }
        drawAt(stack, x, y, context, textRenderer, mouseX, mouseY, offsetScale);
    }

    private double getCardPreviewSize() {
        double cardPreviewSizeSmall = 58;
        double cardPreviewSizeLarge = 80;
        return StackTheCardsClient.shiftKeyPressed ? cardPreviewSizeLarge : cardPreviewSizeSmall;
    }



    public double getCardScale() {
        return 0.007413793103 * getCardPreviewSize();
    }

    private void drawAt(ItemStack stack, int x, int y, DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY, double offsetScale) {
        this.cardTooltipRenderer.draw(stack, x, y, context, textRenderer, mouseX, mouseY, getCardPreviewSize(), offsetScale, getCardScale());
    }
}
