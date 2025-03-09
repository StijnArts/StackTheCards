package drai.dev.stackthecards.tooltips.restrict;

import net.minecraft.nbt.*;
import net.minecraft.network.chat.*;
import net.minecraft.util.*;

public class OrderedTextToTextVisitor implements FormattedCharSink {
    private final MutableComponent text = Component.empty();

    @Override
    public boolean accept(int index, Style style, int codePoint) {
        String car = new String(Character.toChars(codePoint));
        text.append(Component.literal(car).setStyle(style));
        return true;
    }

    public Component getText() {
        return text;
    }

    public static Component get(FormattedCharSequence text) {
        OrderedTextToTextVisitor visitor = new OrderedTextToTextVisitor();
        text.accept(visitor);
        return visitor.getText();
    }
}
