package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.tooltips.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.text.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

import static drai.dev.stackthecards.data.carddata.CardData.JSON_NAME_HEADER_KEY;
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
                JSONArray contents = (JSONArray) json.get("text");
                for (var section : contents) {
                    cardRarity.text.add(CardTooltipSection.parse((JSONObject) section, game));
                }
            } catch (Exception e){
                throw new MalformedJsonException("Card hover tooltip value was malformed: "+e.getMessage());
            }

        }
        return cardRarity;
    }


    public List<Text> getText() {
        var tooltips = new ArrayList<>();
        for (int i = 0; i < sections.size(); i++) {
            var section = sections.get(i);
            tooltips.addAll(section.getText());
            if(i < sections.size()-1 && !section.noLineBreak){
                tooltips.add(NEW_LINE);
            }
        }
    }
}
