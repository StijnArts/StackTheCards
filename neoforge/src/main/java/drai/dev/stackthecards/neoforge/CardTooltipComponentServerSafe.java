package drai.dev.stackthecards.neoforge;

import drai.dev.stackthecards.tooltips.*;
import net.minecraft.world.inventory.tooltip.*;

public class CardTooltipComponentServerSafe implements TooltipComponent {
    private CardTooltipData cardTooltipData;

    public CardTooltipComponentServerSafe(CardTooltipData cardTooltipData) {
        this.cardTooltipData = cardTooltipData;
    }

    public CardTooltipData getCardTooltipData() {
        return cardTooltipData;
    }
}
