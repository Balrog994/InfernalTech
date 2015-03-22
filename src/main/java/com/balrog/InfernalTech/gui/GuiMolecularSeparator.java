package com.balrog.InfernalTech.gui;

import java.util.List;

import com.balrog.InfernalTech.InfernalTech;
import com.balrog.InfernalTech.containers.ContainerMolecularSeparator;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;
import com.google.common.collect.Lists;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiLabel;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLLog;

public class GuiMolecularSeparator extends GuiComponentContainerBase {

	public static final int GUIID = 0;
	private ResourceLocation backgroundimage = new ResourceLocation(InfernalTech.MODID.toLowerCase() + ":" + "textures/gui/machine/molecular_separator.png");
	private InventoryPlayer playerInventory;
	private TileEntityMolecularSeparator tileMolecularSeparator;
	private GuiComponentEnergyMeter energyMeter;
		
	public GuiMolecularSeparator(InventoryPlayer inventory, TileEntityMolecularSeparator tileEntity) {
		super(new ContainerMolecularSeparator(inventory, tileEntity));
		this.playerInventory = inventory;
        this.tileMolecularSeparator = tileEntity;
        this.energyMeter = new GuiComponentEnergyMeter(8,  7,  176,  31,  16,  59);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// TODO Auto-generated method stub
		this.mc.getTextureManager().bindTexture(backgroundimage);
		
        int x = (this.width - xSize) / 2;
        int y = (this.height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize,  ySize);
        
        this.energyMeter.draw(this, this.tileMolecularSeparator.energyStorage);
                
        int progress = this.getProgress(24);
        this.drawTexturedModalRect(x + 79, y + 34, 176, 14, progress + 1, 16);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
		String s = this.tileMolecularSeparator.getDisplayName().getUnformattedText();
        this.fontRendererObj.drawString(s, this.xSize / 2 - this.fontRendererObj.getStringWidth(s) / 2, 6, 4210752);
        this.fontRendererObj.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		
		int localX = mouseX - this.guiLeft;
		int localY = mouseY - this.guiTop;
		
		if(this.energyMeter.isMouseOver(localX, localY)) {
			this.energyMeter.drawTooltip(this, this.tileMolecularSeparator.energyStorage, localX, localY);
		}
	}

	private int getProgress(int iconSize) {
		int elapsedWorkTime = this.tileMolecularSeparator.getField(2);
        int totalWorkTime = this.tileMolecularSeparator.getField(3);
        return elapsedWorkTime != 0 && totalWorkTime != 0 ? elapsedWorkTime * iconSize / totalWorkTime : 0;
	}
	
	

}
