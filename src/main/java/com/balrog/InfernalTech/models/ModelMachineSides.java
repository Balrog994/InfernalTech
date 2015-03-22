package com.balrog.InfernalTech.models;

import org.lwjgl.opengl.GL11;

import com.balrog.InfernalTech.enums.EnumFaceMode;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.model.PositionTextureVertex;
import net.minecraft.client.model.TexturedQuad;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Vec3;
import net.minecraft.util.Vec3i;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelMachineSides {

	private PositionTextureVertex[] vertexPositions = new PositionTextureVertex[4];
	private Vec3 normal;
	
	public ModelMachineSides()
	{
		this.vertexPositions[3] = new PositionTextureVertex(-8, -8, -8, 1, 1);
		this.vertexPositions[2] = new PositionTextureVertex(8, -8, -8, 0, 1);
		this.vertexPositions[1] = new PositionTextureVertex(8, 8, -8, 0, 0);
		this.vertexPositions[0] = new PositionTextureVertex(-8, 8, -8, 1, 0);
		
		Vec3 vec3 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[0].vector3D);
        Vec3 vec31 = this.vertexPositions[1].vector3D.subtractReverse(this.vertexPositions[2].vector3D);
        this.normal = vec31.crossProduct(vec3).normalize();
	}

	public void render(Tessellator tessellator, WorldRenderer renderer, EnumFaceMode[] faceModes, EnumFaceMode filterMode, EnumFacing faceToExclude) {
		float rad_to_deg = (float) (180.0F / Math.PI);
		float scale = 0.0625F;
		
		for(EnumFacing face : EnumFacing.values())
		{
			if(faceModes[face.getIndex()] != filterMode || face == faceToExclude)
				continue;
			
			GlStateManager.pushMatrix();
			
			float angle;
			Vec3 axis;
			
			if(face == EnumFacing.SOUTH) {
				axis = new Vec3(0,1,0);
				angle = (float) Math.PI;
			} else {
				Vec3i faceNormal = face.getDirectionVec();
				Vec3 desiredNormal = new Vec3(faceNormal.getX(), faceNormal.getY(), faceNormal.getZ());
				axis = this.normal.crossProduct(desiredNormal).normalize();
				angle = (float) Math.acos(this.normal.normalize().dotProduct(desiredNormal.normalize()));
			}
			
			GlStateManager.rotate(angle * rad_to_deg, (float)axis.xCoord, (float)axis.yCoord, (float)axis.zCoord);
			
			renderer.startDrawingQuads();
			renderer.setNormal((float)this.normal.xCoord, (float)this.normal.yCoord, (float)this.normal.zCoord);
			
			for (int i = 0; i < 4; ++i)
	        {
	            PositionTextureVertex positiontexturevertex = this.vertexPositions[i];
	            renderer.addVertexWithUV(positiontexturevertex.vector3D.xCoord * (double)scale, positiontexturevertex.vector3D.yCoord * (double)scale, positiontexturevertex.vector3D.zCoord * (double)scale, (double)positiontexturevertex.texturePositionX, (double)positiontexturevertex.texturePositionY);
	        }
			
			tessellator.draw();
			
			GlStateManager.popMatrix();
		}		
		
	}
}
