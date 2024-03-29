package drai.dev.stackthecards.models;

import drai.dev.stackthecards.client.*;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.minecraft.resource.*;
import net.minecraft.util.*;

import static drai.dev.stackthecards.client.StackTheCardsClient.CARD_BACK_MODELS;

public class StackTheCardsModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(ModelLoadingPlugin.Context pluginContext) {
    }

    public void onInitializeModelLoader(ResourceManager manager, Context context) {
        CARD_BACK_MODELS.clear();
        CARD_BACK_MODELS.addAll(manager.findResources("models/stc_cards/backs", path-> path.getPath().endsWith(".json")).keySet());
        for (Identifier identifier : CARD_BACK_MODELS) {
            var newIdentifier  =new Identifier(identifier.getNamespace(), identifier.getPath().replaceAll("models/", "").replaceAll(".json",""));
            context.addModels(newIdentifier);
        }
    }
}
