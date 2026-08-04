package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import joptsimple.internal.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import com.google.gson.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.carddata.CardData.*;

public class CardGame {
    private static final String Json_CARD_STACKING_KEY = "cardStackingDirection";
    private static final String Json_CARD_STACKING_DISTANCE_KEY = "cardStackingDistance";
    public static final String Json_GAME_ID_KEY = "gameId";
    public static final String Json_GAME_CARD_BACK_ITEM_MODEL_KEY = "cardBackModel";
    public static final String Json_GAME_CARD_BACK_CARD_KEY = "cardBackTextureName";
    public static final String Json_GAME_CARD_PACK_ITEM_MODEL_KEY = "cardPackModel";
    public static final String Json_GAME_CARD_PACK_IMAGE_KEY = "cardPackTextureName";
    public static final String Json_GAME_SHOULD_APPLY_EFFECT_KEY = "appliesEffect";
    private static final CardRarity MISSING_RARITY = new CardRarity("missing");
    public boolean hasRoundedCorners = false;
    public boolean appliesEffect = true;
    public String gameId;
    public CardStackingDirection cardStackingDirection = CardStackingDirection.TOP;
    public float cardStackingDistance = 18F;
    public String cardBackModel;
    public HashMap<String, CardSet> cardSets = new HashMap<>();
    public HashMap<String, CardConnection> cardConnections = new HashMap<>();
    public HashMap<String, CardData> cards = new HashMap<>();
    public HashMap<String, CardTextFormatting> formatting = new HashMap<>();
    public String cardBackTextureName = Strings.EMPTY;
    public HashMap<String, CardPack> cardPacks = new HashMap<>();
    public GameCardData cardBackData;
    public String cardPackModel;
    public String cardPackTextureName = Strings.EMPTY;
    public GameCardData cardPackData;
    public HashMap<String, CardRarity> rarities = new HashMap<>();
    public String effectResourceLocation;
    public String name = Strings.EMPTY;
    public HashMap<String, CardPack> parentPacks = new HashMap<>();
    public String nameSpace = Strings.EMPTY;

