package com.balrog.InfernalTech;

import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;

public abstract class CommonProxy {
	protected CommonProxy() {
		
	}
	
	/**
     * Helper to register an item model for rendering.
     * @param item the item to register.
     * @param id the unique id of the item.
     * @param metadata the item metadata.
     */
    public abstract void registerInventoryModel(
        final Item item,
        final String id,
        final int metadata);

    /**
     * Helper to add a model variant in the ModelBakery.
     * @param item the main item.
     * @param variantName the variant name.
     */
    public abstract void addModelBakeryVariant(
        final Item item,
        final String variantName);

	public abstract void registerTileEntity(
			Class<? extends TileEntity> tileEntityClass, 
			String id, 
			TileEntitySpecialRenderer specialRenderer);
	
	public double getReachDistanceForPlayer(EntityPlayer entityPlayer) {
		return 5;
	}

	public EntityPlayer getClientPlayer() {
		return null;
	}
}
