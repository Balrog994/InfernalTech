package com.balrog.InfernalTech.gui;

import java.util.List;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.Container;

public abstract class GuiComponentContainerBase extends GuiContainer {

	public GuiComponentContainerBase(Container p_i1072_1_) {
		super(p_i1072_1_);
		// TODO Auto-generated constructor stub
	}
	
	public void drawRectangle(int x, int y, int texX, int texY, int width, int height) {
		int dx = (this.width - xSize) / 2;
        int dy = (this.height - ySize) / 2;
		
		this.drawTexturedModalRect(dx + x, dy + y, texX, texY, width, height);
	}

	public void drawToolTip(List textLines, int mouseX, int mouseY) {
		this.drawHoveringText(textLines, mouseX, mouseY);
	}
}
