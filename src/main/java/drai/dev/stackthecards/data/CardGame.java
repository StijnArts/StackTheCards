package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import com.ibm.icu.impl.*;
import drai.dev.stackthecards.data.cardData.*;
import net.minecraft.util.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.cardData.CardData.JSON_ROUNDED_CORNERS_ID_KEY;

public class CardGame {
    private static final String JSON_CARD_STACKING_KEY = "cardStackingDirection";
    private static final String JSON_CARD_STACKING_DISTANCE_KEY = "cardStackingDistance";
    public static final String JSON_GAME_ID_KEY = "gameId";
    public static final String JSON_GAME_CARD_BACK_ITEM_MODEL_KEY = "cardBackModel";
    public static final String JSON_GAME_CARD_BACK_CARD_KEY = "cardBackTextureName";
    public Optional<Boolean> hasRoundedCorners = Optional.empty();
    private String gameId;
    public CardStackingDirection cardStackingDirection = CardStackingDirection.TOP;
    public float cardStackingDistance = 18F;
    private Identifier cardBackModel;
    public Map<String, CardSet> cardSets = new HashMap<>();
    public Map<String, CardConnection> cardConnections = new HashMap<>();
    public Map<String, CardData> cards = new HashMap<>();
    public Map<String, CardTextFormatting> formatting = new HashMap<>();
    private String cardBackTextureName;
    private final Map<String, CardPack> cardPacks = new HashMap<>();
    private GameCardData cardBackData;

    public static CardGame parse(JSONObject json) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_GAME_ID_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        CardGame game;
        try{
            game = new CardGame((String) json.get(JSON_GAME_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed");
        }
        if(json.containsKey(JSON_CARD_STACKING_KEY)){
            try{
                game.setCardStackingDirection(CardStackingDirection.valueOf((String) json.get(JSON_CARD_STACKING_KEY)));
            } catch (Exception e){
                throw new MalformedJsonException("Card game stacking direction was malformed");
            }
        }
        if(json.containsKey(JSON_CARD_STACKING_DISTANCE_KEY)){
            try{
                game.setCardStackingDistance((float) (double) json.get(JSON_CARD_STACKING_DISTANCE_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card game id was malformed");
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_ITEM_MODEL_KEY)){
            try{
                var identifierArray = ((String) json.get(JSON_GAME_CARD_BACK_ITEM_MODEL_KEY)).split(":");
                game.setCardBackModel(new Identifier(identifierArray[0], identifierArray[1]));
            } catch (Exception e){
                throw new MalformedJsonException("Card back identifier was malformed");
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_CARD_KEY)){
            try{
                game.setCardBackTextureName((String) json.get(JSON_GAME_CARD_BACK_CARD_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card back cardId was malformed");
            }
        }
        if(json.containsKey(JSON_ROUNDED_CORNERS_ID_KEY)){
            try{
                game.hasRoundedCorners = Optional.of((boolean) json.get(JSON_ROUNDED_CORNERS_ID_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed");
            }
        }
        return game;
    }
    public CardGame(String gameId){/*, CardStackingDirection cardStackingDirection, float cardStackingDistance) {*/
        this.gameId = gameId;
//        addTestConnections();
    }

    public String getGameId() {
        return gameId;
    }

    public Map<String, CardData> getCards() {
        return cards;
    }

    public Map<String, CardSet> getCardSets() {
        return cardSets;
    }

    public CardSet getCardSet(String setId){
        return cardSets.get(setId);
    }


    public Map<String, CardConnection> getConnections() {
        return cardConnections;
    }

    public List<CardConnection> getConnections(CardIdentifier cardIdentifier){
        var connections = cardConnections.values().stream()
                .filter(connection -> connection.getCardIdentifiers().stream().anyMatch(identifier -> identifier!=null && identifier.isEqual(cardIdentifier))).collect(Collectors.toList());
        return connections;
    }

    private void addTestConnections() {
        var layout = new ArrayList<List<CardConnectionEntry>>();
        layout.add(List.of(new CardConnectionEntry(CardSet.hoohTop.getCardIdentifier(),0,0, 0, CardConnectingDirection.BOTTOM, CardRotation.LEFT)));

//        layout.add(List.of(new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT),
//                new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
        layout.add(List.of(new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
//        layout.add(List.of(new CardConnectionEntry(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
//        layout.add(List.of());
        var connection = new CardConnection("hooh_connection", gameId, layout);
        cardConnections.put(connection.getConnectionId(), connection);

        var layout2 = new ArrayList<List<CardConnectionEntry>>();
        layout2.add(List.of(new CardConnectionEntry(CardSet.hoohTop.getCardIdentifier(),0,0, 0, CardConnectingDirection.BOTTOM, CardRotation.LEFT)));
        layout2.add(List.of(new CardConnectionEntry(CardSet.charizard.getCardIdentifier(),0,0, 0, CardConnectingDirection.BOTTOM, CardRotation.LEFT)));
//        layout.add(List.of(new CardConnectionEntry(CardGameRegistry.MISSING_CARD_DATA.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
        layout2.add(List.of(new CardConnectionEntry(CardSet.hoohBottom.getCardIdentifier(),0,0, 0, CardConnectingDirection.TOP, CardRotation.LEFT)));
        var connection2 = new CardConnection("hooh_connection2", gameId, layout2);
        cardConnections.put(connection2.getConnectionId(), connection2);
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public void setCardStackingDirection(CardStackingDirection cardStackingDirection) {
        this.cardStackingDirection = cardStackingDirection;
    }

    public void setCardStackingDistance(float cardStackingDistance) {
        this.cardStackingDistance = cardStackingDistance;
    }

    public Identifier getCardBackModel() {
        return cardBackModel;
    }

    public CardData getCardBackData() {
        if(this.cardBackData == null){
            this.cardBackData = new GameCardData(gameId, cardBackTextureName);
        }
        return cardBackData;
    }

    public void setCardBackModel(Identifier cardBackModel) {
        this.cardBackModel = cardBackModel;
    }

    public String getCardBackTextureName() {
        return cardBackTextureName;
    }

    public void setCardBackTextureName(String cardBackTextureName) {
        this.cardBackTextureName = cardBackTextureName;
    }

    public void addSet(CardSet cardSet) {
        cardSets.put(cardSet.getSetId(), cardSet);
    }

    public void addCard(GameCardData cardData) {
        cards.put(cardData.getCardId(), cardData);
    }

    public void addConnection(CardConnection connection) {
        cardConnections.put(connection.getConnectionId(), connection);
    }

    public void addPack(CardPack cardPack) {
        cardPacks.put(cardPack.getPackId(), cardPack);
    }

    public void addFormatting(CardTextFormatting formatting) {
        this.formatting.put(formatting.formatId, formatting);
    }
}
