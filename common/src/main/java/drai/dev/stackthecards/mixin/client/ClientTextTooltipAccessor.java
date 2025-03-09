package drai.dev.stackthecards.mixin.client;

import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.util.*;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.gen.*;

@Mixin(ClientTextTooltip.class)
public interface ClientTextTooltipAccessor {
    @Accessor("text")
    FormattedCharSequence getText();

}
