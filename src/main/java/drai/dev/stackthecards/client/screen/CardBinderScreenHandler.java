package drai.dev.stackthecards.client.screen;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.player.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.*;
import org.jetbrains.annotations.*;

import java.util.*;

public class CardBinderScreenHandler extends AbstractContainerMenu {
    private final CardBinderInventory inventory;
    public final List<CardItemSlot> cardSlots = new ArrayList<>();
    @Nullable
    private CardIdentifier boundResourceLocation;
    private boolean isBound;

    public CardBinderScreenHandler(int syncId, Inventory playerInventory) {
        super(StackTheCards.CARD_BINDER_SCREEN_HANDLER, syncId);
        this.inventory = new CardBinderInventory(playerInventory.player);
        inventory.startOpen(playerInventory.player);
        this.isBound = inventory.isBound(playerInventory.player);
        if(isBound){
            boundResourceLocation = inventory.getBoundResourceLocation(playerInventory.player);
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
                    var slot = new CardItemSlot(inventory, slotIndex, 27 + i * 54 + page*132, 68 + j * 73, playerInventory.player, isBound, boundResourceLocation);
                    this.addSlot(slot);
                    cardSlots.add(slot);
                    slotIndex++;
                }
            }
        }
    }

    @Override
    public void removed(Player player) {
        inventory.stopOpen(player);
        super.removed(player);
    }

    @Override
    public void clicked(int slotIndex, int button, ClickType clickType, Player player) {
        super.clicked(slotIndex, button, clickType, player);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(Player player, int invSlot) {
        //TODO shift clicking out of the binder inventory currently sets stack count to max
        ItemStack newStack = ItemStack.EMPTY;
        Slot clickedSlot = this.slots.get(invSlot);
        if(!(player instanceof LocalPlayer)){
            if (clickedSlot.hasItem() && clickedSlot.getItem().is(Items.CARD)) {

                ItemStack originalStack = clickedSlot.getItem();
                newStack = originalStack.copy();
                boolean useCardBinderQuickmove = false;
                var cardData = Card.getCardData(originalStack);
                var set = cardData.getCardSet();
                if(isBound){
                    useCardBinderQuickmove = set.getCardGame().getGameId().equalsIgnoreCase(boundResourceLocation.gameId)&&set.getSetId().equalsIgnoreCase(boundResourceLocation.setId);
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
                        clickedSlot.set(ItemStack.EMPTY);
                    } else {
                        clickedSlot.setChanged();
                    }
                } else {
                    if (invSlot < 3*9 ? !super.moveItemStackTo(originalStack, 3*9, this.slots.size(), false) : !super.moveItemStackTo(originalStack, 0, 3*9, false)) {
                        return ItemStack.EMPTY;
                    }
                    if (originalStack.isEmpty()) {
                        clickedSlot.set(ItemStack.EMPTY);
                    } else {
                        clickedSlot.setChanged();
                    }
                }

            }
        }
        return newStack;
    }

    protected boolean insertItem(ItemStack stack, int index, Player player) {
        ItemStack itemStack;
        boolean bl = false;
        int i = index;
        if (stack.isStackable()) {
            while (!stack.isEmpty() && i == index) {
                itemStack = inventory.getItem(i);
                if (!itemStack.isEmpty() && ItemStack.isSameItemSameTags(stack, itemStack)) {
                    int j = itemStack.getCount() + stack.getCount();
                    if (j <= stack.getMaxStackSize()) {
                        stack.setCount(0);
                        itemStack.setCount(j);
                        bl = true;
                    } else if (itemStack.getCount() < stack.getMaxStackSize()) {
                        stack.shrink(stack.getMaxStackSize() - itemStack.getCount());
                        itemStack.setCount(stack.getMaxStackSize());
                        bl = true;
                    }
                }
                ++i;
            }
        }
        if (!stack.isEmpty()) {
            i = index;
            while (index == i) {
                itemStack = inventory.getItem(i);

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
    public boolean stillValid(Player player) {
        return this.inventory.stillValid(player);
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
        private CardIdentifier boundResourceLocation;
        private Player player;

        public CardItemSlot(CardBinderInventory inventory, int index, int x, int y, Player player, boolean isBound, @Nullable CardIdentifier boundResourceLocation) {
            super(inventory, index, x, y);
            this.player = player;
            this.isBound = isBound;
            this.boundResourceLocation = boundResourceLocation;
            this.checkEnabled();
        }

        @Override
        public ItemStack getItem(){
            try{
                return container.getItem(getInventoryIndex());
            } catch (Exception e){
                return ItemStack.EMPTY;
            }
        }

        @Override
        public void setByPlayer(ItemStack stack) {
            if(!isEnabled()) return;
            ((CardBinderInventory)this.container).setStack(getInventoryIndex(), stack, player);
        }

        @Override
        public ItemStack remove(int amount) {
            if(!isEnabled()) return ItemStack.EMPTY;
            var stack = ((CardBinderInventory)this.container).removeStack(getInventoryIndex(), amount, player);
            return stack;
        }

        @Override
        public void set(ItemStack stack) {
            if(!isEnabled()) return;
            ((CardBinderInventory)this.container).setStack(getInventoryIndex(), stack, player);
            this.setChanged();
        }

        public int getInventoryIndex(){
            var index = getContainerSlot();
            var inventoryIndex = index % cardsPerPage + cardsPerPage * StackTheCardsClient.PAGE_INDEX;
            return inventoryIndex;
        }

        @Override
        public boolean mayPlace(ItemStack stack) {
            if(!enabled) return false;
            if(!stack.is(Items.CARD)) return false;
            if(!isBound) return true;
            var data = Card.getCardData(stack);
            var set = data.getCardSet();
            return set.getCardGame().getGameId().equalsIgnoreCase(boundResourceLocation.gameId) && set.getSetId().equalsIgnoreCase(boundResourceLocation.setId) && data.index == getInventoryIndex();
        }

        @Override
        public boolean isHighlightable() {
            return enabled;
        }

        @Override
        public boolean allowModification(Player player) {
            return enabled;
        }

        @Override
        public boolean mayPickup(Player playerEntity) {
            return enabled;
        }

        @Override
        public Optional<ItemStack> tryRemove(int min, int max, Player player) {
            if(!isEnabled()) Optional.empty();
            if (!this.mayPickup(player)) {
                return Optional.empty();
            }
            if (!this.allowModification(player) && max < this.getItem().getCount()) {
                return Optional.empty();
            }
            ItemStack itemStack = this.remove(min = Math.min(min, max));
            if (itemStack.isEmpty()) {
                return Optional.empty();
            }
            if (this.getItem().isEmpty()) {
                this.set(ItemStack.EMPTY);
            }
            return Optional.of(itemStack);
        }

        @Override
        public ItemStack safeTake(int min, int max, Player player) {
            Optional<ItemStack> optional = this.tryRemove(min, max, player);
            optional.ifPresent(stack -> this.onTake(player, (ItemStack)stack));
            return optional.orElse(ItemStack.EMPTY);
        }

        @Override
        public ItemStack safeInsert(ItemStack stack) {
            return this.safeInsert(stack, stack.getCount());
        }

        @Override
        public ItemStack safeInsert(ItemStack stack, int count) {
            if(!enabled) return stack;
            if (stack.isEmpty() || !this.mayPlace(stack)) {
                return stack;
            }
            ItemStack itemStack = this.getItem();
            int i = Math.min(Math.min(count, stack.getCount()), this.getMaxStackSize(stack) - itemStack.getCount());
            if (itemStack.isEmpty()) {
                this.set(stack.split(i));
            } else if (ItemStack.isSameItemSameTags(itemStack, stack)) {
                stack.shrink(i);
                itemStack.grow(i);
                this.set(itemStack);
            }
            return stack;
        }

        public void checkEnabled() {
            var index = this.getInventoryIndex();
            var isWithinBounds = index < container.getContainerSize();
            this.enabled = isWithinBounds;
        }

        public boolean isEnabled(){
            return enabled;
        }
    }
}
