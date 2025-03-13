package drai.dev.stackthecards.items;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.components.*;
import drai.dev.stackthecards.registry.*;
import net.minecraft.*;
import net.minecraft.core.registries.*;
import net.minecraft.locale.*;
import net.minecraft.network.chat.*;
import net.minecraft.resources.*;
import net.minecraft.world.*;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.*;

import java.util.*;

import static drai.dev.stackthecards.network.StackTheCardsNetworking.isIntegratedServer;

public class CardBinder extends Item {

    public static final int MAX_CARDS_PER_PAGE = 8;
    private final DyeColor color;

    public CardBinder(DyeColor color, Item.Properties settings) {
        super(settings);
        this.color = color;
    }

    public static int getOrCreate(ItemStack stack) {
        var data = stack.get(StackTheCardsComponentTypes.CARD_BINDER_PAGE_INDEX.get());
        if (data == null) return 0;
        return data;
    }

    public static int getIndex(ItemStack binderItem, Player clientPlayer) {
        if(isIntegratedServer()) return StackTheCardsClient.PAGE_INDEX;
        return getOrCreate(binderItem);
    }

    public static void setIndex(ItemStack binderItem, int index, Player clientPlayer) {
        if(isIntegratedServer()) StackTheCardsClient.PAGE_INDEX = index;
        binderItem.set(StackTheCardsComponentTypes.CARD_BINDER_PAGE_INDEX.get(), index);
    }

    public static void incrementIndex(ItemStack binderItem, Player clientPlayer) {
        setIndex(binderItem, getIndex(binderItem, clientPlayer) + 1, clientPlayer);
    }

    public static void decrementIndex(ItemStack binderItem, Player clientPlayer) {
        setIndex(binderItem, getIndex(binderItem, clientPlayer) - 1, clientPlayer);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if(!level.isClientSide){
            var data = CardBinderData.getOrCreate(stack);
            var shouldApplyEffect = data.isAppliesEffect();
            var effect = data.getEffect();
            if(entity instanceof LivingEntity livingEntity && shouldApplyEffect){


                if(effect.isBlank()){
                    effect = "minecraft:haste";
                }
                try{
                    var effectResourceLocation = ResourceLocation.tryParse(effect);
                    var effectHolder = BuiltInRegistries.MOB_EFFECT.getHolder(effectResourceLocation);
                    effectHolder.ifPresent(mobEffectReference -> livingEntity.addEffect(new MobEffectInstance(mobEffectReference, 6), entity));
                } catch (Exception e){
                    System.out.println("tried to apply an effect but it wasn't found: " + effect);
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext tooltipContext, List<Component> tooltip, TooltipFlag context) {
        
        var data = CardBinderData.getOrCreate(stack);
        var inventorySize = data.getCardBinderCount();
        if(inventorySize!=0) tooltip.add(Component.literal("Contains " + inventorySize + " Unique "+
                        (inventorySize > 1 ? "cards" : "card"))
                .setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        if(data.isRestricted()){
            var cardResourceLocation = data.getRestrictedTo();
            var text = Component.literal("Customized for " ).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            text.append(CardGameRegistry.getCardGame(cardResourceLocation.gameId).getName()+": ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
            text.append(CardGameRegistry.getCardGame(cardResourceLocation.gameId).getCardSet(cardResourceLocation.setId).getName()+" ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
            tooltip.add(text);
            var effect = data.getEffect();
            if(effect.isBlank()){
                try{
                    var effectResourceLocation = ResourceLocation.tryParse(effect);
                    var effectHolder = BuiltInRegistries.MOB_EFFECT.get(effectResourceLocation);
                    if(effectHolder!=null){
                        tooltip.add(Component.literal("When completed grants: " + Language.getInstance().getOrDefault(effectHolder.getDescriptionId())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                    }
                } catch (Exception e){
                    System.out.println("tried to apply an effect but it wasn't found: " + effect.toString());
                }
            }
        }
    }

    public static ItemStack getItemStack(DyeColor color) {
        return new ItemStack(CardBinder.get(color));
    }

    private static ItemLike get(DyeColor color) {
        if (color == null) {
            return StackTheCardsItems.CARD_BINDER.get();
        }
        switch (color) {
            case WHITE: {
                return StackTheCardsItems.WHITE_CARD_BINDER.get();
            }
            case ORANGE: {
                return StackTheCardsItems.ORANGE_CARD_BINDER.get();
            }
            case MAGENTA: {
                return StackTheCardsItems.MAGENTA_CARD_BINDER.get();
            }
            case LIGHT_BLUE: {
                return StackTheCardsItems.LIGHT_BLUE_CARD_BINDER.get();
            }
            case YELLOW: {
                return StackTheCardsItems.YELLOW_CARD_BINDER.get();
            }
            case LIME: {
                return StackTheCardsItems.LIME_CARD_BINDER.get();
            }
            case PINK: {
                return StackTheCardsItems.PINK_CARD_BINDER.get();
            }
            case GRAY: {
                return StackTheCardsItems.GRAY_CARD_BINDER.get();
            }
            case LIGHT_GRAY: {
                return StackTheCardsItems.LIGHT_GRAY_CARD_BINDER.get();
            }
            case CYAN: {
                return StackTheCardsItems.CYAN_CARD_BINDER.get();
            }
            default: {
                return StackTheCardsItems.CARD_BINDER.get();
            }
            case BLUE: {
                return StackTheCardsItems.BLUE_CARD_BINDER.get();
            }
            case PURPLE: {
                return StackTheCardsItems.PURPLE_CARD_BINDER.get();
            }
            case GREEN: {
                return StackTheCardsItems.GREEN_CARD_BINDER.get();
            }
            case RED: {
                return StackTheCardsItems.RED_CARD_BINDER.get();
            }
            case BLACK:
        }
        return StackTheCardsItems.BLACK_CARD_BINDER.get();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND){
            if(!level.isClientSide){
                user.openMenu(new SimpleMenuProvider(
                        (id, inventory, playerEntity) -> StackTheCards.CARD_BINDER_SCREEN_HANDLER.get().create(id, inventory), Component.literal("Card Binder")));
            }
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(user.getItemInHand(hand));
    }

    public ResourceLocation getModelResourceLocation() {
        return ResourceLocation.fromNamespaceAndPath("stack_the_cards", "item/card_binder_"+color.getName());
    }

    public DyeColor getColor() {
        return color;
    }
}
