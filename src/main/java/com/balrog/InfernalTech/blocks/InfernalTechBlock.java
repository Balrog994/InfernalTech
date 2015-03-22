package com.balrog.InfernalTech.blocks;

import java.util.List;
import java.util.Random;

import com.balrog.InfernalTech.InfernalTech;
import com.balrog.InfernalTech.tileentities.IConfigurableSides;
import com.balrog.InfernalTech.tileentities.IPersistable;
import com.balrog.InfernalTech.tileentities.IToolHarvestable;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;
import com.google.common.collect.Lists;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
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

public abstract class InfernalTechBlock extends BlockContainer {

	private boolean keepInventory = false;
	
	protected InfernalTechBlock(Material materialIn) {
		super(materialIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }
        else
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if(playerIn.isSneaking()) {
        		
        		if (tileentity instanceof IToolHarvestable)
        		{
        			IToolHarvestable toolHarvestable = (IToolHarvestable)tileentity;
        			ItemStack equippedStack = playerIn.getCurrentEquippedItem();
            		if(equippedStack != null) {
            			this.harvestBlock(worldIn, playerIn, pos, state, tileentity);
            			worldIn.setBlockToAir(pos);
            			return true;
            		}
        		}
        		
        		if(tileentity instanceof IConfigurableSides)
        		{
        			IConfigurableSides configurableSides = (IConfigurableSides)tileentity;
        			configurableSides.cycleFaceMode(side);
        			return true;
        		}
        		
        	} else {
        		if(this.hasGui()) {
        			playerIn.openGui(InfernalTech.instance, this.getGuiId(), worldIn, pos.getX(), pos.getY(), pos.getZ());
        		}
        	}

            return true;
        }
    }
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
            
        if (tileentity instanceof IPersistable)
        {
        	IPersistable persistable = (IPersistable)tileentity;
        	
        	if(stack.hasTagCompound()) {
        		persistable.readCommonNBT(stack.getTagCompound());
            }
        	
        	if(!worldIn.isRemote) {
        		worldIn.markBlockForUpdate(pos);
        	}
        }
    }
	
	@Override
	public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
		if (!keepInventory )
        {
            TileEntity tileentity = worldIn.getTileEntity(pos);

            if (tileentity instanceof IInventory)
            {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
                worldIn.updateComparatorOutputLevel(pos, this);
            }
        }

        super.breakBlock(worldIn, pos, state);
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
		return Item.getItemFromBlock(state.getBlock());
    }
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		int meta = damageDropped(state);
		ItemStack stack = new ItemStack(this, 1, meta);
		
		TileEntity te = world.getTileEntity(pos);
		
		NBTTagCompound compound = new NBTTagCompound();
		
		if(te != null && te instanceof IPersistable) {
	      ((IPersistable) te).writeCommonNBT(compound);
	    }
		
		stack.setTagCompound(compound);
		
		return Lists.newArrayList(stack);
	}
	
	@Override
	protected boolean canSilkHarvest() {
		return false;
	}
	
	@Override
	public boolean canSilkHarvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		return false;
	}

	protected abstract int getGuiId();
	protected abstract boolean hasGui();
}
