package drai.dev.stackthecards.items;

import drai.dev.stackthecards.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.registry.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.block.*;
import net.minecraft.client.item.*;
import net.minecraft.entity.*;
import net.minecraft.entity.effect.*;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.registry.*;
import net.minecraft.screen.*;
import net.minecraft.text.*;
import net.minecraft.util.*;
import net.minecraft.world.*;
import org.jetbrains.annotations.*;

import java.util.*;

import static drai.dev.stackthecards.items.CardBinderInventory.*;

public class CardBinder extends Item {

    public static final int MAX_CARDS_PER_PAGE = 8;
    public static final String CARD_BINDER_COUNT_KEY = "ItemCount";
    private final DyeColor color;

    public CardBinder(DyeColor color, Settings settings) {
        super(settings);
        this.color = color;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient){
            var nbt = stack.getOrCreateNbt();
            var shouldApplyEffect = nbt.getInt(CARD_BINDER_SHOULD_APPLY_EFFECT_KEY) == 1;
            var effectIdentifier = Identifier.tryParse(nbt.getString(CARD_BINDER_EFFECT_KEY));
            if(entity instanceof LivingEntity livingEntity && shouldApplyEffect){
                if(effectIdentifier == null){
                    effectIdentifier = new Identifier("minecraft","haste");
                }
                try{
                    var effect = Registries.STATUS_EFFECT.get(effectIdentifier);
                    if(effect!=null){
                        livingEntity.setStatusEffect(new StatusEffectInstance(effect, 6), entity);
                    }
                } catch (Exception e){
                    System.out.println("tried to apply an effect but it wasn't found: " + effectIdentifier.toString());
                }
            }
        }
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        var nbt = stack.getOrCreateNbt();
        var inventorySize = nbt.getInt(CARD_BINDER_COUNT_KEY);
        if(inventorySize!=0) tooltip.add(Text.literal("Contains " + inventorySize + " Unique card(s)").fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));

        if(nbt.contains(CARD_BINDER_RESTRICTION_KEY)){
            var cardIdentifier = CardIdentifier.getCardIdentifier(nbt.getCompound(CARD_BINDER_RESTRICTION_KEY));
            var text = Text.literal("Customized for " ).fillStyle(Style.EMPTY.withColor(Formatting.GRAY));
            text.append(CardGameRegistry.getCardGame(cardIdentifier.gameId).getName()+": ").fillStyle(Style.EMPTY.withColor(Formatting.AQUA));
            text.append(CardGameRegistry.getCardGame(cardIdentifier.gameId).getCardSet(cardIdentifier.setId).getName()+" ").fillStyle(Style.EMPTY.withColor(Formatting.AQUA));
            tooltip.add(text);
            var effectIdentifier = Identifier.tryParse(nbt.getString(CARD_BINDER_EFFECT_KEY));
            if(effectIdentifier != null){
                try{
                    var effect = Registries.STATUS_EFFECT.get(effectIdentifier);
                    if(effect!=null){
                        tooltip.add(Text.literal("When completed grants: " +Language.getInstance().get(effect.getTranslationKey())).fillStyle(Style.EMPTY.withColor(Formatting.GRAY)));
                    }
                } catch (Exception e){
                    System.out.println("tried to apply an effect but it wasn't found: " + effectIdentifier.toString());
                }
            }
        }
    }

    public static ItemStack getItemStack(DyeColor color) {
        return new ItemStack(CardBinder.get(color));
    }

    private static ItemConvertible get(DyeColor color) {
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
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if(hand == Hand.MAIN_HAND){
            if(!world.isClient){
                user.openHandledScreen(new SimpleNamedScreenHandlerFactory(
                        (id, inventory, playerEntity) -> StackTheCards.CARD_BINDER_SCREEN_HANDLER.create(id, inventory), Text.of("Card Binder")));
            }
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    public Identifier getModelIdentifier() {
        return new Identifier("stack_the_cards", "item/card_binder_"+color.asString());
    }

    public DyeColor getColor() {
        return color;
    }
}
