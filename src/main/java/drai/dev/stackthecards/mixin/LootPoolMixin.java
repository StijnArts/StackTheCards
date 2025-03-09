package drai.dev.stackthecards.mixin;

import net.minecraft.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.providers.number.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.function.*;

import static drai.dev.stackthecards.data.cardpacks.CardPack.lootPoolCardPackInjection;

@Mixin(LootPool.class)
public class LootPoolMixin {

    @Shadow @Final public NumberProvider rolls;

    @Shadow @Final public NumberProvider bonusRolls;

    @Inject(at = @At(value = "TAIL"), method = "addRandomItems", locals = LocalCapture.CAPTURE_FAILHARD)
    private void onKey(Consumer<ItemStack> lootConsumer, LootContext context, CallbackInfo ci, Consumer<ItemStack> consumer, int i, int j) {
        lootPoolCardPackInjection(context, consumer, this.rolls.getInt(context) + Mth.floor(this.bonusRolls.getFloat(context) * context.getLuck()));
    }


}
