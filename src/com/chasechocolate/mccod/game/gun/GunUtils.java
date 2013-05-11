package com.chasechocolate.mccod.game.gun;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.chasechocolate.mccod.McCOD;
import com.chasechocolate.mccod.utils.Config;
import com.chasechocolate.mccod.utils.IconMenu;
import com.chasechocolate.mccod.utils.IconMenu.OptionClickEvent;
import com.chasechocolate.mccod.utils.IconMenu.OptionClickEventHandler;
import com.chasechocolate.mccod.utils.PlayerUtils;
import com.chasechocolate.mccod.utils.Utilities;

public class GunUtils {
	private static HashMap<String, Gun> playerGuns = new HashMap<String, Gun>();
	public static HashMap<UUID, Gun> shotBullets = new HashMap<UUID, Gun>();
	
	public static List<Gun> getAllGuns(){
		List<Gun> allGuns = new ArrayList<Gun>();
		
		for(String gunName : Config.getGunsConfig().getKeys(false)){
			int damage;
			int item;
			int ammo;
			
			if(Config.getGunsConfig().isInt(gunName + ".damage")){
				damage = Config.getGunsConfig().getInt(gunName + ".damage");
			} else {
				damage = 6;
			}
			
			if(Config.getGunsConfig().isInt(gunName + ".item")){
				item = Config.getGunsConfig().getInt(gunName + ".item");
			} else {
				item = 292;
			}

			if(Config.getGunsConfig().isInt(gunName + ".ammo")){
				ammo = Config.getGunsConfig().getInt(gunName + ".ammo");
			} else {
				ammo = 128;
			}
			
			Gun gun = new Gun(gunName, item, damage, ammo);
			
			allGuns.add(gun);
		}
		
		return allGuns;
	}
	
	public static Gun getGun(String gunName){
		if(isGun(gunName)){
			Gun gun = null;
			
			for(Gun allGuns : getAllGuns()){
				if(allGuns.getName().equals(gunName)){
					gun = allGuns;
				}
			}
			
			return gun;
		} else {
			return null;
		}
	}
	
	public static void showAvailableGuns(Player player){
		StringBuilder yourGuns = new StringBuilder();
		StringBuilder otherGuns = new StringBuilder();
		
		String yourGunsString = "";
		String otherGunsString = "";
		
		int totalGuns = getAllGuns().size();
		
		for(Gun gun : getAllGuns()){
			if(player.hasPermission(gun.getPermission())){
				otherGuns.append(gun.getName());
				otherGuns.append(", ");
			} else {
				otherGuns.append(gun.getName());
				otherGuns.append(", ");
			}
		}
		
		yourGuns.setLength((yourGuns.length() >= 2 ? yourGuns.length() - 2 : 0));
		otherGuns.setLength((otherGuns.length() >= 2 ? otherGuns.length() - 2 : 0));
		
		if(yourGuns.length() == 0){
			yourGunsString = ChatColor.AQUA + "None!";
		} else {
			yourGunsString = ChatColor.GOLD + yourGuns.toString();
		}
		
		if(otherGuns.length() == 0){
			otherGunsString = ChatColor.AQUA + "None!";
		} else {
			otherGunsString = ChatColor.GOLD + otherGuns.toString();
		}

		player.sendMessage(ChatColor.GREEN + "Your guns (" + yourGunsString.split(", ").length + "/" + totalGuns + "): " + yourGunsString);
		player.sendMessage(ChatColor.GREEN + "Other guns (" + otherGunsString.split(", ").length + "/" + totalGuns + "): " + otherGunsString);
	}

	public static Gun getPlayerGun(Player player){
		if(playerGuns.containsKey(player.getName())){
			Gun gun = playerGuns.get(player.getName());
			return gun;
		} else {
			return null;
		}
	}
	
	public static void setPlayerGun(Player player, Gun gun){
		playerGuns.put(player.getName(), gun);
		
		PlayerUtils.respawn(player);
	}
	
	public static boolean isGun(String gunName){
		if(Config.getGunsConfig().isConfigurationSection(gunName)){
			return true;
		} else {
			return false;
		}
	}
	
	public static IconMenu getGunMenu(){
		String title = ChatColor.RED + "" + ChatColor.BOLD + "Pick a Gun:";
		int size = Utilities.round(getAllGuns().size(), 9);
		
		IconMenu menu = new IconMenu(title, size, new OptionClickEventHandler(){
			@Override
			public void onOptionClick(OptionClickEvent event){
				Player player = event.getPlayer();
				String gunName = ChatColor.stripColor(event.getName());
				Gun gun = getGun(gunName);
				
				setPlayerGun(player, gun);
				player.sendMessage(ChatColor.BLUE + "You now have the gun '" + gunName + "'! Damage: " + gun.getDamage() + " Ammo: " + gun.getAmmo());
			}
		}, McCOD.getInstance());
		/*
		for(int i = 0; i < getAllGuns().size(); i++){
			Gun gun = getAllGuns().get(i);
			ItemStack gunItem = gun.toItemStack();
			ItemMeta meta = gunItem.getItemMeta();
			String[] lore = meta.getLore().toArray(new String[0]);
			
			menu.setOption(i, gunItem, meta.getDisplayName(), lore);
		}
		*/
		return menu;
	}
}