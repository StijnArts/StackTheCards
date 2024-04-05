package drai.dev.stackthecards.items;

import drai.dev.stackthecards.client.screen.*;
import net.minecraft.block.*;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.registry.tag.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.util.collection.*;
import net.minecraft.world.*;

import java.util.*;

public class CardBinder extends Item implements Inventory {
    public static final String SLOT_AMOUNT_KEY = "amount_of_slots";
    public static final String CARD_BINDER_DATA_KEY = "CardBinderData";
    private static final String CARD_BINDER_INVENTORY_KEY = "CardBinderInventory";
    public int size = 100;
    private DefaultedList<ItemStack> inventory = DefaultedList.ofSize(size, ItemStack.EMPTY);

    public CardBinder(Settings settings) {
        super(settings);
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStack itemStack = Objects.requireNonNullElse(this.inventory.get(slot), ItemStack.EMPTY);
        this.inventory.set(slot, ItemStack.EMPTY);
        return itemStack;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return this.removeStack(slot, 1);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.inventory.set(slot, stack);
    }
    @Override
    public void onOpen(PlayerEntity player) {
        var itemStack = player.getMainHandStack();

        int maxSlots = Card.getCardDataNBT(itemStack, CARD_BINDER_DATA_KEY).getCompound(0).getInt(SLOT_AMOUNT_KEY);
        if(maxSlots != 0){
            size = maxSlots;
        }
        var compound2 = itemStack.getOrCreateNbt().getCompound(CARD_BINDER_INVENTORY_KEY);
        var newInventory = DefaultedList.ofSize(size, ItemStack.EMPTY);
        Inventories.readNbt(compound2, newInventory);
        inventory = newInventory;
    }

    @Override
    public void onClose(PlayerEntity player) {
        var itemStack = player.getMainHandStack();
        var nbt = new NbtCompound();
        Inventories.writeNbt(nbt, inventory, true);
        itemStack.removeSubNbt(CARD_BINDER_INVENTORY_KEY);
        itemStack.getOrCreateNbt().put(CARD_BINDER_INVENTORY_KEY, nbt);
    }

    @Override
    public void markDirty() {

    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        this.inventory.clear();
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
            user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                    (id, inventory, p) -> new CardBinderScreenHandler(id, inventory, this), Text.of("Card Binder")));
        return TypedActionResult.success(user.getStackInHand(hand));
    }
}
