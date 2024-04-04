package drai.dev.stackthecards.data;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.cardpacks.*;
import org.json.simple.*;

import static drai.dev.stackthecards.data.carddata.CardData.JSON_NAME_HEADER_KEY;

public class CardRarity {
    public static String JSON_RARITY_NAME_KEY = "rarityName";
    public static String JSON_RARITY_ID_KEY = "rarityId";

    public String rarityId;
    public String rarityName;

    public CardRarity(String rarityId) {
        this.rarityId = rarityId;
    }

    public static CardRarity parse(JSONObject json) throws MalformedJsonException{
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
        return cardRarity;
    }


}
