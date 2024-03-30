package drai.dev.stackthecards.tooltips;

import drai.dev.stackthecards.items.*;
import net.minecraft.client.item.*;
import net.minecraft.item.*;

public class CardTooltipData implements TooltipData {
    private ItemStack stack;
    private CardTooltipData(ItemStack stack) {
        this.stack = stack;
    }

    public static CardTooltipData of(ItemStack self) {
        return new CardTooltipData(self);
    }

    public Card getCard() {
        return (Card) stack.getItem();
    }

    public ItemStack getStack() {
        return stack;
    }
}
