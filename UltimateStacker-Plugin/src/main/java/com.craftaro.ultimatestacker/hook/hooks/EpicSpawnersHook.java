package com.craftaro.ultimatestacker.hook.hooks;

import com.craftaro.core.compatibility.ServerVersion;
import com.craftaro.epicspawners.api.EpicSpawnersApi;
import com.craftaro.epicspawners.api.spawners.spawner.SpawnerData;
import com.craftaro.epicspawners.api.spawners.spawner.SpawnerTier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.GameRule;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class EpicSpawnersHook {
  private boolean enabled;

  public EpicSpawnersHook(boolean enabled) {
      this.enabled = enabled;
  }

  public String getName() {
      return "EpicSpawners";
  }

  public boolean isEnabled() {
      return enabled;
  }

  public List<com.craftaro.core.lootables.loot.Drop> getDrops(LivingEntity entity) {
    // Fetch entities meta-data
	List<com.craftaro.core.lootables.loot.Drop> drops = new ArrayList<com.craftaro.core.lootables.loot.Drop>();
    List<MetadataValue> values = entity.getMetadata("ESData");
    List<MetadataValue> values2 = entity.getMetadata("ESTier");
    
    if (!values.isEmpty()) {
        // Fetch the spawner the entity came from
        SpawnerData spawnerData = EpicSpawnersApi.getSpawnerManager().getSpawnerData(values.get(0).asString());
        SpawnerTier spawnerTier = spawnerData.getTier(values2.get(0).asString());

        // Get the loot table from that spawner
        // Get the EpicSpawnersApi class and its method
        try {
	        Class<?> epicSpawnersApiClass = Class.forName("com.craftaro.epicspawners.api.EpicSpawnersApi");
	        Method getLootManagerMethod = epicSpawnersApiClass.getMethod("getLootManager");
	        Object lootManager = getLootManagerMethod.invoke(null);
	
	        // Get the getDrops method from the loot manager
	        Class<?> entityClass = Class.forName("org.bukkit.entity.LivingEntity"); // Adjust this if needed
            Class<?> spawnerTierClass = Class.forName("com.craftaro.epicspawners.api.spawners.spawner.SpawnerTier");

            // Get the getDrops method from the loot manager with correct parameter types
            Method getDropsMethod = lootManager.getClass().getMethod("getDrops", entityClass, spawnerTierClass);

	        List<?> drops1 = (List<?>) getDropsMethod.invoke(lootManager, entity, spawnerTier);
	
	        // Iterate over drops and print them
	        for (Object drop : drops1) {
	            System.out.println(drop);
	
	            // Perform any additional logic with the drop
	            // You can use reflection to call methods on the drop if needed
	            Method getDropNameMethod = drop.getClass().getMethod("getItemStack");
	            ItemStack item = (ItemStack) getDropNameMethod.invoke(drop);
	            drops.add(new com.craftaro.core.lootables.loot.Drop(item));
	            
	        }
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
        System.out.println(EpicSpawnersApi.getLootManager().getDrops(entity, spawnerTier));
        EpicSpawnersApi.getLootManager().getDrops(entity, spawnerTier).forEach(d -> System.out.println(d));
        EpicSpawnersApi.getLootManager().getDrops(entity, spawnerTier).forEach(d -> System.out.println(d.getItemStack()));
        if (ServerVersion.isServerVersionAtLeast(ServerVersion.V1_13) && !entity.getWorld().getGameRuleValue(GameRule.DO_MOB_LOOT)) {
        	drops.clear();
        }
        */
    }
    return drops;
  }
}