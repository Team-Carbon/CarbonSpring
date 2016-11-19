package net.teamcarbon.carbonspring.cmds;

import net.teamcarbon.carbonlib.CarbonPlugin;
import net.teamcarbon.carbonlib.Misc.Messages.Clr;
import net.teamcarbon.carbonlib.Misc.MiscUtils;
import net.teamcarbon.carbonspring.CarbonSpring;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class ReloadCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (MiscUtils.perm(sender, "carbonspring.reload")) {
			CarbonSpring cs = (CarbonSpring) CarbonPlugin.getPlugin("CarbonSpring");
			if (cs != null) {
				cs.reloadVars();
				sender.sendMessage(Clr.AQUA + cs.getDescription().getName() + " has been reloaded");
			} else {
				sender.sendMessage(Clr.RED + "An error occurred while reloading!");
			}
		} else {
			sender.sendMessage(Clr.RED + "You don't have permission to do that.");
		}
		return true;
	}
}
