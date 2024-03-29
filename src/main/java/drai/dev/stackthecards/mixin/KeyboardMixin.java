package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(Keyboard.class)
public class KeyboardMixin {
    @Inject(at = @At("HEAD"),
            method = "Lnet/minecraft/client/Keyboard;onKey(JIIII)V")
    private void onKey(long window, int key, int scancode, int action, int modifiers, CallbackInfo ci) {
        // update the pressed preview keys when key event was received on the game window
        if (window == MinecraftClient.getInstance().getWindow().getHandle())
            StackTheCardsClient.updateKeys();
    }
}
