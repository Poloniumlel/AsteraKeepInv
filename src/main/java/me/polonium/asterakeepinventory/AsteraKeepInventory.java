package me.polonium.asterakeepinventory;

import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public final class AsteraKeepInventory extends JavaPlugin implements Listener {

    private List<ItemStack> keepItems;

    @Override
    public void onEnable() {
        // Plugin startup logic
        Bukkit.getLogger().info("AsteraKeepInventory has been enabled!");

        // Load keepItems from config
        loadConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        Bukkit.getLogger().info("AsteraKeepInventory has been disabled!");
    }

    private void loadConfig() {
        saveDefaultConfig(); // Saves the default config.yml if it doesn't exist
        keepItems = new ArrayList<>();
        List<String> itemNames = getConfig().getStringList("Keep_Items");
        for (String itemName : itemNames) {
            Material material = Material.matchMaterial(itemName);
            if (material != null) {
                keepItems.add(new ItemStack(material));
            } else {
                Bukkit.getLogger().warning("Invalid material name in config: " + itemName);
            }
        }
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getWorld().getGameRuleValue(GameRule.KEEP_INVENTORY)) {
            List<ItemStack> itemsToKeep = new ArrayList<>();

            // Iterate through player's inventory
            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && isItemToKeep(item)) {
                    itemsToKeep.add(item);
                } else if (item != null) {
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                }
            }

            // Clear player's inventory
            player.getInventory().clear();

            // Add items to keep back into player's inventory
            itemsToKeep.forEach(item -> player.getInventory().addItem(item));
        }
    }

    private boolean isItemToKeep(ItemStack item) {
        // Check if the ItemStack is in the list of keepItems
        for (ItemStack keepItem : keepItems) {
            if (item.isSimilar(keepItem)) {
                return true;
            }
        }
        return false;
    }
}
