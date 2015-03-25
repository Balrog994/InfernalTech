package com.balrog.InfernalTech.energy;

import java.util.List;

import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

import com.google.common.collect.Lists;

public class EnergyNetwork {
	private List<IEnergyChannel> energyChannels = Lists.newArrayList();
	private List<IEnergyReceiver> energyReceivers = Lists.newArrayList();
	private List<IEnergyProvider> energyProviders = Lists.newArrayList();
	
	private boolean receiversDirty = true;
	
	//Visitor Pattern
	public void registerToNetwork(IEnergyChannel channel) {
		if(channel == null)
			return;
		
		if(!channel.registerToNetwork(this))
			return;
		
		this.addChannel(channel);
		
		List<IEnergyChannel> channels = channel.getConnectedChannels();
		for(IEnergyChannel newChannel : channels) {
			EnergyNetwork existingNetwork = newChannel.getNetwork();
			
			if(existingNetwork == null) {
				registerToNetwork(newChannel);
			} else if(newChannel.getNetwork() != this) {
				existingNetwork.destroy();
				registerToNetwork(newChannel);
			}
		}
	}

	public void addChannel(IEnergyChannel channel) {
		if(this.energyChannels.contains(channel))
			return;
		
		this.energyChannels.add(channel);
		
		for(IEnergyReceiver receiver : channel.getReceivers()) {
			this.addReceiver(receiver);
		}
	}
	
	public void addReceiver(IEnergyReceiver receiver) {
		this.energyReceivers.add(receiver);
	}
	
	public void removeReceiver(IEnergyReceiver receiver) {
		
	}

	public void destroy() {
		for(IEnergyChannel channel : this.energyChannels) {
			channel.registerToNetwork(null);
		}
		this.energyChannels.clear();
	}
	
	public void invalidate() {
		for(IEnergyChannel channel : this.energyChannels) {
			channel.invalidate();
		}
	}

	public void init(IEnergyChannel channel) {
		for(IEnergyChannel conn : channel.getConnectedChannels()) {
			EnergyNetwork network = conn.getNetwork();
			if(network != null)
				network.destroy();
		}
		
		this.registerToNetwork(channel);
		this.invalidate();
	}

	public void update() {
		this.updateReceivers();
	}

	private void updateReceivers() {
		if(!this.receiversDirty )
			return;
		
		
	}
}
