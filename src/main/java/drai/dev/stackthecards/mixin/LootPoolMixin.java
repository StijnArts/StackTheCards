package drai.dev.stackthecards.mixin;

import com.llamalad7.mixinextras.sugar.*;
import drai.dev.stackthecards.client.*;
import drai.dev.stackthecards.data.cardpacks.*;
import drai.dev.stackthecards.registry.Items;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.loot.context.*;
import net.minecraft.loot.provider.number.*;
import net.minecraft.util.math.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.function.*;

import static drai.dev.stackthecards.data.cardpacks.CardPack.lootPoolCardPackInjection;

@Mixin(LootPool.class)
public class LootPoolMixin {

    @Shadow @Final public LootNumberProvider rolls;

    @Shadow @Final public LootNumberProvider bonusRolls;

    @Inject(at = @At(value = "TAIL"), method = "addGeneratedLoot", locals = LocalCapture.CAPTURE_FAILHARD)
    private void onKey(Consumer<ItemStack> lootConsumer, LootContext context, CallbackInfo ci, Consumer<ItemStack> consumer, int i, int j) {
        lootPoolCardPackInjection(context, consumer, this.rolls.nextInt(context) + MathHelper.floor(this.bonusRolls.nextFloat(context) * context.getLuck()));
    }


}
