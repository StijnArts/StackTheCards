package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.StackTheCards.*;

public class CardBinderColoringRecipe extends CustomRecipe {

    public CardBinderColoringRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getItem(k);
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
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryManager) {
        DyeItem dyeItem = (DyeItem) Items.WHITE_DYE;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack iterativeItemstack = inventory.getItem(i);
            if (iterativeItemstack.isEmpty()) continue;
            Item item = iterativeItemstack.getItem();
            if (iterativeItemstack.getItem() instanceof CardBinder) {
                cardBinder = iterativeItemstack;
                continue;
            }
            if (!(item instanceof DyeItem)) continue;
            dyeItem = (DyeItem)item;
        }
        ItemStack itemStack3 = CardBinder.getItemStack(dyeItem.getDyeColor());
//        if (cardBinder.hasTag()) {
//            assert cardBinder.getTag() != null;
//            itemStack3.setTag(cardBinder.getTag().copy());
//        }
        CardBinderData.saveChanges(itemStack3, CardBinderData.getOrCreate(cardBinder));
        return itemStack3;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BINDER_COLORING;
    }
}
