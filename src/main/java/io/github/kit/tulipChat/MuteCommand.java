package io.github.kit.tulipChat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class MuteCommand implements CommandExecutor {

    private TulipChat plugin;

    public MuteCommand(TulipChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String @NotNull [] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "Usage: /mute <player>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");
            return true;
        }

        plugin.mutePlayer(target);
        sender.sendMessage(ChatColor.GREEN + target.getName() + " has been muted.");
        return true;
    }
}