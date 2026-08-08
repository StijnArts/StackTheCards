package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;

import java.util.*;

public class CardGameRegistry {
    public static final CardSet MISSING_CARD_SET = new CardSet("missing");
    public static CardData MISSING_CARD_DATA = new CardData("missing", "stack_the_cards");
    public static CardPack MISSING_CARD_PACK = new CardPack("missing", "missing","missing", "stack_the_cards");
    public static CardGame MISSING_CARD_GAME = new CardGame("missing", "stack_the_cards");
    public static Map<String, CardGame> cardGames = new HashMap<>();

    public static Map<String, CardGame> getCardGames() {
        return cardGames;
    }

    public static CardGame getCardGame(String cardGameId){
        if(cardGameId == null || !cardGames.containsKey(cardGameId)) return MISSING_CARD_GAME;
        return cardGames.get(cardGameId);
    }

    public static CardData getCardData(CardIdentifier cardResourceLocation) {
        if(cardResourceLocation == null /*|| !CardIdentifier.isValid(cardResourceLocation)*/) return MISSING_CARD_DATA;
        if(cardResourceLocation.ToList().stream().allMatch(id-> id.equals("missing"))){
            return MISSING_CARD_DATA;
        }
        var cardData = MISSING_CARD_DATA;
        var cardGame = getCardGame(cardResourceLocation.gameId);
        var gameCard = cardGame.getCards().get(cardResourceLocation.cardId);
        if(gameCard != null) cardData = gameCard;
        var cardSet = cardGame.getCardSet(cardResourceLocation.setId);
        if(cardSet != null) cardData = cardSet.getCardData(cardResourceLocation.cardId);
        if(cardData!=MISSING_CARD_DATA) cardData.rarity = cardResourceLocation.rarityId;
        return cardData;
    }

    public static CardPack getPackData(CardIdentifier cardResourceLocation) {
        if(cardResourceLocation == null /*|| !CardIdentifier.isValid(cardResourceLocation)*/) return MISSING_CARD_PACK;
        if(cardResourceLocation.ToList().stream().allMatch(id-> id.equals("missing"))){
            return MISSING_CARD_PACK;
        }

        var cardGame = getCardGame(cardResourceLocation.gameId);
        var gameCard = cardGame.getCardPacks().get(cardResourceLocation.cardId);
        if(gameCard != null) return gameCard;
        var cardSet = cardGame.getCardSet(cardResourceLocation.setId);
        if(cardSet != null) return cardSet.getCardPack(cardResourceLocation.cardId);
        return MISSING_CARD_PACK;
    }

    public static void clear() {
        cardGames.clear();
    }

    public static void registerGame(CardGame cardGame) {
        cardGames.put(cardGame.getGameId(), cardGame);
    }

    public static RegistryFriendlyByteBuf toBuffer(RegistryFriendlyByteBuf registryFriendlyByteBuf) {
        SYNC_CODEC.encode(registryFriendlyByteBuf, new CardGameRegistryMessage(cardGames));
        return registryFriendlyByteBuf;
    }

    public static void fromBuffer(RegistryFriendlyByteBuf buf) {
        var message = SYNC_CODEC.decode(buf);
        cardGames = message.cardGames;
        relinkObjects();
    }

    public static final StreamCodec<FriendlyByteBuf, CardGameRegistryMessage> SYNC_CODEC = StreamCodec.composite(
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardGame.SYNC_CODEC), CardGameRegistryMessage::getCardGames,
            CardGameRegistryMessage::new);

    private static void relinkObjects() {
        cardGames.forEach((key, value) -> {
            value.getParentPacks().values().forEach(pack -> pack.relink(value));
            value.getCardSets().values().forEach(cardSet -> cardSet.relink(value));
            value.getCardPacks().values().forEach(pack -> pack.relink(value));
        });
    }

    public static class CardGameRegistryMessage {

        Map<String, CardGame> cardGames = new HashMap<>();

        public CardGameRegistryMessage(Map<String, CardGame> cardGames) {
            this.cardGames = cardGames;
        }

        public Map<String, CardGame> getCardGames() {
            return cardGames;
        }
    }
}


