package com.balrog.InfernalTech.utils;

import net.minecraft.util.AxisAlignedBB;

public class Collidable implements ICollidable {
	private AxisAlignedBB bounds;

	public Collidable(AxisAlignedBB boundingBox) {
		this.bounds = boundingBox;
		
	}
	
	public AxisAlignedBB getBounds() {
		return this.bounds;
	}
}
