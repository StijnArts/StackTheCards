package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.text.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.carddata.CardData.NEW_LINE;

public class CardRarity {
    public static String JSON_RARITY_NAME_KEY = "rarityName";
    public static String JSON_RARITY_ID_KEY = "rarityId";

    public String rarityId;
    public String rarityName;
    private List<CardTooltipSection> text = new ArrayList<>();

    public CardRarity(String rarityId) {
        this.rarityId = rarityId;
    }

    public static CardRarity parse(JSONObject json, CardGame game) throws MalformedJsonException{
        if(json.isEmpty() || !json.containsKey(JSON_RARITY_ID_KEY)) throw new MalformedJsonException("Card rarity Json was empty");
        CardRarity cardRarity;
        try{
            cardRarity = new CardRarity((String) json.get(JSON_RARITY_ID_KEY));
        } catch (Exception e){
            throw new MalformedJsonException("Card rarity id was malformed: "+e.getMessage());
        }
        if(json.containsKey(JSON_RARITY_NAME_KEY)){
            try{
                cardRarity.rarityName = (String) json.get(JSON_RARITY_NAME_KEY);
            } catch (Exception e){
                throw new MalformedJsonException("Card rarity name was malformed: "+e.getMessage());
            }
        }
        if(json.containsKey("text")){
            try{
                var contents = json.get("text");
                if(contents instanceof JSONArray arrayContents){
                    for (var section : arrayContents) {
                        cardRarity.text.add(CardTooltipSection.parse((JSONObject) section, game));
                    }
                } else {
                    cardRarity.text.add(CardTooltipSection.parse((JSONObject) contents, game));
                }

            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }

        }
        return cardRarity;
    }


    public List<Text> getText() {
        return text.stream().map(text -> text.getText()).flatMap(List::stream).collect(Collectors.toList());
    }
}
