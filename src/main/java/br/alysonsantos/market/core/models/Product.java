package br.alysonsantos.market.core.models;

import br.alysonsantos.market.utils.VaultAPI;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class Product {
    private int ID;
    private long tempo;
    private double price;
    private boolean isPrivate;
    private String owner;
    private String target;
    private ItemStack itemStack;


    public Product(int ID, long tempo, double price, String owner, ItemStack itemStack) {
        this.ID = ID;
        this.tempo = tempo;
        this.price = price;
        this.isPrivate = false;
        this.owner = owner;
        this.itemStack = itemStack;
    }

    public String containsMoneyString(String player) {
        if (player.equals(owner)) {
            return "§eClique para recolher seu produto.";
        } else if (price > VaultAPI.getEconomy().getBalance(player)) {
            return "§cVocê não possui moedas o suficiente.";
        }

        return "§aClique aqui para adquirir este produto.";
    }

    public boolean containsMoney(String player) {
        if (price > VaultAPI.getEconomy().getBalance(player)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Product{" +
                "ID=" + ID +
                ", tempo=" + tempo +
                ", owner='" + owner + '\'' +
                ", itemStack=" + itemStack.getType().toString() +
                '}';
    }
}
