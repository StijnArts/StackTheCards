package drai.dev.stackthecards.registry;

import drai.dev.stackthecards.data.*;

import java.util.*;

public class CardGameRegistry {
    public static CardData MISSING_CARD_DATA = new CardData();
    public static Map<String, CardGame> cardGames = new HashMap<>();

    public static Map<String, CardGame> getCardGames() {
        return cardGames;
    }

    public static CardGame getCardGame(String cardGameId){
        return new CardGame();
    }

    public static CardData getCardData(CardIdentifier cardIdentifier) {
        if(cardIdentifier == null /*|| !CardIdentifier.isValid(cardIdentifier)*/) return MISSING_CARD_DATA;
        if(cardIdentifier.ToList().stream().allMatch(id-> id.equals("missing"))){
            return MISSING_CARD_DATA;
        }

        var cardGame = getCardGame(cardIdentifier.gameId);
        var cardSet = cardGame.getCardSet(cardIdentifier.setId);

        return cardSet.getCardData(cardIdentifier.cardId);
    }
}
