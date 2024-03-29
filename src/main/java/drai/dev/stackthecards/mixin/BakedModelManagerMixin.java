package drai.dev.stackthecards.mixin;

import com.llamalad7.mixinextras.sugar.*;
import com.llamalad7.mixinextras.sugar.ref.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.color.block.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.render.model.json.*;
import net.minecraft.client.texture.*;
import net.minecraft.item.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(BakedModelManager.class)
public class BakedModelManagerMixin {

    @Mutable
    @Shadow @Final private static Map<Identifier, Identifier> LAYERS_TO_LOADERS;

    @Inject(method = "<clinit>", at = @At("TAIL"), cancellable = true)
    private static void reset(CallbackInfo ci){
//        LAYERS_TO_LOADERS.put();
        var layersToLoaders = new HashMap<>(LAYERS_TO_LOADERS);
        layersToLoaders.put(new Identifier("textures/atlas/banner_patterns.png"), new Identifier("stack_the_cards", "card_backs"));
        LAYERS_TO_LOADERS = layersToLoaders;

    }

    /*@Inject(method = "<clinit>", at = @At("TAIL"), remap = false)
    private static void injected(CallbackInfo ci, @Local LocalRef<Map<Identifier, Identifier>> layersToLoadersCapture) {
        var layersToLoaders = new HashMap<>(LAYERS_TO_LOADERS);
        layersToLoaders.put(new Identifier("textures/atlas/card_backs.png"), new Identifier("stack_the_cards", "card_backs"));
        layersToLoadersCapture.set(layersToLoaders);
    }*/
}
