package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.extensions.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screen.ingame.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(HandledScreen.class)
public class HandledScreenMixin{
    @Final
    @Shadow
    protected ScreenHandler handler;

    @Shadow private ItemStack touchDragStack;
    @Shadow @Nullable protected Slot focusedSlot;
    @Unique
    @Nullable
    private Slot mouseLockSlot = null;
    @Unique
    private int mouseLockX = 0;
    @Unique
    private int mouseLockY = 0;

    @Inject(at = @At("TAIL"), method = "drawMouseoverTooltip")
    private void checkStackInSlot(DrawContext context, int x, int y, CallbackInfo ci) {
        if (((ScreenHandler)this.handler).getCursorStack().isEmpty() && this.focusedSlot != null) {
            ItemStack itemStack = this.focusedSlot.getStack();
//            System.out.println("checking scrolling from handledscreenmixin");
            StackTheCardsClient.checkToolTipForScrolling(itemStack);
        }
//
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
