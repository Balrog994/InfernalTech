package com.balrog.InfernalTech.containers;

import com.balrog.InfernalTech.network.PacketHandler;
import com.balrog.InfernalTech.network.PacketPowerStorage;
import com.balrog.InfernalTech.recipes.MolecularSeparatorRecipes;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnaceOutput;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerMolecularSeparator extends Container {

	private TileEntityMolecularSeparator tileMolecularSeparator;
	private int cookTime;
	private int operationProgressTime;
	private int totalEnergyStorage;
	private int totalCookTime;
	
	public ContainerMolecularSeparator(InventoryPlayer playerInventory, TileEntityMolecularSeparator molecularSeparator) {
		this.tileMolecularSeparator = molecularSeparator;
		this.addSlotToContainer(new Slot(molecularSeparator, 0, 56, 34));
		this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, molecularSeparator, 1, 116, 35));
		this.addSlotToContainer(new SlotFurnaceOutput(playerInventory.player, molecularSeparator, 2, 138, 35));
		
		int i;

        for (i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 9; ++j)
            {
                this.addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for (i = 0; i < 9; ++i)
        {
            this.addSlotToContainer(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting listener) {
		// TODO Auto-generated method stub
		super.addCraftingToCrafters(listener);
		listener.func_175173_a(this, this.tileMolecularSeparator);
	}
	
	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub
		super.detectAndSendChanges();
		
		for (int i = 0; i < this.crafters.size(); ++i)
        {
            ICrafting icrafting = (ICrafting)this.crafters.get(i);

            if (this.cookTime != this.tileMolecularSeparator.getField(2))
            {
            	//FMLLog.info("Sending Packet %d,%d", 2, this.tileMolecularSeparator.getField(2));
                icrafting.sendProgressBarUpdate(this, 2, this.tileMolecularSeparator.getField(2));
            }

            /*if (this.totalEnergyStorage != this.tileMolecularSeparator.getField(0))
            {
            	FMLLog.info("Sending Packet %d,%d", 0, this.tileMolecularSeparator.getField(0));
                icrafting.sendProgressBarUpdate(this, 0, this.tileMolecularSeparator.getField(0));
            }*/

            if (this.operationProgressTime != this.tileMolecularSeparator.getField(1))
            {
            	//FMLLog.info("Sending Packet %d,%d", 1, this.tileMolecularSeparator.getField(1));
                icrafting.sendProgressBarUpdate(this, 1, this.tileMolecularSeparator.getField(1));
            }

            if (this.totalCookTime != this.tileMolecularSeparator.getField(3))
            {
            	//FMLLog.info("Sending Packet %d,%d", 3, this.tileMolecularSeparator.getField(3));
                icrafting.sendProgressBarUpdate(this, 3, this.tileMolecularSeparator.getField(3));
            }
        }

        this.cookTime = this.tileMolecularSeparator.getField(2);
        this.totalEnergyStorage = this.tileMolecularSeparator.getField(0);
        this.operationProgressTime = this.tileMolecularSeparator.getField(1);
        this.totalCookTime = this.tileMolecularSeparator.getField(3);
        
        //PacketHandler.sendToAllAround(new PacketPowerStorage(this.tileMolecularSeparator), this.tileMolecularSeparator);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int id, int data) {
		//FMLLog.info("Received Packet %d,%d", id, data);
		
		this.tileMolecularSeparator.setField(id, data);
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer playerIn) {
		// TODO Auto-generated method stub
		return this.tileMolecularSeparator.isUseableByPlayer(playerIn);
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack())
        {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            if (index == 1 || index == 2)
            {
                if (!this.mergeItemStack(itemstack1, 3, 39, true))
                {
                    return null;
                }

                slot.onSlotChange(itemstack1, itemstack);
            }
            else if (index != 0)
            {
                if (MolecularSeparatorRecipes.instance.getRecipe(itemstack1) != null)
                {
                    if (!this.mergeItemStack(itemstack1, 0, 1, false))
                    {
                        return null;
                    }
                }
                else if (index >= 3 && index < 30)
                {
                    if (!this.mergeItemStack(itemstack1, 30, 39, false))
                    {
                        return null;
                    }
                }
                else if (index >= 30 && index < 39 && !this.mergeItemStack(itemstack1, 3, 30, false))
                {
                    return null;
                }
            }
            else if (!this.mergeItemStack(itemstack1, 3, 39, false))
            {
                return null;
            }

            if (itemstack1.stackSize == 0)
            {
                slot.putStack((ItemStack)null);
            }
            else
            {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize)
            {
                return null;
            }

            slot.onPickupFromSlot(playerIn, itemstack1);
        }

        return itemstack;
	}
}
