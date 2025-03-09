package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.data.recipes.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.StackTheCards.BINDER_COLORING;
import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderColoringRecipe extends CustomRecipe {

    public CardBinderColoringRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level level) {
        int i = 0;
        int j = 0;
        for (int k = 0; k < inventory.getContainerSize(); ++k) {
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
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        DyeItem dyeItem = (DyeItem)Items.WHITE_DYE;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
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
        if (cardBinder.hasTag()) {
            assert cardBinder.getTag() != null;
            itemStack3.setTag(cardBinder.getTag().copy());
        }
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
