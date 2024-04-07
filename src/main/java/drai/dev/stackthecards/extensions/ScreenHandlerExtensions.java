package drai.dev.stackthecards.extensions;

import net.minecraft.item.*;
import net.minecraft.util.collection.*;

public interface ScreenHandlerExtensions {
    DefaultedList<ItemStack> getTrackedStacks();
    DefaultedList<ItemStack> getPreviouslyTrackedStacks();
}
