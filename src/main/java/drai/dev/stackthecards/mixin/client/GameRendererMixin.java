package drai.dev.stackthecards.mixin.client;

import drai.dev.stackthecards.client.*;
import net.minecraft.client.render.*;
import net.minecraft.item.*;
import net.minecraft.item.map.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "reset", at = @At("HEAD"), cancellable = true)
    private void reset(CallbackInfo ci){
        StackTheCardsClient.CARD_RENDERER.clearStateTextures();
    }
}
