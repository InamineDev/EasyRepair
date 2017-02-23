package me.inamine.EasyRep;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

public class EasyRepair extends JavaPlugin implements Listener {
	
	ArrayList<Player> cooldown = new ArrayList<Player>();
	ArrayList<Player> handcooldown = new ArrayList<Player>();
	ArrayList<Player> armorcooldown = new ArrayList<Player>();
	ArrayList<Player> allcooldown = new ArrayList<Player>();
	
	static boolean cost = false;
	
	
	private static EasyRepair inst;
	
	public EasyRepair()
	{
		inst = this;
	}
	
	public static EasyRepair getInst()
	{
		return inst;
	}
	
	public static Economy econ = null;
	
	private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null) 
        {
            econ = economyProvider.getProvider();
        }

        return (econ != null);
    }
	
	@Override
	public void onEnable() 
	{
		Utils.addTools();
		FM.checkFiles();
		this.saveDefaultConfig();
		PluginManager pm = Bukkit.getServer().getPluginManager();
        pm.registerEvents(this, this);
        if ( this.getConfig().getBoolean("costs.use"))
        {
        	setupEconomy();
        	cost = true;
        }
        getCommand("easyrepair").setAliases(this.getConfig().getStringList("aliases"));
	}
	
	@Override
	public void onDisable() {}
	
	
	public void repairHand(Player player) 
	{
		if (this.getConfig().getBoolean("use-global-cooldown")) 
		{
			if (player.hasPermission("easyrepair.hand") && !cooldown.contains(player)) 
			{
				ItemStack i = player.getInventory().getItemInHand();
				if (Utils.itemCheck(i, player))
				{
					boolean charged = Utils.chargePlayer(player, "hand");
					if (charged)
					{
						repair(i, player);
						addCooldown(player, "hand");
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("repaired.hand")));
						return;
					}
					else 
					{
						double cost = this.getConfig().getDouble("costs.hand");
						
						if (player.hasPermission("easyrepair.hand.cost.half"))
						{ 
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
									.replace("%cost%", String.valueOf(cost/2))));
							return;
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
								.replace("%cost%", String.valueOf(cost))));
						return;
					}
				}
				else
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("no-repair")));
					return;
				}
			}
			if (cooldown.contains(player)) {
				int gdown = this.getConfig().getInt("global-cooldown");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("cooldown.global").replace("%cooldown%", String.valueOf(gdown))));
				return;
			}
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
				return;
			}
		}
		if (!this.getConfig().getBoolean("use-global-cooldown")) 
		{
			if (player.hasPermission("easyrepair.hand") && !handcooldown.contains(player)) 
			{
				ItemStack i = player.getInventory().getItemInHand();
				if (Utils.itemCheck(i, player))
				{
					boolean charged = Utils.chargePlayer(player, "hand");
					if (charged)
					{
						repair(i, player);
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("repaired.hand")));
						addCooldown(player, "hand");
						return;
					}
					else 
					{
						double cost = this.getConfig().getDouble("costs.hand");
						
						if (player.hasPermission("easyrepair.hand.cost.half"))
						{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
									.replace("%cost%", String.valueOf(cost/2))));
							return;
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
								.replace("%cost%", String.valueOf(cost))));
						return;
					}
					
				}
				else
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("no-repair")));
					return;
				}
			}
			if (handcooldown.contains(player)) {
				int gdown = this.getConfig().getInt("hand-cooldown");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("cooldown.hand").replace("%cooldown%", String.valueOf(gdown))));
				return;
			}
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
				return;
			}
		}
		
	}
	
	public void repairAll(Player player) 
	{
		if (this.getConfig().getBoolean("use-global-cooldown")) 
		{
			if (player.hasPermission("easyrepair.all") && !cooldown.contains(player))
			{
				if (Utils.itemCheck(player.getInventory().getContents(), player))
				{
					boolean charged = Utils.chargePlayer(player, "all");
					if (charged)
					{
						ItemStack[] inv = player.getInventory().getContents();
						for (ItemStack i : inv) 
						{
							if (i != null) 
							{
								if (Utils.tools.contains(i.getType()))
								{
									repair(i, player);
								}
							}
						}
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("repaired.all")));
						addCooldown(player, "all");
						return;
					}
					else 
					{
						double cost = this.getConfig().getDouble("costs.all");
						
						if (player.hasPermission("easyrepair.all.cost.half"))
						{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
									.replace("%cost%", String.valueOf(cost/2))));
							return;
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
								.replace("%cost%", String.valueOf(cost))));
						return;
					}
				}
				
				else
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("no-repair")));
					return;
				}
				
			}

			if (cooldown.contains(player)) {
				int gdown = this.getConfig().getInt("global-cooldown");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("cooldown.global").replace("%cooldown%", String.valueOf(gdown))));
				return;
			}
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
				return;
			}
		}
		if (!this.getConfig().getBoolean("use-global-cooldown")) 
		{
			if (player.hasPermission("easyrepair.all") && !allcooldown.contains(player))
			{
				if (Utils.itemCheck(player.getInventory().getContents(), player))
				{
					boolean charged = Utils.chargePlayer(player, "all");
					if (charged)
					{
						ItemStack[] inv = player.getInventory().getContents();
						for (ItemStack i : inv) 
						{
							if (i != null) 
							{
								if (Utils.tools.contains(i.getType()))
								{
									repair(i, player);
								}
							}
						}
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("repaired.all")));
						addCooldown(player, "all");
						return;
					}
					else 
					{
						double cost = this.getConfig().getDouble("costs.all");
						
						if (player.hasPermission("easyrepair.all.cost.half"))
						{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
									.replace("%cost%", String.valueOf(cost/2))));
							return;
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
								.replace("%cost%", String.valueOf(cost))));
						return;
					}
				}
				else
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("no-repair")));
					return;
				}
				
			}

			if (allcooldown.contains(player)) {
				int gdown = this.getConfig().getInt("all-cooldown");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("cooldown.all").replace("%cooldown%", String.valueOf(gdown))));
				return;
			}
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
				return;
			}
		}
	}
	
	public void repairArmor(Player player) 
	{
		if (this.getConfig().getBoolean("use-global-cooldown")) {
			if ((player.hasPermission("easyrepair.armor")) && (!cooldown.contains(player))) {
				
				if (Utils.itemCheck(player.getInventory().getArmorContents(), player))
				{
					boolean charged = Utils.chargePlayer(player, "armor");
					if (charged)
					{
						ItemStack helmet = player.getInventory().getHelmet();
						ItemStack chest = player.getInventory().getChestplate();
						ItemStack leggings = player.getInventory().getLeggings();
						ItemStack boots = player.getInventory().getBoots();
						if (helmet != null) {
							if (Utils.tools.contains(helmet.getType()))
							{
								helmet.setDurability((short) 0);
							}
						}
						if (chest != null) {
							if (Utils.tools.contains(chest.getType()))
							{
								chest.setDurability((short) 0);
							}
						}
						if (leggings != null) {
							if (Utils.tools.contains(leggings.getType()))
							{
								leggings.setDurability((short) 0);
							}
						}
						if (boots != null) {
							if (Utils.tools.contains(boots.getType()))
							{
								boots.setDurability((short) 0);
							}
						}
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("repaired.armor")));
						addCooldown(player, "armor");	
						return;
					}

					else 
					{
						double cost = this.getConfig().getDouble("costs.armor");
						
						if (player.hasPermission("easyrepair.armor.cost.half"))
						{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
									.replace("%cost%", String.valueOf(cost/2))));
							return;
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
								.replace("%cost%", String.valueOf(cost))));
						return;
					}
				}
				
				else
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("no-repair")));
					return;
				}
				
			}
			if (cooldown.contains(player)) {
				int gdown = this.getConfig().getInt("global-cooldown");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("cooldown.global").replace("%cooldown%", String.valueOf(gdown))));
			}
			else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
			}
		}
		
		if (!this.getConfig().getBoolean("use-global-cooldown")) {
			if ((player.hasPermission("easyrepair.armor")) && (!armorcooldown.contains(player))) {
				if (Utils.itemCheck(player.getInventory().getArmorContents(), player))
				{
					boolean charged = Utils.chargePlayer(player, "armor");
					if (charged)
					{
						ItemStack helmet = player.getInventory().getHelmet();
						ItemStack chest = player.getInventory().getChestplate();
						ItemStack leggings = player.getInventory().getLeggings();
						ItemStack boots = player.getInventory().getBoots();
						if (helmet != null) {
							if (Utils.tools.contains(helmet.getType()))
							{
								helmet.setDurability((short) 0);
							}
						}
						if (chest != null) {
							if (Utils.tools.contains(chest.getType()))
							{
								chest.setDurability((short) 0);
							}
						}
						if (leggings != null) {
							if (Utils.tools.contains(leggings.getType()))
							{
								leggings.setDurability((short) 0);
							}
						}
						if (boots != null) {
							if (Utils.tools.contains(boots.getType()))
							{
								boots.setDurability((short) 0);
							}
						}
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("repaired.armor")));
						addCooldown(player, "armor");	
						return;
					}

					else 
					{
						double cost = this.getConfig().getDouble("costs.armor");
						
						if (player.hasPermission("easyrepair.armor.cost.half"))
						{
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
									.replace("%cost%", String.valueOf(cost/2))));
							return;
						}
						
						player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("economy.not-enough")
								.replace("%cost%", String.valueOf(cost))));
						return;
					}
				}
				
				else
				{
					player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("no-repair")));
					return;
				}
			}
			if (armorcooldown.contains(player)) {
				int gdown = this.getConfig().getInt("armor-cooldown");
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("cooldown.armor").replace("%cooldown%", String.valueOf(gdown))));
			}
			else {
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
			}
		}
		return;
	}

	public void repair(ItemStack item, Player p)
	{
		if (!Utils.badLore(item, p))
		{
			item.setDurability((short) 0);
			return;
		}
	}
	
	public void addCooldown(final Player player, String type) {
		String perm = "easyrepair." + type + ".bypass.cooldown";
		if (!player.hasPermission(perm)) {
			if (this.getConfig().getBoolean("use-global-cooldown")) {
				cooldown.add(player);
				int gdown = this.getConfig().getInt("global-cooldown");
				int gcooldown = gdown * 20;
				Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
					public void run() {
						cooldown.remove(player);
					}
				}, gcooldown);
				return;
			}
			if (!this.getConfig().getBoolean("use-global-cooldown")) {
				if (type == "hand") {
					handcooldown.add(player);
					int hdown = this.getConfig().getInt("hand-cooldown");
					int hcooldown = hdown * 20;
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							handcooldown.remove(player);
						}
					}, hcooldown);
					return;
				}
				if (type == "all") {
					allcooldown.add(player);
					int aldown = this.getConfig().getInt("all-cooldown");
					int alcooldown = aldown * 20;
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
						public void run() {
							allcooldown.remove(player);
						}
					}, alcooldown);
					return;
				}
				if (type == "armor") 
				{
					armorcooldown.add(player);
					int ardown = this.getConfig().getInt("armor-cooldown");
					int arcooldown = ardown * 20;
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() 
					{
						public void run() 
						{
							armorcooldown.remove(player);
						}
					}, arcooldown);
					return;
				}
			}
		}
		else 
		{
			return;
		}
	}
	
	public void remCooldown(Player player) 
	{
		cooldown.remove(player);
		return;
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getName().equalsIgnoreCase("easyrepair") && !(sender instanceof Player)) 
		{
			if (args.length != 1)
			{
				sender.sendMessage("You can only use /repair reload as console!");
				return true;
			}
			if (args[0].equals("reload")) {
				this.reloadConfig();
				FM.checkFiles();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("reload-message")));
				return true;
			}
			
			else
			{
				sender.sendMessage("You can only use /repair reload as console!");
				return true;
			}
			
		}
		
		if (cmd.getName().equalsIgnoreCase("easyrepair") && (sender instanceof Player)) 
		{
			Player player = (Player) sender;

			if (args.length == 0) 
			{
				repairHand(player);
				return true;
			}
			
			if ((args.length == 1) && args[0].equals("reload")) {
				if (player.hasPermission("easyrepair.reload"))
				{
					this.reloadConfig();
					FM.checkFiles();
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("reload-message")));
					cost = this.getConfig().getBoolean("costs.use");
					return true;
				}
				else
				{
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("noPermissions")));
					return true;
				}
			}
			

			if ((args.length == 1) && (args[0].equals("all"))) 
			{
				repairAll(player);
				return true;
			}

			if ((args.length == 1) && (args[0].equals("armor"))) 
			{
				repairArmor(player);
				return true;
			}

			if ((args.length == 1) && (args[0].equals("hand"))) 
			{
				repairHand(player);
				return true;
			}
			
			if ((args.length > 1))
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("bad-usage")));
				return true;
			}
			
			else 
			{
				player.sendMessage(ChatColor.translateAlternateColorCodes('&', FM.getMsg().getString("bad-usage")));
				return true;
			}
			
		}
		return false;
	}
}