package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.book.*;
import net.minecraft.registry.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderRemoveCustomizationRecipe extends SpecialCraftingRecipe {
    public CardBinderRemoveCustomizationRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int i = 0;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getStack(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof CardBinder) {
                ++i;
            } else {
                return false;
            }
            if (i <= 1) continue;
            return false;
        }
        return i == 1;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack iterativeItemstack = inventory.getStack(i);
            if (iterativeItemstack.isEmpty()) continue;
            Item item = iterativeItemstack.getItem();
            if (!(item instanceof CardBinder)) continue;
            cardBinder = iterativeItemstack;
        }
        var itemStackResult = cardBinder.copy();
        if(cardBinder.hasNbt()){
            itemStackResult.setNbt(cardBinder.getNbt().copy());
        }
        var nbt = itemStackResult.getOrCreateNbt();
        nbt.remove(CARD_BINDER_SIZE_KEY);
        nbt.remove(CARD_BINDER_RESTRICTION_KEY);
        nbt.remove(CARD_BINDER_EFFECT_KEY);
        nbt.remove(CARD_BINDER_SHOULD_APPLY_EFFECT_KEY);
        return itemStackResult;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return StackTheCards.BINDER_REMOVE_CUSTOM;
    }
}
