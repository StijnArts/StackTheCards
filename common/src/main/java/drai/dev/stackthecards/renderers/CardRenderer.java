package drai.dev.stackthecards.renderers;

import com.mojang.blaze3d.vertex.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.*;
import net.fabricmc.api.*;
import net.minecraft.client.*;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.*;
import net.minecraft.world.item.*;

import java.util.*;
import java.util.stream.*;

//@Environment(EnvType.CLIENT)
public class CardRenderer {
    TextureManager textureManager = getTextureManager();
    private final HashMap<CardData, CardTexture> cardTextures = new HashMap<>();
    private final HashMap<String, CardConnectionRenderAsset> connectionTextures = new HashMap<>();
    private HashMap<CardPack, CardTexture> cardPackTextures = new HashMap<>();

    public void draw(PoseStack poseStack, MultiBufferSource vertexConsumers, ItemStack stack, int light){
        CardGame cardGame = null;
        if(stack.is(StackTheCardsItems.CARD.get())){
            var cardData = Card.getCardData(stack);
            cardGame = cardData.getCardSet().getCardGame();
            if(textureManager == null) textureManager = Minecraft.getInstance().getTextureManager();
            var isFlipped = Card.getIsFlipped(stack);
            if(CardConnection.hasConnectedCards(stack)){
                var connection = CardConnection.getConnection(stack);
                if(connection==null) return;
                //TODO test this
                double maxOffsetX, maxOffsetY, minOffsetX, minOffsetY;

                var containedCards = CardConnection.getConnectedCards(stack);
                var connectionAsset = getConnectionTexture(connection, containedCards, isFlipped);

                maxOffsetX = connectionAsset.maxOffsetX;
                maxOffsetY = connectionAsset.maxOffsetY;
                minOffsetX = connectionAsset.minOffsetX;
                minOffsetY = connectionAsset.minOffsetY;
                if (connection.resultingCard != null) {

                    // Center of the combined connected cards
                    double centerX = -1 * (connectionAsset.centerX/2);
                    double centerY = -1 * (connectionAsset.centerY/2);

                    // Total size occupied by the connected cards
                    double widthScale = connectionAsset.width / 128;
                    double heightScale = connectionAsset.height / 128;

                    // Scale relative to a single card
                    float scale = (float) Math.max(widthScale, heightScale);

                    double attachedCardsXOffset = cardGame.cardStackingDirection.xMod == 0 ? 0 :
                            (cardGame.cardStackingDirection.xMod < 0 ? maxOffsetX : minOffsetX);
                    double attachedCardsYOffset = cardGame.cardStackingDirection.yMod == 0 ? 0 :
                            (cardGame.cardStackingDirection.yMod < 0 ? maxOffsetY : minOffsetY);
                    poseStack.translate(centerX + attachedCardsXOffset/2, centerY + attachedCardsYOffset/2,0);
                    poseStack.scale(scale, scale, 1.0f);

                    CardTexture.draw(
                            poseStack,
                            vertexConsumers,
                            light,
                            getCardTexture(connection.resultingCard, isFlipped).getRenderLayer(),
                            0,
                            Card.getAttachedCards(stack).size() * -1,
                            1,
                            cardGame,
                            0,
                            0
                    );

                    poseStack.scale(1.0f / scale, 1.0f / scale, 1.0f);
                } else {
                    for (int i = 0; i < connectionAsset.getCards().size(); i++) {
                        var card = connectionAsset.getCards().get(i);
                        CardTexture.drawConnectedCard(
                                poseStack,
                                vertexConsumers,
                                light,
                                card.cardTexture.getRenderLayer(),
                                0,
                                (int) (Card.getAttachedCards(stack).size() * -1 + card.layer * 0.1 + i * 0.1),
                                cardGame,
                                card.xOffset,
                                card.yOffset,
                                card.connectionEntry.rotation
                        );
                    }}
//end of new piece of code

                double attachedCardsXOffset = cardGame.cardStackingDirection.xMod == 0 ? 0 :
                        (cardGame.cardStackingDirection.xMod < 0 ? maxOffsetX : minOffsetX);
                double attachedCardsYOffset = cardGame.cardStackingDirection.yMod == 0 ? 0 :
                        (cardGame.cardStackingDirection.yMod < 0 ? maxOffsetY : minOffsetY);
                if(!connection.isSingle) poseStack.translate(attachedCardsXOffset*cardGame.cardStackingDirection.xMod, attachedCardsYOffset*cardGame.cardStackingDirection.yMod,0);

                drawAttachedCards(poseStack, vertexConsumers, stack, light, isFlipped, cardGame, true);
            } else {
                var cardTexture = getCardTexture(cardData, isFlipped);
                CardTexture.draw(poseStack, vertexConsumers, light, cardTexture.getRenderLayer(), 0, Card.getAttachedCards(stack).size()*-1, cardGame, 0,0);
                drawAttachedCards(poseStack, vertexConsumers, stack, light, 1, isFlipped,cardGame);
            }
        } else if(stack.is(StackTheCardsItems.CARD_PACK.get())){
            CardTexture texture = null;
            var cardPack = CardPack.getCardPack(stack);
            cardGame = CardGameRegistry.getCardGame(cardPack.getGameId());
            texture = getCardPackTexture(cardPack);
            if(texture == null || cardGame == null ) return;
            if(textureManager == null) textureManager = Minecraft.getInstance().getTextureManager();
            CardTexture.draw(poseStack, vertexConsumers, light, texture.getRenderLayer(), 0, 0, 1, cardGame, 0, 0);
        }
    }

