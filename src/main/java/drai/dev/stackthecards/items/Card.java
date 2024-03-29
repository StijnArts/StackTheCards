package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.*;
import net.minecraft.client.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class Card extends Item {
    public String cardId;
    private CardData cardData = new CardData();

    public Card(Settings settings) {
        super(settings);
    }

    public CardData getCardData() {
        return cardData;
    }

    public ModelIdentifier getModelIdentifier() {
        return new ModelIdentifier(getIdentifier(),"inventory");
    }
    public Identifier getIdentifier() {
        return new Identifier("stack_the_cards", "stc_cards/backs/pokemon_tcg_modern_back");
    }


}
