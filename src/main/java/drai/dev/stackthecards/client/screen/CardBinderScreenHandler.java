package drai.dev.stackthecards.client.screen;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CardBinderScreenHandler extends ScreenHandler {
    private final CardBinderInventory inventory;
    public final List<CardItemSlot> cardSlots = new ArrayList<>();
    @Nullable
    private CardIdentifier boundIdentifier;
    private boolean isBound;

    public CardBinderScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(StackTheCards.CARD_BINDER_SCREEN_HANDLER, syncId);
        this.inventory = new CardBinderInventory(playerInventory.player);
        inventory.onOpen(playerInventory.player);
        this.isBound = inventory.isBound(playerInventory.player);
        if(isBound){
            boundIdentifier = inventory.getBoundIdentifier(playerInventory.player);
        }
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
                    var slot = new CardItemSlot(inventory, slotIndex, 27 + i * 54 + page*132, 68 + j * 73, playerInventory.player, isBound, boundIdentifier);
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
        //TODO shift clicking out of the binder inventory currently sets stack count to max
        ItemStack newStack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(invSlot);
        if(!(player instanceof ClientPlayerEntity)){
            if (clickedSlot.hasStack() && clickedSlot.getStack().isOf(Items.CARD)) {

                ItemStack originalStack = clickedSlot.getStack();
                newStack = originalStack.copy();
                boolean useCardBinderQuickmove = false;
                var cardData = Card.getCardData(originalStack);
                var set = cardData.getCardSet();
                if(isBound){
                    useCardBinderQuickmove = set.getCardGame().getGameId().equalsIgnoreCase(boundIdentifier.gameId)&&set.getSetId().equalsIgnoreCase(boundIdentifier.setId);
                }
                if(useCardBinderQuickmove && invSlot < 3*9){
                    var cardIndex = cardData.index;
                    if(!insertItem(originalStack, cardIndex, player)){
                        return ItemStack.EMPTY;
                    } /*else {
                        if (!super.insertItem(originalStack, 0, , false) : !super.insertItem(originalStack, 0, 3*9, false)) {
                            return ItemStack.EMPTY;
                        }
                    }*/
                    if (originalStack.isEmpty()) {
                        clickedSlot.setStack(ItemStack.EMPTY);
                    } else {
                        clickedSlot.markDirty();
                    }
                } else {
                    if (invSlot < 3*9 ? !super.insertItem(originalStack, 3*9, this.slots.size(), false) : !super.insertItem(originalStack, 0, 3*9, false)) {
                        return ItemStack.EMPTY;
                    }
                    if (originalStack.isEmpty()) {
                        clickedSlot.setStack(ItemStack.EMPTY);
                    } else {
                        clickedSlot.markDirty();
                    }
                }

            }
        }
        return newStack;
    }

    protected boolean insertItem(ItemStack stack, int index, PlayerEntity player) {
        ItemStack itemStack;
        boolean bl = false;
        int i = index;
        if (stack.isStackable()) {
            while (!stack.isEmpty() && i == index) {
                itemStack = inventory.getStack(i);
                if (!itemStack.isEmpty() && ItemStack.canCombine(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= stack.getMaxCount()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxCount()) {
                        stack.decrement(stack.getMaxCount() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxCount());
                        bl = true;
                    }
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = index;
            while (index == i) {
                itemStack = inventory.getStack(i);

                if (itemStack.isEmpty()) {
                    if (stack.getCount() > 64) {
                        inventory.setStack(i, stack.split(64), player);
                    } else {
                        inventory.setStack(i, stack.split(stack.getCount()), player);
                    }
                    bl = true;
                    break;
                }
                ++i;
            }
        }
        return bl;
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
        private boolean isBound;
        private CardIdentifier boundIdentifier;
        private PlayerEntity player;

        public CardItemSlot(CardBinderInventory inventory, int index, int x, int y, PlayerEntity player, boolean isBound, @Nullable CardIdentifier boundIdentifier) {
            super(inventory, index, x, y);
            this.player = player;
            this.isBound = isBound;
            this.boundIdentifier = boundIdentifier;
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
            ((CardBinderInventory)this.inventory).setStack(getInventoryIndex(), stack, player);
        }

        @Override
        public ItemStack takeStack(int amount) {
            if(!isEnabled()) return ItemStack.EMPTY;
            var stack = ((CardBinderInventory)this.inventory).removeStack(getInventoryIndex(), amount, player);
            return stack;
        }

        @Override
        public void setStackNoCallbacks(ItemStack stack) {
            if(!isEnabled()) return;
            ((CardBinderInventory)this.inventory).setStack(getInventoryIndex(), stack, player);
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
            if(!stack.isOf(Items.CARD)) return false;
            if(!isBound) return true;
            var data = Card.getCardData(stack);
            var set = data.getCardSet();
            return set.getCardGame().getGameId().equalsIgnoreCase(boundIdentifier.gameId) && set.getSetId().equalsIgnoreCase(boundIdentifier.setId) && data.index == getInventoryIndex();
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
