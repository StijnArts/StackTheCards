package drai.dev.stackthecards.data;

import net.minecraft.client.*;
import net.minecraft.client.item.*;
import net.minecraft.client.render.model.*;
import net.minecraft.client.util.*;
import net.minecraft.util.*;

import java.util.*;

public class CardSet {
    public Map<String, Identifier> textures;
    private CardGame cardGame;
    private String setId;

    public CardGame getCardGame() {
        return cardGame;
    }

    public String getSetIdentifier() {
        return cardGame.getGameIdentifier() + "_" + setId;
    }

    /*public BakedModel getItemModel() {
        // Get the model identifier for the item
        ModelIdentifier modelIdentifier = getModelIdentifier();

        // Load the unbaked model
        UnbakedModel unbakedModel = ModelLoader.MODELS_FINDER.toResourceId().getModelOrMissing(modelIdentifier);

        // Bake the model with the new texture
        BakedModel bakedModel = unbakedModel.bake(ModelLoader.defaultTextureGetter(), ModelLoader.defaultModelGetter(), ModelLoader.getVanillaResourceProvider(), new ModelBakeSettings(null, ModelLoader.defaultModelLoader().getOutliner()));

        // Register the baked model with the model loader
        ModelLoader.instance().putModel(modelIdentifier, bakedModel);

        // Invalidate the model predicate provider for the item
        ModelPredicateProviderRegistry.register(this, modelIdentifier, (stack, world, entity, seed) -> 0);
    }*/
}
