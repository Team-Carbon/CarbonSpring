package net.teamcarbon.carbonspring.events;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@SuppressWarnings("unused")
public class SpringLandEvent extends Event implements Cancellable {
	private Player player;
	private Location landLoc;
	private double damage;
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled;

	/**
	 * Creates an event representing a player landing after being launched by a jump plate
	 * @param player The Player involved
	 * @param landLoc The Location of the landing
	 * @param damage The damage to be dealt upon landing
	 */
	public SpringLandEvent(Player player, Location landLoc, double damage) {
		this.player = player;
		this.landLoc = landLoc;
		this.damage = damage;
	}

	public Player getPlayer() { return player; }
	public Location getLocation() { return landLoc.clone(); }
	public double getDamage() { return damage; }

	public void setDamage(double damage) { this.damage = damage; }

	@Override
	public boolean isCancelled() { return cancelled; }
	@Override
	public void setCancelled(boolean b) { cancelled = b; }
	public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
}
