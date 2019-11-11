package br.alysonsantos.market;

import br.alysonsantos.market.apis.ReflectionUtils;
import br.alysonsantos.market.commands.MercadoCommand;
import br.alysonsantos.market.core.controllers.ConfigurationController;
import br.alysonsantos.market.core.controllers.MarketController;
import br.alysonsantos.market.core.services.ConfigurationService;
import br.alysonsantos.market.core.services.ConfigurationServiceImpl;
import br.alysonsantos.market.core.services.StorageService;
import br.alysonsantos.market.core.services.StorageServiceSQLImpl;
import br.alysonsantos.market.manager.InventoryController;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

public class StoreMarket extends JavaPlugin {
    @Getter
    private static StoreMarket instance;
    @Getter
    private static MarketController marketController;
    @Getter
    private final ConfigurationController configurationController;
    @Getter
    private final Executor executor = ForkJoinPool.commonPool();


    public StoreMarket() {
        super();
        instance = this;
        saveDefaultConfig();

        StorageService storageService;
        storageService = new StorageServiceSQLImpl();
        marketController = new MarketController(storageService);

        ConfigurationService configurationService = new ConfigurationServiceImpl();
        configurationController = new ConfigurationController(configurationService);
    }

    @Override
    public void onEnable() {
        Bukkit.getLogger().warning("Iniciando o plugin, e carregando o banco de dados...");
        configurationController.init();
        marketController.init();

        ReflectionUtils.loadUtils();

        Bukkit.getPluginManager().registerEvents(new PlayerListeners(), this);
        Bukkit.getPluginManager().registerEvents(new InventoryController(), this);
        getCommand("mercado").setExecutor(new MercadoCommand());
    }

    @Override
    public void onDisable() {
        marketController.stop();
    }
}
