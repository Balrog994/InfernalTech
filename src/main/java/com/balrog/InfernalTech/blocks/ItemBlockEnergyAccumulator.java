package com.balrog.InfernalTech.blocks;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockEnergyAccumulator extends ItemBlock {
	
	public ItemBlockEnergyAccumulator(Block block) {
		super(block);
		this.setMaxDamage(0);
		this.setHasSubtypes(true);
	}

	 @Override
	 public int getMetadata(int metadata)
	 {
		 return metadata;
	 }

	 @Override
	 public String getUnlocalizedName(ItemStack stack)
	 {
		 return super.getUnlocalizedName() + ".tier" + stack.getMetadata();
	 }
}
