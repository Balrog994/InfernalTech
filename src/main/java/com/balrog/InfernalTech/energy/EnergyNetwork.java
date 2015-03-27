package com.balrog.InfernalTech.energy;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLLog;
import cofh.api.energy.EnergyStorage;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import com.balrog.InfernalTech.utils.EnergyReceiverEntry;
import com.balrog.InfernalTech.utils.PositionDirection;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

public class EnergyNetwork {
	private List<IEnergyChannel> energyChannels = Lists.newArrayList();
	private Map<PositionDirection, EnergyReceiverEntry> energyReceivers = Maps.newHashMap();	
	private EnergyStorage networkStorage = new EnergyStorage(Integer.MAX_VALUE);
	
	private Iterator<EnergyReceiverEntry> receiversIterator;
	
	//Visitor Pattern
	public void registerToNetwork(World world, IEnergyChannel channel) {
		if(channel == null)
			return;
		
		if(!channel.registerToNetwork(this))
			return;
		
		this.addChannel(channel);
		
		List<IEnergyChannel> channels = channel.getConnectedChannels(world);
		for(IEnergyChannel newChannel : channels) {
			EnergyNetwork existingNetwork = newChannel.getNetwork();
			
			if(existingNetwork == null) {
				registerToNetwork(world, newChannel);
			} else if(newChannel.getNetwork() != this) {
				existingNetwork.destroy();
				registerToNetwork(world, newChannel);
			}
		}
	}

	public void addChannel(IEnergyChannel channel) {
		if(this.energyChannels.contains(channel))
			return;
		
		FMLLog.info("Adding Channel to Network (%s)", channel.getPosition().toString());
		
		if(this.energyChannels.isEmpty())
			EnergyNetworkHandler.instance.registerNetwork(this);
		
		this.energyChannels.add(channel);
		
		for(EnumFacing face : EnumFacing.values()) {
			IEnergyReceiver receiver = channel.getReceiver(face);
			if(receiver != null) {
				this.addReceiver(receiver, face, channel);
			}
		}
	}
	
	public void addReceiver(IEnergyReceiver receiver, EnumFacing face, IEnergyChannel channel) {
		BlockPos pos = channel.getPosition().offset(face);
		
		PositionDirection key = new PositionDirection(pos, face);
		if(this.energyReceivers.containsKey(key)) {
			EnergyReceiverEntry entry = this.energyReceivers.get(key);
			if(entry.channel == channel && entry.face == face && entry.receiver == receiver) {
				FMLLog.info("Receiver already present");
				return;
			}
		}
		
		this.energyReceivers.put(key, new EnergyReceiverEntry(receiver, pos, channel, face));
		this.receiversIterator = null;
		
		FMLLog.info("addReceiver: Network Composed of %d Receivers and %d Channels", this.energyReceivers.size(), this.energyChannels.size());
	}
	
	public void removeReceiver(BlockPos pos) {
		List<PositionDirection> receiversToRemove = Lists.newArrayList();
		
		for(PositionDirection key : this.energyReceivers.keySet()) {
			if(key.pos.equals(pos))
				receiversToRemove.add(key);
		}
		
		for(PositionDirection key : receiversToRemove) {
			this.energyReceivers.remove(key);
		}
		
		this.receiversIterator = null;
		FMLLog.info("removeReceiver: Network Composed of %d Receivers and %d Channels", this.energyReceivers.size(), this.energyChannels.size());
	}

	public void destroy() {
		for(IEnergyChannel channel : this.energyChannels) {
			channel.registerToNetwork(null);
		}
		this.energyChannels.clear();
		
		EnergyNetworkHandler.instance.unregisterNetwork(this);
		
		FMLLog.info("Network Destroyed");
	}
	
	public void invalidate(World world) {
		for(IEnergyChannel channel : this.energyChannels) {
			world.markBlockForUpdate(channel.getPosition());
		}
	}

	public void init(World world, IEnergyChannel channel) {
		for(IEnergyChannel conn : channel.getConnectedChannels(world)) {
			EnergyNetwork network = conn.getNetwork();
			if(network != null)
				network.destroy();
		}
		
		this.registerToNetwork(world, channel);
		this.invalidate(world);
	}

	public void update() {
		this.updateNetworkStorage();
		
		int actualEnergyStorage = this.networkStorage.getEnergyStored();		
		if(actualEnergyStorage <= 0)
			return;
		
		int servedReceivers = 0;
		int totalReceivers = this.energyReceivers.size();
		
		//FMLLog.info("Network Composed of %d Receivers and %d Channels", this.energyReceivers.size(), this.energyChannels.size());
		while(actualEnergyStorage > 0 && servedReceivers < totalReceivers) {
			if(this.receiversIterator == null || !this.receiversIterator.hasNext()) {
				this.receiversIterator = this.energyReceivers.values().iterator();
			}
			
			EnergyReceiverEntry receiver = this.receiversIterator.next();
					
			//FMLLog.info("Sending Energy");
			
			int energyFromChannel = Math.min(receiver.channel.getMaxExtract(receiver.face), actualEnergyStorage);
			int sentEnergy = receiver.receiver.receiveEnergy(receiver.face.getOpposite(), energyFromChannel, false);
			sentEnergy = Math.max(0, sentEnergy);
			
			//FMLLog.info("Sent %d RF to (%s)", sentEnergy, receiver.pos.toString());
			
			actualEnergyStorage -= sentEnergy;
			servedReceivers++;
		}
		
		this.networkStorage.setEnergyStored(actualEnergyStorage);
		actualEnergyStorage = this.networkStorage.getEnergyStored();
		
		float k = (float) actualEnergyStorage / this.networkStorage.getMaxEnergyStored();
		
		for(IEnergyChannel channel : this.energyChannels) {
			if(actualEnergyStorage < 0)
				channel.setEnergyStorage(0);
			else {
				int energyToSend = (int)Math.ceil(channel.getMaxEnergyStored() * k);
				energyToSend = Math.min(energyToSend, channel.getMaxEnergyStored());
				energyToSend = Math.min(energyToSend, actualEnergyStorage);
				
				channel.setEnergyStorage(energyToSend);
				actualEnergyStorage -= energyToSend;
			}
		}
	}

	private void updateNetworkStorage() {
		int totalEnergyStorage = 0;
		int actualEnergyStorage = 0;
		
		for(IEnergyChannel channel : this.energyChannels) {
			totalEnergyStorage += channel.getMaxEnergyStored();
			actualEnergyStorage += channel.getEnergyStored();
		}
		
		this.networkStorage.setCapacity(totalEnergyStorage);
		this.networkStorage.setEnergyStored(actualEnergyStorage);
		
		//FMLLog.info("Network Storage %d RF / %d RF", actualEnergyStorage, totalEnergyStorage);

	}
}
