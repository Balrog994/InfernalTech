package com.balrog.InfernalTech.gui;

import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.EnumFacing;
import cofh.api.energy.IEnergyReceiver;
import cofh.api.energy.IEnergyStorage;

import com.google.common.collect.Lists;

public class GuiComponentEnergyMeter {
	private int x;
	private int y;
	private int texX;
	private int texY;
	private int width;
	private int height;

	public GuiComponentEnergyMeter(int x, int y, int texX, int texY, int width, int height) {
		this.x = x;
		this.y = y;
		this.texX = texX;
		this.texY = texY;
		this.width = width;
		this.height = height;		
	}
	
	public void draw(GuiComponentContainerBase gui, IEnergyStorage storage) {
		float percentageRF = (float)storage.getEnergyStored() / (float)storage.getMaxEnergyStored();
        int invRfViewHeight = (int)(this.height * percentageRF);
        int rfViewHeight = this.height - invRfViewHeight;
        
        gui.drawRectangle(this.x, this.y + rfViewHeight, this.texX, this.texY + rfViewHeight, this.width, invRfViewHeight);
	}

	public boolean isMouseOver(int mouseX, int mouseY) {
		return mouseX >= this.x - 1 && mouseX < this.x + this.width + 1 && mouseY >= this.y - 1 && mouseY < this.y + this.height + 1;
	}

	public void drawTooltip(GuiComponentContainerBase gui, IEnergyStorage storage, int mouseX, int mouseY) {
		List powerTooltipText = Lists.newArrayList();
		powerTooltipText.add("RF: " + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored());
		
		gui.drawToolTip(powerTooltipText, mouseX, mouseY);
	}
}
