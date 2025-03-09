package drai.dev.stackthecards.models.fabric;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.model.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

public class StackTheCardsModelLoaderImpl {

    @Unique
    public static @NotNull BakedModel getBakedModel(ItemRenderer itemRenderer, ModelResourceLocation modelResourceLocation, ModelResourceLocation fallBackModel) {
        //        if(model == null){
//            model = itemRenderer.getItemModelShaper().getModelManager().getModel(fallBackModel);
//        }
        return itemRenderer.getItemModelShaper().getModelManager().getModel(modelResourceLocation.id());
    }
}
