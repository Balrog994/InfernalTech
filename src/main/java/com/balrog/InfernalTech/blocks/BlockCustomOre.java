package com.balrog.InfernalTech.blocks;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockCustomOre extends BlockOre {
	
	public static int maxMetdata = 2;
	public PropertyInteger METADATA = PropertyInteger.create("Type", 0, maxMetdata - 1);
	private boolean init = false;

	protected BlockCustomOre() {
		super();
		this.setDefaultState(this.getDefaultState().withProperty(METADATA, 0));
	}
	
	@Override
	public IBlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(METADATA, meta);
	}
	
	@Override
	public int getMetaFromState(IBlockState state) {
        return (Integer) state.getValue(METADATA);
    }
	
	@Override
	protected BlockState createBlockState() {
		return new BlockState(this, METADATA);
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
		for(int i = 0; i < maxMetdata; i++) {
			list.add(new ItemStack(itemIn, 1, i));
		}			
	}
	
	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return Item.getItemFromBlock(this);
	}
	
	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
	
	@Override
	public int quantityDroppedWithBonus(int fortune, Random random)
    {
        return this.quantityDropped(random);
    }
	
	public int damageDropped(IBlockState state) {
		return this.getMetaFromState(state);
	}
}
