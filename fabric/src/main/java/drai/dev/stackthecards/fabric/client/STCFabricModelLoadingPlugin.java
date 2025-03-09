package drai.dev.stackthecards.fabric.client;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.models.*;
import net.fabricmc.fabric.api.client.model.loading.v1.*;
import net.minecraft.server.packs.resources.*;

public class STCFabricModelLoadingPlugin implements ModelLoadingPlugin {
    @Override
    public void onInitializeModelLoader(Context context) {

    }

    public void onInitializeModelLoader(ResourceManager manager, Context context) {
        StackTheCardsModelLoader.loadModels(manager);
        context.addModels(StackTheCardsClient.CARD_BACK_MODELS);
        context.addModels(StackTheCardsClient.CARD_PACK_MODELS);
    }
}
