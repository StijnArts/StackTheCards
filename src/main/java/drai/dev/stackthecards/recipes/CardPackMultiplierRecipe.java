package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.StackTheCards.PACK_MULTIPLYING_RECIPE;

public class CardPackMultiplierRecipe extends CustomRecipe {
    public CardPackMultiplierRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level level) {
        int i = 0;
        for (int k = 0; k < inventory.getContainerSize(); ++k) {
            ItemStack itemStack = inventory.getItem(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof CardPackItem) {
                ++i;
                if(i == 5){
                    if(!itemStack.hasTag()) return false;
                    if(!CardPack.getCardPack(itemStack).duplicationAllowed) return false;
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
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        var middleStack = inventory.getItem(4);
        var newStack = middleStack.copyWithCount(9);
        newStack.setTag(middleStack.getTag().copy());
        return newStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height == 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PACK_MULTIPLYING_RECIPE;
    }
}
