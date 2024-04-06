package drai.dev.stackthecards.client.screen;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;

import java.util.*;

public class CardBinderScreenHandler extends ScreenHandler {
    private final CardBinderInventory inventory;
    private final List<CardItemSlot> cardSlots = new ArrayList<>();
    public CardBinderScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(StackTheCards.CARD_BINDER_SCREEN_HANDLER, syncId);
        this.inventory = new CardBinderInventory();

        inventory.onOpen(playerInventory.player);

        //This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        int i = (3 - 4) * 18;
        int m;
        int l;
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                var slot = new Slot(playerInventory, l + m * 9 + 9, 8 + l * 18, 103 + m * 18+i);
                this.addSlot(slot);
            }
        }

        for (m = 0; m < CardBinder.MAX_CARDS_PER_PAGE; ++m) {
                var slot = new CardItemSlot(inventory, m, 62 + m * 18, 17 /*+ m * 18*/);
                this.addSlot(slot);
                cardSlots.add(slot);
        }
    }

    @Override
    public void onClosed(PlayerEntity player) {
        inventory.onClose(player);
        super.onClosed(player);
    }

    @Override
    public void onSlotClick(int slotIndex, int button, SlotActionType actionType, PlayerEntity player) {
        super.onSlotClick(slotIndex, button, actionType, player);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int invSlot) {
        ItemStack newStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(invSlot);
        if (slot != null && slot.hasStack() && slot.getStack().isOf(Items.CARD)) {
            ItemStack originalStack = slot.getStack();
            newStack = originalStack.copy();
            if (invSlot < this.inventory.size()) {
                if (!this.insertItem(originalStack, this.inventory.size(), this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.insertItem(originalStack, 0, this.inventory.size(), false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.setStack(ItemStack.EMPTY);
            } else {
                slot.markDirty();
            }
        }

        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public static class CardItemSlot extends Slot {
        private int cardsPerPage = CardBinder.MAX_CARDS_PER_PAGE;

        public CardItemSlot(CardBinderInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
        }

        @Override
        public ItemStack getStack(){
            try{
                return inventory.getStack(getInventoryIndex());
            } catch (Exception e){
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setStack(ItemStack stack) {
            this.inventory.setStack(getInventoryIndex(), stack);
        }

        @Override
        public ItemStack takeStack(int amount) {
            var stack = this.inventory.removeStack(getInventoryIndex(), amount);
            return stack;
        }

        @Override
        public void setStackNoCallbacks(ItemStack stack) {
            this.inventory.setStack(getInventoryIndex(), stack);
            this.markDirty();
        }

        public int getInventoryIndex(){
            var index = getIndex();
            var inventoryIndex = index %cardsPerPage + cardsPerPage * StackTheCardsClient.PAGE_INDEX;
            return inventoryIndex;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            return stack.isOf(Items.CARD);
        }

        @Override
        public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
            if (!this.canTakeItems(player)) {
                return Optional.empty();
            }
            if (!this.canTakePartial(player) && max < this.getStack().getCount()) {
                return Optional.empty();
            }
            ItemStack itemStack = this.takeStack(min = Math.min(min, max));
            if (itemStack.isEmpty()) {
                return Optional.empty();
            }
            if (this.getStack().isEmpty()) {
                this.setStack(ItemStack.EMPTY);
            }
            return Optional.of(itemStack);
        }

        @Override
        public ItemStack takeStackRange(int min, int max, PlayerEntity player) {
            Optional<ItemStack> optional = this.tryTakeStackRange(min, max, player);
            optional.ifPresent(stack -> this.onTakeItem(player, (ItemStack)stack));
            return optional.orElse(ItemStack.EMPTY);
        }

        @Override
        public ItemStack insertStack(ItemStack stack) {
            return this.insertStack(stack, stack.getCount());
        }

        @Override
        public ItemStack insertStack(ItemStack stack, int count) {
            if (stack.isEmpty() || !this.canInsert(stack)) {
                return stack;
            }
            ItemStack itemStack = this.getStack();
            int i = Math.min(Math.min(count, stack.getCount()), this.getMaxItemCount(stack) - itemStack.getCount());
            if (itemStack.isEmpty()) {
                this.setStack(stack.split(i));
            } else if (ItemStack.canCombine(itemStack, stack)) {
                stack.decrement(i);
                itemStack.increment(i);
                this.setStack(itemStack);
            }
            return stack;
        }
    }
}
