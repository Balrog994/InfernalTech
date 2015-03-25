package com.balrog.InfernalTech.energy;

import java.util.List;

public interface IEnergyChannel {

	boolean registerToNetwork(EnergyNetwork energyNetwork);

	List<IEnergyChannel> getConnectedChannels();

	EnergyNetwork getNetwork();

	void invalidate();

}
