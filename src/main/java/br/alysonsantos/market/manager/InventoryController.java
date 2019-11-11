package br.alysonsantos.market.manager;

import br.alysonsantos.market.StoreMarket;
import br.alysonsantos.market.apis.Criar;
import br.alysonsantos.market.core.controllers.MarketController;
import br.alysonsantos.market.core.models.Product;
import br.alysonsantos.market.utils.NumberFormat;
import br.alysonsantos.market.utils.VaultAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryController extends Criar implements Listener {
    private static MarketController marketController = StoreMarket.getMarketController();
    private ProductManager productManager = new ProductManager();
    public static HashMap<String, Integer> inventoryPage = new HashMap<>();

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        if (!inventoryPage.containsKey(p.getName())) inventoryPage.put(p.getName(), 1);

        if (e.getInventory().getName().equals("§8Mercado - Gerenciador:")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

            ItemStack item = e.getCurrentItem();

            if (item.getItemMeta().getDisplayName().equals("§aVoltar")) {
                openInventory(p, 1);
                inventoryPage.put(p.getName(), 1);
            }

            if (item.getItemMeta().getDisplayName().equals("§eSeus produtos à venda.")) {
                openInventoryProducts(p, 1);
                inventoryPage.put(p.getName(), 1);
            }

            if (item.getItemMeta().getDisplayName().equals("§aMercado privado.")) {
                openInventoryPrivate(p, 1);
                inventoryPage.put(p.getName(), 1);
            }

            if (item.getItemMeta().getDisplayName().equals("§cProdutos expirados.")) {
                openInventoryProductsExpirados(p, 1);
                inventoryPage.put(p.getName(), 1);
            }
        }

        if (e.getInventory().getName().equals("§8Seus produtos:")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

            ItemStack item = e.getCurrentItem();

            if (item.getItemMeta().getDisplayName().equals("§aVoltar")) {
                openInventoryProducts(p);
                return;
            }

            Product product = marketController.getCachedProduct().get(Integer.parseInt(item.getItemMeta().getDisplayName().replace("§a#", "").replace("§a.", "")));

            p.getInventory().addItem(product.getItemStack());
            p.sendMessage(new String[]{"", "§e Item retirado do mercado com sucesso!", ""});

            openInventoryProducts(p, 1);
            inventoryPage.put(p.getName(), 1);


            if (product.isPrivate()) {
                marketController.getPrivateProducts().get(product.getTarget()).remove(product);
            }

            marketController.deleteProduct(product);
            marketController.getITEMS_OWNER().get(product.getOwner()).remove(product);
        }

        if (e.getInventory().getName().equals("§8Produtos expirados:")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

            ItemStack item = e.getCurrentItem();

            if (item.getItemMeta().getDisplayName().equals("§aVoltar")) {
                openInventoryProducts(p);
                return;
            }

            Product product = marketController.getCachedProduct().get(Integer.parseInt(item.getItemMeta().getDisplayName().replace("§a#", "").replace("§a.", "")));

            p.getInventory().addItem(product.getItemStack());
            p.sendMessage(new String[]{"", "§e Item retirado do mercado com sucesso!", ""});

            openInventoryProducts(p, 1);
            inventoryPage.put(p.getName(), 1);


            if (product.isPrivate()) {
                marketController.getPrivateProducts().get(product.getTarget()).remove(product);
            }

            marketController.deleteProduct(product);
            marketController.getITEMS_OWNER().get(product.getOwner()).remove(product);
        }

        if (e.getInventory().getName().equals("§8Mercado público:")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

            ItemStack item = e.getCurrentItem();

            if (item.getItemMeta().getDisplayName().equals("§aAvançar")) {
                inventoryPage.put(p.getName(), inventoryPage.get(p.getName()) + 1);
                openInventory(p, inventoryPage.get(p.getName()));
            }

            if (item.getItemMeta().getDisplayName().equals("§aVoltar")) {
                inventoryPage.put(p.getName(), inventoryPage.get(p.getName()) - 1);
                openInventory(p, inventoryPage.get(p.getName()));
            }

            if (item.getItemMeta().getDisplayName().equals("§aGerenciador")) {
                openInventoryProducts(p);
            }

            if (item.getItemMeta().getDisplayName().contains("§a#")) {
                Product product = marketController.getCachedProduct().get(Integer.parseInt(item.getItemMeta().getDisplayName().replace("§a#", "").replace("§a.", "")));


                productManager.buyProduct(p, product);
            }
        }

        if (e.getInventory().getName().equals("§8Mercado pessoal:")) {
            e.setCancelled(true);

            if (e.getCurrentItem() == null) return;
            if (e.getCurrentItem().getType() == Material.AIR) return;
            if (e.getCurrentItem().getItemMeta().getDisplayName() == null) return;

            ItemStack item = e.getCurrentItem();

            if (e.getSlot() == 49) {
                openInventoryProducts(p);
                inventoryPage.put(p.getName(), inventoryPage.get(p.getName()) - 1);
                return;
            }

            if (item.getItemMeta().getDisplayName().equals("§aAvançar")) {
                inventoryPage.put(p.getName(), inventoryPage.get(p.getName()) + 1);
                openInventoryPrivate(p, inventoryPage.get(p.getName()));
            }

            if (item.getItemMeta().getDisplayName().equals("§aVoltar")) {
                inventoryPage.put(p.getName(), inventoryPage.get(p.getName()) - 1);
                openInventoryPrivate(p, inventoryPage.get(p.getName()));
            }

            if (item.getItemMeta().getDisplayName().contains("§a#")) {
                Product product = marketController.getCachedProduct().get(Integer.parseInt(item.getItemMeta().getDisplayName().replace("§a#", "").replace("§a.", "")));

                if (product == null) {
                    p.sendMessage("§cO produto não está mais disponível!");
                    p.closeInventory();
                    return;
                }

                productManager.buyProduct(p, product);
            }
        }
    }

    public static void openInventory(Player p, int page) {
        List<ItemStack> itemStacks = new ArrayList<>();

        marketController.getCachedProduct().entrySet().stream().filter(b -> !b.getValue().isPrivate()).forEach((product) -> {

            ItemStack itemStack = product.getValue().getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName("§a#" + product.getValue().getID() + "§a.");

            if (itemMeta.getLore() == null) {
                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add(" §7╺ Nome do produto: " + (product.getValue().getItemStack().getItemMeta().getDisplayName() == null ? itemStack.getType().toString() : itemStack.getItemMeta().getDisplayName()) + "§f.");
                lore.add(" §7╺ Vendedor: §f" + VaultAPI.getPlayerGroupPrefix(product.getValue().getOwner()) + product.getValue().getOwner() + "§f.");
                lore.add(" §7╺ Preço: §a$" + NumberFormat.formartAmount2(product.getValue().getPrice()) + "§f.");
                lore.add("");
                lore.add(product.getValue().containsMoneyString(p.getName()));
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                itemStacks.add(itemStack);
                return;
            }

            List<String> lore = itemStack.getItemMeta().getLore();
            lore.add(" ");
            lore.add(" §7╺ Nome do produto: " + (product.getValue().getItemStack().getItemMeta().getDisplayName() == null ? itemStack.getType().toString() : itemStack.getItemMeta().getDisplayName()) + "§f.");
            lore.add(" §7╺ Vendedor: §f" + VaultAPI.getPlayerGroupPrefix(product.getValue().getOwner()) + product.getValue().getOwner() + "§f.");
            lore.add(" §7╺ Preço: §a$" + NumberFormat.formartAmount2(product.getValue().getPrice()) + "§f.");
            lore.add("");
            lore.add(product.getValue().containsMoneyString(p.getName()));

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            itemStacks.add(itemStack);
        });

        openGui(p, itemStacks, page, 21, "§8Mercado público:", 6, 10, 38, 42);
    }

    private static Inventory openGui(Player player, List<ItemStack> items, int page, int amountPerPage, String title,
                                     int lineAmount, int inicialIndex, int backSlot, int advanceSlot) {
        int quantidadeDePaginas = items.size() / amountPerPage;
        int inicial = (page - 1) * amountPerPage;
        if (inicial > items.size()) {

        } else {

            List<ItemStack> subLista = items.subList(inicial, items.size());
            Inventory menu = Bukkit.createInventory(null, lineAmount * 9, title);

            if (items.isEmpty()) menu.setItem(22, add(Material.WEB, "§cNada..."));

            int current = 0;
            for (ItemStack item : subLista) {
                while (isColumn(inicialIndex, 1) || isColumn(inicialIndex, 9)) {
                    inicialIndex++;
                }
                menu.setItem(getFreeSlot(menu), item);
                current++;
                inicialIndex++;
                if (current == amountPerPage) {
                    break;
                }
            }
            if (page > 1) {
                menu.setItem(47, add(Material.ARROW, "§aVoltar"));
            }

            if (page <= quantidadeDePaginas) {
                menu.setItem(51, add(Material.ARROW, "§aAvançar"));
            }

            menu.setItem(49, add(Material.NAME_TAG, "§aGerenciador", new String[]{"§7Clique para gerenciar seus produtos."}));
            player.openInventory(menu);
            return menu;
        }
        return null;
    }

    public static void openInventoryProducts(Player p, int page) {
        List<ItemStack> itemStacks = new ArrayList<>();

        marketController.getITEMS_OWNER().get(p.getName()).forEach((product) -> {

            ItemStack itemStack = product.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName("§a#" + product.getID() + "§a.");

            List<String> lore = new ArrayList<>();
            lore.add("§7Clique para recolher este produto.");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            itemStacks.add(itemStack);
        });

        openGuiProducts(p, itemStacks, page, 21, "§8Seus produtos:", 6, 10, 38, 42);
    }

    private static Inventory openGuiProducts(Player player, List<ItemStack> items, int page, int amountPerPage, String title,
                                             int lineAmount, int inicialIndex, int backSlot, int advanceSlot) {
        int quantidadeDePaginas = items.size() / amountPerPage;
        int inicial = (page - 1) * amountPerPage;
        if (inicial > items.size()) {

        } else {

            List<ItemStack> subLista = items.subList(inicial, items.size());
            Inventory menu = Bukkit.createInventory(null, lineAmount * 9, title);

            if (items.isEmpty()) menu.setItem(22, add(Material.WEB, "§cNada..."));

            int current = 0;
            for (ItemStack item : subLista) {
                while (isColumn(inicialIndex, 1) || isColumn(inicialIndex, 9)) {
                    inicialIndex++;
                }
                menu.setItem(getFreeSlot(menu), item);
                current++;
                inicialIndex++;
                if (current == amountPerPage) {
                    break;
                }
            }
            if (page > 1) {
                menu.setItem(47, add(Material.ARROW, "§aVoltar"));
            }

            if (page <= quantidadeDePaginas) {
                menu.setItem(51, add(Material.ARROW, "§aAvançar"));
            }

            menu.setItem(49, add(Material.ARROW, "§aVoltar"));
            player.openInventory(menu);
            return menu;
        }
        return null;
    }

    public static void openInventoryProductsExpirados(Player p, int page) {
        List<ItemStack> itemStacks = new ArrayList<>();

        marketController.getITEMS_EXPIRADOS().get(p.getName()).forEach((product) -> {

            ItemStack itemStack = product.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName("§a#" + product.getID() + "§a.");

            List<String> lore = new ArrayList<>();
            lore.add("§7Clique para recolher este produto.");
            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            itemStacks.add(itemStack);
        });

        openGuiProducts(p, itemStacks, page, 21, "§8Produtos expirados:", 6, 10, 38, 42);
    }

    public static void openInventoryPrivate(Player p, int page) {
        List<ItemStack> itemStacks = new ArrayList<>();

        marketController.getPrivateProducts().get(p.getName()).forEach((product) -> {

            ItemStack itemStack = product.getItemStack().clone();
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.setDisplayName("§a#" + product.getID() + "§a.");

            if (itemMeta.getLore() == null) {
                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add(" §7╺ Nome do produto: " + (product.getItemStack().getItemMeta().getDisplayName() == null ? itemStack.getType().toString() : itemStack.getItemMeta().getDisplayName()) + "§f.");
                lore.add(" §7╺ Vendedor: §f" + VaultAPI.getPlayerGroupPrefix(product.getOwner()) + product.getOwner() + "§f.");
                lore.add(" §7╺ Preço: §a$" + NumberFormat.formartAmount2(product.getPrice()) + "§f.");
                lore.add("");
                lore.add(product.containsMoneyString(p.getName()));
                itemMeta.setLore(lore);
                itemStack.setItemMeta(itemMeta);

                itemStacks.add(itemStack);
                return;
            }

            List<String> lore = itemStack.getItemMeta().getLore();
            lore.add(" ");
            lore.add(" §7╺ Nome do produto: " + (product.getItemStack().getItemMeta().getDisplayName() == null ? itemStack.getType().toString() : itemStack.getItemMeta().getDisplayName()) + "§f.");
            lore.add(" §7╺ Vendedor: §f" + VaultAPI.getPlayerGroupPrefix(product.getOwner()) + product.getOwner() + "§f.");
            lore.add(" §7╺ Preço: §a$" + NumberFormat.formartAmount2(product.getPrice()) + "§f.");
            lore.add("");
            lore.add(product.containsMoneyString(p.getName()));

            itemMeta.setLore(lore);
            itemStack.setItemMeta(itemMeta);

            itemStacks.add(itemStack);
        });

        openGuiPrivate(p, itemStacks, page, 14, "§8Mercado pessoal:", 6, 10, 38, 42);
    }

    private static Inventory openGuiPrivate(Player player, List<ItemStack> items, int page, int amountPerPage, String title,
                                            int lineAmount, int inicialIndex, int backSlot, int advanceSlot) {
        int quantidadeDePaginas = items.size() / amountPerPage;
        int inicial = (page - 1) * amountPerPage;
        if (inicial > items.size()) {

        } else {

            List<ItemStack> subLista = items.subList(inicial, items.size());
            Inventory menu = Bukkit.createInventory(null, lineAmount * 9, title);

            if (items.isEmpty()) menu.setItem(22, add(Material.WEB, "§cNada..."));

            int current = 0;
            for (ItemStack item : subLista) {
                while (isColumn(inicialIndex, 1) || isColumn(inicialIndex, 9)) {
                    inicialIndex++;
                }
                menu.setItem(getFreeSlot(menu), item);
                current++;
                inicialIndex++;
                if (current == amountPerPage) {
                    break;
                }
            }
            if (page > 1) {
                menu.setItem(47, add(Material.ARROW, "§aVoltar"));
            }

            if (page <= quantidadeDePaginas) {
                menu.setItem(51, add(Material.ARROW, "§aAvançar"));
            }

            menu.setItem(49, add(Material.ARROW, "§aVoltar"));
            player.openInventory(menu);
            return menu;
        }
        return null;
    }

    private static int getColumn(int index) {
        if (index < 9) {
            return index + 1;
        }
        return (index % 9) + 1;
    }

    private static boolean isColumn(int index, int colunm) {
        return getColumn(index) == colunm;
    }

    private static int getFreeSlot(Inventory inv) {
        int[] ints = {10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32, 33, 34};
        int[] array;
        for (int length = (array = ints).length, j = 0; j < length; ++j) {
            int i = array[j];
            if (inv.getItem(i) == null || inv.getItem(i).getType() == Material.AIR) {
                return i;
            }
        }
        return 1;
    }

    public static void openInventoryProducts(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 4 * 9, "§8Mercado - Gerenciador:");

        inventory.setItem(11, add(Material.ENDER_PEARL, "§eSeus produtos à venda.", new String[]{"§7Clique para visualizar seus produtos", "§7que estão à venda no momento."}));
        inventory.setItem(13, add(Material.BEACON, "§aMercado privado.", new String[]{"§7Clique para visualizar seu mercado privado."}));
        inventory.setItem(15, add(Material.ROTTEN_FLESH, "§cProdutos expirados.", new String[]{"§7Clique para recolher seus produtos", "§7que não foram comprados."}));

        inventory.setItem(31, add(Material.ARROW, "§aVoltar"));

        player.openInventory(inventory);
    }
}
