package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import org.jetbrains.annotations.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardGame.*;
import static drai.dev.stackthecards.data.carddata.CardData.JSON_ROUNDED_CORNERS_ID_KEY;

public class CardSet {
    private static final String JSON_SET_ID_KEY = "setId";
    private static final String JSON_GAME_CARD_BACK_NAMESPACE_CARD_KEY = "cardBackTextureNameSpace";
    public String gameId;
    private final String setId;
    private final Map<String, CardData> cards = new HashMap<>();
    @Nullable
    public String cardBackTextureName;
    @Nullable
    private ResourceLocation cardBackModel;
    private final Map<String, CardPack> cardPacks = new HashMap<>();
    @Nullable
    private GameCardData cardBackData;
    public Optional<Boolean> hasRoundedCorners = Optional.empty();
    private String effectResourceLocation;
    private String name;
    public boolean appliesEffect = true;
    private Map<String, CardPack> parentPacks = new HashMap<>();
    private int ordering = 0;
    private String cardBackTextureNameSpace;

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
                cardSet.setCardBackModel(new ResourceLocation(identifierArray[0], identifierArray[1]));
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
                cardSet.hasRoundedCorners = Optional.of((boolean) json.get(JSON_ROUNDED_CORNERS_ID_KEY));
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
        this.cardBackModel = identifier;
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
        return cardBackModel;
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
}
