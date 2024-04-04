package drai.dev.stackthecards.data.cardpacks;

import drai.dev.stackthecards.data.*;
import net.minecraft.util.*;

import java.util.*;

public class PullResult {
    public List<CardIdentifier> pulledCards = new ArrayList<>();
    public List<Identifier> pulledItems = new ArrayList<>();

    public List<CardIdentifier> getPulledCards() {
        return pulledCards;
    }

    public List<Identifier> getPulledItems(){
        return pulledItems;
    }
}
