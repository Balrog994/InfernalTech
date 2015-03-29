package com.balrog.InfernalTech.utils;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.Vec3;

public class Util {
	public static Vec3 getEyePosition(EntityPlayer player) {
		double x = player.posX, y = player.posY, z = player.posZ;
	    if(player.worldObj.isRemote) {
	      //take into account any eye changes done by mods.
	      y += player.getEyeHeight() - player.getDefaultEyeHeight();
	    } else {
	      y += player.getEyeHeight();
	      if(player instanceof EntityPlayerMP && player.isSneaking()) {
	        y -= 0.08;
	      }
	    }
	    return new Vec3(x,y,z);
	  }
}
