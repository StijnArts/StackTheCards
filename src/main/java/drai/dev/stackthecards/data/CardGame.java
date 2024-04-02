package drai.dev.stackthecards.data;

import com.ibm.icu.impl.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.util.math.*;

import java.util.*;
import java.util.stream.*;

public class CardGame {
    private String gameId= "missing";
    public CardStackingDirection cardStackingDirection = CardStackingDirection.TOP;
    public float cardStackingDistance = 18F;
    public String getGameIdentifier() {
        return gameId;
    }
    public Map<String, CardSet> cardSets = new HashMap<>();
    public Map<String, CardConnection> cardConnections = new HashMap<>();
    public Map<String, CardData> cards = new HashMap<>();

    public CardGame() {
        var layout = new ArrayList<List<CardConnectionEntry>>();
        layout.add(List.of(new CardConnectionEntry(CardSet.hoohTop.getCardIdentifier(),0,0, 0, CardConnectingDirection.BOTTOM, CardRotation.LEFT)));

//        layout.add(List.of(new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT),
//                new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
        layout.add(List.of(new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
//        layout.add(List.of(new CardConnectionEntry(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
//        layout.add(List.of());
        var connection = new CardConnection("hooh_connection", this, layout);
        cardConnections.put(connection.getConnectionId(), connection);

        var layout2 = new ArrayList<List<CardConnectionEntry>>();
        layout2.add(List.of(new CardConnectionEntry(CardSet.hoohTop.getCardIdentifier(),0,0, 0, CardConnectingDirection.BOTTOM, CardRotation.LEFT)));
        layout2.add(List.of(new CardConnectionEntry(CardSet.charizard.getCardIdentifier(),0,0, 0, CardConnectingDirection.BOTTOM, CardRotation.LEFT)));
//        layout.add(List.of(new CardConnectionEntry(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
        layout2.add(List.of(new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
        var connection2 = new CardConnection("hooh_connection2", this, layout2);
        cardConnections.put(connection2.getConnectionId(), connection2);
    }

    public CardGame(String gameId, CardStackingDirection cardStackingDirection, float cardStackingDistance, Map<String, CardSet> cardSets, Map<String, CardConnection> cardConnections, Map<String, CardData> cards) {
        this.gameId = gameId;
        this.cardStackingDirection = cardStackingDirection;
        this.cardStackingDistance = cardStackingDistance;
        this.cardSets = cardSets;
        this.cardConnections = cardConnections;
        this.cards = cards;
    }

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


    public Map<String, CardConnection> getConnections() {
        return cardConnections;
    }

    public List<CardConnection> getConnections(CardIdentifier cardIdentifier){
        var connections = cardConnections.values().stream()
                .filter(connection -> connection.getCardIdentifiers().stream().anyMatch(identifier -> identifier!=null && identifier.isEqual(cardIdentifier))).collect(Collectors.toList());
        return connections;
    }
}
