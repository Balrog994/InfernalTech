package com.balrog.InfernalTech.utils;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import cofh.api.energy.IEnergyReceiver;

import com.balrog.InfernalTech.energy.IEnergyChannel;

public class EnergyReceiverEntry {

	public IEnergyReceiver receiver;
	public BlockPos pos;
	public IEnergyChannel channel;
	public EnumFacing face;

	public EnergyReceiverEntry(IEnergyReceiver receiver, BlockPos pos, IEnergyChannel channel, EnumFacing face) {
		this.receiver = receiver;
		this.pos = pos;
		this.channel = channel;
		this.face = face;
	}

}
