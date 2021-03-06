package com.chasechocolate.mccod.listeners;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import com.chasechocolate.mccod.McCOD;
import com.chasechocolate.mccod.events.CODDeathEvent;
import com.chasechocolate.mccod.game.GameUtils;
import com.chasechocolate.mccod.game.arena.ArenaUtils;
import com.chasechocolate.mccod.game.gun.Gun;
import com.chasechocolate.mccod.game.gun.GunUtils;
import com.chasechocolate.mccod.utils.PlayerUtils;

public class EntityDamageListener implements Listener {
	@SuppressWarnings("unused")
	private McCOD plugin;
	
	public EntityDamageListener(McCOD plugin){
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event){
		if(event.getEntity() instanceof Player){
			Player damaged = (Player) event.getEntity();
			if(event.getDamager() instanceof Player){
				Player damager = (Player) event.getDamager();
				if(GameUtils.isInGame(damaged) && GameUtils.isInGame(damager)){
					if(ArenaUtils.getPlayerArena(damaged).equals(ArenaUtils.getPlayerArena(damager))){
						int damage = event.getDamage();
						int currentHealth = damaged.getHealth();
						int minHealth = 0;
						
						if(PlayerUtils.sameTeam(damager, damaged) && PlayerUtils.sameArena(damager, damaged)){
							event.setCancelled(true);
							return;
						}
						
						if(currentHealth - damage <= minHealth){
							event.setCancelled(true);
							
							CODDeathEvent deathEvent = new CODDeathEvent(damaged, damager);
							Bukkit.getPluginManager().callEvent(deathEvent);
						}
					}
				}
			}
			
			if(event.getDamager() instanceof Snowball){
				Snowball snowball = (Snowball) event.getDamager();
				if(snowball.getShooter() instanceof Player){
					Player shooter = (Player) snowball.getShooter();
					if(GameUtils.isInGame(shooter) && GameUtils.isInGame(damaged)){
						UUID id = snowball.getUniqueId();
						
						if(!(PlayerUtils.sameTeam(shooter, damaged)) && PlayerUtils.sameArena(shooter, damaged)){
							if(GunUtils.shotBullets.containsKey(id)){
								Gun gun = GunUtils.shotBullets.get(id);
								event.setDamage(gun.getDamage());
								GunUtils.shotBullets.remove(id);
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent event){
		if(event.getEntity() instanceof Player){
			Player damaged = (Player) event.getEntity();
			if(GameUtils.isInGame(damaged)){
				int damage = event.getDamage();
				int currentHealth = damaged.getHealth();
				int minHealth = 0;
				
				if(currentHealth - damage <= minHealth){
					CODDeathEvent deathEvent = new CODDeathEvent(damaged);
					Bukkit.getPluginManager().callEvent(deathEvent);
				}
			}
		}
	}
}