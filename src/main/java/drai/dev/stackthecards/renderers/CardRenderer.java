package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.joml.*;

import java.lang.Math;
import java.util.*;
@Environment(EnvType.CLIENT)
public class CardRenderer {
    TextureManager textureManager = getTextureManager();
    private final HashMap<CardData, CardTexture> cardTextures = new HashMap<>();

    public static CardTexture getCardTextureFromMap(CardData cardData) {
        var renderer = StackTheCardsClient.CARD_RENDERER;
        if(renderer.cardTextures.containsKey(cardData)){
            return renderer.cardTextures.get(cardData);
        }
        return null;
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, int light){
        var cardData = Card.getCardData(stack);
        var cardGame = cardData.getCardSet().getCardGame();
        if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
        var isFlipped = Card.getIsFlipped(stack);
        CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(cardData, isFlipped).getRenderLayer(), 0, Card.getAttachedCards(stack).size()*-1, cardGame);
        drawAttachedCards(matrices, vertexConsumers, stack, light, 1, isFlipped,cardGame);
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack,int light, double scale){
        var cardData = Card.getCardData(stack);
        var cardGame = cardData.getCardSet().getCardGame();
        if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
        CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(cardData, false).getRenderLayer(), 0, Card.getAttachedCards(stack).size()*-1, scale, cardGame);
        drawAttachedCards(matrices, vertexConsumers, stack, light, scale, false, cardGame);
        cardTextures.size();
    }

    private static void drawAttachedCards(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, int light, double scale, boolean isFlipped, CardGame cardGame) {
        var attachedCards= Card.getAttachedCards(stack);
        for (int i = 0; i < attachedCards.size(); i++) {
            var attachedCardData = Card.getCardData(Card.getAsItemStack(attachedCards.get(i)));
            CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(attachedCardData, isFlipped).getRenderLayer(), i+1, 1, scale, cardGame);
        }
    }

    public static CardTexture getCardTexture(CardData cardData, boolean isFlipped) {
        if(isFlipped){
            cardData = cardData.getCardSet().getCardBackData();
        }
        var renderer = StackTheCardsClient.CARD_RENDERER;
        return renderer.cardTextures.compute(cardData, ((cardData1, texture) -> Objects.requireNonNullElseGet(texture, () -> new CardTexture(cardData1))));
    }

    public void clearStateTextures() {
        for (CardTexture cardTexture :
                cardTextures.values()) {
            cardTexture.close();
        }
        cardTextures.clear();
    }

    public TextureManager getTextureManager() {
        return MinecraftClient.getInstance().getTextureManager();
    }

    public HashMap<CardData, CardTexture> getCardTextures() {
        return cardTextures;
    }
}
