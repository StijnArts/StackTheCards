package drai.dev.stackthecards.tooltips;

import drai.dev.stackthecards.items.*;
import net.minecraft.client.item.*;
import net.minecraft.item.*;

public class CardTooltipData implements TooltipData {
    private final Card card;
    private CardTooltipData(Card card) {
        this.card = card;
    }

    public static CardTooltipData of(ItemStack self) {
        return new CardTooltipData((Card)self.getItem());
    }

    public Card getCard() {
        return card;
    }
}
