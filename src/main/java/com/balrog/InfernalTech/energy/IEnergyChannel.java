package com.balrog.InfernalTech.energy;

import java.util.Collection;
import java.util.List;

import com.balrog.InfernalTech.utils.ICollidable;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import cofh.api.energy.IEnergyHandler;
import cofh.api.energy.IEnergyProvider;
import cofh.api.energy.IEnergyReceiver;

public interface IEnergyChannel extends IEnergyHandler {

	boolean registerToNetwork(EnergyNetwork energyNetwork);

	List<IEnergyChannel> getConnectedChannels(World world);

	EnergyNetwork getNetwork();

	BlockPos getPosition();

	IEnergyReceiver getReceiver(EnumFacing face);

	int getMaxEnergyStored();
	int getEnergyStored();
	
	int getMaxExtract(EnumFacing face);

	void setEnergyStorage(int energy);

	void removeChannelConnection(World world, IEnergyChannel channel);

	Collection<? extends ICollidable> getCollidableComponents();

}
