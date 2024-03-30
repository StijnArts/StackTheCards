package drai.dev.stackthecards.mixin;

import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.items.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.entity.data.*;
import net.minecraft.entity.decoration.*;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.sound.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import net.minecraft.world.event.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

@Mixin(ItemFrameEntity.class)
public abstract class ItemFrameEntityMixin extends AbstractDecorationEntity {
    protected ItemFrameEntityMixin(EntityType<? extends AbstractDecorationEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow public abstract ItemStack getHeldItemStack();

    @Shadow private boolean fixed;

    @Shadow public abstract void setHeldItemStack(ItemStack stack);

    @Shadow public abstract SoundEvent getRotateItemSound();

    @Shadow public abstract int getRotation();

    @Shadow public abstract void setRotation(int value);

    @Shadow @Final private static TrackedData<ItemStack> ITEM_STACK;

    @Shadow public abstract SoundEvent getAddItemSound();

    @Shadow protected abstract void setAsStackHolder(ItemStack stack);

    @Inject(method = "setHeldItemStack(Lnet/minecraft/item/ItemStack;Z)V", at = @At("HEAD"), cancellable = true)
    private void setCardItem(ItemStack value, boolean update, CallbackInfo ci){
        if(value.isOf(Items.CARD)){
            if (!value.isEmpty()) {
                value = value.copyWithCount(1);
            }
            if(StackTheCardsClient.ctrlKeyPressed){
                Card.toggleCardFlipped(value);
                System.out.println("flipped the card");
            }
            this.setFlag(5, true);

            this.setAsStackHolder(value);
            this.getDataTracker().set(ITEM_STACK, value);
            if (!value.isEmpty()) {
                if(StackTheCardsClient.ctrlKeyPressed){
                    this.playSound(SoundEvents.ITEM_BOOK_PAGE_TURN, 0.3f, 1f);
                }
                this.playSound(this.getAddItemSound(), 1.0f, 1.0f);
            }
            if (update && this.attachmentPos != null) {
                this.getWorld().updateComparators(this.attachmentPos, Blocks.AIR);
            }
            ci.cancel();
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void setCardItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir){
        ItemStack itemStack = player.getStackInHand(hand);
        ItemStack currentItemstack = this.getHeldItemStack();
        boolean bl = !currentItemstack.isEmpty();
        if(currentItemstack.isOf(Items.CARD)) {
            if(!this.fixed && !this.getWorld().isClient && bl){
                if(itemStack.isEmpty() || !itemStack.isOf(Items.CARD)){
                    if(StackTheCardsClient.ctrlKeyPressed){
                        setHeldItemStack(currentItemstack);
                        System.out.println("flipped the card");
                        cir.setReturnValue(ActionResult.CONSUME);
                    }
                }else {
                    if (!this.isRemoved()) {
                        if(StackTheCardsClient.shiftKeyPressed){
//                    var cardsToAttach = new ArrayList<>();
                            var oldTopCardItemStack = currentItemstack.copyWithCount(1);
                            var newTopCardItemStack = itemStack.copyWithCount(1);
                            Card.removeAttachedCards(oldTopCardItemStack);
                            Card.attachCard(newTopCardItemStack, Card.getCardIdentifier(oldTopCardItemStack));
                            Card.attachCards(newTopCardItemStack, Card.getAttachedCards(currentItemstack));

                            this.setHeldItemStack(newTopCardItemStack);
                        } else {
                            Card.attachCard(currentItemstack, Card.getCardIdentifier(itemStack));
                            this.setHeldItemStack(currentItemstack);
                        }
                        this.emitGameEvent(GameEvent.BLOCK_CHANGE, player);
                        if (!player.getAbilities().creativeMode) {
                            itemStack.decrement(1);
                        }
                    }
                    cir.setReturnValue(ActionResult.CONSUME);
                }
            }
        }
    }

    //TODO write logic for detecting if all cards around the card thats placed are able to connect and if so set isConnected in cardData to true
    //TODO cards should be able to be in multiple configurations and should be able to define a position for each of those configurations
    //example of how to access worlds and blockstates to check if theres an item frame next to
   /* @Override
    public boolean canStayAttached() {
        if (this.fixed) {
            return true;
        }
        if (!this.getWorld().isSpaceEmpty(this)) {
            return false;
        }
        BlockState blockState = this.getWorld().getBlockState(this.attachmentPos.offset(this.facing.getOpposite()));
        if (!(blockState.isSolid() || this.facing.getAxis().isHorizontal() && AbstractRedstoneGateBlock.isRedstoneGate(blockState))) {
            return false;
        }
        return this.getWorld().getOtherEntities(this, this.getBoundingBox(), PREDICATE).isEmpty();
    }*/
    @Inject(method = "dropHeldStack", at = @At("HEAD"), cancellable = true)
    private void dropCardItem(Entity entity, boolean alwaysDrop, CallbackInfo ci){
        if (this.fixed) {
            ci.cancel();
            return;
        }
        ItemStack stack = this.getHeldItemStack();
        if(stack.isOf(Items.CARD)){
            var attachedCards = Card.getAttachedCards(stack);
            if(alwaysDrop){
                for (var attachedCard: attachedCards) {
                    System.out.println(attachedCard.forPrint());
                    var attachedCardItemStack = Card.getAsItemStack(attachedCard);
                    Card.removeAttachedCards(attachedCardItemStack);
                    dropStack(attachedCardItemStack);
                }
                Card.removeAttachedCards(stack);
                dropStack(stack);
                ci.cancel();
                return;
            }

            if(attachedCards.size()>0){
                ItemStack detachingCardItemstack = null;
                if(StackTheCardsClient.shiftKeyPressed){
                    detachingCardItemstack = stack;
                    var newTopCard = Card.getAsItemStack(Card.getTopCardFromStack(stack));
                    Card.attachCards(newTopCard, Card.getAttachedCards(stack));
                    setHeldItemStack(newTopCard);
                    Card.removeAttachedCards(stack);
                } else {
                    detachingCardItemstack = Card.getAsItemStack(Card.popCardFromStack(stack));
                    setHeldItemStack(stack);
                }
                if (entity instanceof PlayerEntity playerEntity && playerEntity.getAbilities().creativeMode) {
                    ci.cancel();
                    return;
                }
                dropStack(detachingCardItemstack);
                ci.cancel();
            } else {
                Card.removeAttachedCards(stack);
                this.setFlag(5, false);
            }
        }
    }
}
