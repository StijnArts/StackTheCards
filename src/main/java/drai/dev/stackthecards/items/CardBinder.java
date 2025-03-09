package drai.dev.stackthecards.items;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
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
import org.jetbrains.annotations.*;

import java.util.*;

import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinder extends Item {

    public static final int MAX_CARDS_PER_PAGE = 8;
    public static final String CARD_BINDER_COUNT_KEY = "ItemCount";
    private final DyeColor color;

    public CardBinder(DyeColor color, Item.Properties settings) {
        super(settings);
        this.color = color;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        if(!level.isClientSide){
            var nbt = stack.getOrCreateTag();
            var shouldApplyEffect = nbt.getInt(CARD_BINDER_SHOULD_APPLY_EFFECT_KEY) == 1;
            var effectResourceLocation = ResourceLocation.tryParse(nbt.getString(CARD_BINDER_EFFECT_KEY));
            if(entity instanceof LivingEntity livingEntity && shouldApplyEffect){
                if(effectResourceLocation == null){
                    effectResourceLocation = new ResourceLocation("minecraft","haste");
                }
                try{
                    var effect = BuiltInRegistries.MOB_EFFECT.get(effectResourceLocation);
                    if(effect!=null){
                        livingEntity.addEffect(new MobEffectInstance(effect, 6), entity);
                    }
                } catch (Exception e){
                    System.out.println("tried to apply an effect but it wasn't found: " + effectResourceLocation.toString());
                }
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag context) {
        var nbt = stack.getOrCreateTag();
        var inventorySize = nbt.getInt(CARD_BINDER_COUNT_KEY);
        if(inventorySize!=0) tooltip.add(Component.literal("Contains " + inventorySize + " Unique card(s)").setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        if(nbt.contains(CARD_BINDER_RESTRICTION_KEY)){
            var cardResourceLocation = CardIdentifier.getCardIdentifier(nbt.getCompound(CARD_BINDER_RESTRICTION_KEY));
            var text = Component.literal("Customized for " ).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY));
            text.append(CardGameRegistry.getCardGame(cardResourceLocation.gameId).getName()+": ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
            text.append(CardGameRegistry.getCardGame(cardResourceLocation.gameId).getCardSet(cardResourceLocation.setId).getName()+" ").setStyle(Style.EMPTY.withColor(ChatFormatting.AQUA));
            tooltip.add(text);
            var effectResourceLocation = ResourceLocation.tryParse(nbt.getString(CARD_BINDER_EFFECT_KEY));
            if(effectResourceLocation != null){
                try{
                    var effect = BuiltInRegistries.MOB_EFFECT.get(effectResourceLocation);
                    if(effect!=null){
                        tooltip.add(Component.literal("When completed grants: " + Language.getInstance().getOrDefault(effect.getDescriptionId())).setStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
                    }
                } catch (Exception e){
                    System.out.println("tried to apply an effect but it wasn't found: " + effectResourceLocation.toString());
                }
            }
        }
    }

    public static ItemStack getItemStack(DyeColor color) {
        return new ItemStack(CardBinder.get(color));
    }

    private static ItemLike get(DyeColor color) {
        if (color == null) {
            return Items.CARD_BINDER;
        }
        switch (color) {
            case WHITE: {
                return Items.WHITE_CARD_BINDER;
            }
            case ORANGE: {
                return Items.ORANGE_CARD_BINDER;
            }
            case MAGENTA: {
                return Items.MAGENTA_CARD_BINDER;
            }
            case LIGHT_BLUE: {
                return Items.LIGHT_BLUE_CARD_BINDER;
            }
            case YELLOW: {
                return Items.YELLOW_CARD_BINDER;
            }
            case LIME: {
                return Items.LIME_CARD_BINDER;
            }
            case PINK: {
                return Items.PINK_CARD_BINDER;
            }
            case GRAY: {
                return Items.GRAY_CARD_BINDER;
            }
            case LIGHT_GRAY: {
                return Items.LIGHT_GRAY_CARD_BINDER;
            }
            case CYAN: {
                return Items.CYAN_CARD_BINDER;
            }
            default: {
                return Items.CARD_BINDER;
            }
            case BLUE: {
                return Items.BLUE_CARD_BINDER;
            }
            case PURPLE: {
                return Items.PURPLE_CARD_BINDER;
            }
            case GREEN: {
                return Items.GREEN_CARD_BINDER;
            }
            case RED: {
                return Items.RED_CARD_BINDER;
            }
            case BLACK:
        }
        return Items.BLACK_CARD_BINDER;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player user, InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND){
            if(!level.isClientSide){
                user.openMenu(new SimpleMenuProvider(
                        (id, inventory, playerEntity) -> StackTheCards.CARD_BINDER_SCREEN_HANDLER.create(id, inventory), Component.literal("Card Binder")));
            }
            return InteractionResultHolder.success(user.getItemInHand(hand));
        }
        return InteractionResultHolder.pass(user.getItemInHand(hand));
    }

    public ResourceLocation getModelResourceLocation() {
        return new ResourceLocation("stack_the_cards", "item/card_binder_"+color.getName());
    }

    public DyeColor getColor() {
        return color;
    }
}
