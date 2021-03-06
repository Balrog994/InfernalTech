package com.balrog.InfernalTech;

import com.balrog.InfernalTech.blocks.BlockMolecularSeparator;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ClientProxy extends CommonProxy {

	public ClientProxy()
	{
		super();
		FMLLog.fine("Created ClientProxy");
	}
	
	@Override
	public void registerInventoryModel(Item item, String id, int metadata) {
		FMLLog.fine("Registering inventory for item %s and metadata %d", id, metadata);
        Minecraft.getMinecraft()
        	.getRenderItem()
        	.getItemModelMesher()
        	.register(item, metadata, new ModelResourceLocation(InfernalTech.MODID.toLowerCase() + ":" + id, "inventory"));
		
	}

	@Override
	public void addModelBakeryVariant(Item item, String variantName) {
		FMLLog.fine("Registering variant %s for item %s.", variantName, item.getUnlocalizedName());
        ModelBakery.addVariantName(item, variantName);
	}

	@Override
	public void registerTileEntity(Class<? extends TileEntity> tileEntityClass, String id, Class<? extends TileEntitySpecialRenderer> specialRenderer) {
		try
		{
			if(specialRenderer != null)
				ClientRegistry.registerTileEntity(tileEntityClass, id, specialRenderer.newInstance());
			else
				GameRegistry.registerTileEntity(tileEntityClass, id);
		}
		catch(Exception ex)
		{
			FMLLog.severe(ex.toString());
		}
	}

	@Override
	public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
		if(entityPlayer instanceof EntityPlayerMP) {
			return ((EntityPlayerMP) entityPlayer).theItemInWorldManager.getBlockReachDistance();
	    }
		return super.getReachDistanceForPlayer(entityPlayer);
	}
	
	@Override
	public EntityPlayer getClientPlayer() {
	    return Minecraft.getMinecraft().thePlayer;
	}
}
