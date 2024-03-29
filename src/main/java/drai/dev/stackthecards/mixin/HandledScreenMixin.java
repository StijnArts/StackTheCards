package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.tooltips.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(HandledScreen.class)
public class HandledScreenMixin {
    @Final
    @Shadow
    protected ScreenHandler handler;

    @Unique
    @Nullable
    private Slot mouseLockSlot = null;
    @Unique
    private int mouseLockX = 0;
    @Unique
    private int mouseLockY = 0;
    @Inject(at = @At("HEAD"), method = "isPointOverSlot(Lnet/minecraft/screen/slot/Slot;DD)Z", cancellable = true)
    private void forceFocusSlot(Slot slot, double pointX, double pointY, CallbackInfoReturnable<Boolean> cir) {
        if (this.mouseLockSlot != null) {
            // Handling the case where the hovered item stack get swapped for air while the tooltip is locked
            // When this happens, the lockTooltipPosition() hook will not be called (there is no tooltip for air),
            // so we need to perform cleanup logic here.
            //
            // We also need to check if the slot is still part of the handler,
            // as it may have been removed (this is the case when switching tabs in the creative inventory)

            if (this.mouseLockSlot.hasStack() && this.handler.slots.contains(this.mouseLockSlot))
                cir.setReturnValue(slot == this.mouseLockSlot && this.handler.getCursorStack().isEmpty());
            else
                // reset the lock if the stack is no longer present
                this.mouseLockSlot = null;
        }
    }

    /**
     * Makes the current mouse position available via extensions to the DrawContext.
     */
    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V")
    private void captureMousePosition(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        DrawContextExtensions extensions = (DrawContextExtensions) context;
        extensions.setMouseY(mouseY);
        extensions.setMouseX(mouseX);
    }

}
