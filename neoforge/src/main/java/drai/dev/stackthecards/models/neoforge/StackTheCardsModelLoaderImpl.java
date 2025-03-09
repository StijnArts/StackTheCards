package drai.dev.stackthecards.models.neoforge;

import net.minecraft.client.renderer.entity.*;
import net.minecraft.client.resources.model.*;
import org.jetbrains.annotations.*;
import org.spongepowered.asm.mixin.*;

public class StackTheCardsModelLoaderImpl {

    @Unique
    public static @NotNull BakedModel getBakedModel(ItemRenderer itemRenderer, ModelResourceLocation modelResourceLocation, ModelResourceLocation fallBackModel) {
        return itemRenderer.getItemModelShaper().getModelManager().getModel(modelResourceLocation);
    }
}
