package io.github.kit.tulipChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TulipChat extends JavaPlugin implements Listener {

    private Set<Player> mutedPlayers;
    private List<String> badWords;
    private boolean globalChatEnabled;
    private String chatPrefix;

    @Override
    public void onEnable() {
        getLogger().info(ChatColor.GREEN + "[TulipChat] Plugin Enabled!");

        // Load configuration
        saveDefaultConfig();
        loadConfigValues();

        mutedPlayers = new HashSet<>();

        // Register event listener for chat
        Bukkit.getPluginManager().registerEvents(this, this);

        // Register commands
        getCommand("mute").setExecutor(new MuteCommand(this));
        getCommand("unmute").setExecutor(new UnmuteCommand(this));
        getCommand("toggleGlobalChat").setExecutor(new ToggleGlobalChatCommand(this));
    }

    @Override
    public void onDisable() {
        getLogger().info(ChatColor.RED + "[TulipChat] Plugin Disabled!");
    }

    private void loadConfigValues() {
        FileConfiguration config = getConfig();
        badWords = config.getStringList("bad-words");
        globalChatEnabled = config.getBoolean("global-chat-enabled");
        chatPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix", "&7[Player] "));
    }

    public void mutePlayer(Player player) {
        mutedPlayers.add(player);
    }

    public void unmutePlayer(Player player) {
        mutedPlayers.remove(player);
    }

    public boolean isPlayerMuted(Player player) {
        return mutedPlayers.contains(player);
    }

    public boolean isBadWord(String message) {
        for (String word : badWords) {
            if (message.toLowerCase().contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    public boolean isGlobalChatEnabled() {
        return globalChatEnabled;
    }

    public void toggleGlobalChat() {
        globalChatEnabled = !globalChatEnabled;
        getConfig().set("global-chat-enabled", globalChatEnabled);
        saveConfig();
        getLogger().info("[TulipChat] Global chat toggled to: " + globalChatEnabled);
    }

    @EventHandler
    public void onPlayerChat(@NotNull AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();

        // Check if player is muted
        if (isPlayerMuted(player)) {
            player.sendMessage(ChatColor.RED + "You are muted and cannot send messages.");
            event.setCancelled(true);
            return;
        }

        // Check for bad words
        if (isBadWord(event.getMessage())) {
            player.sendMessage(ChatColor.RED + "Your message contains inappropriate language.");
            event.setCancelled(true);
            return;
        }

        // Set chat format with prefix
        String format = chatPrefix + ChatColor.WHITE + "%s: %s";
        event.setFormat(format);

        // If global chat is disabled, restrict chat to nearby players
        if (!isGlobalChatEnabled()) {
            event.getRecipients().clear();
            for (Player recipient : Bukkit.getOnlinePlayers()) {
                if (recipient.getWorld().equals(player.getWorld()) && recipient.getLocation().distance(player.getLocation()) < 100) {
                    event.getRecipients().add(recipient);
                }
            }
        }
    }
}