package net.teamcarbon.carbonspring.listeners;

import net.teamcarbon.carbonlib.Misc.MiscUtils;
import net.teamcarbon.carbonspring.CarbonSpring;
import net.teamcarbon.carbonspring.events.SpringLandEvent;
import net.teamcarbon.carbonspring.events.SpringTriggerEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SpringListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void springTrigger(final SpringTriggerEvent e) {
		if (MiscUtils.perm(e.getPlayer(), "carbonspring.use.plate.*", "carbonspring.use.plate." + e.getPlateType().name().toLowerCase())) {
			if (MiscUtils.perm(e.getPlayer(), "carbonspring.use.base.*", "carbonspring.use.base." + e.getBaseMaterial().name().toLowerCase())) {
				Bukkit.getScheduler().runTaskLater(CarbonSpring.inst, new Runnable() {
					public void run() {
						e.getPlayer().setVelocity(e.getJumpVector());
						if (!CarbonSpring.isLaunched(e.getPlayer())) CarbonSpring.setLaunched(e.getPlayer(), true);
					}
				}, 1L);
			}
		}
	}

	@EventHandler(ignoreCancelled = false)
	public void springLand(final SpringLandEvent e) {
		Bukkit.getScheduler().runTaskLater(CarbonSpring.inst, new Runnable() {
			public void run() {
				Player p = e.getPlayer();
				if (CarbonSpring.isLaunched(p) && System.currentTimeMillis() - CarbonSpring.getLaunchTime(p) > 100) {
					CarbonSpring.setLaunched(p, false);
					GameMode gm = p.getGameMode();
					if (!e.isCancelled() && e.getDamage() > 0.0 && MiscUtils.exactEq(gm, GameMode.SURVIVAL, GameMode.ADVENTURE)) {
						if (MiscUtils.perm(p, "carbonspring.nofalldamage")) e.setDamage(0.0);
						p.damage(e.getDamage());
					}
				}
			}
		}, 1L);
	}
}
