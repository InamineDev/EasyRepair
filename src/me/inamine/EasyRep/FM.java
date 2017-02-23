package me.inamine.EasyRep;

import java.io.File;

import org.bukkit.configuration.file.YamlConfiguration;

public class FM {

	private static YamlConfiguration msg;
	
	
	public static void checkFiles() {
		if(!EasyRepair.getInst().getDataFolder().exists()) {
			EasyRepair.getInst().getDataFolder().mkdir();
		}
		
		File m = new File(EasyRepair.getInst().getDataFolder(), "messages.yml");
		if(!m.exists()){
			EasyRepair.getInst().saveResource("messages.yml", true);
		}
		
		
		msg = YamlConfiguration.loadConfiguration(m);
	}
	
	public static YamlConfiguration getMsg() {
		return msg;
	}
	
}
