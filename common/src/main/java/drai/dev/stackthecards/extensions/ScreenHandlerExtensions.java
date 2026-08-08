package drai.dev.stackthecards.extensions;

import net.minecraft.core.*;
import net.minecraft.world.item.*;

public interface ScreenHandlerExtensions {
    NonNullList<ItemStack> getTrackedStacks();
    NonNullList<ItemStack> getPreviouslyTrackedStacks();
}
