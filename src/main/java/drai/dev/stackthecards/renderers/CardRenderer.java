package drai.dev.stackthecards.renderers;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.render.*;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.*;
import net.minecraft.item.*;

import java.util.*;
import java.util.stream.*;

@Environment(EnvType.CLIENT)
public class CardRenderer {
    TextureManager textureManager = getTextureManager();
    private final HashMap<CardData, CardTexture> cardTextures = new HashMap<>();
    private final HashMap<String, CardConnectionRenderAsset> connectionTextures = new HashMap<>();
    private HashMap<CardPack, CardTexture> cardPackTextures = new HashMap<>();

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, int light){
        CardGame cardGame = null;
        CardTexture texture = null;
        if(stack.isOf(Items.CARD)){
            var cardData = Card.getCardData(stack);
            cardGame = cardData.getCardSet().getCardGame();
            if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
            var isFlipped = Card.getIsFlipped(stack);
            if(CardConnection.hasConnectedCards(stack)){
                var connection = CardConnection.getConnection(stack);
                var containedCards = CardConnection.getConnectedCards(stack);
                if(connection==null) return;
                var connectionAsset = getConnectionTexture(connection, containedCards, isFlipped);
                for (var card: connectionAsset.getCards()) {
                    CardTexture.drawConnectedCard(matrices, vertexConsumers, light, card.cardTexture.getRenderLayer(), 0,
                        (int) (Card.getAttachedCards(stack).size()*-1+ card.layer*0.1), cardGame, card.xOffset, card.yOffset,card.connectionEntry.rotation);

                }
                double attachedCardsXOffset = cardGame.cardStackingDirection.xMod == 0 ? 0 : (cardGame.cardStackingDirection.xMod <0 ? connectionAsset.maxOffsetX : connectionAsset.minOffsetX);
                double attachedCardsYOffset = cardGame.cardStackingDirection.yMod == 0 ? 0 : (cardGame.cardStackingDirection.yMod <0 ? connectionAsset.maxOffsetY : connectionAsset.minOffsetY);
                if(!connection.isSingle) matrices.translate(attachedCardsXOffset*cardGame.cardStackingDirection.xMod, attachedCardsYOffset*cardGame.cardStackingDirection.yMod,0);
                drawAttachedCards(matrices, vertexConsumers, stack, light, isFlipped,cardGame, true);
            } else {
                var cardTexture = getCardTexture(cardData, isFlipped);
                CardTexture.draw(matrices, vertexConsumers, light, cardTexture.getRenderLayer(), 0, Card.getAttachedCards(stack).size()*-1, cardGame, 0,0);
                drawAttachedCards(matrices, vertexConsumers, stack, light, 1, isFlipped,cardGame);
            }
        } else if(stack.isOf(Items.CARD_PACK)){
            var cardPack = CardPack.getCardPack(stack);
            cardGame = CardGameRegistry.getCardGame(cardPack.getGameId());
            texture = getCardPackTexture(cardPack);
            if(texture == null || cardGame == null ) return;
            if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
            CardTexture.draw(matrices, vertexConsumers, light, texture.getRenderLayer(), 0, 0, 1, cardGame, 0, 0);
        }
    }

    private void drawAttachedCards(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, int light, boolean isFlipped, CardGame cardGame, boolean b) {
        var attachedCards= Card.getAttachedCards(stack);
        for (int i = 0; i < attachedCards.size(); i++) {
            var attachedCardData = Card.getCardData(Card.getAsItemStack(attachedCards.get(i)));
            CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(attachedCardData, isFlipped).getRenderLayer(), i, 1, 1, cardGame, 0, 0);
        }
    }

    public void draw(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack,int light, double scale){
        CardGame cardGame = null;
        CardTexture texture = null;
        if(stack.isOf(Items.CARD)){
            var cardData = Card.getCardData(stack);
            cardGame = cardData.getCardSet().getCardGame();
            texture = getCardTexture(cardData, false);
        } else if(stack.isOf(Items.CARD_PACK)){
            var cardPack = CardPack.getCardPack(stack);
            cardGame = CardGameRegistry.getCardGame(cardPack.getGameId());
            texture = getCardPackTexture(cardPack);
        }
        if(texture == null || cardGame == null ) return;
        if(textureManager == null) textureManager = MinecraftClient.getInstance().getTextureManager();
        CardTexture.draw(matrices, vertexConsumers, light, texture.getRenderLayer(), 0, Card.getAttachedCards(stack).size()*-1, scale, cardGame, 0, 0);
        drawAttachedCards(matrices, vertexConsumers, stack, light, scale, false, cardGame);
    }

    private CardTexture getCardPackTexture(CardPack cardPack) {
        var renderer = StackTheCardsClient.CARD_RENDERER;
        return renderer.cardPackTextures.compute(cardPack, ((cardData1, texture) -> Objects.requireNonNullElseGet(texture, () -> new CardTexture(cardData1))));
    }

    private static void drawAttachedCards(MatrixStack matrices, VertexConsumerProvider vertexConsumers, ItemStack stack, int light, double scale, boolean isFlipped, CardGame cardGame) {
        var attachedCards= Card.getAttachedCards(stack);
        for (int i = 0; i < attachedCards.size(); i++) {
            var attachedCardData = Card.getCardData(Card.getAsItemStack(attachedCards.get(i)));
            CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(attachedCardData, isFlipped).getRenderLayer(), i+1, 1, scale, cardGame, 0, 0);
        }
    }


    private CardConnectionRenderAsset getConnectionTexture(CardConnection connection, List<CardIdentifier> containedCards, boolean isFlipped) {
        var connectionId = getConnectionIdForTexture(connection, containedCards, isFlipped);
        return connectionTextures.compute(connectionId, ((cardData1, texture) -> Objects.requireNonNullElseGet(texture, () -> new CardConnectionRenderAsset(connection, containedCards, isFlipped))));
    }

    public static CardTexture getCardTexture(CardData cardData, boolean isFlipped) {
        if(isFlipped){
            cardData = cardData.getCardBackData();
        }
        var renderer = StackTheCardsClient.CARD_RENDERER;
        return renderer.cardTextures.compute(cardData, ((cardData1, texture) -> Objects.requireNonNullElseGet(texture, () -> new CardTexture(cardData1))));
    }

    private String getConnectionIdForTexture(CardConnection connection, List<CardIdentifier> containedCards, boolean isFlipped) {
        return connection.getCardGame().getGameId()+":"+connection.getConnectionId()+":"+
                containedCards.stream().map(CardIdentifier::forPrint).collect(Collectors.joining(",")) +":flipped="+isFlipped;
    }

    public void clearStateTextures() {
        for (CardTexture cardTexture : cardTextures.values()) {
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
