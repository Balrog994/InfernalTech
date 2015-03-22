package com.balrog.InfernalTech.tileentities;

import com.balrog.InfernalTech.enums.EnumFaceMode;

import net.minecraft.util.EnumFacing;

public interface IConfigurableSides {

	void cycleFaceMode(EnumFacing side);
	EnumFacing getFrontFace();
	EnumFaceMode[] getFaceModes();
}
