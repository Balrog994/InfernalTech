package com.balrog.InfernalTech.tileentities;

import java.util.Arrays;
import java.util.List;

import com.balrog.InfernalTech.blocks.BlockEnergyChannel;
import com.balrog.InfernalTech.energy.EnergyNetwork;
import com.balrog.InfernalTech.energy.IEnergyChannel;
import com.balrog.InfernalTech.enums.EnumFaceMode;
import com.google.common.collect.Lists;

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
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.FMLLog;

public class TileEntityEnergyChannel extends TileEntity implements IEnergyChannel, IPersistable, IToolHarvestable, IUpdatePlayerListBox {

	private IBlockState state;
	private EnergyStorage energyStorage = new EnergyStorage(640, 640, 640);
	private IEnergyReceiver[] receivers = new IEnergyReceiver[6];
	private List<IEnergyChannel> channels = Lists.newArrayList();
	private boolean[] connections = new boolean[6];
	private boolean neighborsDirty = true;
	private EnergyNetwork energyNetwork;
	
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
			this.updateNetwork(this.worldObj);
			this.updateNeighbors(this.worldObj);
			
			/*for(EnumFacing face : EnumFacing.values())
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
				
				if(this.providers[face.ordinal()] != null) {
					int energyToReceive = this.energyStorage.receiveEnergy(this.energyStorage.getMaxReceive(), true);
					int extractedEnergy = this.providers[face.ordinal()].extractEnergy(face.getOpposite(), energyToReceive, false);
					this.energyStorage.receiveEnergy(extractedEnergy, false);
				}
			}*/
		}
	}

	private void updateNetwork(World world) {
		if(this.getNetwork() != null)
			return;
		
		FMLLog.info("Updating Network");
		
		if(this.attachToAdjacentNetworkIfUnique(world))
			return;
		
		FMLLog.info("Initializing New Network");
		
		EnergyNetwork network = new EnergyNetwork();
		network.init(world, this);
		
		if(getNetwork() != null && !world.isRemote) {
			world.notifyNeighborsOfStateChange(this.pos, getBlockType());
		}
	}

	private boolean attachToAdjacentNetworkIfUnique(World world) {
		EnergyNetwork network = null;
		for(IEnergyChannel channel : this.channels) {
			if(network == null)
				network = channel.getNetwork();
			else if(network != channel.getNetwork())
				return false;
		}
		
		if(network == null || !this.registerToNetwork(network))
			return false;
		
		network.addChannel(this);
		network.invalidate(world);
		
		FMLLog.info("Attached to existing network");
		return true; 
	}

	public void updateNeighbors(World world) {
		if(!this.neighborsDirty)
			return;
		
		if(!world.isRemote) {
			FMLLog.info("Updating Neighbors");
			
			boolean[] oldConnections = this.connections.clone();
			this.channels.clear();
			
			for(EnumFacing face : EnumFacing.values())
			{
				BlockPos blockPos = this.pos.offset(face);
				TileEntity entity = world.getTileEntity(blockPos);
				boolean connected = false;
				
				if(entity instanceof IEnergyChannel) {
					this.channels.add((IEnergyChannel)entity);
					
					this.notifyReceiverRemoved(face);
					this.receivers[face.ordinal()] = null;
					connected = true;
				} else {
					if(entity instanceof IEnergyReceiver) {
						IEnergyReceiver receiver = (IEnergyReceiver)entity;
						
						if(this.receivers[face.ordinal()] != receiver) {
							this.notifyReceiverRemoved(face);
							this.notifyReceiverAdded(receiver, face);
						}
						this.receivers[face.ordinal()] = receiver;
						connected = true;
					} else {
						this.notifyReceiverRemoved(face);						
						this.receivers[face.ordinal()] = null;
					}
				}
				
				this.connections[face.ordinal()] = connected;
			}
			
			if(!Arrays.equals(this.connections, oldConnections)) {
			
				this.state = null;
				world.markBlockForUpdate(this.pos);
			}
		} else {		
			this.markDirty();
		}
		
		this.neighborsDirty = false;
	}
	
	private void notifyReceiverRemoved(EnumFacing face)
	{
		if(this.receivers[face.ordinal()] != null && this.energyNetwork != null)
			this.energyNetwork.removeReceiver(this.pos.offset(face));
	}
	
	private void notifyReceiverAdded(IEnergyReceiver receiver, EnumFacing face)
	{
		if(this.energyNetwork != null)
			this.energyNetwork.addReceiver(receiver, face, this);
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

	@Override
	public boolean registerToNetwork(EnergyNetwork energyNetwork) {
		FMLLog.info("Registered to Network");
		this.energyNetwork = energyNetwork;
		return true;
	}

	@Override
	public List<IEnergyChannel> getConnectedChannels(World world) {
		List<IEnergyChannel> channels = Lists.newArrayList();
		
		for(EnumFacing face : EnumFacing.values())
		{
			BlockPos blockPos = this.pos.offset(face);
			TileEntity entity = world.getTileEntity(blockPos);
			
			if(entity instanceof IEnergyChannel) {
				channels.add((IEnergyChannel)entity);
			}
		}
		
		return channels;
	}

	@Override
	public EnergyNetwork getNetwork() {
		return this.energyNetwork;
	}

	@Override
	public BlockPos getPosition() {
		return this.pos;
	}

	@Override
	public IEnergyReceiver getReceiver(EnumFacing face) {
		return this.receivers[face.ordinal()];
	}

	@Override
	public int getMaxEnergyStored() {
		return this.energyStorage.getMaxEnergyStored();
	}

	@Override
	public int getEnergyStored() {
		return this.energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxExtract(EnumFacing face) {
		return this.energyStorage.getMaxExtract();
	}

	@Override
	public void setEnergyStorage(int energy) {
		this.energyStorage.setEnergyStored(energy);
	}

	public void onBreakBlock(World worldIn) {
		FMLLog.info("onBreakBlock in TileEntity");
		
		for(IEnergyChannel channel : this.channels) {
			channel.removeChannelConnection(this);
			channel.invalidate();
		}
		this.channels.clear();
		this.receivers = new IEnergyReceiver[6];
		
		EnergyNetwork network = this.getNetwork();
		if(network != null)
			network.destroy();
		
		this.invalidate();
	}
	
	@Override
	public void invalidate() {
		super.invalidate();
		this.invalidateNeighbors();
	}

	@Override
	public void removeChannelConnection(IEnergyChannel channel) {
		this.channels.remove(channel);
		this.invalidate();
	}
}
