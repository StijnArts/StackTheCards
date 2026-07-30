package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.resources.*;
import com.google.gson.*;

import java.util.*;

import static drai.dev.stackthecards.data.CardConnectionEntry.*;
import static drai.dev.stackthecards.data.carddata.CardData.*;

public class GameCardPack extends CardPack{
    public GameCardPack(String gameId, String packId, String nameSpace) {
        super(gameId, packId, nameSpace);
    }

    public GameCardPack(String packId, String gameId, String nameSpace, CardTooltipLine detailHeader, ArrayList<CardTooltipSection> hoverTooltipSections, ArrayList<CardTooltipSection> detailTooltipSections,
                        ArrayList<CardPackPool> pools, HashMap<ResourceLocation, Integer> guaranteedItems, HashMap<CardIdentifier, Integer> guaranteedCards, String packName,
                        double weight, boolean droppedByMobs, boolean duplicationAllowed) {
        super(packId, gameId, nameSpace, detailHeader, hoverTooltipSections, detailTooltipSections, pools, guaranteedItems,
                guaranteedCards, packName, weight, droppedByMobs, duplicationAllowed);
    }

    public static CardPack parse(JsonObject json, CardGame game, String nameSpace) throws MalformedJsonException {
        if(json.isEmpty() || !json.has(Json_PACK_ID_KEY)) throw new MalformedJsonException("Card pack Json was empty");
        GameCardPack cardPack;
        if(json.has(Json_PARENT_KEY)) {
            try {
                cardPack = (GameCardPack) game.getParentPack(json.get(Json_PARENT_KEY).getAsString()).copy(json.get(Json_PACK_ID_KEY).getAsString());
            } catch (Exception e) {
                throw new MalformedJsonException("Card pack parent was malformed: " + e.getMessage());
            }
        } else {
            try {
                cardPack = new GameCardPack(game.getGameId(), json.get(Json_PACK_ID_KEY).getAsString(), nameSpace);
            } catch (Exception e) {
                throw new MalformedJsonException("Card pack id was malformed: " + e.getMessage());
            }
        }
        if(json.has(Json_NAME_HEADER_KEY)){
            try{
                cardPack.packName = json.get(Json_NAME_HEADER_KEY).getAsString();
            } catch (Exception e){
                throw new MalformedJsonException("Card pack name value was malformed: "+e.getMessage());
            }
        }
        if(json.has("canBeDuplicated")){
            try{
                cardPack.duplicationAllowed = json.get("canBeDuplicated").getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Card pack name value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_CARD_HOVER_TOOLTIP_KEY)){
            try{
                JsonArray contents =  json.get(Json_CARD_HOVER_TOOLTIP_KEY).getAsJsonArray();
                for (var section : contents) {
                    cardPack.hoverTooltipSections.add(CardTooltipSection.parse( section.getAsJsonObject(), game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_CARD_DETAIL_TOOLTIP_KEY)){
            try{
                JsonArray contents =  json.get(Json_CARD_DETAIL_TOOLTIP_KEY).getAsJsonArray();
                for (var section : contents) {
                    cardPack.detailTooltipSections.add(CardTooltipSection.parse( section.getAsJsonObject(), game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail tooltip value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_DETAIL_HEADER_KEY)){
            try{
                cardPack.detailHeader = new CardTooltipLine();
                var textContents = json.get(Json_DETAIL_HEADER_KEY);
                if(textContents.isJsonArray()){
                    for (var textSegment: textContents.getAsJsonArray()) {
                        cardPack.detailHeader.lineSegments.add(CardTooltipLine.parse( textSegment.getAsJsonObject(), game));
                    }
                } else{
                    cardPack.detailHeader.text = textContents.getAsString();
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card detail header value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_PACK_POOLS_KEY)){
            try{
                cardPack.detailHeader = new CardTooltipLine();
                JsonArray pools =  json.get(Json_PACK_POOLS_KEY).getAsJsonArray();
                for (var pool: pools) {
                    try{
                        cardPack.pools.add(CardPackPool.parse(pool.getAsJsonObject(), game, cardPack));
                    } catch(Exception e){
                        throw new MalformedJsonException("Card Pack pool entry was malformed: "+e.getMessage());
                    }
                }

            } catch (Exception e){
                throw new MalformedJsonException("Card pools value was malformed: "+e.getMessage());
            }
        }

        if(json.has(Json_GUARANTEED_CARDS_KEY)){
            try{
                JsonArray contents =  json.get(Json_GUARANTEED_CARDS_KEY).getAsJsonArray();
                for (var section : contents) {
                    var sectionAsObject = section.getAsJsonObject();
                    cardPack.guaranteedCards.put(new CardIdentifier(sectionAsObject.get(Json_SELF_GAME_ID_KEY).getAsString(),
                                    sectionAsObject.get(Json_SELF_SET_ID_KEY).getAsString(),sectionAsObject.get(Json_SELF_CARD_ID_KEY).getAsString(),
                                    (sectionAsObject.has("rarity") ? sectionAsObject.get("rarity").getAsString() : "")),
                            sectionAsObject.get("amount").getAsInt());
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card pack guarnteed cards value was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_GUARANTEED_ITEMS_KEY)){
            try{
                var identifierArray = ( json.get(Json_GUARANTEED_ITEMS_KEY));
                for (var identifier : identifierArray.getAsJsonArray()) {
                    var identifierAsObject = identifier.getAsJsonObject();
                    var identifierSplit = (identifierAsObject.get("itemId").getAsString()).split(":");
                    cardPack.guaranteedItems.put(ResourceLocation.fromNamespaceAndPath(identifierSplit[0], identifierSplit[1]),
                            identifierAsObject.get("amount").getAsInt());
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_WEIGHT_IN_LOOT_POOL_KEY)){
            try{
                cardPack.weight =  json.get(Json_WEIGHT_IN_LOOT_POOL_KEY).getAsInt();
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        if(json.has(Json_DROPPED_BY_MOBS_KEY)){
            try{
                cardPack.droppedByMobs = json.get(Json_DROPPED_BY_MOBS_KEY).getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Card guaranteed items was malformed: "+e.getMessage());
            }
        }
        return cardPack;
    }

    @Override
    public CardPack copy(String packId) {
        return new GameCardPack(packId, this.gameId, this.nameSpace, this.detailHeader, this.hoverTooltipSections,
                this.detailTooltipSections, this.pools, this.guaranteedItems, this.guaranteedCards, this.packName, this.weight, this.droppedByMobs, this.duplicationAllowed);
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
    public String getEffectResourceLocation() {
        return getCardGame().getEffectResourceLocation();
    }
}
