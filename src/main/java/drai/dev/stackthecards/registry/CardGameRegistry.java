package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;

import java.util.*;

public class CardGameRegistry {
    public static CardData MISSING_CARD_DATA = new CardData("missing");
    public static CardPack MISSING_CARD_PACK = new CardPack("missing", "missing","missing");
    public static Map<String, CardGame> cardGames = new HashMap<>();

    public static Map<String, CardGame> getCardGames() {
        return cardGames;
    }

    public static CardGame getCardGame(String cardGameId){
        if(cardGameId == null || !cardGames.containsKey(cardGameId)) return new CardGame("missing");
        return cardGames.get(cardGameId);
    }

    public static CardData getCardData(CardIdentifier cardIdentifier) {
        if(cardIdentifier == null /*|| !CardIdentifier.isValid(cardIdentifier)*/) return MISSING_CARD_DATA;
        if(cardIdentifier.ToList().stream().allMatch(id-> id.equals("missing"))){
            return MISSING_CARD_DATA;
        }
        var cardGame = getCardGame(cardIdentifier.gameId);
        var gameCard = cardGame.getCards().get(cardIdentifier.cardId);
        if(gameCard != null) return gameCard;
        var cardSet = cardGame.getCardSet(cardIdentifier.setId);
        if(cardSet != null) return cardSet.getCardData(cardIdentifier.cardId);
        return MISSING_CARD_DATA;
    }

    public static CardPack getPackData(CardIdentifier cardIdentifier) {
        if(cardIdentifier == null /*|| !CardIdentifier.isValid(cardIdentifier)*/) return MISSING_CARD_PACK;
        if(cardIdentifier.ToList().stream().allMatch(id-> id.equals("missing"))){
            return MISSING_CARD_PACK;
        }

        var cardGame = getCardGame(cardIdentifier.gameId);
        var gameCard = cardGame.getCardPacks().get(cardIdentifier.cardId);
        if(gameCard != null) return gameCard;
        var cardSet = cardGame.getCardSet(cardIdentifier.setId);
        if(cardSet != null) return cardSet.getCardPack(cardIdentifier.cardId);
        return MISSING_CARD_PACK;
    }

    public static void clear() {
        cardGames.clear();
    }

    public static void registerGame(CardGame cardGame) {
        cardGames.put(cardGame.getGameId(), cardGame);
    }
}
