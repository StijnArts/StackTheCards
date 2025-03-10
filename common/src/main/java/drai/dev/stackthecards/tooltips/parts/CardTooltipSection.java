package drai.dev.stackthecards.tooltips.parts;

import com.google.gson.stream.*;
import drai.dev.stackthecards.data.*;
import net.minecraft.network.*;
import net.minecraft.network.chat.*;
import net.minecraft.network.codec.*;
import org.json.simple.*;

import java.util.*;
import java.util.stream.*;

public class CardTooltipSection {
    private static final String JSON_SECTION_KEY = "parts";
    private static final String JSON_NO_NEW_LINE_KEY = "noNewLine";
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

    public static CardTooltipSection parse(JSONObject json, CardGame game) throws MalformedJsonException {
        if(json.isEmpty() || !json.containsKey(JSON_SECTION_KEY)) throw new MalformedJsonException("Card Game Json was empty");
        var section = new CardTooltipSection();
        JSONArray parts = (JSONArray) json.get(JSON_SECTION_KEY);
        for (var part : parts) {
            section.parts.add(CardTooltipLine.parse((JSONObject)part, game));
        }
        if(json.containsKey(JSON_NO_NEW_LINE_KEY)){
            try{
                section.noLineBreak = (boolean) json.get(JSON_NO_NEW_LINE_KEY);
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
