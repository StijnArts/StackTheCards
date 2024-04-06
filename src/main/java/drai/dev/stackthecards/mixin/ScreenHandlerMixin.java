package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.extensions.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import net.minecraft.util.collection.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin implements ScreenHandlerExtensions {
    @Shadow @Final private DefaultedList<ItemStack> trackedStacks;

    @Shadow @Final private DefaultedList<ItemStack> previousTrackedStacks;

    @Inject(at = @At("RETURN"), method = "getCursorStack")
    private void checkStackInSlot(CallbackInfoReturnable<ItemStack> cir) {
//        StackTheCardsClient.checkToolTipForScrolling(cir.getReturnValue());
    }

    @Override
    public DefaultedList<ItemStack> getTrackedStacks() {
        return this.trackedStacks;
    }

    @Override
    public DefaultedList<ItemStack> getPreviouslyTrackedStacks() {
        return this.previousTrackedStacks;
    }
}
