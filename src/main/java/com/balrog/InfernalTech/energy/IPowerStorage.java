package com.balrog.InfernalTech.energy;

import net.minecraft.util.BlockPos;

public interface IPowerStorage {

	void setEnergyStored(int storedEnergy);
	int getEnergyStored();
	BlockPos getPosition();

}
