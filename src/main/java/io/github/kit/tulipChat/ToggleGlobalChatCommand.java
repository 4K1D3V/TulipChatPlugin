package io.github.kit.tulipChat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ToggleGlobalChatCommand implements CommandExecutor {

    private TulipChat plugin;

    public ToggleGlobalChatCommand(TulipChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, Command command, String label, String[] args) {
        plugin.toggleGlobalChat();
        String status = plugin.isGlobalChatEnabled() ? "enabled" : "disabled";
        sender.sendMessage(ChatColor.YELLOW + "Global chat is now " + status + ".");
        return true;
    }
}