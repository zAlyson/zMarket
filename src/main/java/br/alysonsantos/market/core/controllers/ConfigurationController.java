package br.alysonsantos.market.core.controllers;

import br.alysonsantos.market.StoreMarket;
import br.alysonsantos.market.core.services.ConfigurationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ConfigurationController implements Controller {
    private final ConfigurationService configurationService;

    @Getter
    private HashMap<String, Integer> marketLimite;
    @Getter
    private HashMap<String, Integer> marketLimitePrivate;
    @Getter
    private String inventoryNameProducts;
    @Getter
    private String inventoryNameProductsExpire;
    @Getter
    private String inventoryNameProductsPrivate;
    @Getter
    private String inventoryNameProductsSell;
    @Getter
    private String actionbarSaleAnnouncement;
    @Getter
    private List<String> productForSalePrivate;
    @Getter
    private List<String> receivedProductPrivate;
    @Getter
    private List<String> productForSale;
    @Getter
    private List<String> youBought;
    @Getter
    private List<String> productSold;
    @Getter
    private int valueLimitMax;
    @Getter
    private int valueLimitMin;
    @Getter
    private int time;

    @Override
    public void init() {
        marketLimite = new HashMap<>();
        marketLimitePrivate = new HashMap<>();

        valueLimitMax = configurationService.getConfig().getInt("value_limit_max");
        valueLimitMin = configurationService.getConfig().getInt("value_limit_min");
        time = configurationService.getConfig().getInt("product_expire_time");

        configurationService.getConfig().getConfigurationSection("limit_products").getKeys(false).forEach(a -> {
            marketLimite.put(configurationService.getConfig().getString("limit_products." + a), Integer.parseInt(a));
        });

        configurationService.getConfig().getConfigurationSection("limit_private_products").getKeys(false).forEach(a -> {
            marketLimitePrivate.put(configurationService.getConfig().getString("limit_private_products." + a), Integer.parseInt(a));
        });

        actionbarSaleAnnouncement = parseColors(configurationService.getConfig().getString("messages.actionbar_sale_announcement"));

        productForSalePrivate = parseColors(configurationService.getConfig().getStringList("messages.product_for_sale_private"));
        receivedProductPrivate = parseColors(configurationService.getConfig().getStringList("messages.received_product_private"));
        productForSale = parseColors(configurationService.getConfig().getStringList("messages.product_for_sale"));
        productSold = parseColors(configurationService.getConfig().getStringList("messages.product_sold"));
        youBought = parseColors(configurationService.getConfig().getStringList("messages.you_bought"));

        Bukkit.getLogger().warning("Configuração carregada com sucesso!");
    }

    private String parseColors(final String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    private List<String> parseColors(final List<String> strings) {
        return strings.stream().map(this::parseColors).collect(Collectors.toList());
    }

    @Override
    public void stop() {

    }
}