package com.balrog.InfernalTech.blocks;

import com.balrog.InfernalTech.CommonProxy;
import com.balrog.InfernalTech.renderers.TileEntityConfigurableSidesRenderer;
import com.balrog.InfernalTech.tileentities.TileEntityEnergyAccumulator;
import com.balrog.InfernalTech.tileentities.TileEntityEnergyChannel;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockEnergyChannel extends InfernalTechBlock {

	public static final PropertyBool NORTH = PropertyBool.create("north");
	public static final PropertyBool SOUTH = PropertyBool.create("south");
	public static final PropertyBool EAST = PropertyBool.create("east");
	public static final PropertyBool WEST = PropertyBool.create("west");
	public static final PropertyBool DOWN = PropertyBool.create("down");
	public static final PropertyBool UP = PropertyBool.create("up");
	
	public static final String UnlocalizedName = "energy_channel"; 
	public static final String ID = "energy_channel";
	
	public static final BlockEnergyChannel instance = new BlockEnergyChannel();
	
	protected BlockEnergyChannel() {
		super(Material.iron);

		this.setDefaultState(this.blockState.getBaseState()
				.withProperty(NORTH, false)
				.withProperty(SOUTH, false)
				.withProperty(EAST, false)
				.withProperty(WEST, false)
				.withProperty(DOWN, false)
				.withProperty(UP, false)
		);
		
		this.setLightOpacity(0);
		this.setUnlocalizedName(BlockEnergyChannel.UnlocalizedName);
		this.setCreativeTab(CreativeTabs.tabBlock);
		this.setHardness(10.0f);
	}
	
	public static void init(CommonProxy proxy)
	{
		GameRegistry.registerBlock(BlockEnergyChannel.instance, BlockEnergyChannel.ID);
		proxy.registerTileEntity(TileEntityEnergyChannel.class, BlockEnergyChannel.ID + "TileEntity", null);
		proxy.registerInventoryModel(Item.getItemFromBlock(BlockEnergyChannel.instance), ID, 0);
	}
	
	public static void setState(boolean north, boolean south, boolean east, boolean west, boolean down, boolean up, World worldIn, BlockPos pos)
	{
        TileEntity tileentity = worldIn.getTileEntity(pos);
        
        IBlockState newState = BlockEnergyChannel.instance.getDefaultState()
        		.withProperty(NORTH, north)
				.withProperty(SOUTH, south)
				.withProperty(EAST, east)
				.withProperty(WEST, west)
				.withProperty(DOWN, down)
				.withProperty(UP, up);
        
        worldIn.setBlockState(pos, newState, 3);
        worldIn.setBlockState(pos, newState, 3);
        
        if(tileentity != null) {
        	tileentity.validate();
        	worldIn.setTileEntity(pos, tileentity);
        }
        
        worldIn.markBlockForUpdate(pos);
	}
	
	@Override
    public int getRenderType() 
	{ 
		return 3; 
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean isFullCube() {
		return false;
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
		return 0;
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState();
	}
	
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		TileEntity te = worldIn.getTileEntity(pos);
        if(te instanceof TileEntityEnergyChannel)
        {
        	TileEntityEnergyChannel cte = (TileEntityEnergyChannel) te;
            return cte.getState();
        }
        return state;
	}
	
	@Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if(te instanceof TileEntityEnergyChannel)
        {
        	TileEntityEnergyChannel cte = (TileEntityEnergyChannel) te;
            return cte.getState();
        }
        return state;
    }
	
	@Override
	public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if(tileEntity instanceof TileEntityEnergyChannel) {
			TileEntityEnergyChannel energyChannel = (TileEntityEnergyChannel)tileEntity;
			energyChannel.invalidateNeighbors();
		}
	}
	
	@Override
	protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {NORTH, SOUTH, EAST, WEST, DOWN, UP});
    }

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		// TODO Auto-generated method stub
		return new TileEntityEnergyChannel();
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

}
