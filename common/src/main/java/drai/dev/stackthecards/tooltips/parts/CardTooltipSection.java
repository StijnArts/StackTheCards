package drai.dev.stackthecards.tooltips.parts;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.*;
import com.google.gson.*;

import java.util.*;
import java.util.stream.*;

public class CardTooltipSection {
    private static final String Json_SECTION_KEY = "parts";
    private static final String Json_NO_NEW_LINE_KEY = "noNewLine";
    private List<CardTooltipLine> parts = new ArrayList<>();
    public boolean noLineBreak = false;

    public static final StreamCodec<FriendlyByteBuf, CardTooltipSection> SYNC_CODEC = StreamCodec.composite(
            ByteBufCodecs.collection(ArrayList::new, CardTooltipLine.SYNC_CODEC), CardTooltipSection::getParts,
            ByteBufCodecs.BOOL, CardTooltipSection::isNoLineBreak,
            CardTooltipSection::new);

    public CardTooltipSection() {
    }

    public CardTooltipSection(List<CardTooltipLine> parts, boolean noLineBreak) {
        this.parts = parts;
        this.noLineBreak = noLineBreak;
    }

    public List<Component> getText() {
        return parts.stream().map(CardTooltipLine::getTextComponent).collect(Collectors.toList());
    }

    public static CardTooltipSection parse(JsonObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.has(Json_SECTION_KEY)) throw new MalformedJsonException("Section was missing parts array");
        var section = new CardTooltipSection();
        JsonArray parts = json.get(Json_SECTION_KEY).getAsJsonArray();
        for (var part : parts) {
            if(part.isJsonObject()) section.parts.add(CardTooltipLine.parse(part.getAsJsonObject(), game));
        }
        if(json.has(Json_NO_NEW_LINE_KEY)){
            try{
                section.noLineBreak = json.get(Json_NO_NEW_LINE_KEY).getAsBoolean();
            } catch (Exception e){
                throw new MalformedJsonException("Component no new line break was malformed: "+e.getMessage());
            }
        }
        return section;
    }

    public List<CardTooltipLine> getParts() {
        return parts;
    }

    public boolean isNoLineBreak() {
        return noLineBreak;
    }
}
