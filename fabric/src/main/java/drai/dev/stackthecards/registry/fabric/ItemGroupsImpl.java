package drai.dev.stackthecards.registry.fabric;

import net.fabricmc.fabric.api.itemgroup.v1.*;
import net.minecraft.world.item.*;

public class ItemGroupsImpl {
    public static CreativeModeTab.Builder getBuilder(){
        return FabricItemGroup.builder();
    }
}
