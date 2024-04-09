package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.util.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.carddata.CardData.JSON_ROUNDED_CORNERS_ID_KEY;

public class CardGame {
    private static final String JSON_CARD_STACKING_KEY = "cardStackingDirection";
    private static final String JSON_CARD_STACKING_DISTANCE_KEY = "cardStackingDistance";
    public static final String JSON_GAME_ID_KEY = "gameId";
    public static final String JSON_GAME_CARD_BACK_ITEM_MODEL_KEY = "cardBackModel";
    public static final String JSON_GAME_CARD_BACK_CARD_KEY = "cardBackTextureName";
    public static final String JSON_GAME_CARD_PACK_ITEM_MODEL_KEY = "cardPackModel";
    public static final String JSON_GAME_CARD_PACK_IMAGE_KEY = "cardPackTextureName";
    public static final String JSON_GAME_SHOULD_APPLY_EFFECT_KEY = "appliesEffect";
    private static final CardRarity MISSING_RARITY = new CardRarity("missing");
    public Optional<Boolean> hasRoundedCorners = Optional.empty();
    public boolean appliesEffect = true;
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
    private Identifier cardPackModel;
    private String cardPackTextureName;
    private GameCardData cardPackData;
    private final Map<String, CardRarity> rarities = new HashMap<>();
    private String effectIdentifier;
    private String name;
    private Map<String, CardPack> parentPacks = new HashMap<>();

    public static CardGame parse(JSONObject json) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_GAME_ID_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        CardGame game;
        try{
            game = new CardGame((String) json.get(JSON_GAME_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_CARD_STACKING_KEY)){
            try{
                game.setCardStackingDirection(CardStackingDirection.valueOf((String) json.get(JSON_CARD_STACKING_KEY)));
            } catch (Exception e){
                throw new MalformedJsonException("Card game stacking direction was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey("name")){
            try{
                game.name = (String) json.get("name");
            } catch (Exception e){
                throw new MalformedJsonException("Card game name was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey("effect")){
            try{
                game.effectIdentifier = (String) json.get("effect");
            } catch (Exception e){
                throw new MalformedJsonException("Card game effect was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_STACKING_DISTANCE_KEY)){
            try{
                game.setCardStackingDistance((float) (double) json.get(JSON_CARD_STACKING_DISTANCE_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_ITEM_MODEL_KEY)){
            try{
                var identifierArray = ((String) json.get(JSON_GAME_CARD_BACK_ITEM_MODEL_KEY)).split(":");
                game.setCardBackModel(new Identifier(identifierArray[0], identifierArray[1]));
            } catch (Exception e){
                throw new MalformedJsonException("Card back identifier was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_CARD_KEY)){
            try{
                game.setCardBackTextureName((String) json.get(JSON_GAME_CARD_BACK_CARD_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card back cardId was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_PACK_ITEM_MODEL_KEY)){
            try{
                var identifierArray = ((String) json.get(JSON_GAME_CARD_PACK_ITEM_MODEL_KEY)).split(":");
                game.setCardPackItemModel(new Identifier(identifierArray[0], identifierArray[1]));
            } catch (Exception e){
                throw new MalformedJsonException("Card pack identifier was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_PACK_IMAGE_KEY)){
            try{
                game.setCardPackTextureName((String) json.get(JSON_GAME_CARD_PACK_IMAGE_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card pack image string was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_ROUNDED_CORNERS_ID_KEY)){
            try{
                game.hasRoundedCorners = Optional.of((boolean) json.get(JSON_ROUNDED_CORNERS_ID_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_SHOULD_APPLY_EFFECT_KEY)){
            try{
                game.appliesEffect = (boolean) json.get(JSON_GAME_SHOULD_APPLY_EFFECT_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        return game;
    }

    private void setCardPackTextureName(String s) {
        this.cardPackTextureName = s;
    }

    private void setCardPackItemModel(Identifier identifier) {
        this.cardPackModel = identifier;
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
        var foundSet = cardSets.get(setId);
        if(foundSet == null) return CardGameRegistry.MISSING_CARD_SET;
        return foundSet;
    }


    public Map<String, CardConnection> getConnections() {
        return cardConnections;
    }

    public List<CardConnection> getConnections(CardIdentifier cardIdentifier){
        var connections = cardConnections.values().stream()
                .filter(connection -> connection.getCardIdentifiers().stream().anyMatch(identifier -> identifier!=null && identifier.isEqual(cardIdentifier))).collect(Collectors.toList());
        return connections;
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

    public CardData getCardPackData() {
        if(this.cardPackData == null){
            this.cardPackData = new GameCardData(gameId, cardPackTextureName);
        }
        return cardPackData;
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

    public Map<String, CardPack> getCardPacks() {
        return cardPacks;
    }

    public Identifier getCardPackModel() {
        return cardPackModel;
    }

    public void addRarity(CardRarity rarity) {
        this.rarities.put(rarity.rarityId, rarity);
    }

    public CardRarity getRarity(String rarityId) {
        var rarity = rarities.get(rarityId);
        if(rarity == null) return MISSING_RARITY;
        return rarities.get(rarityId);
    }

    public String getEffectIdentifier() {
        return effectIdentifier;
    }

    public String getName() {
        return name;
    }

    public void addParentPacks(CardPack cardPack) {
        this.parentPacks.put(cardPack.getPackId(),cardPack);
    }

    public CardPack getParentPack(String s) {
        return parentPacks.get(s);
    }
}
