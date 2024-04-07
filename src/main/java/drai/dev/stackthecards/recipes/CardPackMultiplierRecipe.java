package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.items.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.world.*;

import static drai.dev.stackthecards.StackTheCards.PACK_MULTIPLYING_RECIPE;

public class CardPackMultiplierRecipe extends SpecialCraftingRecipe {
    public CardPackMultiplierRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int i = 0;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getStack(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof CardPackItem) {
                ++i;
                if(i == 5){
                    if(!itemStack.hasNbt()) return false;
                }
            } else {
                return false;
            }
            if (i <= 9) continue;
            return false;
        }
        return i == 9;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        var middleStack = inventory.getStack(4);
        var newStack = middleStack.copyWithCount(9);
        newStack.setNbt(middleStack.getNbt().copy());
        return newStack;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height == 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PACK_MULTIPLYING_RECIPE;
    }
}
