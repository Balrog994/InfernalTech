package com.balrog.InfernalTech.tileentities;

import java.util.Arrays;

import com.balrog.InfernalTech.blocks.BlockEnergyChannel;
import com.balrog.InfernalTech.enums.EnumFaceMode;

import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.server.gui.IUpdatePlayerListBox;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockable;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLLog;

public class TileEntityEnergyChannel extends TileEntity implements IEnergyReceiver, IEnergyProvider, IPersistable, IUpdatePlayerListBox, IToolHarvestable {

	private IBlockState state;
	private EnergyStorage energyStorage = new EnergyStorage(640, 640, 640);
	private IEnergyReceiver[] receivers = new IEnergyReceiver[6];
	private IEnergyProvider[] providers = new IEnergyProvider[6];
	private boolean[] connections = new boolean[6];
	private boolean neighborsDirty = true;
	
	@Override
	public boolean canConnectEnergy(EnumFacing from) {
		return true;
	}

	@Override
	public void readCommonNBT(NBTTagCompound compound) {
		int connections = compound.getInteger("connections");
		
		for(int i = 0; i < 6; i++) {
			this.connections[i] = (connections & (1 << i)) != 0;
		}
		
		this.energyStorage.readFromNBT(compound);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		this.readCommonNBT(compound);
	}

	@Override
	public void writeCommonNBT(NBTTagCompound compound) {
		int connections = 0;
		
		for(int i = 0; i < 6; i++) {
			connections |= ((this.connections[i] ? 1 : 0) << i);
		}
		
		compound.setInteger("connections", connections);
		
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
				IEnergyReceiver receiver = this.receivers[face.ordinal()];
				
				if(receiver != null) {
					int energyToSend = this.energyStorage.extractEnergy(this.energyStorage.getMaxExtract(), true);
					if(energyToSend > 0)
					{
						int receivedEnergy = receiver.receiveEnergy(face.getOpposite(), energyToSend, true);
						if(receivedEnergy > 0) {
							int effectiveExtractedEnergy = this.energyStorage.extractEnergy(receivedEnergy, false);
							int effectiveInsertedEnergy = receiver.receiveEnergy(face.getOpposite(), effectiveExtractedEnergy, false);
							if(effectiveInsertedEnergy < effectiveExtractedEnergy)
								FMLLog.info("Inserted %d RF, Extracted %d RF", effectiveInsertedEnergy, effectiveExtractedEnergy);
							
							if(effectiveInsertedEnergy > 0)
								FMLLog.info("Sent %d RF to %s", effectiveInsertedEnergy, face.toString());
						}
					}
				}
				
				/*if(this.providers[face.ordinal()] != null) {
					int energyToReceive = this.energyStorage.receiveEnergy(this.energyStorage.getMaxReceive(), true);
					int extractedEnergy = this.providers[face.ordinal()].extractEnergy(face.getOpposite(), energyToReceive, false);
					this.energyStorage.receiveEnergy(extractedEnergy, false);
				}*/
			}
		}
	}

	public void updateNeighbors() {
		if(!this.worldObj.isRemote) {
			FMLLog.info("Updating Neighbors");
			
			boolean[] oldConnections = this.connections.clone();
			
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
				
				this.connections[face.ordinal()] = this.receivers[face.ordinal()] != null || this.providers[face.ordinal()] != null;
			}
			
			if(!Arrays.equals(this.connections, oldConnections)) {
			
				this.state = null;
				this.worldObj.markBlockForUpdate(this.pos);
			}
		} else {		
			this.markDirty();
		}
		
		this.neighborsDirty = false;
	}
	
	public Packet getDescriptionPacket() {
	    NBTTagCompound tagCompound = new NBTTagCompound();
	    writeToNBT(tagCompound);
	    return new S35PacketUpdateTileEntity(this.pos, 0, tagCompound);
	}
	
	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		NBTTagCompound tagCompound = pkt.getNbtCompound();
		readFromNBT(tagCompound);
				
		this.state = null;
		this.worldObj.markBlockForUpdate(this.pos);
	}
	
	public IBlockState getState() {
		if(state == null)
        {
            state = getBlockType().getDefaultState()
            		.withProperty(BlockEnergyChannel.NORTH, this.hasConnection(EnumFacing.NORTH))
    				.withProperty(BlockEnergyChannel.SOUTH, this.hasConnection(EnumFacing.SOUTH))
    				.withProperty(BlockEnergyChannel.EAST, this.hasConnection(EnumFacing.EAST))
    				.withProperty(BlockEnergyChannel.WEST, this.hasConnection(EnumFacing.WEST))
    				.withProperty(BlockEnergyChannel.DOWN, this.hasConnection(EnumFacing.DOWN))
    				.withProperty(BlockEnergyChannel.UP, this.hasConnection(EnumFacing.UP));
        }
        return state;
	}
	
	public void setState(IBlockState state)
    {
        this.state = state;
    }
	
	private boolean hasConnection(EnumFacing side)
	{
		return this.connections[side.ordinal()];
	}

	public void invalidateNeighbors() {
		this.neighborsDirty = true;
	}
}
