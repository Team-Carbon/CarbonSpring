package net.teamcarbon.carbonspring;

import net.teamcarbon.carbonspring.cmds.ReloadCommand;
import net.teamcarbon.carbonspring.listeners.SpringListener;
import net.teamcarbon.carbonspring.listeners.PlayerListener;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class CarbonSpring extends JavaPlugin {

	private boolean sp = true, wp = true, gp = true, ip = true, tbt = false, rs = true;
	private List<Material> bb = new ArrayList<>();

	public String SIGN_HEADER;
	private HashMap<Player, Long> launched = new HashMap<>();
	private HashMap<Player, Double> pendingDamage = new HashMap<>();

	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		SIGN_HEADER = ChatColor.translateAlternateColorCodes('&', "&c&l[&4&lSpring&c&l]");
		reloadVars();

		pm.registerEvents(new PlayerListener(this), this);
		pm.registerEvents(new SpringListener(this), this);
		getCommand("carbonspringreload").setExecutor(new ReloadCommand(this));
	}

	public void setLaunched(Player p, boolean l) {
		if (l && !launched.containsKey(p)) { launched.put(p, System.currentTimeMillis()); }
		else if (!l && launched.containsKey(p)) { launched.remove(p); }
	}
	public void setPendingDamage(Player p, double dmg) {
		if (dmg > 0.0) pendingDamage.put(p, dmg);
		else if (pendingDamage.containsKey(p)) pendingDamage.remove(p);
	}

	public boolean isBaseBlock(Material mat) { return bb.contains(mat); }
	public boolean isStonePlateEnabled() { return sp; }
	public boolean isWoodPlateEnabled() { return wp; }
	public boolean isGoldPlateEnabled() { return gp; }
	public boolean isIronPlateEnabled() { return ip; }
	public boolean requireThreeByThree() { return tbt; }
	public boolean requireSign() { return rs; }
	public boolean isLaunched(Player p) { return launched.containsKey(p); }
	public long getLaunchTime(Player p) { return isLaunched(p) ? launched.get(p) : -1L; }
	public boolean hasPendingDmg(Player p) { return pendingDamage.containsKey(p); }
	public double getPendingDmg(Player p) { return hasPendingDmg(p) ? pendingDamage.get(p) : 0.0; }

	public void reloadVars() {
		reloadConfig();
		sp = getConfig().getBoolean("enabled-plates.stone", true);
		wp = getConfig().getBoolean("enabled-plates.wood", true);
		gp = getConfig().getBoolean("enabled-plates.gold", true);
		ip = getConfig().getBoolean("enabled-plates.iron", true);
		tbt = getConfig().getBoolean("require-three-by-three", false);
		rs = getConfig().getBoolean("require-sign", true);
		bb.clear();
		if (getConfig().contains("base-blocks") && getConfig().isList("base-blocks")) {
			for (String s : getConfig().getStringList("base-blocks")) {
				try {
					Material mat = Material.valueOf(s);
					if (!bb.contains(mat)) bb.add(mat);
				} catch (Exception e) {
					getLogger().log(Level.WARNING, "Failed to parse material: " + s);
				}
			}
			if (bb.isEmpty()) bb.add(Material.REDSTONE_BLOCK);
		} else { bb.add(Material.REDSTONE_BLOCK); }
	}
}
