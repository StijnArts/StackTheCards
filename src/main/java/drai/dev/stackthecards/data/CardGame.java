package drai.dev.stackthecards.data;

import java.util.*;

public class CardGame {
    private String gameId= "missing";
    public CardStackingDirection cardStackingDirection = CardStackingDirection.TOP;
    public float cardStackingDistance = 18F;
    public String getGameIdentifier() {
        return gameId;
    }
    public Map<String, CardSet> cardSets = new HashMap<>();
    public Map<String, CardData> cards = new HashMap<>();
    public String getGameId() {
        return gameId;
    }

    public CardStackingDirection getCardStackingDirection() {
        return cardStackingDirection;
    }

    public float getCardStackingDistance() {
        return cardStackingDistance;
    }

    public Map<String, CardData> getCards() {
        return cards;
    }

    public Map<String, CardSet> getCardSets() {
        return cardSets;
    }

    public CardSet getCardSet(String setId){
        return new CardSet();
    }


}
