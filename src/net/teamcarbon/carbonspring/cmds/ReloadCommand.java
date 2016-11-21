package net.teamcarbon.carbonspring.cmds;

import net.teamcarbon.carbonspring.CarbonSpring;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {

	private CarbonSpring plugin;
	public ReloadCommand(CarbonSpring p) { plugin = p; }

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender.hasPermission("carbonspring.reload")) {
			plugin.reloadVars();
			sender.sendMessage(ChatColor.AQUA + plugin.getDescription().getName() + " has been reloaded");
		} else {
			sender.sendMessage(ChatColor.RED + "You don't have permission to do that!");
		}
		return true;
	}
}
