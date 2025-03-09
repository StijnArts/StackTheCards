package drai.dev.stackthecards.models;

import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.minecraft.resources.*;
import net.minecraft.server.packs.resources.*;

import static drai.dev.stackthecards.client.StackTheCardsClient.CARD_BACK_MODELS;
import static drai.dev.stackthecards.client.StackTheCardsClient.CARD_PACK_MODELS;

public class StackTheCardsModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
    }

    public void onInitializeModelLoader(ResourceManager manager, Context context) {
        CARD_BACK_MODELS.clear();
        CARD_BACK_MODELS.addAll(manager.listResources("models/stc_cards/backs", path-> path.getPath().endsWith(".json")).keySet());
        for (ResourceLocation identifier : CARD_BACK_MODELS) {
            var newResourceLocation  =new ResourceLocation(identifier.getNamespace(), identifier.getPath().replaceAll("models/", "")
                    .replaceAll(".json",""));
            context.addModels(newResourceLocation);
        }
        CARD_PACK_MODELS.clear();
        CARD_PACK_MODELS.addAll(manager.listResources("models/stc_cards/packs", path-> path.getPath().endsWith(".json")).keySet());
        for (ResourceLocation identifier : CARD_PACK_MODELS) {
            var newResourceLocation  =new ResourceLocation(identifier.getNamespace(), identifier.getPath().replaceAll("models/", "")
                    .replaceAll(".json",""));
            context.addModels(newResourceLocation);
        }
    }
}
