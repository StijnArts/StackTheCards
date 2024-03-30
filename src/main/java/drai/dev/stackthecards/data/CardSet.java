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
    public CardGame getCardGame() {
        return cardGame;
    }

    public String getSetIdentifier() {
        return cardGame.getGameIdentifier() + "_" + setId;
    }

    public Map<String, CardData> getCards() {
        return cards;
    }

    public static CardData charizard = new CardData("charizard");
    public CardData getCardData(String cardId){
        if(cardId.equals("charizard")){
            return charizard;
        }
        return CardGameRegistry.MISSING_CARD_DATA;
    }

    public static CardData cardBackData = new CardData("high_res_modern_card_back");
    public CardData getCardBackData() {
        return cardBackData;
    }
}
