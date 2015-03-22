package com.balrog.InfernalTech.tileentities;

import net.minecraft.nbt.NBTTagCompound;

public interface IPersistable {
	public void readCommonNBT(NBTTagCompound compound);
	public void writeCommonNBT(NBTTagCompound compound);
}
