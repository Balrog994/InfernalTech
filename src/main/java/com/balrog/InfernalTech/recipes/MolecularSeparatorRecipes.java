package com.balrog.InfernalTech.recipes;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class MolecularSeparatorRecipes {
	private Map molecularSeparatorRecipes = Maps.newHashMap();
	public static final MolecularSeparatorRecipes instance = new MolecularSeparatorRecipes();
	
	private MolecularSeparatorRecipes() {
		this.addRecipe(Blocks.lapis_ore, new MolecularSeparatorRecipe(2000, new ItemStack(Items.dye, 5, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(Blocks.dirt, new MolecularSeparatorRecipe(2000,	new ItemStack(Items.flower_pot, 5), null));
	}

	public void addRecipe(Block input, MolecularSeparatorRecipe recipe) {
		this.addRecipe(Item.getItemFromBlock(input), recipe);
	}

	public void addRecipe(Item input, MolecularSeparatorRecipe recipe) {
		this.addRecipe(new ItemStack(input, 1, 32767), recipe);
	}

	public void addRecipe(ItemStack itemStack, MolecularSeparatorRecipe recipe) {
		this.molecularSeparatorRecipes.put(itemStack, recipe);
	}
	
	public MolecularSeparatorRecipe getRecipe(ItemStack stack)
    {
        Iterator iterator = this.molecularSeparatorRecipes.entrySet().iterator();
        Entry entry;

        do
        {
            if (!iterator.hasNext())
            {
                return null;
            }

            entry = (Entry)iterator.next();
        }
        while (!this.compareItemStacks(stack, (ItemStack)entry.getKey()));

        return (MolecularSeparatorRecipe)entry.getValue();
    }
	
	private boolean compareItemStacks(ItemStack stack1, ItemStack stack2)
    {
        return stack2.getItem() == stack1.getItem() && (stack2.getMetadata() == 32767 || stack2.getMetadata() == stack1.getMetadata());
    }
}
