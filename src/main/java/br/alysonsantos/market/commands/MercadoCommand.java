package br.alysonsantos.market.commands;

import br.alysonsantos.market.StoreMarket;
import br.alysonsantos.market.core.controllers.ConfigurationController;
import br.alysonsantos.market.manager.InventoryController;
import br.alysonsantos.market.manager.ProductManager;
import com.mojang.authlib.BaseUserAuthentication;
import com.sun.org.apache.regexp.internal.RE;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class MercadoCommand implements CommandExecutor {
    private ProductManager productManager = new ProductManager();
    private ConfigurationController configurationController = StoreMarket.getInstance().getConfigurationController();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {

        if (!(commandSender instanceof Player)) return true;

        Player player = (Player) commandSender;


        if (args.length == 1 && args[0].equalsIgnoreCase("ver")) {
            InventoryController.openInventory(player, 1);
            InventoryController.inventoryPage.put(player.getName(), 1);
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(new String[]{"", "§a§l MERCADO AJUDA:", "", "§f  /mercado vender <valor> <player>§7 - Vender um produto no mercado pessoal de alguém.", "§f  /mercado vender <valor>§7 - Vender um produto no mercado público.", "§f  /mercado ver§7 - Visualizar o mercado.", ""});
            return true;
        }

        if (args[0].equalsIgnoreCase("vender")) {
            if (args.length == 1) {
                player.sendMessage(new String[]{"", "§e Comando correto: §7/mercado vender <valor> <player>§e.", ""});
                return true;
            }

            try {
                int AMOUNT = Integer.parseInt(args[1]);

                if (player.getItemInHand().getType() == Material.AIR) {
                    player.sendMessage("§cÉ necessário que esteja segurando um item válido em sua mão.");
                    return true;
                }

                if (AMOUNT > configurationController.getValueLimitMax()) {
                    player.sendMessage(new String[]{"", "§c Este valor é muito alto, tente vender por um preço menor.", ""});
                    return true;
                }

                if (AMOUNT < configurationController.getValueLimitMin()) {
                    player.sendMessage(new String[]{"", "§c Este valor é muito pequeno, tente vender por um preço um pouco maior.", ""});
                    return true;
                }

                configurationController.getMarketLimite().forEach((a, b) -> {
                    if (StoreMarket.getMarketController().getITEMS_OWNER().get(player.getName()).size() == b) {
                        player.sendMessage("§cSeu limite de produtos a venda foi atingido.");
                        return;
                    }
                    return;
                });

                if (args.length == 3) {
                    Player target = Bukkit.getPlayer(args[2]);
                    if (target == null) {
                        player.sendMessage("§cEste jogador está offline ou não existe.");
                        return true;
                    }

                    configurationController.getMarketLimitePrivate().forEach((a, b) -> {
                        if (StoreMarket.getMarketController().getPrivateProducts().get(target.getName()).size() == b) {
                            player.sendMessage(new String[]{"", "§c O mercado pessoal deste jogador está lotado.", ""});
                            return;
                        }
                    });

                    productManager.sellProductPrivate(player, target, AMOUNT);
                    return true;
                }

                productManager.sellProduct(player, AMOUNT);
            } catch (NumberFormatException e) {
                player.sendMessage("§cDigite um valor válido, que contenha apenas números.");
            }
        }
        return false;
    }
}
