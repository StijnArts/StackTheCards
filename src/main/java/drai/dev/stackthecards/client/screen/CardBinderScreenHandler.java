package drai.dev.stackthecards.client.screen;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;

import java.util.*;

public class CardBinderScreenHandler extends ScreenHandler {
    private final CardBinderInventory inventory;
    public final List<CardItemSlot> cardSlots = new ArrayList<>();
    public CardBinderScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(StackTheCards.CARD_BINDER_SCREEN_HANDLER, syncId);
        this.inventory = new CardBinderInventory(playerInventory.player);
        inventory.onOpen(playerInventory.player);
        //This will place the slot in the correct locations for a 3x3 Grid. The slots exist on both server and client!
        //This will not render the background of the slots however, this is the Screens job
        int m;
        int l;
        //The player inventory
        for (m = 0; m < 3; ++m) {
            for (l = 0; l < 9; ++l) {
                var slot = new Slot(playerInventory, l + m * 9 + 9, 48 + l * 18, 182+m * 18);
                this.addSlot(slot);
            }
        }
        int slotIndex = 0;
        for (int page = 0; page < 2; page++) {
            for (int j = 0; j < 2; ++j) {
                for (int i = 0; i < 2; i++) {
                    var slot = new CardItemSlot(inventory, slotIndex, 27 + i * 54 + page*132, 68 + j * 73);
                    this.addSlot(slot);
                    cardSlots.add(slot);
                    slotIndex++;
                }
            }
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
        if(player instanceof ClientPlayerEntity){

        } else {
            if (slot.hasStack() && slot.getStack().isOf(Items.CARD)) {
                ItemStack originalStack = slot.getStack();
                newStack = originalStack.copy();
                if (invSlot < 3*9 ? !this.insertItem(originalStack, 3*9, this.slots.size(), false) : !this.insertItem(originalStack, 0, 3*9, false)) {
                    return ItemStack.EMPTY;
                }
                if (originalStack.isEmpty()) {
                    slot.setStack(ItemStack.EMPTY);
                } else {
                    slot.markDirty();
                }
            }
        }
        return newStack;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return this.inventory.canPlayerUse(player);
    }

    public void checkEnabledSlots() {
        for (CardItemSlot slot : cardSlots) {
            slot.checkEnabled();
        }
    }

    public static class CardItemSlot extends Slot {
        private int cardsPerPage = CardBinder.MAX_CARDS_PER_PAGE;
        private boolean enabled;

        public CardItemSlot(CardBinderInventory inventory, int index, int x, int y) {
            super(inventory, index, x, y);
            this.checkEnabled();
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
            if(!isEnabled()) return;
            this.inventory.setStack(getInventoryIndex(), stack);
        }

        @Override
        public ItemStack takeStack(int amount) {
            if(!isEnabled()) return ItemStack.EMPTY;
            var stack = this.inventory.removeStack(getInventoryIndex(), amount);
            return stack;
        }

        @Override
        public void setStackNoCallbacks(ItemStack stack) {
            if(!isEnabled()) return;
            this.inventory.setStack(getInventoryIndex(), stack);
            this.markDirty();
        }

        public int getInventoryIndex(){
            var index = getIndex();
            var inventoryIndex = index % cardsPerPage + cardsPerPage * StackTheCardsClient.PAGE_INDEX;
            return inventoryIndex;
        }

        @Override
        public boolean canInsert(ItemStack stack) {
            if(!enabled) return false;
            return stack.isOf(Items.CARD);
        }

        @Override
        public boolean canBeHighlighted() {
            return enabled;
        }

        @Override
        public boolean canTakePartial(PlayerEntity player) {
            return enabled;
        }

        @Override
        public boolean canTakeItems(PlayerEntity playerEntity) {
            return enabled;
        }

        @Override
        public Optional<ItemStack> tryTakeStackRange(int min, int max, PlayerEntity player) {
            if(!isEnabled()) Optional.empty();
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
            if(!enabled) return stack;
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

        public void checkEnabled() {
            var index = this.getInventoryIndex();
            var isWithinBounds = index < inventory.size();
            this.enabled = isWithinBounds;
        }

        public boolean isEnabled(){
            return enabled;
        }
    }
}
