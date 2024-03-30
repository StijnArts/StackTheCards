package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ScreenHandler.class)
public class ScreenHandlerMixin {
    @Inject(at = @At("RETURN"), method = "getCursorStack")
    private void checkStackInSlot(CallbackInfoReturnable<ItemStack> cir) {
//        StackTheCardsClient.checkToolTipForScrolling(cir.getReturnValue());
    }
}
