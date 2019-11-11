package br.alysonsantos.market.core.controllers;

import br.alysonsantos.market.StoreMarket;
import br.alysonsantos.market.core.models.Product;
import br.alysonsantos.market.core.services.StorageService;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Data
@AllArgsConstructor
public class MarketController implements Controller {
    private final StorageService storageService;
    private final Map<Integer, Product> cachedProduct = new HashMap<>();
    private final Map<String, List<Product>> ITEMS_EXPIRADOS = new HashMap<>();
    private final Map<String, List<Product>> ITEMS_OWNER = new HashMap<>();
    private final Map<String, List<Product>> privateProducts = new HashMap<>();

    @Override
    public void init() {
        storageService.init();
        cachedProduct.clear();

        Map<Integer, Product> productMap = storageService.getProducts();
        StoreMarket.getInstance().getLogger().log(Level.INFO, "[Storage] Foram carregados {0} produtos da base de dados.", productMap.size());

        cachedProduct.putAll(productMap);
    }

    public void saveProduct(Product product) {
        cachedProduct.put(product.getID(), product);
        storageService.save(product).join();
    }

    public void deleteProduct(Product product) {
        storageService.delete(product).join();
        cachedProduct.remove(product.getID());
    }

    public void loadProductsByPlayer(Player nome) {
        Bukkit.getScheduler().runTaskAsynchronously(StoreMarket.getInstance(), new Runnable() {
            @Override
            public void run() {
                List<Product> productsOwner = new ArrayList<>();
                List<Product> productsPrivates = new ArrayList<>();
                List<Product> productsExpire = new ArrayList<>();


                cachedProduct.forEach((a, b) -> {

                    if (b.getOwner().equals(nome.getName()) && b.getTempo() < System.currentTimeMillis()) {
                        productsExpire.add(b);
                        return;
                    }

                    if (b.isPrivate() && b.getTarget().equals(nome.getName())) {
                        productsPrivates.add(b);
                    }

                    if (b.getOwner().equals(nome.getName())) {
                        productsOwner.add(b);
                    }
                });

                ITEMS_OWNER.put(nome.getName(), productsOwner);
                privateProducts.put(nome.getName(), productsPrivates);
                ITEMS_EXPIRADOS.put(nome.getName(), productsExpire);
            }
        });
    }

    @Override
    public void stop() {
        cachedProduct.forEach((a, b) -> saveProduct(b));

        cachedProduct.clear();
        storageService.stop();
    }
}
