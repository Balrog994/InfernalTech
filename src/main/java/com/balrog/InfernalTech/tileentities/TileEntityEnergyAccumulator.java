package com.balrog.InfernalTech.tileentities;

import java.util.List;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import com.balrog.InfernalTech.enums.EnumFaceMode;
import com.google.common.collect.Lists;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLLog;

public class TileEntityEnergyAccumulator extends TileEntity implements IEnergyReceiver, IEnergyProvider, IConfigurableSides, IPersistable, IUpdatePlayerListBox, IToolHarvestable {

	public EnumFaceMode[] faceMode = new EnumFaceMode[] {
		EnumFaceMode.OUTPUT,EnumFaceMode.INPUT,EnumFaceMode.NONE,EnumFaceMode.NONE,EnumFaceMode.NONE,EnumFaceMode.NONE
	};
	
	public EnergyStorage energyStorage = new EnergyStorage(400000, 80, 80);
	private IEnergyReceiver[] receivers = new IEnergyReceiver[6];
	private IEnergyProvider[] providers = new IEnergyProvider[6];
	private boolean neighborsDirty = true;;
	
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
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.readCommonNBT(compound);
	}
	
	public void writeCommonNBT(NBTTagCompound compound)
	{
		for(EnumFacing face : EnumFacing.values())
		{
			compound.setString(face.getName() + "Mode", this.faceMode[face.getIndex()].toString());
		}
		
		this.energyStorage.writeToNBT(compound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		this.writeCommonNBT(compound);
	}

	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return this.faceMode[from.ordinal()] != EnumFaceMode.NONE;
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
	public EnumFacing getFrontFace() {
		return EnumFacing.values()[this.getBlockMetadata()];
	}

	@Override
	public EnumFaceMode[] getFaceModes() {
		return this.faceMode;
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		if(this.faceMode[from.ordinal()] != EnumFaceMode.OUTPUT)
			return 0;
		
		return this.energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		if(this.faceMode[from.ordinal()] != EnumFaceMode.INPUT)
			return 0;
		
		return this.energyStorage.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int getEnergyStored(EnumFacing from) {
		// TODO Auto-generated method stub
		return this.energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored(EnumFacing from) {
		// TODO Auto-generated method stub
		return this.energyStorage.getMaxEnergyStored();
	}
	
	@Override
	public boolean receiveClientEvent(int id, int type) {
		this.faceMode[id] = EnumFaceMode.values()[type];
		return true;
	}

	@Override
	public void update() {
		if(!this.worldObj.isRemote)
		{
			if(this.neighborsDirty )
				this.updateNeighbors();
			
			for(EnumFacing face : EnumFacing.values())
			{
				if(this.receivers[face.ordinal()] != null) {
					if(this.getBlockMetadata() == 0) {
						this.receivers[face.ordinal()].receiveEnergy(face.getOpposite(), this.energyStorage.getMaxExtract(), false);
					} else {
						int energyToSend = this.energyStorage.extractEnergy(this.energyStorage.getMaxExtract(), true);
						int receivedEnergy = this.receivers[face.ordinal()].receiveEnergy(face.getOpposite(), energyToSend, false);
						this.energyStorage.extractEnergy(receivedEnergy, false);
					}
				}
				
				if(this.providers[face.ordinal()] != null) {
					if(this.getBlockMetadata() == 0) {
						this.providers[face.ordinal()].extractEnergy(face.getOpposite(), this.energyStorage.getMaxReceive(), false);
					} else {
						int energyToReceive = this.energyStorage.receiveEnergy(this.energyStorage.getMaxReceive(), true);
						int extractedEnergy = this.providers[face.ordinal()].extractEnergy(face.getOpposite(), energyToReceive, false);
						this.energyStorage.receiveEnergy(extractedEnergy, false);
					}
				}
			}
		}
	}

	public void updateNeighbors() {
		if(!this.worldObj.isRemote) {
			for(EnumFacing face : EnumFacing.values())
			{
				BlockPos blockPos = this.pos.offset(face);
				TileEntity entity = this.worldObj.getTileEntity(blockPos);
				if(entity instanceof IEnergyReceiver && this.faceMode[face.ordinal()] == EnumFaceMode.OUTPUT) {
					this.receivers[face.ordinal()] = ((IEnergyReceiver)entity);
				} else {
					this.receivers[face.ordinal()] = null;
				}
				
				if(entity instanceof IEnergyProvider && this.faceMode[face.ordinal()] == EnumFaceMode.INPUT) {
					this.providers[face.ordinal()] = ((IEnergyProvider)entity);
				} else {
					this.providers[face.ordinal()] = null;
				}
			}
		}
		
		this.neighborsDirty = false;
	}
}
