package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.network.syncher.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.decoration.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.gameevent.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemFrame.class)
public abstract class ItemFrameEntityMixin extends HangingEntity {
    protected ItemFrameEntityMixin(EntityType<? extends HangingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Shadow private boolean fixed;

    @Shadow @Final private static EntityDataAccessor<ItemStack> DATA_ITEM;

    @Shadow public abstract SoundEvent getAddItemSound();

    @Shadow
    public abstract void onItemChanged(ItemStack stack);

    @Shadow public abstract ItemStack getItem();
    @Shadow public abstract void setItem(ItemStack stack);

    @Inject(method = "setItem(Lnet/minecraft/world/item/ItemStack;Z)V", at = @At("HEAD"), cancellable = true)
    private void setCardItem(ItemStack value, boolean update, CallbackInfo ci){
        if(value.is(Items.CARD)){
            if (!value.isEmpty()) {
                value = value.copyWithCount(1);
            }
            if(StackTheCardsClient.ctrlKeyPressed){
                Card.toggleCardFlipped(value);
//                System.out.println("flipped the card");
            }
            this.setSharedFlag(5, true);

            this.onItemChanged(value);
            this.getEntityData().set(DATA_ITEM, value);
            if (!value.isEmpty()) {
                if(StackTheCardsClient.ctrlKeyPressed){
                    this.playSound(SoundEvents.BOOK_PAGE_TURN, 0.3f, 1f);
                }
                this.playSound(this.getAddItemSound(), 1.0f, 1.0f);
            }
            if (update) {
                this.level().updateNeighbourForOutputSignal(this.pos, Blocks.AIR);
            }
            ci.cancel();
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void setCardItem(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir){
        ItemStack itemStack = player.getItemInHand(hand);
        ItemStack currentItemstack = this.getItem();
        boolean bl = !currentItemstack.isEmpty();
        boolean isClient = this.level().isClientSide;
        if(!this.fixed && !isClient && bl){
            if(currentItemstack.is(drai.dev.stackthecards.registry.Items.CARD)) {
                if(itemStack.isEmpty() || !itemStack.is(Items.CARD)){
                    if(StackTheCardsClient.ctrlKeyPressed){
                        this.setItem(currentItemstack);
//                        System.out.println("flipped the card");
                        cir.setReturnValue(InteractionResult.CONSUME);
                    }
                }else {
                    if (!this.isRemoved()) {
                        if(CardConnection.checkCardConnection(currentItemstack, itemStack)){
                            this.setItem(currentItemstack);
                        } else if(StackTheCardsClient.shiftKeyPressed || CardConnection.checkSingleCardConnection(itemStack)){
//                    var cardsToAttach = new ArrayList<>();
                            var oldTopCardItemStack = currentItemstack.copyWithCount(1);
                            var newTopCardItemStack = itemStack.copyWithCount(1);
                            Card.removeAttachedCards(oldTopCardItemStack);
                            Card.attachCard(newTopCardItemStack, Card.getCardIdentifier(oldTopCardItemStack));
                            Card.attachCards(newTopCardItemStack, Card.getAttachedCards(currentItemstack));
                            this.setItem(newTopCardItemStack);
                        } else {
                            Card.attachCard(currentItemstack, Card.getCardIdentifier(itemStack));
                            this.setItem(currentItemstack);
                        }

                        this.gameEvent(GameEvent.BLOCK_CHANGE, player);
                        if (!player.getAbilities().instabuild) {
                            itemStack.shrink(1);
                        }
                    }
                    cir.setReturnValue(InteractionResult.CONSUME);
                }
            }
        }
    }



    @Inject(method = "getPickResult", at = @At("HEAD"), cancellable = true)
    private void setCardItem(CallbackInfoReturnable<ItemStack> cir){
        ItemStack currentItemstack = this.getItem();
        boolean bl = !currentItemstack.isEmpty();
        if(bl){
            if(currentItemstack.is(Items.CARD)) {
                var pickedStack = currentItemstack.copy();
                Card.removeAttachedCards(pickedStack);
                CardConnection.breakConnections(pickedStack);
                cir.setReturnValue(pickedStack);
                cir.cancel();
            }
        }
    }

    @Inject(method = "dropItem(Lnet/minecraft/world/entity/Entity;Z)V", at = @At("HEAD"), cancellable = true)
    private void dropCardItem(Entity entity, boolean alwaysDrop, CallbackInfo ci){
        if (this.fixed) {
            ci.cancel();
            return;
        }
        ItemStack stack = this.getItem();
        if(stack.is(Items.CARD)){
            var attachedCards = Card.getAttachedCards(stack);
            if(alwaysDrop){
                for (var attachedCard: attachedCards) {
                    var attachedCardItemStack = Card.getAsItemStack(attachedCard);
                    Card.removeAttachedCards(attachedCardItemStack);
                    CardConnection.breakConnections(attachedCardItemStack);
                    spawnAtLocation(attachedCardItemStack);
                }
                Card.removeAttachedCards(stack);
                CardConnection.breakConnections(stack);
                spawnAtLocation(stack);
                ci.cancel();
                return;
            }
            if(StackTheCardsClient.shiftKeyPressed && CardConnection.hasConnectedCards(stack)){
                if (detachedConnectedCard(entity, ci, stack)) return;
            }
            if(!attachedCards.isEmpty()){
                ItemStack detachingCardItemstack = null;
                if(StackTheCardsClient.shiftKeyPressed){
                    detachingCardItemstack = stack;
                    var newTopCard = Card.getAsItemStack(Card.getTopCardFromStack(stack));
                    Card.attachCards(newTopCard, Card.getAttachedCards(stack));
                    setItem(newTopCard);
                    Card.removeAttachedCards(stack);
                } else {
                    detachingCardItemstack = Card.getAsItemStack(Card.popCardFromStack(stack));
                    setItem(stack);
                }
                if (entity instanceof Player playerEntity && playerEntity.getAbilities().instabuild) {
                    ci.cancel();
                    return;
                }
                spawnAtLocation(detachingCardItemstack);
                ci.cancel();
            } else {
                if(CardConnection.hasConnectedCards(stack)) {
                    if (detachedConnectedCard(entity, ci, stack)) return;
                }
                Card.removeAttachedCards(stack);
                this.setSharedFlag(5, false);
            }
        }
    }

    @Unique
    private boolean detachedConnectedCard(Entity entity, CallbackInfo ci, ItemStack stack) {
        var detachingCardItemstack = Card.getAsItemStack(CardConnection.removeConnection(stack));
        var connectedCards = CardConnection.getConnectedCards(stack);
        var connections = Card.getCardData(stack).getCardGame().getConnections(Card.getCardIdentifier(stack));
        boolean shouldDrop = true;
        for (CardConnection connection : connections) {
            if(connection.matches(connectedCards) && connection.contains(connectedCards)) {
                CardConnection.breakConnections(stack);
                if(connection.isSingle){
                    shouldDrop = false;
                    break;
                }
                for (var card : connectedCards) {
                    CardConnection.addToConnection(stack, card, connection);
                }
                break;
            }
        }
        onItemChanged(stack);
        if (entity instanceof Player playerEntity && playerEntity.getAbilities().instabuild) {
            ci.cancel();
            return true;
        }
        if(shouldDrop) spawnAtLocation(detachingCardItemstack);
        ci.cancel();
        return false;
    }
}
