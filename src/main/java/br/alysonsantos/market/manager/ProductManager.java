package br.alysonsantos.market.manager;

import br.alysonsantos.market.StoreMarket;
import br.alysonsantos.market.apis.ActionBarAPI;
import br.alysonsantos.market.core.controllers.ConfigurationController;
import br.alysonsantos.market.core.controllers.MarketController;
import br.alysonsantos.market.core.models.Product;
import br.alysonsantos.market.utils.NumberFormat;
import br.alysonsantos.market.utils.VaultAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public class ProductManager {
    private MarketController marketController = StoreMarket.getMarketController();
    private ConfigurationController configurationController = StoreMarket.getInstance().getConfigurationController();

    public void sellProduct(Player player, double amount) {
        if (!marketController.getITEMS_OWNER().containsKey(player.getName())) {
            player.sendMessage(new String[]{"", "§c Ocorreu um erro ao carregar seus dados. Relogue caso o erro persistir.", ""});
            return;
        }

        configurationController.getProductForSale().forEach((a) -> {
            player.sendMessage(a.replace("%sale%", NumberFormat.formartAmount2(amount)));
        });

        Product product = new Product(new Random().nextInt(10000), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(configurationController.getTime()), amount, player.getName(), player.getItemInHand());
        marketController.saveProduct(product);

        ActionBarAPI.broadcastActionBar(configurationController.getActionbarSaleAnnouncement().replace("%salesman%", VaultAPI.getPlayerGroupPrefix(player.getName()) + player.getName()).replace("%sale%", NumberFormat.formartAmount2(amount)));

        addProductInOwner(player.getName(), product);
        removeItens(player.getInventory(), player.getItemInHand(), player.getItemInHand().getAmount());
    }

    public void sellProductPrivate(Player player, Player target, double amount) {
        if (!marketController.getITEMS_OWNER().containsKey(player.getName())) {
            player.sendMessage(new String[]{"", "§c Ocorreu um erro ao carregar seus dados. Relogue caso o erro persistir.", ""});
            return;
        }

        configurationController.getProductForSalePrivate().forEach((a) -> {
            player.sendMessage(a.replace("%price%", NumberFormat.formartAmount2(amount)).replace("%target%", VaultAPI.getPlayerGroupPrefix(player.getName()) + target.getName()));
        });

        Product product = new Product(new Random().nextInt(10000), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(configurationController.getTime()), amount, true, player.getName(), target.getName(), player.getItemInHand());
        product.setPrivate(true);
        marketController.saveProduct(product);

        configurationController.getReceivedProductPrivate().forEach(a -> {
            target.sendMessage(a.replace("%target%", VaultAPI.getPlayerGroupPrefix(player.getName()) + player.getName()).replace("%price%", NumberFormat.formartAmount2(amount)));
        });

        addProductInListaPrivate(target.getName(), product);
        addProductInOwner(player.getName(), product);
        removeItens(player.getInventory(), player.getItemInHand(), player.getItemInHand().getAmount());
    }

    public void addProductInOwner(String nome, Product product) {
        marketController.getITEMS_OWNER().get(nome).add(product);
    }

    public void addProductInListaPrivate(String nome, Product product) {
        marketController.getPrivateProducts().get(nome).add(product);
    }

    public void buyProduct(Player player, Product product) {
        Player owner = Bukkit.getPlayer(product.getOwner());

        if (product.getOwner().equals(player.getName())) {
            player.closeInventory();

            player.getInventory().addItem(product.getItemStack());
            player.sendMessage(new String[]{"", "§a Você recolheu o produto do mercado.", ""});

            marketController.deleteProduct(product);
            marketController.getITEMS_OWNER().get(product.getOwner()).remove(product);

            return;
        }

        if (product.getPrice() > VaultAPI.getEconomy().getBalance(player.getName())) {
            player.sendMessage("§cVocê não possui dinheiro suficiente para comprar este produto.");
            player.closeInventory();
            return;
        }

        if (owner != null) {
            configurationController.getProductSold().forEach((a) -> {
                owner.sendMessage(a.replace("%buyer%", VaultAPI.getPlayerGroupPrefix(player.getName()) + player.getName()));
            });
        }

        VaultAPI.getEconomy().depositPlayer(product.getOwner(), product.getPrice());
        VaultAPI.getEconomy().withdrawPlayer(player.getName(), product.getPrice());

        player.getInventory().addItem(product.getItemStack());

        if (product.isPrivate()) {
            marketController.getPrivateProducts().get(product.getTarget()).remove(product);
        }

        configurationController.getYouBought().forEach((a) -> {
            player.sendMessage(a.replace("%salesman%", VaultAPI.getPlayerGroupPrefix(product.getOwner()) + product.getOwner()).replace("%price%", NumberFormat.formartAmount2(product.getPrice())));
        });

        marketController.deleteProduct(product);
        marketController.getITEMS_OWNER().get(product.getOwner()).remove(product);

        player.closeInventory();
    }

    private void removeItens(Inventory inventory, ItemStack item, int amount) {
        for (java.util.Map.Entry<Integer, ? extends ItemStack> map : inventory.all(item.getType()).entrySet()) {
            if (map.getValue().isSimilar(item)) {
                ItemStack currentItem = map.getValue();
                if (currentItem.getAmount() <= amount) {
                    amount -= currentItem.getAmount();
                    inventory.clear(map.getKey());
                } else {
                    currentItem.setAmount(currentItem.getAmount() - amount);
                    amount = 0;
                }

            }
            if (amount == 0)
                break;
        }
    }
}
