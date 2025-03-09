package drai.dev.stackthecards.recipes;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.items.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.StackTheCards.CUSTOM_BINDER;
import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinderCustomizationRecipe extends CustomRecipe {
    public CardBinderCustomizationRecipe(ResourceLocation id, CraftingBookCategory category) {
        super(id, category);
    }

    @Override
    public boolean matches(CraftingContainer inventory, Level level) {
        int i = 0;
        int j = 0;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int k = 0; k < inventory.getContainerSize(); ++k) {
            ItemStack itemStack = inventory.getItem(k);
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
        var binderInventory = NonNullList.withSize(CardBinderInventory.getInventorySize(cardBinder), ItemStack.EMPTY);
        var compound2 = cardBinder.getOrCreateTag().getCompound(CARD_BINDER_INVENTORY_KEY);
        ContainerHelper.loadAllItems(compound2, binderInventory);
        var inventoryIsEmpty = binderInventory.stream().allMatch(ItemStack::isEmpty);
        return i == 1 && j == 1 && inventoryIsEmpty;
    }

    @Override
    public ItemStack assemble(CraftingContainer inventory, RegistryAccess registryManager) {
        ItemStack gameOrSetDefiningItem = ItemStack.EMPTY;
        ItemStack cardBinder = ItemStack.EMPTY;
        for (int i = 0; i < inventory.getContainerSize(); ++i) {
            ItemStack iterativeItemstack = inventory.getItem(i);
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
        CardIdentifier cardResourceLocation = null;
        var item = gameOrSetDefiningItem.getItem();
        if(item instanceof Card){
            var cardData = Card.getCardData(gameOrSetDefiningItem);
            inventorySize = cardData.getCountInGroup();
            cardResourceLocation = cardData.getCardIdentifier();
        } else {
            var cardData = CardPack.getCardPack(gameOrSetDefiningItem);
            inventorySize = cardData.getCountInGroup();
            cardResourceLocation = new CardIdentifier(cardData.getGameId(), cardData.getSetId(), cardData.getPackId(), "");
        }
        var itemStackResult = cardBinder.copy();
        if(cardBinder.hasTag()){
            itemStackResult.setTag(cardBinder.getTag().copy());
        }
        var nbt = itemStackResult.getOrCreateTag();
        nbt.putInt(CARD_BINDER_SIZE_KEY, inventorySize);
        nbt.put(CARD_BINDER_RESTRICTION_KEY, CardIdentifier.createNbt(cardResourceLocation));
        return itemStackResult;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CUSTOM_BINDER;
    }
}
