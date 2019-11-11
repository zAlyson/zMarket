package br.alysonsantos.market.core.services;

import br.alysonsantos.market.StoreMarket;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

public class ConfigurationServiceImpl implements ConfigurationService {

    @Override
    public FileConfiguration getConfig() {
        return StoreMarket.getInstance().getConfig();
    }

}