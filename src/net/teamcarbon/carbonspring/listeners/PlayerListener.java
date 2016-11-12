package net.teamcarbon.carbonspring.listeners;

import net.teamcarbon.carbonlib.Misc.Messages.Clr;
import net.teamcarbon.carbonlib.Misc.MiscUtils;
import net.teamcarbon.carbonlib.Misc.TypeUtils;
import net.teamcarbon.carbonspring.CarbonSpring;
import net.teamcarbon.carbonspring.events.SpringLandEvent;
import net.teamcarbon.carbonspring.events.SpringTriggerEvent;
import net.teamcarbon.carbonspring.events.SpringTriggerEvent.PlateType;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

public class PlayerListener implements Listener {

	@EventHandler
	public void platePress(PlayerInteractEvent e) {
		if (e.getAction() == Action.PHYSICAL) {
			boolean l;
			Block b = e.getClickedBlock();
			PlateType pt = null;
			switch (b.getType()) {
				case STONE_PLATE: l = CarbonSpring.isStonePlateEnabled(); pt = PlateType.STONE; break;
				case WOOD_PLATE: l = CarbonSpring.isWoodPlateEnabled(); pt = PlateType.WOOD; break;
				case GOLD_PLATE: l = CarbonSpring.isGoldPlateEnabled(); pt = PlateType.GOLD; break;
				case IRON_PLATE: l = CarbonSpring.isIronPlateEnabled(); pt = PlateType.IRON; break;
				default: l = false;
			}
			if (l && blockCheck(b)) {
				b = e.getClickedBlock();
				boolean useDefVec = false;
				double dist = 5, height = 10, distDiv = 8.0, heightDiv = 7.0;
				Player p = e.getPlayer();
				Vector v = p.getLocation().getDirection();
				double d = 0.0;
				BlockState signState = b.getRelative(0, -2, 0).getState();
				if (signState instanceof Sign) {
					Sign sign = (Sign) signState;
					String[] lines = sign.getLines();
					String l0 = lines[0], l1 = lines[1], l2 = lines[2], l3 = lines[3];
					if (l0.equals(CarbonSpring.SIGN_HEADER)) {

						// Parse permission line first (last line, but oh well)
						if (!l3.isEmpty()) {
							if (!MiscUtils.perm(e.getPlayer(), "carbonspring.use.perm.*", "carbonspring.use.perm." + l3)) {
								return;
							}
						}

						// Parse vector line
						if (!l1.isEmpty()) {
							if (l1.contains(":")) {
								String[] lp = new String[3];
								lp[0] = l1.split(":")[0];
								lp[1] = l1.split(":")[1].split(",")[0];
								lp[2] = l1.split(":")[1].split(",")[1];
								if (MiscUtils.eq(lp[0].toUpperCase(), "N", "NE", "E", "SE", "S", "SW", "W", "NW")) {
									if (TypeUtils.isDouble(lp[1]) && TypeUtils.isDouble(lp[2])) {
										int[] dm = new int[2];
										if (MiscUtils.eq(lp[0], "N", "S")) dm[0] = 0; // N/S doesn't move X-wise
										if (MiscUtils.eq(lp[0], "E", "W")) dm[1] = 0; // E/S doesn't move Z-wise
										if (MiscUtils.eq(lp[0], "N", "NE", "NW")) dm[1] = -1; // All N moves -Z
										if (MiscUtils.eq(lp[0], "NE", "E", "SE")) dm[0] = 1; // All E moves +X
										if (MiscUtils.eq(lp[0], "SE", "S", "SW")) dm[1] = 1; // All S moves +Z
										if (MiscUtils.eq(lp[0], "SW", "W", "NW")) dm[0] = -1; // All W moves -X
										dist = Math.abs(Double.parseDouble(lp[1]));
										height = Double.parseDouble(lp[2]) / heightDiv;
										v = new Vector((dist * dm[0]) / distDiv, height, (dist * dm[1]) / distDiv);
									} else useDefVec = true;
								} else useDefVec = true;
							} else if (l1.contains(",")) {
								String[] lp = l1.split(",");
								if (lp.length == 2) {
									if (TypeUtils.isDouble(lp[0]) && TypeUtils.isDouble(lp[1])) {
										dist = Double.parseDouble(lp[0]);
										height = Double.parseDouble(lp[1]);
										v = new Vector(dist / distDiv, height / heightDiv, dist / distDiv);
									} else useDefVec = true;
								} else if (lp.length == 3) {
									if (TypeUtils.isDouble(lp[0]) && TypeUtils.isDouble(lp[1]) && TypeUtils.isDouble(lp[2])) {
										double x = Double.parseDouble(lp[0]), y = Double.parseDouble(lp[1]), z = Double.parseDouble(lp[2]);
										v = new Vector(x, y, z);
									} else useDefVec = true;
								} else {
									useDefVec = true;
								}
							} else if (TypeUtils.isDouble(l1)) {
								v = new Vector(0, Double.parseDouble(l1) / heightDiv, 0);
							} else useDefVec = true;
						} else useDefVec = true;

						// Parse damage line
						if (TypeUtils.isDouble(l2)) { d = Double.parseDouble(l2); }

					} else useDefVec = true;
				} else useDefVec = true;
				if (useDefVec) v = new Vector(v.getX() * (dist / distDiv), height / heightDiv, v.getZ() * (dist / distDiv));
				Material bm = b.getRelative(0, -1, 0).getType();
				CarbonSpring.setPendingDamage(p, d);
				SpringTriggerEvent jpte = new SpringTriggerEvent(p, pt, bm, b.getLocation(), v);
				CarbonSpring.pm.callEvent(jpte);
			}
		}
	}

