package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.network.*;
import net.minecraft.network.codec.*;
import net.minecraft.resources.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardConnectionEntry.*;

public class CardPackPool {
    private static final String JSON_POOL_MINIMUM_AMOUNT_KEY = "minimumCardsFromPool";
    private static final String JSON_POOL_MAXIMUM_AMOUNT_KEY = "maximumCardsFromPool";
    private static final String JSON_POOL_PULL_CHANCE_KEY = "poolPullChancePercent";
    private static final String JSON_POOL_CARDS_KEY = "cards";
    private static final String JSON_POOL_RARITIES_KEY = "rarities";
    private static final String JSON_POOL_ITEMS_KEY = "items";
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
    public static CardPackPool parse(JSONObject json, CardGame game, CardPack cardPack) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_POOL_MINIMUM_AMOUNT_KEY) ||
                (!json.containsKey(JSON_POOL_CARDS_KEY) && !json.containsKey(JSON_POOL_RARITIES_KEY) && !json.containsKey(JSON_POOL_ITEMS_KEY) /*&& !json.containsKey(JSON_POOL_TAGS_KEY)*/)) throw new MalformedJsonException("Card pack Json was empty");
        CardPackPool pool;
        try{
            pool = new CardPackPool((int) (long) json.get(JSON_POOL_MINIMUM_AMOUNT_KEY), cardPack);
        } catch (Exception e){
            throw new MalformedJsonException("Card minimum value was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_POOL_MAXIMUM_AMOUNT_KEY)){
            try{
                var max = (int) (long) json.get(JSON_POOL_MAXIMUM_AMOUNT_KEY);
                if(max > pool.minimumAmountOfCardsFromPool){
                    pool.maximumAmountOfCardsFromPool = max;
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pack maximum value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_POOL_PULL_CHANCE_KEY)){
            try{
                pool.poolPullChancePercent = (int) (long) json.get(JSON_POOL_PULL_CHANCE_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card pack pull chance value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_POOL_CARDS_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_POOL_CARDS_KEY);
                for (var section : contents) {
                    var sectionAsObject = (JSONObject)section;
                    pool.cardsInPool.put(new CardIdentifier((String) sectionAsObject.get(JSON_SELF_GAME_ID_KEY),
                                    (String) sectionAsObject.get(JSON_SELF_SET_ID_KEY),(String) sectionAsObject.get(JSON_SELF_CARD_ID_KEY),
                                    (sectionAsObject.containsKey("rarity") ? (String) sectionAsObject.get("rarity") : "")),
                            (int) (long)sectionAsObject.get("weight"));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pool cards value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_POOL_RARITIES_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_POOL_RARITIES_KEY);
                for (var rarity : contents) {
                    var rarityAsObject = (JSONObject)rarity;
                    var foundRarity = game.getRarity((String) ((JSONObject) rarity).get("rarityId"));
                    if(!foundRarity.rarityId.equals("missing")){
                        var cardCount = cardPack.getCardGame().getCardSet(cardPack.setId).getCards().values().stream()
                                .filter(cardData -> cardData.cardRarityIds.contains(foundRarity.rarityId)).toList().size();
                        if(cardCount > 0){
                            pool.raritiesInPool.put(foundRarity,
                                    (int) (long)rarityAsObject.get("weight"));
                        }
                    }
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pool rarities value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_POOL_ITEMS_KEY)){
            try{
                var identifierArray = ((JSONArray) json.get(JSON_POOL_ITEMS_KEY));
                for (var identifier : identifierArray) {
                    var identifierAsObject = (JSONObject)identifier;
                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
                    pool.itemsInPool.put(ResourceLocation.fromNamespaceAndPath(identifierSplit[0], identifierSplit[1]),
                            (int) (long)identifierAsObject.get("weight"));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card items pools value was malformed: "+e.getMessage());
            }
        }
//        if(json.containsKey(JSON_POOL_TAGS_KEY)){
//            try{
//                var identifierArray = ((JSONArray) json.get(JSON_POOL_TAGS_KEY));
//                for (var identifier : identifierArray) {
//                    var identifierAsObject = (JSONObject)identifier;
//                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
//                    pool.tagsInPool.put(ResourceLocation.fromNamespaceAndPath(identifierSplit[0], identifierSplit[1]),
//                            (int) (long)identifierAsObject.get("weight")));
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
