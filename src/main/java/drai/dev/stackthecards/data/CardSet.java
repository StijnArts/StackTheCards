package drai.dev.stackthecards.data;

import drai.dev.stackthecards.registry.*;
import net.minecraft.client.*;
import net.minecraft.client.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.*;
import net.minecraft.util.*;

import java.util.*;

public class CardSet {
    public Map<String, Identifier> textures;
    private CardGame cardGame;
    private String setId= "missing";
    private Map<String, CardData> cards = new HashMap<>();

    public CardSet() {
        cards.put("charizard", charizard);
        cards.put("hooh_top", hoohTop);
        cards.put("hooh_bottom", hoohBottom);
    }

    public CardGame getCardGame() {
        return new CardGame();//cardGame;
    }

    public String getSetIdentifier() {
        return getCardGame().getGameIdentifier() + "_" + setId;
    }

    public Map<String, CardData> getCards() {
        return cards;
    }

    public static CardData charizard = new CardData("charizard");
    public static CardData hoohTop = new CardData("hooh_top");
    public static CardData hoohBottom = new CardData("hooh_bottom");
    public CardData getCardData(String cardId){
        if(cards.containsKey(cardId)){
            return cards.get(cardId);
        } else
        return CardGameRegistry.MISSING_CARD_DATA;
    }

    public static CardData cardBackData = new CardData("high_res_modern_card_back");
    public CardData getCardBackData() {
        return cardBackData;
    }
}
