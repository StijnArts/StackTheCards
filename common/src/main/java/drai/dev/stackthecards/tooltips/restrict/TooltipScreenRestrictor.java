package drai.dev.stackthecards.tooltips.restrict;

import drai.dev.stackthecards.mixin.client.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.screens.inventory.tooltip.*;
import net.minecraft.network.chat.*;

import java.util.*;

public class TooltipScreenRestrictor {
    private static boolean shouldFlip = false;

    public static void fix(List<ClientTooltipComponent> components, Font font, int x, int width) {
        shouldFlip = false;

        int forcedWidth = 0;
        for (ClientTooltipComponent component : components) {
            if (!(component instanceof ClientTextTooltip)) {
                int width2 = component.getWidth(font);
                if (width2 > forcedWidth)
                    forcedWidth = width2;
            }
        }

        int maxWidth = width - 20 - x;
        if (forcedWidth > maxWidth || maxWidth < 100) {
            shouldFlip = true;
            maxWidth = x - 28;
        }

        wrapNewLines(components);
        wrapLongLines(components, font, maxWidth);
    }


    public static int shouldFlip(List<ClientTooltipComponent> components, Font font, int x) {
        int maxWidth = 0;
        for (ClientTooltipComponent ClientTooltipComponent : components) {
            int newWidth = ClientTooltipComponent.getWidth(font);
            if (newWidth > maxWidth) {
                maxWidth = newWidth;
            }
        }
        int renderX = x + 12;

        if (shouldFlip)
            renderX -= 28 + maxWidth;

        return renderX;
    }


    private static void wrapLongLines(List<ClientTooltipComponent> components, Font font, int maxSize) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof ClientTextTooltip orderedTextTooltipComponent) {
                Component text = OrderedTextToTextVisitor.get(((ClientTextTooltipAccessor) orderedTextTooltipComponent).getText());
                if (text.getSiblings().isEmpty()) continue;

                List<ClientTooltipComponent> wrapped = font.split(text, maxSize).stream().map(ClientTooltipComponent::create).toList();
                components.remove(i);
                components.addAll(i, wrapped);
            }
        }
    }

    private static void wrapNewLines(List<ClientTooltipComponent> components) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i) instanceof ClientTextTooltip orderedTextTooltipComponent) {
                Component text = OrderedTextToTextVisitor.get(((ClientTextTooltipAccessor) orderedTextTooltipComponent).getText());

                List<Component> children = text.getSiblings();
                for (int j = 0; j < children.size() - 1; j++) {
                    String code = children.get(j).getString() + children.get(j + 1).getString();
                    if (code.equals("\\n")) {
                        components.set(i, ClientTooltipComponent.create(textWithChildren(children, 0, j).getVisualOrderText()));
                        components.add(i + 1, ClientTooltipComponent.create(textWithChildren(children, j + 2, children.size()).getVisualOrderText()));
                        break;
                    }
                }
            }
        }
    }

    private static Component textWithChildren(List<Component> children, int from, int end) {
        MutableComponent text = Component.literal("");
        for (int i = from; i < end; i++)
            text.append(children.get(i));
        return text;
    }
}
