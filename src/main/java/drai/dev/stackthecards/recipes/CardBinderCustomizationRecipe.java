package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
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

import static drai.dev.stackthecards.StackTheCards.CUSTOM_BINDER;
import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderCustomizationRecipe extends SpecialCraftingRecipe {
    public CardBinderCustomizationRecipe(Identifier id, CraftingRecipeCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        int i = 0;
        int j = 0;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int k = 0; k < inventory.size(); ++k) {
            ItemStack itemStack = inventory.getStack(k);
            if (itemStack.isEmpty()) continue;
            if (itemStack.getItem() instanceof Card || itemStack.getItem() instanceof CardPackItem) {
                ++i;
            } else if (itemStack.getItem() instanceof CardBinder) {
                ++j;
                cardBinder = itemStack;
            } else {
                return false;
            }
            if (j <= 1 && i <= 1) continue;
            return false;
        }
        var binderInventory = DefaultedList.ofSize(CardBinderInventory.getInventorySize(cardBinder), ItemStack.EMPTY);
        var compound2 = cardBinder.getOrCreateNbt().getCompound(CARD_BINDER_INVENTORY_KEY);
        Inventories.readNbt(compound2, binderInventory);
        var inventoryIsEmpty = binderInventory.stream().allMatch(ItemStack::isEmpty);
        return i == 1 && j == 1 && inventoryIsEmpty;
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack gameOrSetDefiningItem = ItemStack.EMPTY;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.size(); ++i) {
            ItemStack iterativeItemstack = inventory.getStack(i);
            if (iterativeItemstack.isEmpty()) continue;
            Item item = iterativeItemstack.getItem();
            if (item instanceof Card || item instanceof CardPackItem) {
                gameOrSetDefiningItem = iterativeItemstack;
                continue;
            }
            if (!(item instanceof CardBinder)) continue;
            cardBinder = iterativeItemstack;
        }
        var inventorySize = 120;
        CardIdentifier cardIdentifier = null;
        var item = gameOrSetDefiningItem.getItem();
        if(item instanceof Card){
            var cardData = Card.getCardData(gameOrSetDefiningItem);
            inventorySize = cardData.getCountInGroup();
            cardIdentifier = cardData.getCardIdentifier();
        } else {
            var cardData = CardPack.getCardPack(gameOrSetDefiningItem);
            inventorySize = cardData.getCountInGroup();
            cardIdentifier = new CardIdentifier(cardData.getGameId(), cardData.getSetId(), cardData.getPackId(), "");
        }
        var itemStackResult = cardBinder.copy();
        if(cardBinder.hasNbt()){
            itemStackResult.setNbt(cardBinder.getNbt().copy());
        }
        var nbt = itemStackResult.getOrCreateNbt();
        nbt.putInt(CARD_BINDER_SIZE_KEY, inventorySize);
        nbt.put(CARD_BINDER_RESTRICTION_KEY, CardIdentifier.createNbt(cardIdentifier));
        return itemStackResult;
    }

    @Override
    public boolean fits(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CUSTOM_BINDER;
    }
}
