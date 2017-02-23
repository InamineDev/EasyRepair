package me.inamine.EasyRep;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Utils 
{

	public static List<Material> tools = new ArrayList<Material>();
	public static List<String> noRepair = EasyRepair.getInst().getConfig().getStringList("blacklist-lore");
	

	public static boolean chargePlayer(Player player, String type)
	{
		if (EasyRepair.cost)
		{
			if (player.hasPermission("easyrepair." + type + ".cost.free"))
			{
				return true;
			}

			double cost = EasyRepair.getInst().getConfig().getDouble("costs." + type);
			boolean halfMoney = EasyRepair.econ.getBalance(player) >= (cost / 2);
			
			if (player.hasPermission("easyrepair." + type + ".cost.half"))
			{
				if (halfMoney)
				{
					EasyRepair.econ.withdrawPlayer(player, (cost/2));
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.charged." + type)
							.replace("%cost%", String.valueOf(cost/2))));
					return true;
				}
				else
				{
					return false;
				}
			}
			
			boolean enoughMoney = EasyRepair.econ.getBalance(player) >= cost;
			
			if (enoughMoney)
			{
				EasyRepair.econ.withdrawPlayer(player, cost);
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.charged." + type)
						.replace("%cost%", String.valueOf(cost))));
				return true;
			}
			else{
				return false;
			}
		}
		else{
			return true;
		}
	}
	
	
	public static void addTools()
	{
		tools.add(Material.DIAMOND_AXE);
		tools.add(Material.DIAMOND_PICKAXE);
		tools.add(Material.DIAMOND_HOE);
		tools.add(Material.DIAMOND_SPADE);
		tools.add(Material.DIAMOND_SWORD);
		tools.add(Material.DIAMOND_HELMET);
		tools.add(Material.DIAMOND_CHESTPLATE);
		tools.add(Material.DIAMOND_LEGGINGS);
		tools.add(Material.DIAMOND_BOOTS);
		tools.add(Material.IRON_AXE);
		tools.add(Material.IRON_PICKAXE);
		tools.add(Material.IRON_HOE);
		tools.add(Material.IRON_SPADE);
		tools.add(Material.IRON_SWORD);
		tools.add(Material.IRON_HELMET);
		tools.add(Material.IRON_CHESTPLATE);
		tools.add(Material.IRON_LEGGINGS);
		tools.add(Material.IRON_BOOTS);
		tools.add(Material.GOLD_AXE);
		tools.add(Material.GOLD_PICKAXE);
		tools.add(Material.GOLD_HOE);
		tools.add(Material.GOLD_SPADE);
		tools.add(Material.GOLD_SWORD);
		tools.add(Material.GOLD_HELMET);
		tools.add(Material.GOLD_CHESTPLATE);
		tools.add(Material.GOLD_LEGGINGS);
		tools.add(Material.GOLD_BOOTS);
		tools.add(Material.WOOD_AXE);
		tools.add(Material.WOOD_PICKAXE);
		tools.add(Material.WOOD_HOE);
		tools.add(Material.WOOD_SPADE);
		tools.add(Material.WOOD_SWORD);
		tools.add(Material.LEATHER_HELMET);
		tools.add(Material.LEATHER_CHESTPLATE);
		tools.add(Material.LEATHER_LEGGINGS);
		tools.add(Material.LEATHER_BOOTS);
		tools.add(Material.STONE_AXE);
		tools.add(Material.STONE_PICKAXE);
		tools.add(Material.STONE_HOE);
		tools.add(Material.STONE_SPADE);
		tools.add(Material.STONE_SWORD);
		tools.add(Material.CHAINMAIL_HELMET);
		tools.add(Material.CHAINMAIL_CHESTPLATE);
		tools.add(Material.CHAINMAIL_LEGGINGS);
		tools.add(Material.CHAINMAIL_BOOTS);
		tools.add(Material.FISHING_ROD);
		tools.add(Material.FLINT_AND_STEEL);
		tools.add(Material.BOW);
		tools.add(Material.SHEARS);
		
	}
	
	static boolean badLore(ItemStack i, Player p)
	{
		boolean blacklisted = false;
		if (p.hasPermission("easyrepair.bypass.lore"))
		{
			return false;
		}
		if ( i.hasItemMeta())
		{
			if ( i.getItemMeta().hasLore())
			{
				if ( i.getItemMeta().hasLore())
				{
					for (String line : i.getItemMeta().getLore()) 
					{
						if (Utils.noRepair.contains(ChatColor.stripColor(line)))
						{
							blacklisted = true;
						}
					}
				}
			}
		}
		
		
		return blacklisted;
	}
	
	public static boolean itemCheck(ItemStack item, Player p)
	{
		boolean needed = false;
		if (!(item == null))
		{
			if (tools.contains(item.getType()))
			{
				if (item.getDurability() != 0)
				{
					if (!badLore(item, p))
					{
						needed = true;
					}
				}
			}
			
		}
		return needed;
	}
	
	public static boolean itemCheck(ItemStack[] items, Player p)
	{
		boolean needed = false;
		for (ItemStack item : items)
		{
			if (!(item == null))
			{
				if (tools.contains(item.getType()))
				{
					if (item.getDurability() != 0)
					{
						if (!badLore(item, p))
						{
							needed = true;
						}
					}
				}
			}
		}
		return needed;
	}
	
}