	@EventHandler
	public void move(PlayerMoveEvent e) {
		if (((Entity)e.getPlayer()).isOnGround() && CarbonSpring.isLaunched(e.getPlayer())) {
			double dmg = CarbonSpring.getPendingDmg(e.getPlayer());
			SpringLandEvent jple = new SpringLandEvent(e.getPlayer(), e.getTo(), dmg);
			CarbonSpring.pm.callEvent(jple);
		}
	}

	@EventHandler
	public void damage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (e.getCause() == DamageCause.FALL && CarbonSpring.isLaunched(p)) {
				e.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void signCreate(SignChangeEvent e) {
		if (MiscUtils.perm(e.getPlayer(), "carbonspring.signcreate")) {
			String[] lines = e.getLines();
			if (ChatColor.stripColor(lines[0]).equalsIgnoreCase(ChatColor.stripColor(CarbonSpring.SIGN_HEADER))) {
				String power = lines[1], damage = lines[2], perm = lines[3];
				if (!power.isEmpty() && !(power.matches("^\\d+(\\.\\d+)?(,\\d+(\\.\\d+)?){0,2}$")
						|| power.toUpperCase().matches("^(N|NE|E|SE|S|SW|W|NW):\\d+(\\.\\d+)?,\\d+(\\.\\d+)?$"))) {
					e.getPlayer().sendMessage(Clr.RED + "Invalid power settings on line 2");
					e.setLine(0, Clr.trans("&4[Invalid]"));
					e.getBlock().getState().update();
					return;
				} else if (!MiscUtils.perm(e.getPlayer(), "carbonspring.createsign.power")) {
					e.getPlayer().sendMessage(Clr.RED + "You don't have permission to override jump plate power");
					e.setLine(0, Clr.trans("&4[No Perm]"));
					e.getBlock().getState().update();
					return;
				}
				if (!damage.isEmpty() && !TypeUtils.isInteger(damage)) {
					e.getPlayer().sendMessage(Clr.RED + "Invalid damage on line 3");
					e.setLine(0, Clr.trans("&4[Invalid]"));
					e.getBlock().getState().update();
					return;
				} else if (!MiscUtils.perm(e.getPlayer(), "carbonspring.createsign.damage")) {
					e.getPlayer().sendMessage(Clr.RED + "You don't have permission to override jump plate damage");
					e.setLine(0, Clr.trans("&4[No Perm]"));
					e.getBlock().getState().update();
					return;
				}
				if (!perm.isEmpty() && !MiscUtils.perm(e.getPlayer(), "carbonspring.createsign.perm")) {
					e.getPlayer().sendMessage(Clr.RED + "You don't have permission to override jump plate permission");
					e.setLine(0, Clr.trans("&4[No Perm]"));
					e.getBlock().getState().update();
					return;
				}
				e.setLine(0, CarbonSpring.SIGN_HEADER);
				e.getBlock().getState().update();
			}
		}
	}

	private boolean blockCheck(Block b) {
		BlockFace[] bf = new BlockFace[] {BlockFace.DOWN, BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH,
				BlockFace.SOUTH, BlockFace.WEST, BlockFace.WEST, BlockFace.NORTH, BlockFace.NORTH};
		if (CarbonSpring.requireSign()) {
			if (!(b.getRelative(0, -2, 0).getState() instanceof Sign)) return false;
			Sign sign = (Sign)b.getRelative(0, -2, 0).getState();
			if (!sign.getLine(0).equals(CarbonSpring.SIGN_HEADER)) return false;
		}
		for (BlockFace f : bf) {
			if (f == BlockFace.DOWN && !CarbonSpring.requireThreeByThree())
				return CarbonSpring.isBaseBlock(b.getRelative(f).getType());
			else {
				b = b.getRelative(f);
				if (!CarbonSpring.isBaseBlock(b.getType())) return false;
			}
		}
		return true;
	}
}
