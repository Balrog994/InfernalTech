package com.balrog.InfernalTech.materials;

import java.util.List;

import com.balrog.InfernalTech.CommonProxy;
import com.balrog.InfernalTech.blocks.BlockMolecularSeparator;
import com.balrog.InfernalTech.renderers.TileEntityConfigurableSidesRenderer;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCoalPowder extends Item {

	public static final String UnlocalizedName = "coal_powder"; 
	public static final String ID = "coal_powder";
	
	public static final ItemCoalPowder instance = new ItemCoalPowder();
	
	private ItemCoalPowder() {
		setUnlocalizedName(ItemCoalPowder.UnlocalizedName);
		setCreativeTab(CreativeTabs.tabMaterials);
	}
	
	public static void init(CommonProxy proxy, boolean isServerSide)
	{
		GameRegistry.registerItem(ItemCoalPowder.instance, ItemCoalPowder.UnlocalizedName);
		proxy.registerInventoryModel(ItemCoalPowder.instance, ID, 0);
	}
	
	public static void postInit(CommonProxy proxy) {
		
	}
	
	@Override
	public boolean doesSneakBypassUse(World world, BlockPos pos, EntityPlayer player) {
		return true;
	}
}
