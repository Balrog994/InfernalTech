package com.balrog.InfernalTech.recipes;

import net.minecraft.item.ItemStack;

public class MolecularSeparatorRecipe {

	public final int energyRequired;
	public final ItemStack primaryOutput;
	public final ItemStack secondaryOutput;

	public MolecularSeparatorRecipe(int energyRequired, ItemStack primaryOutput, ItemStack secondaryOutput) {
		this.energyRequired = energyRequired;
		this.primaryOutput = primaryOutput;
		this.secondaryOutput = secondaryOutput;
	}
}
