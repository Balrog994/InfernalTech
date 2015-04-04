package com.balrog.InfernalTech.blocks;

import java.util.List;
import java.util.Random;

import com.balrog.InfernalTech.CommonProxy;
import com.balrog.InfernalTech.InfernalTech;
import com.balrog.InfernalTech.renderers.TileEntityConfigurableSidesRenderer;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class BlockCustomOre extends BlockOre {
	public static final String UnlocalizedName = "block_custom_ore";
	public static final String ID = "block_custom_ore";
	
	public static BlockCustomOre copper_ore;
	public static BlockCustomOre lead_ore;
	public static BlockCustomOre nickel_ore;
	public static BlockCustomOre silver_ore;
	public static BlockCustomOre tin_ore;

	protected BlockCustomOre(String variantName, int harvestLevel) {
		super();
		this.setDefaultState(this.getDefaultState());
		this.setHardness(3.0F);
		this.setResistance(5.0F);
		this.setStepSound(soundTypeStone);
		this.setHarvestLevel("pickaxe", harvestLevel);
		this.setUnlocalizedName(BlockCustomOre.UnlocalizedName + "." + variantName);
	}
	
	public static void init(CommonProxy proxy, boolean isServerSide)
	{
		GameRegistry.registerBlock(copper_ore = new BlockCustomOre("copper", 1), BlockCustomOre.ID + ".copper");
		GameRegistry.registerBlock(lead_ore = new BlockCustomOre("lead", 2), BlockCustomOre.ID + ".lead");
		GameRegistry.registerBlock(nickel_ore = new BlockCustomOre("nickel", 2), BlockCustomOre.ID + ".nickel");
		GameRegistry.registerBlock(silver_ore = new BlockCustomOre("silver", 2), BlockCustomOre.ID + ".silver");
		GameRegistry.registerBlock(tin_ore = new BlockCustomOre("tin", 1), BlockCustomOre.ID + ".tin");
		
		proxy.registerInventoryModel(Item.getItemFromBlock(copper_ore), ID + ".copper", 0);
		proxy.registerInventoryModel(Item.getItemFromBlock(lead_ore), ID + ".lead", 0);
		proxy.registerInventoryModel(Item.getItemFromBlock(nickel_ore), ID + ".nickel", 0);
		proxy.registerInventoryModel(Item.getItemFromBlock(silver_ore), ID + ".silver", 0);
		proxy.registerInventoryModel(Item.getItemFromBlock(tin_ore), ID + ".tin", 0);
		
		OreDictionary.registerOre("oreCopper", new ItemStack(copper_ore, 1, 0));
		OreDictionary.registerOre("oreLead", new ItemStack(lead_ore, 1, 0));
		OreDictionary.registerOre("oreNickel", new ItemStack(nickel_ore, 1, 0));
		OreDictionary.registerOre("oreSilver", new ItemStack(silver_ore, 1, 0));
		OreDictionary.registerOre("oreTin", new ItemStack(tin_ore, 1, 0));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
        return 0;
    }
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return this.quantityDropped(random);
    }
	
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
	
	@Override
	public int getExpDrop(IBlockAccess world, BlockPos pos, int fortune) {
		return 0;
	}
}
