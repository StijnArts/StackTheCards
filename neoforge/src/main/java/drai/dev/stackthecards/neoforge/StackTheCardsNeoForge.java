package drai.dev.stackthecards.neoforge;

import com.mojang.datafixers.util.*;
import drai.dev.stackthecards.StackTheCards;
import drai.dev.stackthecards.neoforge.client.*;
import drai.dev.stackthecards.tooltips.*;
import net.minecraft.world.item.*;
import net.neoforged.bus.api.*;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.*;

@Mod(StackTheCards.MOD_ID)
public final class StackTheCardsNeoForge {
    public StackTheCardsNeoForge() {
        // Run our common setup.
        StackTheCards.init();
    }

    @SubscribeEvent
    public static void onTooltipComponent(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        var tooltipImage = stack.getTooltipImage();
        if(tooltipImage.isPresent()){
            if (tooltipImage.get() instanceof CardTooltipData previewData)
                event.getTooltipElements().add(Either.right(new CardTooltipComponentServerSafe(previewData)));
        }
    }
}
