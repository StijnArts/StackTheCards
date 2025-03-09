package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.resources.*;
import net.minecraft.util.*;
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
    private static final String JSON_POOL_TAGS_KEY = "tags";
    public int minimumAmountOfCardsFromPool = 0;
    public int maximumAmountOfCardsFromPool = 0;
    public int poolPullChancePercent = 100;
    public Map<CardIdentifier, Integer> cardsInPool = new HashMap<>();
    public Map<CardRarity, Integer> raritiesInPool = new HashMap<>();
    public Map<ResourceLocation, Integer> itemsInPool = new HashMap<>();
    private final CardPack cardPack;
//    public Map<ResourceLocation, Integer> tagsInPool = new HashMap<>();

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
                    pool.itemsInPool.put(new ResourceLocation(identifierSplit[0], identifierSplit[1]),
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
//                    pool.tagsInPool.put(new ResourceLocation(identifierSplit[0], identifierSplit[1]),
//                            (int) (long)identifierAsObject.get("weight")));
//                }
//            } catch (Exception e){
//                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
//            }
//        }
        return pool;
    }


}
