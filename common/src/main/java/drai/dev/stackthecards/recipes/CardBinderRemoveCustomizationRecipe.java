package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.data.components.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderRemoveCustomizationRecipe extends CustomRecipe {
    public CardBinderRemoveCustomizationRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput inventory, Level level) {
        int i = 0;
        ItemStack cardBinder = null;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getItem(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof CardBinder) {
                ++i;
                cardBinder = itemStack;
            } else {
                return false;
            }
        }
        if(cardBinder == null) return false;
        var data = CardBinderData.getOrCreate(cardBinder);
        var inventoryIsEmpty = data.inventory.stream().allMatch(ItemStack::isEmpty);
        return inventoryIsEmpty;
    }

    @Override
    public ItemStack assemble(CraftingInput inventory, HolderLookup.Provider registryManager) {
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack iterativeItemstack = inventory.getItem(i);
            if (iterativeItemstack.isEmpty()) continue;
            Item item = iterativeItemstack.getItem();
            if (!(item instanceof CardBinder)) continue;
            cardBinder = iterativeItemstack;
        }
        var itemStackResult = cardBinder.copy();
        if(cardBinder.has(StackTheCardsComponentTypes.CARD_BINDER_DATA_COMPONENT.get())){
            CardBinderData.saveChanges(itemStackResult, new CardBinderData());
        }
        return itemStackResult;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return StackTheCards.BINDER_REMOVE_CUSTOM;
    }
}
