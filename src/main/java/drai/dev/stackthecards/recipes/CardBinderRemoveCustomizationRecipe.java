package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderRemoveCustomizationRecipe extends CustomRecipe {
    public CardBinderRemoveCustomizationRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level level) {
        int i = 0;
        for (int k = 0; k < inventory.getContainerSize(); ++k) {
            ItemStack itemStack = inventory.getItem(k);
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
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack iterativeItemstack = inventory.getItem(i);
            if (iterativeItemstack.isEmpty()) continue;
            Item item = iterativeItemstack.getItem();
            if (!(item instanceof CardBinder)) continue;
            cardBinder = iterativeItemstack;
        }
        var itemStackResult = cardBinder.copy();
        if(cardBinder.hasTag()){
            itemStackResult.setTag(cardBinder.getTag().copy());
        }
        var nbt = itemStackResult.getOrCreateTag();
        nbt.remove(CARD_BINDER_SIZE_KEY);
        nbt.remove(CARD_BINDER_RESTRICTION_KEY);
        nbt.remove(CARD_BINDER_EFFECT_KEY);
        nbt.remove(CARD_BINDER_SHOULD_APPLY_EFFECT_KEY);
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
