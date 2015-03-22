package com.balrog.InfernalTech.enums;

import com.google.common.base.Predicate;

import net.minecraft.util.IStringSerializable;

public enum EnumFaceMode implements IStringSerializable {
	NONE(0, "none"),
	INPUT(1, "input"),
	OUTPUT(2, "output");
	
	private final int mode;
	private final String name;
	
	private EnumFaceMode(int mode, String name)
	{
		this.mode = mode;
		this.name = name;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public int getMode() {
		return this.mode;
	}
}
