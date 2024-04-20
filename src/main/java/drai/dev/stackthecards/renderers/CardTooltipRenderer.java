package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.tooltip.*;
import net.minecraft.item.*;

public class CardTooltipRenderer {
    public CardRenderer cardRenderer;
    public CardTooltipRenderer(CardRenderer cardRenderer) {
        this.cardRenderer = cardRenderer;
    }

    public void draw(ItemStack stack, int x, int y, DrawContext context,
                     double cardSpace, double offsetScale, double cardScale) {
//        if(!StackTheCardsClient.cardLoreKeyPressed){
            drawCardPreview(stack, x, y, context, cardSpace, offsetScale, cardScale);
//        }
    }

    private void drawCardPreview(ItemStack stack, int x, int y, DrawContext context, double cardSpace, double offsetScale, double cardScale) {
        var matrices = context.getMatrices();
        var cardData = Card.getCardData(stack);
        double yOffset = (cardData.getYOffset())*offsetScale;
        double xOffset = (cardData.getXOffset())*offsetScale;
        int borderSizeX = (int) (cardSpace-2*xOffset);
        int borderSizeY = (int) (cardSpace-2*yOffset);
        context.draw(()->TooltipBackgroundRenderer.render(context, x, y, borderSizeX, borderSizeY, 400));
        matrices.translate((float)(x -xOffset+(StackTheCardsClient.cardLoreKeyPressed ? 2F : 1.5F)), (float)(y -yOffset+(StackTheCardsClient.cardLoreKeyPressed ? 2F : 1.5F)), 500);
        cardRenderer.draw(matrices, context.getVertexConsumers(), stack, 15728880, cardScale);
    }
}
