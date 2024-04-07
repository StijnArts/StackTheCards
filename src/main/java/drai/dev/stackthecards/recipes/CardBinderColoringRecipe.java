package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.block.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

import static drai.dev.stackthecards.StackTheCards.BINDER_COLORING;
import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderColoringRecipe extends SpecialCraftingRecipe {

    public CardBinderColoringRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getStack(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof CardBinder) {
                ++i;
            } else if (itemStack.getItem() instanceof DyeItem) {
                ++j;
            } else {
                return false;
            }
            if (j <= 1 && i <= 1) continue;
            return false;
        }
        return i == 1 && j == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        DyeItem dyeItem = (DyeItem)Items.WHITE_DYE;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack iterativeItemstack = inventory.getStack(i);
            if (iterativeItemstack.isEmpty()) continue;
            Item item = iterativeItemstack.getItem();
            if (iterativeItemstack.getItem() instanceof CardBinder) {
                cardBinder = iterativeItemstack;
                continue;
            }
            if (!(item instanceof DyeItem)) continue;
            dyeItem = (DyeItem)item;
        }
        ItemStack itemStack3 = CardBinder.getItemStack(dyeItem.getColor());
        if (cardBinder.hasNbt()) {
            itemStack3.setNbt(cardBinder.getNbt().copy());
        }
        return itemStack3;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BINDER_COLORING;
    }
}
