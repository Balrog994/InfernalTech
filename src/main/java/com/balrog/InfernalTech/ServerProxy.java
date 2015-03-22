package com.balrog.InfernalTech;

import com.balrog.InfernalTech.blocks.BlockMolecularSeparator;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ServerProxy extends CommonProxy {

	public ServerProxy() {
		super();
		FMLLog.fine("Created ServerProxy");
	}
	
	@Override
	public void registerInventoryModel(Item item, String id, int metadata) {
		FMLLog.fine("Registering inventory for item %s and metadata %d. no-op.", id, metadata);
	}

	@Override
	public void addModelBakeryVariant(Item item, String variantName) {
		FMLLog.fine("Registering variant %s for item %s. no-op.", variantName, item.getUnlocalizedName());
	}

	@Override
	public void registerTileEntity(Class<? extends TileEntity> tileEntityClass,
			String id, TileEntitySpecialRenderer specialRenderer) {
		
		GameRegistry.registerTileEntity(tileEntityClass, id);
	}

}
