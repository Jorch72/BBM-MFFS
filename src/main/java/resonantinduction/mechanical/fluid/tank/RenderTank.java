package resonantinduction.mechanical.fluid.tank;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

import org.lwjgl.opengl.GL11;

import resonantinduction.core.render.RenderFluidHelper;
import resonantinduction.old.client.model.ModelTankSide;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dark.lib.helpers.ColorCode;

@SideOnly(Side.CLIENT)
public class RenderTank extends TileEntitySpecialRenderer
{
    private ModelTankSide model;

    public RenderTank()
    {
        model = new ModelTankSide();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float var8)
    {
        FluidStack liquid = tileEntity instanceof TileTank ? ((TileTank) tileEntity).getTank().getFluid(): null;
        this.renderTank(tileEntity, x, y, z, 0, liquid);
    }

    public void renderTank(TileEntity tileEntity, double x, double y, double z, int meta, FluidStack liquid)
    {
        if (liquid != null && liquid.amount > 100)
        {
            int[] displayList = RenderFluidHelper.getFluidDisplayLists(liquid, tileEntity.worldObj, false);

            GL11.glPushMatrix();
            GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            bindTexture(RenderFluidHelper.getFluidSheet(liquid));

            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glScalef(1.01F, 1.01F, 1.01F);
            int cap = tileEntity instanceof TileTank ? ((TileTank) tileEntity).getTank().getCapacity() : liquid.amount;
            GL11.glCallList(displayList[(int) ((float) liquid.amount / (float) (cap) * (RenderFluidHelper.DISPLAY_STAGES - 1))]);

            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }

        /*
        byte renderSides = 0;
        if (tileEntity instanceof TileTank)
        {
            renderSides = ((TileTank) tileEntity).renderSides;
        }
        
        boolean bot = TileTank.canRenderSide(renderSides, ForgeDirection.UP);
        boolean top = TileTank.canRenderSide(renderSides, ForgeDirection.DOWN);
        boolean north = TileTank.canRenderSide(renderSides, ForgeDirection.NORTH);
        boolean south = TileTank.canRenderSide(renderSides, ForgeDirection.SOUTH);
        boolean east = TileTank.canRenderSide(renderSides, ForgeDirection.EAST);
        boolean west = TileTank.canRenderSide(renderSides, ForgeDirection.WEST);
        for (int i = 0; i < 4; i++)
        {
            ForgeDirection dir = ForgeDirection.getOrientation(i + 2);

            if (!TileTank.canRenderSide(renderSides, dir.getOpposite()))
            {
                GL11.glPushMatrix();

                GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
                GL11.glScalef(1.0F, -1F, -1F);
                boolean left = false;
                boolean right = false;
                switch (dir)
                {
                    case NORTH:
                        GL11.glRotatef(180f, 0f, 1f, 0f);
                        left = west;
                        right = east;
                        break;
                    case SOUTH:
                        GL11.glRotatef(0f, 0f, 1f, 0f);
                        left = east;
                        right = west;
                        break;
                    case WEST:
                        GL11.glRotatef(90f, 0f, 1f, 0f);
                        left = south;
                        right = north;
                        break;
                    case EAST:
                        GL11.glRotatef(270f, 0f, 1f, 0f);
                        left = north;
                        right = south;
                        break;
                }
                bindTexture(this.getTexture(tileEntity.getBlockType().blockID, tileEntity.getBlockMetadata()));
                model.render(0.0625F, left, right, top, bot);
                GL11.glPopMatrix();
            }
        }*/

    }

    public ResourceLocation getTexture(int block, int meta)
    {
        String texture = "";
        if (ColorCode.get(meta) == ColorCode.RED)
        {
            texture = "textures/blocks/obsidian.png";
        }
        else
        {
            texture = "textures/blocks/iron_block.png";
        }
        return new ResourceLocation(texture);
    }
}