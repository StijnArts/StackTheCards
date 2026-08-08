package drai.dev.stackthecards.neoforge.client;

import drai.dev.stackthecards.models.*;
import net.minecraft.server.packs.resources.*;
import net.minecraft.util.profiling.*;

public class StackTheCardsModelReloadListener extends SimplePreparableReloadListener<Void> {

    @Override
    protected Void prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        // Load and parse model Json here, but don't touch rendering
        StackTheCardsModelLoader.loadModels(resourceManager);
        return null;
    }

    @Override
    protected void apply(Void nothing, ResourceManager resourceManager, ProfilerFiller profiler) {
        // Apply parsed models here; safe to update state
//        StackTheCardsModelLoader.applyModels();
    }
}

