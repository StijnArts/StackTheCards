package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.StackTheCards.*;
import static drai.dev.stackthecards.data.components.StackTheCardsComponentTypes.*;

public class CardPackMultiplierRecipe extends CustomRecipe {
    public CardPackMultiplierRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        int i = 0;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getItem(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof CardPackItem) {
                ++i;
                if(i == 5){
                    if(!itemStack.has(CARD_PACK_DATA_COMPONENT.get())) return false;
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
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryManager) {
        var middleStack = inventory.getItem(4);
        var newStack = middleStack.copyWithCount(9);
        newStack.set(CARD_PACK_DATA_COMPONENT.get(), middleStack.get(CARD_PACK_DATA_COMPONENT.get()));
        return newStack;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height == 9;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return PACK_MULTIPLYING_RECIPE.get();
    }
}
