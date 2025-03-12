package drai.dev.stackthecards.registry;

import net.minecraft.core.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.*;

public class StackTheCardsBlocks {
    public static final Block GAMING_TABLE = registerItem(new Block(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)),"gaming_table");
    public static <T extends Block> T registerItem(T item, String name){
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation("stack_the_cards", name), item);
        return item;
    }

    public static void register(){
        System.out.println("Registered items for Stack the Cards");
    }
}
