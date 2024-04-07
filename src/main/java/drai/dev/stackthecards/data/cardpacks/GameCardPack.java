package drai.dev.stackthecards.data.cardpacks;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.tooltips.parts.*;
import org.json.simple.*;

import static drai.dev.stackthecards.data.carddata.CardData.*;
import static drai.dev.stackthecards.data.carddata.CardData.JSON_DETAIL_HEADER_KEY;

public class GameCardPack extends CardPack{
    public GameCardPack(String gameId, String packId) {
        super(gameId, packId);
    }
    public static CardPack parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_PACK_ID_KEY)) throw new MalformedJsonException("Card pack Json was empty");
        GameCardPack cardPack;
        try{
            cardPack = new GameCardPack(game.getGameId(), (String) json.get(JSON_PACK_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card pack id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_NAME_HEADER_KEY)){
            try{
                cardPack.packName = (String) json.get(JSON_NAME_HEADER_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card has rounded corners value was malformed: "+e.getMessage());
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
        return cardPack;
    }

    @Override
    public String getPackTextureLocation() {
        return gameId + "/" + packId;
    }

    @Override
    public int getCountInGroup() {
        return getCardGame().getCards().size();
    }
}
