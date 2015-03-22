package com.balrog.InfernalTech.renderers;

import com.balrog.InfernalTech.models.ModelMachineSides;
import com.balrog.InfernalTech.enums.EnumFaceMode;
import com.balrog.InfernalTech.tileentities.IConfigurableSides;
import com.balrog.InfernalTech.tileentities.TileEntityMolecularSeparator;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TileEntityConfigurableSidesRenderer extends TileEntitySpecialRenderer {

	private static final ResourceLocation textureInput = new ResourceLocation("infernaltech:textures/blocks/Config_Blue.png");
	private static final ResourceLocation textureOutput = new ResourceLocation("infernaltech:textures/blocks/Config_Red.png");
	private ModelMachineSides machineSidesModel = new ModelMachineSides();
	
	@Override
	public void renderTileEntityAt(TileEntity entity, double posX, double posY, double posZ, float time, int meta) 
	{
		this.renderTileEntityAt((IConfigurableSides)entity, posX, posY, posZ, time, meta);
	}
	
	public void renderTileEntityAt(IConfigurableSides entity, double posX, double posY, double posZ, float time, int meta) 
	{
		EnumFacing faceToExclude = entity.getFrontFace();
		
		Tessellator tessellator = Tessellator.getInstance();
		WorldRenderer renderer = tessellator.getWorldRenderer();
		
		GlStateManager.pushMatrix();
        GlStateManager.translate((float)posX + .5F, (float)posY + .5F, (float)posZ + .5F);

        GlStateManager.enableCull();
        GlStateManager.doPolygonOffset(-3.0F, -3.0F);
        GlStateManager.enablePolygonOffset();
        
        this.bindTexture(textureInput);
        machineSidesModel.render(tessellator, renderer, entity.getFaceModes(), EnumFaceMode.INPUT, faceToExclude);
        
        this.bindTexture(textureOutput);
        machineSidesModel.render(tessellator, renderer, entity.getFaceModes(), EnumFaceMode.OUTPUT, faceToExclude);
        
        GlStateManager.doPolygonOffset(0.0F, 0.0F);
        GlStateManager.disablePolygonOffset();
        GlStateManager.popMatrix();
	}

}
