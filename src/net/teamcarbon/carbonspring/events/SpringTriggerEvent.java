package net.teamcarbon.carbonspring.events;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.util.Vector;

@SuppressWarnings("unused")
public class SpringTriggerEvent extends Event implements Cancellable {
	public enum PlateType { STONE, WOOD, IRON, GOLD }
	private Player player;
	private PlateType plateType;
	private Material baseMat;
	private Location plateLoc;
	private Vector jumpVector;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	/**
	 * Creates an event representing a jump plate being triggered by a player
	 * @param player The Player involved
	 * @param plateType The PlateType of the plate used
	 * @param baseMat The Material of the block under the spring plate
	 * @param plateLoc The Location of the jump plate
	 */
	public SpringTriggerEvent(Player player, PlateType plateType, Material baseMat, Location plateLoc, Vector jumpVector) {
		this.player = player;
		this.plateType = plateType;
		this.baseMat = baseMat;
		this.plateLoc = plateLoc;
		this.jumpVector = jumpVector;
	}

	public Player getPlayer() { return player; }
	public PlateType getPlateType() { return plateType; }
	public Material getBaseMaterial() { return baseMat; }
	public Location getLocation() { return plateLoc.clone(); }
	public Vector getJumpVector() { return jumpVector.clone(); }

	public void setJumpVector(Vector jumpVector) { this.jumpVector = jumpVector;}

	@Override
	public boolean isCancelled() { return cancelled; }
	@Override
	public void setCancelled(boolean b) { cancelled = b; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
