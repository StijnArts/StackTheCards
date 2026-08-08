package drai.dev.stackthecards.mixin;

import com.llamalad7.mixinextras.sugar.*;
import net.minecraft.util.*;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.providers.number.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.function.*;

import static drai.dev.stackthecards.data.cardpacks.CardPack.*;

@Mixin(LootPool.class)
public class LootPoolMixin {

    @Shadow @Final
    private NumberProvider rolls;

    @Shadow @Final
    private NumberProvider bonusRolls;

    @Inject(at = @At(value = "TAIL"), method = "addRandomItems")
    private void onKey(Consumer<ItemStack> lootConsumer, LootContext lootContext, CallbackInfo ci) {
        lootPoolCardPackInjection(lootContext, lootConsumer, this.rolls.getInt(lootContext) + Mth.floor(this.bonusRolls.getFloat(lootContext) * lootContext.getLuck()));
    }


}
