package com.balrog.InfernalTech.network;

import com.balrog.InfernalTech.InfernalTech;
import com.balrog.InfernalTech.energy.IPowerStorage;

import cofh.api.energy.IEnergyHandler;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPowerStorage implements IMessage, IMessageHandler<PacketPowerStorage, IMessage> {

	private BlockPos pos;
	private int storedEnergy;

	public PacketPowerStorage()
	{
		
	}
	
	public PacketPowerStorage(IPowerStorage storage)
	{
		this.pos = storage.getPosition();
		this.storedEnergy = storage.getEnergyStored();
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.pos = new BlockPos(buf.readInt(), buf.readInt(), buf.readInt());
		this.storedEnergy = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(pos.getX());
	    buf.writeInt(pos.getY());
	    buf.writeInt(pos.getZ());
	    buf.writeInt(storedEnergy);
	}

	@Override
	public IMessage onMessage(PacketPowerStorage message, MessageContext ctx) {
		EntityPlayer player = InfernalTech.proxy.getClientPlayer();
	    TileEntity te = player.worldObj.getTileEntity(message.pos);
	    if(te instanceof IPowerStorage) {
	    	FMLLog.info("PacketPowerStorage Received");
	    	IPowerStorage me = (IPowerStorage) te;
	    	me.setEnergyStored(message.storedEnergy);
	    }
	    return null;
	}

}
