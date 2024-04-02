package drai.dev.stackthecards.tooltips;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import drai.dev.stackthecards.tooltips.parts.*;
import net.minecraft.text.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

public class CardTooltipSection {
    private static final String JSON_SECTION_KEY = "parts";
    private final List<CardTooltipPart> parts = new ArrayList<>();

    public List<Text> getText() {
        return parts.stream().map(CardTooltipPart::getTextComponent).collect(Collectors.toList());
    }

    public static CardTooltipSection parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_SECTION_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        var section = new CardTooltipSection();
        JSONArray parts = (JSONArray) json.get(JSON_SECTION_KEY);
        for (var part : parts) {
            section.parts.add(CardTooltipPart.parse((JSONObject)part, game));
        }
        return section;
    }
}
