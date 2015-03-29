package com.balrog.InfernalTech.tileentities;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;

import com.balrog.InfernalTech.containers.ContainerMolecularSeparator;
import com.balrog.InfernalTech.enums.EnumFaceMode;
import com.balrog.InfernalTech.materials.ItemCoalPowder;
import com.balrog.InfernalTech.network.PacketHandler;
import com.balrog.InfernalTech.recipes.MolecularSeparatorRecipe;
import com.balrog.InfernalTech.recipes.MolecularSeparatorRecipes;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.EnumFaceDirection;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLLog;

public class TileEntityMolecularSeparator extends TileEntityLockable implements IPersistable, IToolHarvestable, IConfigurableSides, IInventory, IUpdatePlayerListBox, ISidedInventory, IEnergyReceiver {
	
	public EnumFaceMode[] faceMode = new EnumFaceMode[] {
		EnumFaceMode.OUTPUT,EnumFaceMode.INPUT,EnumFaceMode.NONE,EnumFaceMode.NONE,EnumFaceMode.NONE,EnumFaceMode.NONE
	};
	private ItemStack[] itemStacks = new ItemStack[3];
	
	private int meta;
	private IExtendedBlockState state;
	private int totalWorkTime;
	private int elapsedWorkTime;
	private int operationProgressTime;
	private int[] inputSlots = new int[] { 0 };
	private int[] outputSlots = new int[] { 1,2 };
	private int[] noSlots = new int[0];
	
	public EnergyStorage energyStorage = new EnergyStorage(400000, 10000, 20);

	public void readCommonNBT(NBTTagCompound compound)
	{
		for(EnumFacing face : EnumFacing.values())
		{
			if(compound.hasKey(face.getName() + "Mode"))
				this.faceMode[face.getIndex()] = EnumFaceMode.valueOf(compound.getString(face.getName() + "Mode"));
			else
				this.faceMode[face.getIndex()] = EnumFaceMode.NONE;
		}
		
		this.energyStorage.readFromNBT(compound);

		this.totalWorkTime = compound.getInteger("totalWorkTime");
		this.elapsedWorkTime = compound.getInteger("elapsedWorkTime");
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		
		FMLLog.info("Reading Molecular Assembler from NBT");
		
		NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.itemStacks = new ItemStack[this.getSizeInventory()];

        for (int i = 0; i < nbttaglist.tagCount(); ++i)
        {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.itemStacks.length)
            {
                this.itemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }
		
		this.readCommonNBT(compound);
	}
	