    private void drawAttachedCards(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, int light, boolean isFlipped, CardGame cardGame, boolean b) {
        var attachedCards= Card.getAttachedCards(stack);
        for (int i = 0; i < attachedCards.size(); i++) {
            var attachedCardData = Card.getCardData(Card.getAsItemStack(attachedCards.get(i)));
            CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(attachedCardData, isFlipped).getRenderLayer(), i, 1, 1, cardGame, 0, 0);
        }
    }

    public void draw(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, int light, double scale){
        CardGame cardGame = null;
        CardTexture texture = null;
        if(stack.is(StackTheCardsItems.CARD.get())){
            var cardData = Card.getCardData(stack);
            cardGame = cardData.getCardSet().getCardGame();
            texture = getCardTexture(cardData, false);
        } else if(stack.is(StackTheCardsItems.CARD_PACK.get())){
            var cardPack = CardPack.getCardPack(stack);
            cardGame = CardGameRegistry.getCardGame(cardPack.getGameId());
            texture = getCardPackTexture(cardPack);
        }
        if(texture == null || cardGame == null ) return;
        if(textureManager == null) textureManager = Minecraft.getInstance().getTextureManager();
        CardTexture.draw(matrices, vertexConsumers, light, texture.getRenderLayer(), 0, Card.getAttachedCards(stack).size()*-1, scale, cardGame, 0, 0);
        drawAttachedCards(matrices, vertexConsumers, stack, light, scale, false, cardGame);
    }

    private CardTexture getCardPackTexture(CardPack cardPack) {
        var renderer = StackTheCardsClient.CARD_RENDERER;
        return renderer.cardPackTextures.compute(cardPack, (cardData1, texture) ->
                Objects.requireNonNullElseGet(texture, () -> new CardTexture(cardData1)));
    }

    private static void drawAttachedCards(PoseStack matrices, MultiBufferSource vertexConsumers, ItemStack stack, int light, double scale, boolean isFlipped, CardGame cardGame) {
        var attachedCards= Card.getAttachedCards(stack);
        for (int i = 0; i < attachedCards.size(); i++) {
            var attachedCardData = Card.getCardData(Card.getAsItemStack(attachedCards.get(i)));
            CardTexture.draw(matrices, vertexConsumers, light, getCardTexture(attachedCardData, isFlipped).getRenderLayer(), i+1, 1, scale, cardGame, 0, 0);
        }
    }


    private CardConnectionRenderAsset getConnectionTexture(CardConnection connection, List<CardIdentifier> containedCards, boolean isFlipped) {
        var connectionId = getConnectionIdForTexture(connection, containedCards, isFlipped);
        return connectionTextures.compute(connectionId,
                ((cardData1, texture) ->
                        Objects.requireNonNullElseGet(texture, () ->
                        {
//                            if(connection.resultingCard!=null) return new ResulingCardConnectionRenderAsset();
                            return new CardConnectionRenderAsset(connection, containedCards, isFlipped);
                        })
                )
        );
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
        return Minecraft.getInstance().getTextureManager();
    }

    public HashMap<CardData, CardTexture> getCardTextures() {
        return cardTextures;
    }
}
