package drai.dev.stackthecards.models.neoforge;

import drai.dev.stackthecards.mixin.client.*;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.model.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

public class StackTheCardsModelLoaderImpl {

    @Unique
    public static @NotNull BakedModel getBakedModel(ItemRenderer itemRenderer, ModelResourceLocation modelResourceLocation, ModelResourceLocation fallBackModel) {
        ModelManager modelManager = itemRenderer.getItemModelShaper().getModelManager();
//        var modModels = ((ModelManagerAccessor)modelManager).getBakedRegistry().entrySet().stream().filter(modelResourceLocationBakedModelEntry -> modelResourceLocationBakedModelEntry.getKey().id().getPath().contains("cards/")).toList();
        var model = modelManager.getModel(modelResourceLocation);
        if(model == null){
            model = modelManager.getModel(fallBackModel);
        }
        return model;
    }
}
