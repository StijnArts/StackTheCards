package drai.dev.stackthecards.mixin.client;

import net.minecraft.client.resources.model.*;
import net.minecraft.resources.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(ModelManager.class)
public class BakedModelManagerMixin {

    @Mutable
    @Shadow @Final private static Map<ResourceLocation, ResourceLocation> VANILLA_ATLASES;

    @Inject(method = "<clinit>", at = @At("TAIL"), cancellable = true)
    private static void reset(CallbackInfo ci){
//        LAYERS_TO_LOADERS.put();
        var layersToLoaders = new HashMap<>(VANILLA_ATLASES);
        layersToLoaders.put(ResourceLocation.withDefaultNamespace("textures/atlas/banner_patterns.png"), ResourceLocation.fromNamespaceAndPath("stack_the_cards", "card_backs"));
        VANILLA_ATLASES = layersToLoaders;
    }

    /*@Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void injected(CallbackInfo ci, @Local LocalRef<Map<ResourceLocation, ResourceLocation>> layersToLoadersCapture) {
        var layersToLoaders = new HashMap<>(LAYERS_TO_LOADERS);
        layersToLoaders.put(ResourceLocation.fromNamespaceAndPath("textures/atlas/card_backs.png"), ResourceLocation.fromNamespaceAndPath("stack_the_cards", "card_backs"));
        layersToLoadersCapture.set(layersToLoaders);
    }*/
}
