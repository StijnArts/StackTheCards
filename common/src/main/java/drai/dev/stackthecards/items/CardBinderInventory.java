package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.data.carddata.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.core.*;
import net.minecraft.resources.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;

import java.util.List;

public class CardBinderInventory implements Container {
    public int size;
    private NonNullList<ItemStack> inventory;

    public CardBinderInventory(Player player){
        var itemStack = player.getMainHandItem();
        var data = CardBinderData.getOrCreate(itemStack);
        size = data.getAmountOfSlots();
        inventory = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    @Override
    public int getContainerSize() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return this.inventory.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getItem(int slot) {
        return this.inventory.get(slot);
    }

    @Override
    public ItemStack removeItem(int slot, int amount) {
        return ContainerHelper.removeItem(inventory, slot, amount);
    }

    @Override
    public ItemStack removeItemNoUpdate(int slot) {
        return this.removeItem(slot, 1);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        if(slot < inventory.size()){
            this.inventory.set(slot, stack);
        }
    }

    @Override
    public void startOpen(Player player) {
        var itemStack = player.getMainHandItem();
        var data = CardBinderData.getOrCreate(itemStack);
        size = data.getAmountOfSlots();
        inventory = data.inventory;
    }

    public String getColorAffix(Player player){
        var itemStack = player.getMainHandItem();
        String color = ((CardBinder)itemStack.getItem()).getColor().toString();
        if(color == null || color.isEmpty()){
            return "_brown";
        } else {
            try{
                return "_"+color;
            } catch (Exception e){
                return "";
            }
        }
    }

    @Override
    public void stopOpen(Player player) {
        var itemStack = player.getMainHandItem();
        var data = CardBinderData.getOrCreate(itemStack);
        data.inventory = inventory;
        var shouldApplyEffect = true;
        String effect = "";
        List<CardData> collectionCards;
        var identifier = getRestrictedIdentifier(itemStack);
        if(identifier.setId.equalsIgnoreCase("missing")){
            var game = CardGameRegistry.getCardGame(identifier.gameId);
            collectionCards = game.cards.values().stream().toList();
            if(game.getEffectResourceLocation() != null) effect = game.getEffectResourceLocation();
            shouldApplyEffect = game.appliesEffect;
        } else {
            var set = CardGameRegistry.getCardGame(identifier.gameId).getCardSet(identifier.setId);
            collectionCards = set.getCards().values().stream().toList();
            if(set.getEffectResourceLocation() != null) effect = set.getEffectResourceLocation();
            shouldApplyEffect = set.appliesEffect;
        }
        var distinctCards = inventory.stream().filter(stack -> !stack.isEmpty()).map(Card::getCardData).distinct().toList();
        var cardsInInventory = distinctCards.stream().filter(collectionCards::contains).toList();
        var isBoundToSetOrGame = isBound(player);
        if(isBoundToSetOrGame && cardsInInventory.size() == collectionCards.size() && shouldApplyEffect){
            data.appliesEffect =  true;
        } else {
            data.appliesEffect = false;
        }
        data.cardBinderCount = distinctCards.size();
        data.effect = effect;
        CardBinderData.saveChanges(itemStack, data);
    }

    @Override
    public void setChanged() {

    }

    public CardIdentifier getRestrictedIdentifier(ItemStack stack){
        return CardBinderData.getOrCreate(stack).getRestrictedTo();
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    @Override
    public void clearContent() {
        this.inventory.clear();
    }

    public NonNullList<ItemStack> getInventory() {
        return inventory;
    }

    public ItemStack removeStack(int inventoryIndex, int amount, Player player) {
        var stack = this.removeItem(inventoryIndex, amount);
        return stack;
    }

    public void setStack(int inventoryIndex, ItemStack stack, Player player) {
        this.setItem(inventoryIndex, stack);
    }

    public boolean isBound(Player player) {
        return !getBoundResourceLocation(player).setId.equalsIgnoreCase("missing");
    }

    public CardIdentifier getBoundResourceLocation(Player player){
        var itemStack = player.getMainHandItem();
        return getRestrictedIdentifier(itemStack);
    }
}
