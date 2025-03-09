package drai.dev.stackthecards.mixin.client;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.renderer.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "resetData", at = @At("HEAD"), cancellable = true)
    private void reset(CallbackInfo ci){
        StackTheCardsClient.CARD_RENDERER.clearStateTextures();
    }
}
