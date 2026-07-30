package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import com.google.gson.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardConnectionEntry.*;

public class CardPackPool {
    private static final String Json_POOL_MINIMUM_AMOUNT_KEY = "minimumCardsFromPool";
    private static final String Json_POOL_MAXIMUM_AMOUNT_KEY = "maximumCardsFromPool";
    private static final String Json_POOL_PULL_CHANCE_KEY = "poolPullChancePercent";
    private static final String Json_POOL_CARDS_KEY = "cards";
    private static final String Json_POOL_RARITIES_KEY = "rarities";
    private static final String Json_POOL_ITEMS_KEY = "items";
    public int minimumAmountOfCardsFromPool = 0;
    public int maximumAmountOfCardsFromPool = 0;
    public int poolPullChancePercent = 100;
    public HashMap<CardIdentifier, Integer> cardsInPool = new HashMap<>();
    public HashMap<CardRarity, Integer> raritiesInPool = new HashMap<>();
    public HashMap<ResourceLocation, Integer> itemsInPool = new HashMap<>();
    public transient CardPack cardPack;

    public static final StreamCodec<FriendlyByteBuf, CardPackPool> SYNC_CODEC = new StreamCodec<FriendlyByteBuf, CardPackPool>() {
        @Override
        public void encode(FriendlyByteBuf buffer, CardPackPool value) {
            ByteBufCodecs.INT.encode(buffer, value.minimumAmountOfCardsFromPool);
            ByteBufCodecs.INT.encode(buffer, value.maximumAmountOfCardsFromPool);
            ByteBufCodecs.INT.encode(buffer, value.poolPullChancePercent);

            ByteBufCodecs.map(HashMap::new, CardIdentifier.STREAM_CODEC, ByteBufCodecs.INT).encode(buffer, value.cardsInPool);
            ByteBufCodecs.map(HashMap::new, CardRarity.SYNC_CODEC, ByteBufCodecs.INT).encode(buffer, value.raritiesInPool);
            ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT).encode(buffer, value.itemsInPool);
        }

