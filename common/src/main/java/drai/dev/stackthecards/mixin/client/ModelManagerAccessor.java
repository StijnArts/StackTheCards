package drai.dev.stackthecards.mixin.client;

import net.minecraft.client.resources.model.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

import java.util.*;

@Mixin(ModelManager.class)
public interface ModelManagerAccessor {
    @Accessor("bakedRegistry")
    Map<ModelResourceLocation, BakedModel> getBakedRegistry();

}
