package br.alysonsantos.market.core.models;

import lombok.Data;
import org.bukkit.inventory.*;

import java.util.List;

@Data
public class Owner {
    private String name;
    private List<ItemStack> itemStacks;
    private List<ItemStack> itemStacksExpire;

    public Owner(String name) {
        this.name = name;
    }
}
