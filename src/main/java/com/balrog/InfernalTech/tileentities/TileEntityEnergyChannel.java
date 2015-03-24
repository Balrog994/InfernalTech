package com.balrog.InfernalTech.tileentities;

import com.balrog.InfernalTech.blocks.BlockEnergyChannel;
import com.balrog.InfernalTech.enums.EnumFaceMode;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

public class TileEntityEnergyChannel extends TileEntity implements IEnergyReceiver, IEnergyProvider, IPersistable, IUpdatePlayerListBox, IToolHarvestable {

	private EnergyStorage energyStorage = new EnergyStorage(640, 640, 640);
	private IEnergyReceiver[] receivers = new IEnergyReceiver[6];
	private IEnergyProvider[] providers = new IEnergyProvider[6];
	private boolean neighborsDirty = true;
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public void readCommonNBT(NBTTagCompound compound) {
		this.energyStorage.readFromNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.readCommonNBT(compound);
	}

	@Override
	public void writeCommonNBT(NBTTagCompound compound) {
		this.energyStorage.writeToNBT(compound);
	}
	
	@Override
	public void writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		this.writeCommonNBT(compound);
	}

	@Override
	public int extractEnergy(EnumFacing from, int maxExtract, boolean simulate) {
		return this.energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int receiveEnergy(EnumFacing from, int maxReceive, boolean simulate) {
		// TODO Auto-generated method stub
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
	public void update() {
		if(!this.worldObj.isRemote)
		{
			if(this.neighborsDirty )
				this.updateNeighbors();
			
			for(EnumFacing face : EnumFacing.values())
			{
				if(this.receivers[face.ordinal()] != null) {
					int energyToSend = this.energyStorage.extractEnergy(this.energyStorage.getMaxExtract(), true);
					int receivedEnergy = this.receivers[face.ordinal()].receiveEnergy(face.getOpposite(), energyToSend, false);
					this.energyStorage.extractEnergy(receivedEnergy, false);
				}
				
				if(this.providers[face.ordinal()] != null) {
					int energyToReceive = this.energyStorage.receiveEnergy(this.energyStorage.getMaxReceive(), true);
					int extractedEnergy = this.providers[face.ordinal()].extractEnergy(face.getOpposite(), energyToReceive, false);
					this.energyStorage.receiveEnergy(extractedEnergy, false);

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
				if(entity instanceof IEnergyReceiver) {
					this.receivers[face.ordinal()] = ((IEnergyReceiver)entity);
				} else {
					this.receivers[face.ordinal()] = null;
				}
				
				if(entity instanceof IEnergyProvider) {
					this.providers[face.ordinal()] = ((IEnergyProvider)entity);
				} else {
					this.providers[face.ordinal()] = null;
				}
			}
			
			IBlockState state = this.worldObj.getBlockState(this.pos);
			
			state = state
				.withProperty(BlockEnergyChannel.NORTH, this.hasConnection(EnumFacing.NORTH))
				.withProperty(BlockEnergyChannel.SOUTH, this.hasConnection(EnumFacing.SOUTH))
				.withProperty(BlockEnergyChannel.EAST, this.hasConnection(EnumFacing.EAST))
				.withProperty(BlockEnergyChannel.WEST, this.hasConnection(EnumFacing.WEST))
				.withProperty(BlockEnergyChannel.DOWN, this.hasConnection(EnumFacing.DOWN))
				.withProperty(BlockEnergyChannel.UP, this.hasConnection(EnumFacing.UP));
			
			this.worldObj.setBlockState(this.pos, state);
		}
		
		this.neighborsDirty = false;
	}
	
	private boolean hasConnection(EnumFacing side)
	{
		return this.receivers[side.ordinal()] != null || this.providers[side.ordinal()] != null;
	}

	public void invalidateNeighbors() {
		this.neighborsDirty = true;
	}
}
