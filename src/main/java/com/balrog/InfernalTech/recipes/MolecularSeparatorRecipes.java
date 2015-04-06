package com.balrog.InfernalTech.recipes;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.balrog.InfernalTech.blocks.BlockCustomOre;
import com.balrog.InfernalTech.materials.ItemOrePowder;
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
		this.addRecipe(Blocks.stone, new MolecularSeparatorRecipe(2000, new ItemStack(Blocks.cobblestone, 1), null));
		this.addRecipe(Blocks.cobblestone, new MolecularSeparatorRecipe(2000, new ItemStack(Blocks.sand, 1), null));
		this.addRecipe(Blocks.redstone_ore, new MolecularSeparatorRecipe(2000, new ItemStack(Items.redstone, 5), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(Blocks.lapis_ore, new MolecularSeparatorRecipe(2000, new ItemStack(Items.dye, 5, EnumDyeColor.BLUE.getDyeDamage()), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(Blocks.iron_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.iron_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(Blocks.gold_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.gold_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(BlockCustomOre.copper_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.copper_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(BlockCustomOre.lead_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.lead_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(BlockCustomOre.nickel_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.nickel_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(BlockCustomOre.silver_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.silver_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
		this.addRecipe(BlockCustomOre.tin_ore, new MolecularSeparatorRecipe(2000, new ItemStack(ItemOrePowder.tin_powder, 2), new ItemStack(Blocks.cobblestone, 1)));
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
