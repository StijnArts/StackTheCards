package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.extensions.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(AbstractContainerScreen.class)
public class HandledScreenMixin{
    @Final
    @Shadow
    protected AbstractContainerMenu menu;

    @Shadow @Nullable protected Slot hoveredSlot;
    @Unique
    @Nullable
    private Slot mouseLockSlot = null;
    @Unique
    private int mouseLockX = 0;
    @Unique
    private int mouseLockY = 0;

    @Inject(at = @At("TAIL"), method = "renderTooltip")
    private void checkStackInSlot(GuiGraphics context, int x, int y, CallbackInfo ci) {
        if (((AbstractContainerMenu)this.menu).getCarried().isEmpty() && this.hoveredSlot != null) {
            ItemStack itemStack = this.hoveredSlot.getItem();
//            System.out.println("checking scrolling from handledscreenmixin");
            StackTheCardsClient.checkToolTipForScrolling(itemStack);
        }
//
    }

    /**
     * Makes the current mouse position available via extensions to the GuiGraphics.
     */
    @Inject(at = @At("HEAD"), method = "render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V")
    private void captureMousePosition(GuiGraphics context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        DrawContextExtensions extensions = (DrawContextExtensions) context;
        extensions.setMouseY(mouseY);
        extensions.setMouseX(mouseX);
    }
}
