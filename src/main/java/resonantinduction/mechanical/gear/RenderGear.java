package resonantinduction.mechanical.gear;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraftforge.client.model.AdvancedModelLoader;
import net.minecraftforge.client.model.IModelCustom;

import org.lwjgl.opengl.GL11;

import resonantinduction.core.Reference;
import calclavia.lib.render.RenderUtility;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderGear
{
	public static final RenderGear INSTANCE = new RenderGear();
	public final IModelCustom MODEL = AdvancedModelLoader.loadModel(Reference.MODEL_DIRECTORY + "gears.obj");

	public void renderInventory(Block block, int metadata, int modelID, RenderBlocks renderer)
	{
		GL11.glRotatef(90, 1, 0, 0);
		switch (metadata)
		{
			default:
				RenderUtility.bind(Reference.BLOCK_TEXTURE_DIRECTORY + "planks_oak.png");
				break;
			case 1:
				RenderUtility.bind(Reference.BLOCK_TEXTURE_DIRECTORY + "cobblestone.png");
				break;
			case 2:
				RenderUtility.bind(Reference.BLOCK_TEXTURE_DIRECTORY + "iron_block.png");
				break;
		}
		MODEL.renderOnly("SmallGear");
	}

	public void renderDynamic(PartGear part, double x, double y, double z, int tier)
	{
		if (part.getMultiBlock().isPrimary())
		{
			GL11.glPushMatrix();
			// Center the model first.
			GL11.glTranslatef((float) x + 0.5f, (float) y + 0.5f, (float) z + 0.5f);
			GL11.glPushMatrix();

			RenderUtility.rotateFaceBlockToSide(part.placementSide);

			GL11.glRotatef((float) Math.toDegrees(part.angle), 0, 1, 0);

			switch (tier)
			{
				default:
					RenderUtility.bind(Reference.BLOCK_TEXTURE_DIRECTORY + "planks_oak.png");
					break;
				case 1:
					RenderUtility.bind(Reference.BLOCK_TEXTURE_DIRECTORY + "cobblestone.png");
					break;
				case 2:
					RenderUtility.bind(Reference.BLOCK_TEXTURE_DIRECTORY + "iron_block.png");
					break;
			}

			if (part.getMultiBlock().isConstructed())
			{
				MODEL.renderOnly("LargeGear");
			}
			else
			{
				MODEL.renderOnly("SmallGear");
			}

			GL11.glPopMatrix();
			GL11.glPopMatrix();
		}
	}
}