	public void writeCommonNBT(NBTTagCompound compound)
	{
		for(EnumFacing face : EnumFacing.values())
		{
			compound.setString(face.getName() + "Mode", this.faceMode[face.getIndex()].toString());
		}
		
		this.energyStorage.writeToNBT(compound);
		
		compound.setInteger("totalWorkTime", this.totalWorkTime);
		compound.setInteger("elapsedWorkTime", this.elapsedWorkTime);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		
		FMLLog.info("Writing Molecular Assembler to NBT");
		
		NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.itemStacks.length; ++i)
        {
            if (this.itemStacks[i] != null)
            {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.itemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        compound.setTag("Items", nbttaglist);
		
        this.writeCommonNBT(compound);
	}

	public IBlockState getState() {
		if(state == null)
        {
            state = (IExtendedBlockState)getBlockType().getDefaultState();
        }
        return state;
	}
	
	public void setState(IExtendedBlockState state)
    {
        this.state = state;
    }

	@Override
	public String getName() {
		return "tile.molecular_separator.name";
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return new ContainerMolecularSeparator(playerInventory, this);
	}

	@Override
	public String getGuiID() {
		// TODO Auto-generated method stub
		return "molecular_separator";
	}

	@Override
	public int getSizeInventory() {
		// TODO Auto-generated method stub
		return this.itemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int index) {
		// TODO Auto-generated method stub
		return this.itemStacks[index];
	}

	@Override
	public ItemStack decrStackSize(int index, int count) {
		if (this.itemStacks[index] != null)
        {
            ItemStack itemstack;

            if (this.itemStacks[index].stackSize <= count)
            {
                itemstack = this.itemStacks[index];
                this.itemStacks[index] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.itemStacks[index].splitStack(count);

                if (this.itemStacks[index].stackSize == 0)
                {
                    this.itemStacks[index] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int index) {
		if (this.itemStacks[index] != null)
        {
            ItemStack itemstack = this.itemStacks[index];
            this.itemStacks[index] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
	}

	@Override
	public void setInventorySlotContents(int index, ItemStack stack) {
		boolean flag = stack != null && stack.isItemEqual(this.itemStacks[index]) && ItemStack.areItemStackTagsEqual(stack, this.itemStacks[index]);
        this.itemStacks[index] = stack;

        if (stack != null && stack.stackSize > this.getInventoryStackLimit())
        {
            stack.stackSize = this.getInventoryStackLimit();
        }

        if (index == 0 && !flag)
        {
            this.totalWorkTime = this.getOperationTime(stack);
            this.elapsedWorkTime = 0;
            this.markDirty();
        }
	}

	private int getOperationTime(ItemStack stack) {
		// TODO Auto-generated method stub
		return 200;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.worldObj.getTileEntity(this.pos) != this ? false : player.getDistanceSq((double)this.pos.getX() + 0.5D, (double)this.pos.getY() + 0.5D, (double)this.pos.getZ() + 0.5D) <= 64.0D;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if(index != 0)
			return false;
		
		if(this.itemStacks[0] != null && stack.isItemEqual(this.itemStacks[0]))
			return true;
		
		MolecularSeparatorRecipe recipe = MolecularSeparatorRecipes.instance.getRecipe(stack);
		return recipe != null;
	}

	@Override
	public int getField(int id) {
		switch (id)
        {
            case 0:
                return this.energyStorage.getEnergyStored();
            case 1:
                return this.operationProgressTime;
            case 2:
                return this.elapsedWorkTime;
            case 3:
                return this.totalWorkTime;
            default:
                return 0;
        }
	}

	@Override
	public void setField(int id, int value) {
		switch (id)
        {
            case 0:
                this.energyStorage.setEnergyStored(value);
                break;
            case 1:
                this.operationProgressTime = value;
                break;
            case 2:
                this.elapsedWorkTime = value;
                break;
            case 3:
                this.totalWorkTime = value;
        }
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public void clear() {
		for (int i = 0; i < this.itemStacks.length; ++i)
        {
            this.itemStacks[i] = null;
        }
	}

	@Override
	public void update() {
		
		if (!this.worldObj.isRemote)
		{
			if(this.isWorking())
			{
				if(this.canWork())
				{
					int requiredRF = Math.min(this.energyStorage.getMaxExtract(), this.totalWorkTime - this.elapsedWorkTime);
					this.elapsedWorkTime += requiredRF;
					this.energyStorage.extractEnergy(requiredRF, false);
					
					if(this.elapsedWorkTime == this.totalWorkTime)
					{
						MolecularSeparatorRecipe recipe = MolecularSeparatorRecipes.instance.getRecipe(this.itemStacks[0]);
						this.totalWorkTime = recipe.energyRequired;
						this.elapsedWorkTime = 0;
						
						if(this.itemStacks[1] == null)
							this.itemStacks[1] = recipe.primaryOutput.copy();
						else
							this.itemStacks[1].stackSize += recipe.primaryOutput.stackSize;
						
						if(recipe.secondaryOutput != null)
						{
							if(this.itemStacks[2] == null)
								this.itemStacks[2] = recipe.secondaryOutput.copy();
							else
								this.itemStacks[2].stackSize += recipe.secondaryOutput.stackSize;
						}
						
						--this.itemStacks[0].stackSize;
						
						if(this.itemStacks[0].stackSize <= 0)
							this.itemStacks[0] = null;
					}
				}
			}
			
			PacketHandler.sendToAllAround(new PacketPowerStorage(this), this);
		}
	}

	private boolean canWork() {
		if (this.itemStacks[0] == null || this.energyStorage.getEnergyStored() < this.energyStorage.getMaxExtract())
			return false;
		
        MolecularSeparatorRecipe recipe = MolecularSeparatorRecipes.instance.getRecipe(this.itemStacks[0]);
        if (recipe == null) return false;
        if (this.itemStacks[1] == null && this.itemStacks[2] == null) return true;
        if (this.itemStacks[1] != null &&!this.itemStacks[1].isItemEqual(recipe.primaryOutput)) return false;
        if (this.itemStacks[2] != null && !this.itemStacks[2].isItemEqual(recipe.secondaryOutput)) return false;
        int primaryExpectedStackSize = (this.itemStacks[1] != null ? this.itemStacks[1].stackSize : 0) + recipe.primaryOutput.stackSize;
        int secondaryExpectedStackSize = (this.itemStacks[2] != null ? this.itemStacks[2].stackSize : 0) + (recipe.secondaryOutput != null ? recipe.secondaryOutput.stackSize : 0);
        
        if(primaryExpectedStackSize > getInventoryStackLimit() || secondaryExpectedStackSize > getInventoryStackLimit())
        	return false;
        
        if(this.itemStacks[1] != null && primaryExpectedStackSize > this.itemStacks[1].getMaxStackSize())
        	return false;
        
        if(this.itemStacks[2] != null && secondaryExpectedStackSize > this.itemStacks[2].getMaxStackSize())
        	return false;
        
        return true;
	}

	private boolean isWorking() {
		return this.elapsedWorkTime < this.totalWorkTime && this.energyStorage.getEnergyStored() > this.energyStorage.getMaxExtract();
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		EnumFaceMode mode = this.faceMode[side.ordinal()];
		
		switch(mode)
		{
		case INPUT:
			return this.inputSlots;
		case OUTPUT:
			return this.outputSlots;
		default:
			return this.noSlots;
		}
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		
		if(this.faceMode[direction.ordinal()] == EnumFaceMode.INPUT && index == 0)
			return this.isItemValidForSlot(index, itemStackIn);
		return false;
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		if(this.faceMode[direction.ordinal()] == EnumFaceMode.OUTPUT && index > 0)
			return true;
		return false;
	}

	public void cycleFaceMode(EnumFacing side) {
		switch(this.faceMode[side.ordinal()])
		{
		case INPUT:
			this.faceMode[side.ordinal()] = EnumFaceMode.OUTPUT;
			break;
		case NONE:
			this.faceMode[side.ordinal()] = EnumFaceMode.INPUT;
			break;
		case OUTPUT:
			this.faceMode[side.ordinal()] = EnumFaceMode.NONE;
			break;
		default:
			break;
		}
		
		this.worldObj.addBlockEvent(this.pos, this.getBlockType(), side.ordinal(), this.faceMode[side.ordinal()].getMode());
	}
	
	@Override
	public boolean receiveClientEvent(int id, int type) {
		this.faceMode[id] = EnumFaceMode.values()[type];
		return true;
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		return this.energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		return this.energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		return this.energyStorage.getMaxEnergyStored();
	}

	@Override
	public EnumFacing getFrontFace() {
		return EnumFacing.values()[this.getBlockMetadata()];
	}

	@Override
	public EnumFaceMode[] getFaceModes() {
		return this.faceMode;
	}
}
