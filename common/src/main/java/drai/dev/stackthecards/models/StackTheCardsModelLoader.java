package drai.dev.stackthecards.models;

import dev.architectury.injectables.annotations.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.*;
import net.minecraft.server.packs.resources.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

import java.util.*;

import static drai.dev.stackthecards.client.StackTheCardsClient.CARD_BACK_MODELS;
import static drai.dev.stackthecards.client.StackTheCardsClient.CARD_PACK_MODELS;

public class StackTheCardsModelLoader {

    public static void loadModels(ResourceManager manager) {
        CARD_BACK_MODELS.clear();
        manager.listResources("models/stc_cards/backs", path -> path.getPath().endsWith(".json")).forEach((id, resource) -> {
            ResourceLocation newResourceLocation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                    id.getPath().replace("models/", "").replace(".json", ""));
            CARD_BACK_MODELS.add(newResourceLocation);
        });

        CARD_PACK_MODELS.clear();
        manager.listResources("models/stc_cards/packs", path -> path.getPath().endsWith(".json")).forEach((id, resource) -> {
            ResourceLocation newResourceLocation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(),
                    id.getPath().replace("models/", "").replace(".json", ""));
            CARD_PACK_MODELS.add(newResourceLocation);
        });
    }



    @ExpectPlatform
    public static @NotNull BakedModel getBakedModel(ItemRenderer itemRenderer, ModelResourceLocation modelResourceLocation, ModelResourceLocation fallBackModel) {
        throw new AssertionError();
    }
    /*private static final Set<ResourceLocation> CARD_BACK_MODELS = new HashSet<>();
    private static final Set<ResourceLocation> CARD_PACK_MODELS = new HashSet<>();

    public static Set<ResourceLocation> getCardBackModels() {
        return CARD_BACK_MODELS;
    }

    public static Set<ResourceLocation> getCardPackModels() {
        return CARD_PACK_MODELS;
    }

    public static void initialize() {
        loadModels();
    }

    @ExpectPlatform
    public static void loadModels() {
        // This will be replaced by platform-specific implementations.
        throw new AssertionError();
    }*/
    /*@Override
    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
    }

    public void onInitializeModelLoader(ResourceManager manager, Context context) {
        CARD_BACK_MODELS.clear();
        CARD_BACK_MODELS.addAll(manager.listResources("models/stc_cards/backs", path-> path.getPath().endsWith(".json")).keySet());
        for (ResourceLocation identifier : CARD_BACK_MODELS) {
            var newResourceLocation  =ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), identifier.getPath().replaceAll("models/", "").replaceAll(".json",""));
            context.addModels(newResourceLocation);
        }
        CARD_PACK_MODELS.clear();
        CARD_PACK_MODELS.addAll(manager.listResources("models/stc_cards/packs", path-> path.getPath().endsWith(".json")).keySet());
        for (ResourceLocation identifier : CARD_PACK_MODELS) {
            var newResourceLocation  =ResourceLocation.fromNamespaceAndPath(identifier.getNamespace(), identifier.getPath().replaceAll("models/", "").replaceAll(".json",""));
            context.addModels(newResourceLocation);
        }
    }*/
}
