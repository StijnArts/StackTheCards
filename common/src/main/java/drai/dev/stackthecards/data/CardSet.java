package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import joptsimple.internal.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import org.jetbrains.annotations.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardGame.*;
import static drai.dev.stackthecards.data.carddata.CardData.*;

public class CardSet {
    private static final String JSON_SET_ID_KEY = "setId";
    private static final String JSON_GAME_CARD_BACK_NAMESPACE_CARD_KEY = "cardBackTextureNameSpace";
    public String gameId;
    public final String setId;
    public HashMap<String, CardData> cards = new HashMap<>();
    @Nullable
    public String cardBackTextureName  = Strings.EMPTY;
    @Nullable
    public String cardBackModel = Strings.EMPTY;
    public HashMap<String, CardPack> cardPacks = new HashMap<>();
    @Nullable
    public GameCardData cardBackData;
    public boolean hasRoundedCorners = false;
    public String effectResourceLocation;
    public String name;
    public boolean appliesEffect = true;
    public HashMap<String, CardPack> parentPacks = new HashMap<>();
    public int ordering = 0;
    public String cardBackTextureNameSpace;

    public static final StreamCodec<FriendlyByteBuf, CardSet> SYNC_CODEC = new StreamCodec<>() {

        @Override
        public void encode(FriendlyByteBuf buffer, CardSet value) {
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.setId);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.gameId);

            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.ofNullable(value.cardBackTextureName));
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.ofNullable(value.cardBackModel));
            ByteBufCodecs.optional(CardData.SYNC_CODEC).encode(buffer, Optional.ofNullable(value.cardBackData));

            ByteBufCodecs.BOOL.encode(buffer, value.hasRoundedCorners);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.effectResourceLocation);
            ByteBufCodecs.STRING_UTF8.encode(buffer, value.name);
            ByteBufCodecs.BOOL.encode(buffer, value.appliesEffect);

            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .encode(buffer, value.parentPacks);

            ByteBufCodecs.INT.encode(buffer, value.ordering);
            ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).encode(buffer, Optional.ofNullable(value.cardBackTextureNameSpace));

            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .encode(buffer, value.cardPacks);

            ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardData.SYNC_CODEC)
                    .encode(buffer, value.cards);
        }

        @Override
        public CardSet decode(FriendlyByteBuf buffer) {
            String setId = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String gameId = ByteBufCodecs.STRING_UTF8.decode(buffer);

            String cardBackTextureName = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buffer).orElse(null);
            String cardBackModel = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buffer).orElse(null);
            CardData cardBackData = ByteBufCodecs.optional(CardData.SYNC_CODEC).decode(buffer).orElse(null);

            boolean hasRoundedCorners = ByteBufCodecs.BOOL.decode(buffer);
            String effectResourceLocation = ByteBufCodecs.STRING_UTF8.decode(buffer);
            String name = ByteBufCodecs.STRING_UTF8.decode(buffer);
            boolean appliesEffect = ByteBufCodecs.BOOL.decode(buffer);

            HashMap<String, CardPack> parentPacks = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .decode(buffer);

            int ordering = ByteBufCodecs.INT.decode(buffer);
            String cardBackTextureNameSpace = ByteBufCodecs.optional(ByteBufCodecs.STRING_UTF8).decode(buffer).orElse(null);

            HashMap<String, CardPack> packs = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardPack.SYNC_CODEC)
                    .decode(buffer);
            HashMap<String, CardData> cards = ByteBufCodecs.map(HashMap::new, ByteBufCodecs.STRING_UTF8, CardData.SYNC_CODEC)
                    .decode(buffer);

            return new CardSet(setId, gameId, cardBackTextureName, cardBackModel, (GameCardData) cardBackData,
                    hasRoundedCorners, effectResourceLocation, name, appliesEffect,
                    parentPacks, ordering, cardBackTextureNameSpace, packs, cards);
        }
    };

    public CardSet(String setId, String gameId, @Nullable String cardBackTextureName, @Nullable String cardBackModel,
                   @Nullable GameCardData cardBackData, boolean hasRoundedCorners, String effectResourceLocation,
                   String name, boolean appliesEffect, HashMap<String, CardPack> parentPacks, int ordering, String cardBackTextureNameSpace,
                   HashMap<String, CardPack> packs, HashMap<String, CardData> cards) {
        this.setId = setId;
        this.gameId = gameId;
        this.cardBackTextureName = cardBackTextureName;
        this.cardBackModel = cardBackModel;
        this.cardBackData = cardBackData;
        this.hasRoundedCorners = hasRoundedCorners;
        this.effectResourceLocation = effectResourceLocation;
        this.name = name;
        this.appliesEffect = appliesEffect;
        this.parentPacks = parentPacks;
        this.ordering = ordering;
        this.cardBackTextureNameSpace = cardBackTextureNameSpace;
        this.cardPacks = packs;
        this.cards = cards;
    }

    public CardSet(String cardSetId) {
        this.setId = cardSetId;
    }

    public static CardSet parse(JSONObject json) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_SET_ID_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        CardSet cardSet;
        try{
            cardSet = new CardSet((String) json.get(JSON_SET_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card game id was malformed: "+e.getMessage());
        }
        if(json.containsKey("name")){
            try{
                cardSet.name = (String) json.get("name");
            } catch (Exception e){
                throw new MalformedJsonException("Card set name was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_ITEM_MODEL_KEY)){
            try{
                var identifierArray = ((String) json.get(JSON_GAME_CARD_BACK_ITEM_MODEL_KEY)).split(":");
                cardSet.setCardBackModel(ResourceLocation.fromNamespaceAndPath(identifierArray[0], identifierArray[1]));
            } catch (Exception e){
                throw new MalformedJsonException("Card back identifier was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey("ordering")){
            try{
                cardSet.ordering = (int)(long) json.get("ordering");
            } catch (Exception e){
                throw new MalformedJsonException("Card pack name value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_CARD_KEY)){
            try{
                cardSet.setCardBackTextureName((String) json.get(JSON_GAME_CARD_BACK_CARD_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card back cardId was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_CARD_BACK_NAMESPACE_CARD_KEY)){
            try{
                cardSet.cardBackTextureNameSpace = (String) json.get(JSON_GAME_CARD_BACK_NAMESPACE_CARD_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card back namespace was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_ROUNDED_CORNERS_ID_KEY)){
            try{
                var hasRoundedCorners = (boolean) json.get(JSON_ROUNDED_CORNERS_ID_KEY);
                cardSet.hasRoundedCorners = hasRoundedCorners;
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey("effect")){
            try{
                cardSet.effectResourceLocation = (String) json.get("effect");
            } catch (Exception e){
                throw new MalformedJsonException("Card set effect was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GAME_SHOULD_APPLY_EFFECT_KEY)){
            try{
                cardSet.appliesEffect = (boolean) json.get(JSON_GAME_SHOULD_APPLY_EFFECT_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
            }
        }
        return cardSet;
    }

    private void setCardBackModel(ResourceLocation identifier) {
        this.cardBackModel = identifier.toString();
    }

    private void setCardBackTextureName(String cardBackTextureName) {
        this.cardBackTextureName = cardBackTextureName;
    }

    public CardGame getCardGame() {
        return CardGameRegistry.getCardGame(gameId);//cardGame;
    }

    public String getSetResourceLocation() {
        return getCardGame().getGameId() + "_" + setId;
    }

    public Map<String, CardData> getCards() {
        return cards;
    }

    public CardData getCardData(String cardId){
        if(cards.containsKey(cardId)){
            return cards.get(cardId);
        } else return CardGameRegistry.MISSING_CARD_DATA;
    }

    @Nullable
    public CardData getCardBackTextureName() {
        if(cardBackTextureName==null) return null;
        if(this.cardBackData == null){
            this.cardBackData = new GameCardData(gameId, cardBackTextureName, cardBackTextureNameSpace);
        }
        return cardBackData;
    }

    public ResourceLocation getCardBackModel() {
        if(cardBackModel==null || cardBackModel.isEmpty()) return null;
        return ResourceLocation.parse(cardBackModel);
    }

    public String getSetId() {
        return setId;
    }

    public int getOrdering() {
        return ordering;
    }
    public void addCard(CardData cardData) {
        cards.put(cardData.getCardId(), cardData);
    }

    public void addPack(CardPack cardPack) {
        cardPacks.put(cardPack.getPackId(), cardPack);
    }

    public void setGame(CardGame cardGame) {
        this.gameId = cardGame.getGameId();
    }

    public List<CardData> getDroppedCards() {
        return cards.values().stream().toList();
    }

    public CardPack getCardPack(String packId) {
        if(cardPacks.containsKey(packId)){
            return cardPacks.get(packId);
        } else
            return CardGameRegistry.MISSING_CARD_PACK;
    }

    public Map<String, CardPack> getCardPacks() {
        return cardPacks;
    }

    public String getEffectResourceLocation() {
        return effectResourceLocation == null ? "" : effectResourceLocation;
    }

    public String getName() {
        return name;
    }

    public void addParentPacks(CardPack cardPack) {
        this.parentPacks.put(cardPack.getPackId(), cardPack);
    }

    public CardPack getParentPack(String id) {
        return parentPacks.get(id);
    }

    public String getGameId() {
        return gameId;
    }

    public @Nullable GameCardData getCardBackData() {
        return cardBackData;
    }

    public boolean isHasRoundedCorners() {
        return hasRoundedCorners;
    }

    public boolean isAppliesEffect() {
        return appliesEffect;
    }

    public Map<String, CardPack> getParentPacks() {
        return parentPacks;
    }

    public String getCardBackTextureNameSpace() {
        return cardBackTextureNameSpace;
    }

    public void relink(CardGame value) {
        cards.values().forEach(cardData -> cardData.relink(value, this));
        cardPacks.values().forEach(cardData -> cardData.relink(value));
        parentPacks.values().forEach(cardData -> cardData.relink(value));
        if(cardBackData != null) cardBackData.relink(value, this);
    }
}