    public static final StreamCodec<FriendlyByteBuf, CardGame> SYNC_CODEC = new StreamCodec<FriendlyByteBuf, CardGame>() {
        @Override
        public void encode(FriendlyByteBuf buffer, CardGame value) {
            ByteBufCodecs.BOOL.encode(buffer, value.hasRoundedCorners);
            ByteBufCodecs.BOOL.encode(buffer, value.appliesEffect);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.gameId);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.name);
            CardStackingDirection.STREAM_CODEC.encode(buffer, value.cardStackingDirection);
            ByteBufCodecs.FLOAT.encode(buffer, value.cardStackingDistance);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.cardBackModel);

            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardSet.SYNC_CODEC)
                    .encode(buffer, value.cardSets);
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardConnection.SYNC_CODEC)
                    .encode(buffer, value.cardConnections);
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardData.SYNC_CODEC)
                    .encode(buffer, value.cards);
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardTextFormatting.SYNC_CODEC)
                    .encode(buffer, value.formatting);

            ByteBufCodecs.STRING_UTF8.encode(buffer, value.cardBackTextureName);

            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .encode(buffer, value.cardPacks);

            ByteBufCodecs.optional(CardData.SYNC_CODEC)
                    .encode(buffer, Optional.ofNullable(value.cardBackData)); // Nullable

            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.ofNullable(value.cardPackModel));
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.cardPackTextureName);

            ByteBufCodecs.optional(CardData.SYNC_CODEC)
                    .encode(buffer, Optional.ofNullable(value.cardPackData)); // Nullable

            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardRarity.SYNC_CODEC)
                    .encode(buffer, value.rarities);
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.ofNullable(value.effectResourceLocation));
            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .encode(buffer, value.parentPacks);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.nameSpace);
        }

        @Override
        public CardGame decode(FriendlyByteBuf buffer) {
            boolean hasRoundedCorners = ByteBufCodecs.BOOL.decode(buffer);
            boolean appliesEffect = ByteBufCodecs.BOOL.decode(buffer);
            String gameId = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String name = ByteBufCodecs.STRING_UTF8.decode(buffer);
            CardStackingDirection cardStackingDirection = CardStackingDirection.STREAM_CODEC.decode(buffer);
            float cardStackingDistance = ByteBufCodecs.FLOAT.decode(buffer);
            String cardBackModel = ByteBufCodecs.STRING_UTF8.decode(buffer);

            HashMap<String, CardSet> cardSets = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardSet.SYNC_CODEC)
                    .decode(buffer);
            HashMap<String, CardConnection> cardConnections = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardConnection.SYNC_CODEC)
                    .decode(buffer);
            HashMap<String, CardData> cards = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardData.SYNC_CODEC)
                    .decode(buffer);
            HashMap<String, CardTextFormatting> formatting = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardTextFormatting.SYNC_CODEC)
                    .decode(buffer);

            String cardBackTextureName = ByteBufCodecs.STRING_UTF8.decode(buffer);

            HashMap<String, CardPack> cardPacks = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .decode(buffer);

            GameCardData cardBackData = (GameCardData) ByteBufCodecs.optional(CardData.SYNC_CODEC).decode(buffer).orElse(null);

            String cardPackModel = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buffer).orElse(null);
            String cardPackTextureName = ByteBufCodecs.STRING_UTF8.decode(buffer);

            GameCardData cardPackData = (GameCardData) ByteBufCodecs.optional(CardData.SYNC_CODEC).decode(buffer).orElse(null);

            HashMap<String, CardRarity> rarities = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardRarity.SYNC_CODEC)
                    .decode(buffer);
            String effectResourceLocation = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buffer).orElse(null);
            HashMap<String, CardPack> parentPacks = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .decode(buffer);
            String nameSpace = ByteBufCodecs.STRING_UTF8.decode(buffer);

            return new CardGame(hasRoundedCorners, appliesEffect, gameId, cardStackingDirection, cardStackingDistance,
                    cardBackModel, cardSets, cardConnections, cards, formatting,
                    cardBackTextureName, cardPacks, cardBackData, cardPackModel,
                    cardPackTextureName, cardPackData, rarities, effectResourceLocation,
                    name, parentPacks, nameSpace);
        }
    };

    public CardGame(boolean hasRoundedCorners, boolean appliesEffect, String gameId, CardStackingDirection cardStackingDirection,
                    float cardStackingDistance, String cardBackModel, HashMap<String, CardSet> cardSets,
                    HashMap<String, CardConnection> cardConnections,
                    HashMap<String, CardData> cards, HashMap<String, CardTextFormatting> formatting,
                    String cardBackTextureName, HashMap<String, CardPack> cardPacks, GameCardData cardBackData, String cardPackModel,
                    String cardPackTextureName, GameCardData cardPackData, HashMap<String, CardRarity> rarities, String effectResourceLocation,
                    String name, HashMap<String, CardPack> parentPacks, String nameSpace) {
        this.hasRoundedCorners = hasRoundedCorners;
        this.appliesEffect = appliesEffect;
        this.gameId = gameId;
        this.cardStackingDirection = cardStackingDirection;
        this.cardStackingDistance = cardStackingDistance;
        this.cardBackModel = cardBackModel;
        this.cardSets = cardSets;
        this.cardConnections = cardConnections;
        this.cards = cards;
        this.formatting = formatting;
        this.cardBackTextureName = cardBackTextureName;
        this.cardPacks = cardPacks;
        this.cardBackData = cardBackData;
        this.cardPackModel = cardPackModel;
        this.cardPackTextureName = cardPackTextureName;
        this.cardPackData = cardPackData;
        this.rarities = rarities;
        this.effectResourceLocation = effectResourceLocation;
        this.name = name;
        this.parentPacks = parentPacks;
        this.nameSpace = nameSpace;
    }

    public static CardGame parse(JsonObject json, String nameSpace) throws MalformedJsonException {
        if(json.isEmpty() || !json.has(Json_GAME_ID_KEY)) throw new MalformedJsonException("Card Game Json was empty or missing Game Id");
        CardGame game;
        try{
            game = new CardGame(json.get(Json_GAME_ID_KEY).getAsString(),nameSpace);
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
        }
        if(json.has(Json_CARD_STACKING_KEY)){
            try{
                game.setCardStackingDirection(CardStackingDirection.valueOf(json.get(Json_CARD_STACKING_KEY).getAsString()));
            } catch (Exception e){
                throw new MalformedJsonException("Card game stacking direction was malformed: "+e.getMessage());
            }
        }
        if(json.has("name")){
            try{
                game.name = json.get("name").getAsString();
            } catch (Exception e){
                throw new MalformedJsonException("Card game name was malformed: "+e.getMessage());
            }
        }
        if(json.has("effect")){
            try{
                game.effectResourceLocation = json.get("effect").getAsString();
            } catch (Exception e){
                throw new MalformedJsonException("Card game effect was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_CARD_STACKING_DISTANCE_KEY)){
            try{
                game.setCardStackingDistance(json.get(Json_CARD_STACKING_DISTANCE_KEY).getAsFloat());
            } catch (Exception e){
                throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_GAME_CARD_BACK_ITEM_MODEL_KEY)){
            try{
                var identifierArray = (json.get(Json_GAME_CARD_BACK_ITEM_MODEL_KEY).getAsString()).split(":");
                game.setCardBackModel(ResourceLocation.fromNamespaceAndPath(identifierArray[0], identifierArray[1]));
            } catch (Exception e){
                throw new MalformedJsonException("Card back identifier was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_GAME_CARD_BACK_CARD_KEY)){
            try{
                game.setCardBackTextureName(json.get(Json_GAME_CARD_BACK_CARD_KEY).getAsString());
            } catch (Exception e){
                throw new MalformedJsonException("Card back cardId was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_GAME_CARD_PACK_ITEM_MODEL_KEY)){
            try{
                var identifierArray = (json.get(Json_GAME_CARD_PACK_ITEM_MODEL_KEY).getAsString()).split(":");
                game.setCardPackItemModel(ResourceLocation.fromNamespaceAndPath(identifierArray[0], identifierArray[1]));
            } catch (Exception e){
                throw new MalformedJsonException("Card pack identifier was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_GAME_CARD_PACK_IMAGE_KEY)){
            try{
                game.setCardPackTextureName(json.get(Json_GAME_CARD_PACK_IMAGE_KEY).getAsString());
            } catch (Exception e){
                throw new MalformedJsonException("Card pack image string was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_ROUNDED_CORNERS_ID_KEY)){
            try{
                game.hasRoundedCorners = json.get(Json_ROUNDED_CORNERS_ID_KEY).getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_GAME_SHOULD_APPLY_EFFECT_KEY)){
            try{
                game.appliesEffect =  json.get(Json_GAME_SHOULD_APPLY_EFFECT_KEY).getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        return game;
    }

    private void setCardPackTextureName(String s) {
        this.cardPackTextureName = s;
    }

    private void setCardPackItemModel(ResourceLocation identifier) {
        this.cardPackModel = identifier.toString();
    }

    public CardGame(String gameId, String nameSpace){/*, CardStackingDirection cardStackingDirection, float cardStackingDistance) {*/
        this.gameId = gameId;
//        addTestConnections();
        this.nameSpace = nameSpace;
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

    public List<CardConnection> getConnections(CardIdentifier cardResourceLocation){
        var connections = cardConnections.values().stream()
                .filter(connection -> connection.getCardIdentifiers().stream().anyMatch(identifier -> identifier!=null && identifier.equals(cardResourceLocation))).collect(Collectors.toList());
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

    public ResourceLocation getCardBackModel() {
        if(cardBackModel == null || cardBackModel.isEmpty()) return null;
        return ResourceLocation.parse(cardBackModel);
    }

    public CardData getCardBackData() {
        if(this.cardBackData == null){
            this.cardBackData = new GameCardData(gameId, cardBackTextureName, nameSpace);
        }
        return cardBackData;
    }

    public void setCardBackModel(ResourceLocation cardBackModel) {
        this.cardBackModel = cardBackModel.toString();
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

    public ModelResourceLocation getCardPackModel() {
        if(cardPackModel == null || cardPackModel.isEmpty()) return null;
        return new ModelResourceLocation(ResourceLocation.parse(cardPackModel), "");
    }

    public void addRarity(CardRarity rarity) {
        this.rarities.put(rarity.rarityId, rarity);
    }

    public CardRarity getRarity(String rarityId) {
        var rarity = rarities.get(rarityId);
        if(rarity == null) return MISSING_RARITY;
        return rarities.get(rarityId);
    }

    public String getEffectResourceLocation() {
        return effectResourceLocation;
    }

    public void addParentPacks(CardPack cardPack) {
        this.parentPacks.put(cardPack.getPackId(),cardPack);
    }

    public CardPack getParentPack(String s) {
        return parentPacks.get(s);
    }

    public List<CardConnection> getSingleConnections() {
        return cardConnections.values().stream().filter(connection -> connection.isSingle).collect(Collectors.toList());
    }

    public boolean hasRoundedCorners() {
        return hasRoundedCorners;
    }

    public boolean appliesEffect() {
        return appliesEffect;
    }

    public CardStackingDirection getCardStackingDirection() {
        return cardStackingDirection;
    }

    public float getCardStackingDistance() {
        return cardStackingDistance;
    }

    public Map<String, CardConnection> getCardConnections() {
        return cardConnections;
    }

    public Map<String, CardTextFormatting> getFormatting() {
        return formatting;
    }

    public String getCardPackTextureName() {
        return cardPackTextureName;
    }

    public GameCardData getCardPackData() {
        return cardPackData;
    }

    public Map<String, CardRarity> getRarities() {
        return rarities;
    }

    public Map<String, CardPack> getParentPacks() {
        return parentPacks;
    }

    public String getNameSpace() {
        return nameSpace;
    }

    public String getName() {
        return name;
    }
}
