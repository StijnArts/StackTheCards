package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.util.*;
import org.json.simple.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardConnectionEntry.*;
import static drai.dev.stackthecards.data.carddata.CardData.*;
import static drai.dev.stackthecards.data.carddata.CardData.JSON_DETAIL_HEADER_KEY;

public class GameCardPack extends CardPack{
    public GameCardPack(String gameId, String packId) {
        super(gameId, packId);
    }

    public GameCardPack(String packId, String gameId, CardTooltipLine detailHeader, List<CardTooltipSection> hoverTooltipSections, List<CardTooltipSection> detailTooltipSections,
                        List<CardPackPool> pools, Map<Identifier, Integer> guaranteedItems, Map<CardIdentifier, Integer> guaranteedCards, String packName,
                        double weight, boolean droppedByMobs) {
        super(packId, gameId, detailHeader, hoverTooltipSections, detailTooltipSections, pools, guaranteedItems, guaranteedCards, packName, weight, droppedByMobs);
    }

    public static CardPack parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_PACK_ID_KEY)) throw new MalformedJsonException("Card pack Json was empty");
        GameCardPack cardPack;
        if(json.containsKey(JSON_PARENT_KEY)) {
            try {
                cardPack = (GameCardPack) game.getParentPack((String) json.get(JSON_PARENT_KEY)).copy((String) json.get(JSON_PACK_ID_KEY));
            } catch (Exception e) {
                throw new MalformedJsonException("Card pack parent was malformed: " + e.getMessage());
            }
        } else {
            try {
                cardPack = new GameCardPack(game.getGameId(), (String) json.get(JSON_PACK_ID_KEY));
            } catch (Exception e) {
                throw new MalformedJsonException("Card pack id was malformed: " + e.getMessage());
            }
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardPack.packName = (String) json.get(JSON_NAME_HEADER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card pack name value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_HOVER_TOOLTIP_KEY);
                for (var section : contents) {
                    cardPack.hoverTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_CARD_DETAIL_TOOLTIP_KEY);
                for (var section : contents) {
                    cardPack.detailTooltipSections.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_DETAIL_HEADER_KEY)){
            try{
                cardPack.detailHeader = new CardTooltipLine();
                var textContents = json.get(JSON_DETAIL_HEADER_KEY);
                if(textContents instanceof String contentsAsString){
                    cardPack.detailHeader.text = contentsAsString;
                } else if(textContents instanceof JSONArray contentsAsJsonArray){
                    for (var textSegment: contentsAsJsonArray) {
                        cardPack.detailHeader.lineSegments.add(CardTooltipLine.parse((JSONObject) textSegment, game));
                    }
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail header value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_PACK_POOLS_KEY)){
            try{
                cardPack.detailHeader = new CardTooltipLine();
                JSONArray pools = (JSONArray) json.get(JSON_PACK_POOLS_KEY);
                for (var pool: pools) {
                    try{
                        cardPack.pools.add(CardPackPool.parse((JSONObject)pool, game));
                    } catch(Exception e){
                        throw new MalformedJsonException("Card Pack pool entry was malformed: "+e.getMessage());
                    }
                }

            } catch (Exception e){
                throw new MalformedJsonException("Card pools value was malformed: "+e.getMessage());
            }
        }

        if(json.containsKey(JSON_GUARANTEED_CARDS_KEY)){
            try{
                JSONArray contents = (JSONArray) json.get(JSON_GUARANTEED_CARDS_KEY);
                for (var section : contents) {
                    var sectionAsObject = (JSONObject)section;
                    cardPack.guaranteedCards.put(new CardIdentifier((String) sectionAsObject.get(JSON_SELF_GAME_ID_KEY),
                                    (String) sectionAsObject.get(JSON_SELF_SET_ID_KEY),(String) sectionAsObject.get(JSON_SELF_CARD_ID_KEY),
                                    (sectionAsObject.containsKey("rarity") ? (String) sectionAsObject.get("rarity") : "")),
                            (int) (long)sectionAsObject.get("amount"));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pack guarnteed cards value was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_GUARANTEED_ITEMS_KEY)){
            try{
                var identifierArray = ((JSONArray) json.get(JSON_GUARANTEED_ITEMS_KEY));
                for (var identifier : identifierArray) {
                    var identifierAsObject = (JSONObject)identifier;
                    var identifierSplit = ((String)identifierAsObject.get("itemId")).split(":");
                    cardPack.guaranteedItems.put(new Identifier(identifierSplit[0], identifierSplit[1]),
                            (int) (long)identifierAsObject.get("amount"));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_WEIGHT_IN_LOOT_POOL_KEY)){
            try{
                cardPack.weight = (int) (long) json.get(JSON_WEIGHT_IN_LOOT_POOL_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey(JSON_DROPPED_BY_MOBS_KEY)){
            try{
                cardPack.droppedByMobs = (boolean) json.get(JSON_DROPPED_BY_MOBS_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        return cardPack;
    }

    @Override
    public CardPack copy(String packId) {
        return new GameCardPack(packId, this.gameId, this.detailHeader, this.hoverTooltipSections, this.detailTooltipSections, this.pools, this.guaranteedItems, this.guaranteedCards, this.packName, this.weight, this.droppedByMobs);
    }

    @Override
    public String getPackTextureLocation() {
        return gameId + "/" + packId;
    }

    @Override
    public int getCountInGroup() {
        return getCardGame().getCards().size();
    }

    @Override
    public String getEffectIdentifier() {
        return getCardGame().getEffectIdentifier();
    }
}