        @Override
        public CardPackPool decode(FriendlyByteBuf buffer) {
            int minimumAmountOfCardsFromPool = ByteBufCodecs.INT.decode(buffer);
            int maximumAmountOfCardsFromPool = ByteBufCodecs.INT.decode(buffer);
            int poolPullChancePercent = ByteBufCodecs.INT.decode(buffer);

            HashMap<CardIdentifier, Integer> cardsInPool = ByteBufCodecs.map(HashMap::new, CardIdentifier.STREAM_CODEC, ByteBufCodecs.INT).decode(buffer);
            HashMap<CardRarity, Integer> raritiesInPool = ByteBufCodecs.map(HashMap::new, CardRarity.SYNC_CODEC, ByteBufCodecs.INT).decode(buffer);
            HashMap<ResourceLocation, Integer> itemsInPool = ByteBufCodecs.map(HashMap::new, ResourceLocation.STREAM_CODEC, ByteBufCodecs.INT).decode(buffer);

            return new CardPackPool(itemsInPool, raritiesInPool, cardsInPool, poolPullChancePercent, maximumAmountOfCardsFromPool, minimumAmountOfCardsFromPool);
        }
    };


    public CardPackPool(HashMap<ResourceLocation, Integer> itemsInPool,
                        HashMap<CardRarity, Integer> raritiesInPool, HashMap<CardIdentifier, Integer> cardsInPool,
                        int poolPullChancePercent, int maximumAmountOfCardsFromPool, int minimumAmountOfCardsFromPool) {
        this.itemsInPool = itemsInPool;
        this.raritiesInPool = raritiesInPool;
        this.cardsInPool = cardsInPool;
        this.poolPullChancePercent = poolPullChancePercent;
        this.maximumAmountOfCardsFromPool = maximumAmountOfCardsFromPool;
        this.minimumAmountOfCardsFromPool = minimumAmountOfCardsFromPool;
    }

    public CardPackPool(int minimumAmountOfCardsFromPool, CardPack cardPack){
        this.minimumAmountOfCardsFromPool = minimumAmountOfCardsFromPool;
        this.maximumAmountOfCardsFromPool = minimumAmountOfCardsFromPool;
        this.cardPack = cardPack;
    }
    public static CardPackPool parse(JsonObject json, CardGame game, CardPack cardPack) throws MalformedJsonException {
        if(json.isEmpty() || !json.has(Json_POOL_MINIMUM_AMOUNT_KEY) ||
                (!json.has(Json_POOL_CARDS_KEY) && !json.has(Json_POOL_RARITIES_KEY) && !json.has(Json_POOL_ITEMS_KEY) /*&& !json.has(Json_POOL_TAGS_KEY)*/)) throw new MalformedJsonException("Card pack Json was empty");
        CardPackPool pool;
        try{
            pool = new CardPackPool(json.get(Json_POOL_MINIMUM_AMOUNT_KEY).getAsInt(), cardPack);
        } catch (Exception e){
            throw new MalformedJsonException("Card minimum value was malformed: "+e.getMessage());
        }
        if(json.has(Json_POOL_MAXIMUM_AMOUNT_KEY)){
            try{
                var max =  json.get(Json_POOL_MAXIMUM_AMOUNT_KEY).getAsInt();
                if(max > pool.minimumAmountOfCardsFromPool){
                    pool.maximumAmountOfCardsFromPool = max;
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pack maximum value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_POOL_PULL_CHANCE_KEY)){
            try{
                pool.poolPullChancePercent =  json.get(Json_POOL_PULL_CHANCE_KEY).getAsInt();
            } catch (Exception e){
                throw new MalformedJsonException("Card pack pull chance value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_POOL_CARDS_KEY)){
            try{
                JsonArray contents =  json.get(Json_POOL_CARDS_KEY).getAsJsonArray();
                for (var section : contents) {
                    var sectionAsObject = section.getAsJsonObject();
                    pool.cardsInPool.put(new CardIdentifier(sectionAsObject.get(Json_SELF_GAME_ID_KEY).getAsString(),
                                    sectionAsObject.get(Json_SELF_SET_ID_KEY).getAsString(),sectionAsObject.get(Json_SELF_CARD_ID_KEY).getAsString(),
                                    (sectionAsObject.has("rarity") ? sectionAsObject.get("rarity").getAsString() : "")),
                            sectionAsObject.get("weight").getAsInt());
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pool cards value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_POOL_RARITIES_KEY)){
            try{
                JsonArray contents =  json.get(Json_POOL_RARITIES_KEY).getAsJsonArray();
                for (var rarity : contents) {
                    var rarityAsObject = rarity.getAsJsonObject();
                    var foundRarity = game.getRarity(rarity.getAsJsonObject().get("rarityId").getAsString());
                    if(!foundRarity.rarityId.equals("missing")){
                        var cardCount = cardPack.getCardGame().getCardSet(cardPack.setId).getCards().values().stream()
                                .filter(cardData -> cardData.cardRarityIds.contains(foundRarity.rarityId)).toList().size();
                        if(cardCount > 0){
                            pool.raritiesInPool.put(foundRarity,
                                    rarityAsObject.get("weight").getAsInt());
                        }
                    }
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pool rarities value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_POOL_ITEMS_KEY)){
            try{
                var identifierArray = json.get(Json_POOL_ITEMS_KEY).getAsJsonArray();
                for (var identifier : identifierArray) {
                    var identifierAsObject = identifier.getAsJsonObject();
                    var identifierSplit = identifierAsObject.get("itemId").getAsString().split(":");
                    pool.itemsInPool.put(ResourceLocation.fromNamespaceAndPath(identifierSplit[0], identifierSplit[1]),
                            identifierAsObject.get("weight").getAsInt());
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card items pools value was malformed: "+e.getMessage());
            }
        }
//        if(json.has(Json_POOL_TAGS_KEY)){
//            try{
//                var identifierArray = ( json.get(Json_POOL_TAGS_KEY));
//                for (var identifier : identifierArray) {
//                    var identifierAsObject = identifier;
//                    var identifierSplit = (identifierAsObject.get("itemId")).split(":");
//                    pool.tagsInPool.put(ResourceLocation.fromNamespaceAndPath(identifierSplit[0], identifierSplit[1]),
//                            identifierAsObject.get("weight")));
//                }
//            } catch (Exception e){
//                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
//            }
//        }
        return pool;
    }

    public void setCardPack(CardPack cardPack) {
        this.cardPack = cardPack;
    }
}
