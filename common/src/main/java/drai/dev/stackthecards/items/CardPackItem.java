package drai.dev.stackthecards.items;

import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.core.registries.*;
import net.minecraft.resources.*;
import net.minecraft.sounds.*;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;

import static drai.dev.stackthecards.StackTheCards.PACK_RIP;
import static drai.dev.stackthecards.data.cardpacks.CardPack.*;

public class CardPackItem extends Item {
    public static final ResourceLocation PACK_RIP_IDENTIFIER = ResourceLocation.fromNamespaceAndPath("stack_the_cards", "pack_rip");
    public CardPackItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        var cardPack = CardPack.getCardPack(itemStack);
        var pullResult = cardPack.pull();
        var cardsToDrop = pullResult.getPulledCards();
        var itemsToDrop = pullResult.getPulledItems();
        if(!level.isClientSide){
            level.playSound(null, user.blockPosition(), PACK_RIP.get(), SoundSource.PLAYERS, 0.4f, 0.9f);
        }
        for (var card : cardsToDrop) {
            user.spawnAtLocation(Card.getAsItemStack(card), 1F);
        }
        for (var item : itemsToDrop) {
            user.spawnAtLocation(new ItemStack(BuiltInRegistries.ITEM.get(item)), 1F);
        }
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.success(itemStack);
    }

    public void playSound(Player user, SoundEvent sound, float volume, float pitch) {
        if (!user.isSilent()) {
            user.level().playSound(null, user.getX(), user.getY(), user.getZ(), sound, user.getSoundSource(), volume, pitch);
        }
    }

    public static ItemStack of(CardPack cardPack){
        var itemStack = new ItemStack(StackTheCardsItems.CARD_PACK.get());
        addCardPackResourceLocation(itemStack, cardPack.getResourceLocation());
        return itemStack;
    }


}
