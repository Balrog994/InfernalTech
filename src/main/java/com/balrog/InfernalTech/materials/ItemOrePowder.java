package com.balrog.InfernalTech.materials;

import com.balrog.InfernalTech.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ItemOrePowder extends Item {
	public static final String UnlocalizedName = "ore_powder"; 
	public static final String ID = "ore_powder";
	
	public static ItemOrePowder iron_powder;
	public static ItemOrePowder gold_powder;
	public static ItemOrePowder copper_powder;
	public static ItemOrePowder lead_powder;
	public static ItemOrePowder nickel_powder;
	public static ItemOrePowder silver_powder;
	public static ItemOrePowder tin_powder;
	
	private ItemOrePowder(String variantName) {
		setUnlocalizedName(ItemOrePowder.UnlocalizedName + "." + variantName);
		setCreativeTab(CreativeTabs.tabMaterials);
	}
	
	public static void init(CommonProxy proxy, boolean isServerSide)
	{
		GameRegistry.registerItem(iron_powder = new ItemOrePowder("iron"), ItemOrePowder.UnlocalizedName + ".iron");
		GameRegistry.registerItem(gold_powder = new ItemOrePowder("gold"), ItemOrePowder.UnlocalizedName + ".gold");
		GameRegistry.registerItem(copper_powder = new ItemOrePowder("copper"), ItemOrePowder.UnlocalizedName + ".copper");
		GameRegistry.registerItem(lead_powder = new ItemOrePowder("lead"), ItemOrePowder.UnlocalizedName + ".lead");
		GameRegistry.registerItem(nickel_powder = new ItemOrePowder("nickel"), ItemOrePowder.UnlocalizedName + ".nickel");
		GameRegistry.registerItem(silver_powder = new ItemOrePowder("silver"), ItemOrePowder.UnlocalizedName + ".silver");
		GameRegistry.registerItem(tin_powder = new ItemOrePowder("tin"), ItemOrePowder.UnlocalizedName + ".tin");
		
		proxy.registerInventoryModel(iron_powder, ID + ".iron", 0);
		proxy.registerInventoryModel(gold_powder, ID + ".gold", 0);
		proxy.registerInventoryModel(copper_powder, ID + ".copper", 0);
		proxy.registerInventoryModel(lead_powder, ID + ".lead", 0);
		proxy.registerInventoryModel(nickel_powder, ID + ".nickel", 0);
		proxy.registerInventoryModel(silver_powder, ID + ".silver", 0);
		proxy.registerInventoryModel(tin_powder, ID + ".tin", 0);
	}
}
