package com.balrog.InfernalTech.energy;

import java.util.List;

import com.google.common.collect.Lists;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;

public class EnergyNetworkHandler {
	
	private List<EnergyNetwork> networks = Lists.newArrayList();
	public static final EnergyNetworkHandler instance = new EnergyNetworkHandler();
	
	private EnergyNetworkHandler() {
		
	}
	
	@SubscribeEvent
	public void onServerTick(TickEvent.ServerTickEvent event) {
		if(event.phase == Phase.START) {
			tickStart(event);
		} else {
			tickEnd(event);
		}
	}
	
	public void registerNetwork(EnergyNetwork network) {
		this.networks.add(network);
	}
	
	public void unregisterNetwork(EnergyNetwork network) {
		this.networks.remove(network);
	}

	private void tickStart(ServerTickEvent event) {
		
	}
	
	private void tickEnd(ServerTickEvent event) {
		for(EnergyNetwork network : this.networks) {
			network.update();
		}
	}
}
