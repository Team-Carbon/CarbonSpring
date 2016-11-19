package net.teamcarbon.carbonspring;

import net.teamcarbon.carbonlib.CarbonPlugin;
import net.teamcarbon.carbonlib.Misc.Messages.Clr;
import net.teamcarbon.carbonlib.Misc.MiscUtils;
import net.teamcarbon.carbonspring.cmds.ReloadCommand;
import net.teamcarbon.carbonspring.listeners.SpringListener;
import net.teamcarbon.carbonspring.listeners.PlayerListener;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SuppressWarnings("unused")
public class CarbonSpring extends CarbonPlugin {

	private static boolean sp = true, wp = true, gp = true, ip = true, tbt = false, rs = true;
	private static List<Material> bb = new ArrayList<>();

	public static String SIGN_HEADER;
	private static HashMap<Player, Long> launched = new HashMap<>();
	private static HashMap<Player, Double> pendingDamage = new HashMap<>();

	public String getDebugPath() { return "debug-messages-enabled"; }

	public void enablePlugin() {
		SIGN_HEADER = Clr.trans("&c&l[&4&lSpring&c&l]");
		reloadVars();

		pm().registerEvents(new PlayerListener(), this);
		pm().registerEvents(new SpringListener(), this);
		getCommand("carbonspringreload").setExecutor(new ReloadCommand());
	}

	public void disablePlugin() { }

	public static void setLaunched(Player p, boolean l) {
		if (l && !launched.containsKey(p)) { launched.put(p, System.currentTimeMillis()); }
		else if (!l && launched.containsKey(p)) { launched.remove(p); }
	}
	public static void setPendingDamage(Player p, double dmg) {
		if (dmg > 0.0) pendingDamage.put(p, dmg);
		else if (pendingDamage.containsKey(p)) pendingDamage.remove(p);
	}

	public static boolean isBaseBlock(Material mat) { return bb.contains(mat); }
	public static boolean isStonePlateEnabled() { return sp; }
	public static boolean isWoodPlateEnabled() { return wp; }
	public static boolean isGoldPlateEnabled() { return gp; }
	public static boolean isIronPlateEnabled() { return ip; }
	public static boolean requireThreeByThree() { return tbt; }
	public static boolean requireSign() { return rs; }
	public static boolean isLaunched(Player p) { return launched.containsKey(p); }
	public static long getLaunchTime(Player p) { return isLaunched(p) ? launched.get(p) : -1L; }
	public static boolean hasPendingDmg(Player p) { return pendingDamage.containsKey(p); }
	public static double getPendingDmg(Player p) { return hasPendingDmg(p) ? pendingDamage.get(p) : 0.0; }

	public void reloadVars() {
		reloadConf();
		sp = getConfig().getBoolean("enabled-plates.stone", true);
		wp = getConfig().getBoolean("enabled-plates.wood", true);
		gp = getConfig().getBoolean("enabled-plates.gold", true);
		ip = getConfig().getBoolean("enabled-plates.iron", true);
		tbt = getConfig().getBoolean("require-three-by-three", false);
		rs = getConfig().getBoolean("require-sign", true);
		bb.clear();
		if (getConfig().contains("base-blocks") && inst.getConfig().isList("base-blocks")) {
			for (String s : inst.getConfig().getStringList("base-blocks")) {
				Material mat = MiscUtils.getMaterial(s);
				if (mat != null && !bb.contains(mat)) { bb.add(mat); }
			}
			if (bb.isEmpty()) bb.add(Material.REDSTONE_BLOCK);
		} else { bb.add(Material.REDSTONE_BLOCK); }
	}
}
