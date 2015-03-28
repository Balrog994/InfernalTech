package com.balrog.InfernalTech.blocks;

import java.util.List;
import java.util.Random;

import com.balrog.InfernalTech.CommonProxy;
import com.balrog.InfernalTech.InfernalTech;
import com.balrog.InfernalTech.gui.GuiMolecularSeparator;
import com.balrog.InfernalTech.renderers.TileEntityConfigurableSidesRenderer;
import com.balrog.InfernalTech.tileentities.IConfigurableSides;
import com.balrog.InfernalTech.tileentities.IPersistable;
import com.balrog.InfernalTech.tileentities.IToolHarvestable;
import com.balrog.InfernalTech.tileentities.TileEntityEnergyAccumulator;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnergyAccumulator extends InfernalTechBlock {
	
	public static final PropertyInteger TIER = PropertyInteger.create("tier", 0, 1);
	
	public static final String UnlocalizedName = "energy_accumulator"; 
	public static final String ID = "energy_accumulator"; 
	private boolean keepInventory = false;
	
	public static final BlockEnergyAccumulator instance = new BlockEnergyAccumulator();

	protected BlockEnergyAccumulator() {
		super(Material.iron);

		this.setDefaultState(this.getDefaultState().withProperty(TIER, 0));
		
		this.setUnlocalizedName(BlockEnergyAccumulator.UnlocalizedName);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(10.0f);
		this.setLightLevel(1.0f);
	}
	
	public static void init(CommonProxy proxy)
	{
		GameRegistry.registerBlock(BlockEnergyAccumulator.instance, ItemBlockEnergyAccumulator.class, BlockEnergyAccumulator.ID);
		proxy.registerTileEntity(TileEntityEnergyAccumulator.class, BlockEnergyAccumulator.ID + "TileEntity", new TileEntityConfigurableSidesRenderer());
		
		proxy.addModelBakeryVariant(Item.getItemFromBlock(BlockEnergyAccumulator.instance), InfernalTech.MODID.toLowerCase() + ":" + ID + ".tier0");
		proxy.addModelBakeryVariant(Item.getItemFromBlock(BlockEnergyAccumulator.instance), InfernalTech.MODID.toLowerCase() + ":" + ID + ".tier1");
		
		proxy.registerInventoryModel(Item.getItemFromBlock(BlockEnergyAccumulator.instance), ID + ".tier0", 0);
		proxy.registerInventoryModel(Item.getItemFromBlock(BlockEnergyAccumulator.instance), ID + ".tier1", 1);
		
	}
	
		@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return new TileEntityEnergyAccumulator();
	}

	@Override
	protected int getGuiId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	protected boolean hasGui() {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
    public int getRenderType() 
	{ 
		return 3; 
	}
	
	@Override
	public boolean isOpaqueCube() {
		return true;
	}
	
	@Override
	public boolean isFullCube() {
		return true;
	}
	
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if(tileEntity instanceof TileEntityEnergyAccumulator) {
			TileEntityEnergyAccumulator energyAccumulator = (TileEntityEnergyAccumulator)tileEntity;
			energyAccumulator.invalidateNeighbors();
		}
	}
	
	@Override
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityEnergyAccumulator) {
			TileEntityEnergyAccumulator energyAccumulator = (TileEntityEnergyAccumulator)tileEntity;
			energyAccumulator.invalidateNeighbors();
		}
	}
	
	@Override
	protected BlockState createBlockState() {
		// TODO Auto-generated method stub
		return new BlockState(this, new IProperty[] { TIER });
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return (Integer) state.getValue(TIER);
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(TIER, meta);
	}
	
	@Override
	public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
		return this.getDefaultState().withProperty(TIER, meta);
	}
	
	@Override
  	@SideOnly(Side.CLIENT)
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list)
	{
		for(int tier = 0; tier <= 1; tier++) {
			list.add(new ItemStack(itemIn, 1, tier));
		}
	}
	
	@Override
	public int damageDropped(IBlockState state)
	{
		return this.getMetaFromState(state);
	}
}
