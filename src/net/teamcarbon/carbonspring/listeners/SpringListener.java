package net.teamcarbon.carbonspring.listeners;

import net.teamcarbon.carbonspring.CarbonSpring;
import net.teamcarbon.carbonspring.events.SpringLandEvent;
import net.teamcarbon.carbonspring.events.SpringTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpringListener implements Listener {

	private CarbonSpring plugin;
	public SpringListener(CarbonSpring p) { plugin = p; }

	@EventHandler(ignoreCancelled = true)
	public void springTrigger(final SpringTriggerEvent e) {
		if (e.getPlayer().hasPermission("carbonspring.use.plate.*")
				|| e.getPlayer().hasPermission("carbonspring.use.plate." + e.getPlateType().name().toLowerCase())) {
			if (e.getPlayer().hasPermission("carbonspring.use.base.*")
					|| e.getPlayer().hasPermission("carbonspring.use.base." + e.getBaseMaterial().name().toLowerCase())) {
				Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
					public void run() {
						e.getPlayer().setVelocity(e.getJumpVector());
						if (!plugin.isLaunched(e.getPlayer())) plugin.setLaunched(e.getPlayer(), true);
					}
				}, 1L);
			}
		}
	}

	@EventHandler()
	public void springLand(final SpringLandEvent e) {
		Bukkit.getScheduler().runTaskLater(plugin, new Runnable() {
			public void run() {
				Player p = e.getPlayer();
				if (plugin.isLaunched(p) && System.currentTimeMillis() - plugin.getLaunchTime(p) > 100) {
					plugin.setLaunched(p, false);
					GameMode gm = p.getGameMode();
					if (!e.isCancelled() && e.getDamage() > 0.0 && gm == GameMode.SURVIVAL || gm == GameMode.ADVENTURE) {
						if (p.hasPermission("carbonspring.nofalldamage")) e.setDamage(0.0);
						p.damage(e.getDamage());
					}
				}
			}
		}, 1L);
	}
}
