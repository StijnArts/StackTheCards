package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
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
    private static final String JSON_GUARANTEED_CARDS_KEY = "guaranteedCards";
    private static final String JSON_GUARANTEED_ITEMS_KEY = "guaranteedItems";
    public int minimumAmountOfCardsFromPool = 0;
    public int maximumAmountOfCardsFromPool = 0;
    public int poolPullChancePercent = 100;
    public Map<CardIdentifier, Integer> cardsInPool = new HashMap<>();
    public Map<CardIdentifier, Integer> guaranteedCards = new HashMap<>();
    public Map<CardRarity, Integer> raritiesInPool = new HashMap<>();
    public Map<Identifier, Integer> itemsInPool = new HashMap<>();
    public Map<Identifier, Integer> guaranteedItems = new HashMap<>();
//    public Map<Identifier, Integer> tagsInPool = new HashMap<>();

    public CardPackPool(int minimumAmountOfCardsFromPool){
        this.minimumAmountOfCardsFromPool = minimumAmountOfCardsFromPool;
        this.maximumAmountOfCardsFromPool = minimumAmountOfCardsFromPool;
    }
    public static CardPackPool parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_POOL_MINIMUM_AMOUNT_KEY) ||
                (!json.containsKey(JSON_POOL_CARDS_KEY) && !json.containsKey(JSON_POOL_RARITIES_KEY) && !json.containsKey(JSON_POOL_ITEMS_KEY) /*&& !json.containsKey(JSON_POOL_TAGS_KEY)*/)) throw new MalformedJsonException("Card pack Json was empty");
        CardPackPool pool;
        try{
            pool = new CardPackPool(Integer.parseInt((String) json.get(JSON_POOL_MINIMUM_AMOUNT_KEY)));
        } catch (Exception e){
            throw new MalformedJsonException("Card pack id was malformed");
        }
        if(json.containsKey(JSON_POOL_MAXIMUM_AMOUNT_KEY)){
            try{
                var max = Integer.parseInt((String)json.get(JSON_POOL_MAXIMUM_AMOUNT_KEY));
                if(max > pool.minimumAmountOfCardsFromPool){
                    pool.maximumAmountOfCardsFromPool = max;
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed");
            }
        }
        if(json.containsKey(JSON_POOL_PULL_CHANCE_KEY)){
            try{
                pool.poolPullChancePercent = Integer.parseInt((String)json.get(JSON_POOL_PULL_CHANCE_KEY));
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed");
            }
        }
        if(json.containsKey(JSON_POOL_CARDS_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_POOL_CARDS_KEY);
                for (var section : contents) {
                    var sectionAsObject = (JSONObject)section;
                    pool.cardsInPool.put(new CardIdentifier((String) sectionAsObject.get(JSON_SELF_GAME_ID_KEY),
                            (String) sectionAsObject.get(JSON_SELF_SET_ID_KEY),(String) sectionAsObject.get(JSON_SELF_CARD_ID_KEY)),
                            Integer.valueOf((String) sectionAsObject.get("weight")));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_POOL_RARITIES_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_POOL_RARITIES_KEY);
                for (var rarity : contents) {
                    var rarityAsObject = (JSONObject)rarity;
                    pool.raritiesInPool.put(game.getRarity((String) ((JSONObject) rarity).get("rarityId")),
                            Integer.valueOf((String) rarityAsObject.get("weight")));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_POOL_ITEMS_KEY)){
            try{
                var identifierArray = ((JSONArray) json.get(JSON_POOL_ITEMS_KEY));
                for (var identifier : identifierArray) {
                    var identifierAsObject = (JSONObject)identifier;
                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
                    pool.itemsInPool.put(new Identifier(identifierSplit[0], identifierSplit[1]),
                            Integer.valueOf((String) identifierAsObject.get("weight")));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }
//        if(json.containsKey(JSON_POOL_TAGS_KEY)){
//            try{
//                var identifierArray = ((JSONArray) json.get(JSON_POOL_TAGS_KEY));
//                for (var identifier : identifierArray) {
//                    var identifierAsObject = (JSONObject)identifier;
//                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
//                    pool.tagsInPool.put(new Identifier(identifierSplit[0], identifierSplit[1]),
//                            Integer.valueOf((String) identifierAsObject.get("weight")));
//                }
//            } catch (Exception e){
//                throw new MalformedJsonException("Card hover tooltip value was malformed");
//            }
//        }
        if(json.containsKey(JSON_GUARANTEED_CARDS_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_GUARANTEED_CARDS_KEY);
                for (var section : contents) {
                    var sectionAsObject = (JSONObject)section;
                    pool.guaranteedCards.put(new CardIdentifier((String) sectionAsObject.get(JSON_SELF_GAME_ID_KEY),
                                    (String) sectionAsObject.get(JSON_SELF_SET_ID_KEY),(String) sectionAsObject.get(JSON_SELF_CARD_ID_KEY)),
                            Integer.valueOf((String) sectionAsObject.get("amount")));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }
        if(json.containsKey(JSON_GUARANTEED_ITEMS_KEY)){
            try{
                var identifierArray = ((JSONArray) json.get(JSON_GUARANTEED_ITEMS_KEY));
                for (var identifier : identifierArray) {
                    var identifierAsObject = (JSONObject)identifier;
                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
                    pool.guaranteedItems.put(new Identifier(identifierSplit[0], identifierSplit[1]),
                            Integer.valueOf((String) identifierAsObject.get("weight")));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed");
            }
        }

        return pool;
    }
}
