package com.balrog.InfernalTech.blocks;

import java.util.ArrayList;
import java.util.List;

import com.balrog.InfernalTech.CommonProxy;
import com.balrog.InfernalTech.InfernalTech;
import com.balrog.InfernalTech.energy.IEnergyChannel;
import com.balrog.InfernalTech.renderers.TileEntityConfigurableSidesRenderer;
import com.balrog.InfernalTech.tileentities.TileEntityEnergyAccumulator;
import com.balrog.InfernalTech.tileentities.TileEntityEnergyChannel;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;
import com.balrog.InfernalTech.utils.ICollidable;
import com.balrog.InfernalTech.utils.RaytraceResult;
import com.balrog.InfernalTech.utils.Util;
import com.google.common.collect.Lists;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
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
	
	public static void init(CommonProxy proxy, boolean isServerSide)
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
            return cte.getState(state);
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
            return cte.getState(state);
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
	public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityEnergyChannel) {
			TileEntityEnergyChannel energyChannel = (TileEntityEnergyChannel)tileEntity;
			energyChannel.invalidateNeighbors();
		}
	}
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		FMLLog.info("Block Broken (%s)", pos.toString());
		
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if(tileEntity instanceof TileEntityEnergyChannel) {
			TileEntityEnergyChannel energyChannel = (TileEntityEnergyChannel)tileEntity;
			energyChannel.onBreakBlock(worldIn);
		}		
		
		super.breakBlock(worldIn, pos, state);
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

	@Override
	public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List list, Entity collidingEntity) {

		
		
		this.setBlockBounds(5 / 16f, 5/16f, 5/16f, 11/16f, 11/16f, 11/16f);
		super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
		
		this.setBlockBounds(0, 0, 0, 1, 1, 1);
	}
	
	@Override
	public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction) {
		RaytraceResult raytraceResult = doRayTrace(world, pos, origin, direction, null);
		if(raytraceResult == null) {
			return null;
		}

		if(raytraceResult.movingObjectPosition != null) {
			raytraceResult.movingObjectPosition.hitInfo = raytraceResult.component;

		}
		return raytraceResult.movingObjectPosition;
	}
	
	public RaytraceResult doRayTrace(World world, BlockPos pos, EntityPlayer entityPlayer) {
	    List<RaytraceResult> allHits = doRayTraceAll(world, pos, entityPlayer);
	    if(allHits == null) {
	      return null;
	    }
	    Vec3 origin = Util.getEyePosition(entityPlayer);
	    return RaytraceResult.getClosestHit(origin, allHits);
	}
	
	public List<RaytraceResult> doRayTraceAll(World world, BlockPos pos, EntityPlayer entityPlayer) {
	    double pitch = Math.toRadians(entityPlayer.rotationPitch);
	    double yaw = Math.toRadians(entityPlayer.rotationYaw);

	    double dirX = -Math.sin(yaw) * Math.cos(pitch);
	    double dirY = -Math.sin(pitch);
	    double dirZ = Math.cos(yaw) * Math.cos(pitch);

	    double reachDistance = InfernalTech.proxy.getReachDistanceForPlayer(entityPlayer);

	    Vec3 origin = Util.getEyePosition(entityPlayer);
	    Vec3 direction = origin.addVector(dirX * reachDistance, dirY * reachDistance, dirZ * reachDistance);
	    return doRayTraceAll(world, pos, origin, direction, entityPlayer);
	}
	
	private RaytraceResult doRayTrace(World world, BlockPos pos, Vec3 origin, Vec3 direction, EntityPlayer entityPlayer) {
	    List<RaytraceResult> allHits = doRayTraceAll(world, pos, origin, direction, entityPlayer);
	    if(allHits == null) {
	    	return null;
	    }
	    return RaytraceResult.getClosestHit(origin, allHits);
	}
	
	protected List<RaytraceResult> doRayTraceAll(World world, BlockPos pos, Vec3 origin, Vec3 direction, EntityPlayer player) {

	    TileEntity te = world.getTileEntity(pos);
	    if(!(te instanceof IEnergyChannel)) {
	    	return null;
	    }
	    IEnergyChannel channel = (IEnergyChannel) te;
	    List<RaytraceResult> hits = Lists.newArrayList();

	    if(player == null) {
	    	player = InfernalTech.proxy.getClientPlayer();
	    }

    	List<ICollidable> components = new ArrayList<ICollidable>(channel.getCollidableComponents());
    	for (ICollidable component : components) {
    		AxisAlignedBB bounds = component.getBounds();
			setBlockBounds((float)bounds.minX, (float)bounds.minY, (float)bounds.minZ, (float)bounds.maxX, (float)bounds.maxY, (float)bounds.maxZ);
			MovingObjectPosition hitPos = super.collisionRayTrace(world, pos, origin, direction);
			if(hitPos != null) {
				hits.add(new RaytraceResult(component, hitPos));
			}
    	}

    	setBlockBounds(0, 0, 0, 1, 1, 1);

	    return hits;
	}
}
