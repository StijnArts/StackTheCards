package drai.dev.stackthecards.tooltips;

import drai.dev.stackthecards.items.*;
import net.minecraft.world.inventory.tooltip.*;
import net.minecraft.world.item.*;

public class CardTooltipData implements TooltipComponent {
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
