package dark.assembly.client.gui;

import org.lwjgl.opengl.GL11;

import cpw.mods.fml.client.FMLClientHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import dark.assembly.common.AssemblyLine;
import dark.assembly.common.machine.encoder.TileEntityEncoder;
import dark.core.prefab.invgui.GuiButtonImage;

public class GuiEncoderCoder extends GuiEncoderBase
{
    public static final ResourceLocation TEXTURE_CODE_BACK = new ResourceLocation(AssemblyLine.instance.DOMAIN, AssemblyLine.GUI_DIRECTORY + "gui_encoder_coder.png");
    private GuiTaskList taskListGui;

    public GuiEncoderCoder(EntityPlayer player, TileEntityEncoder tileEntity)
    {
        super(player, tileEntity);
    }

    @Override
    protected void drawBackgroundLayer(int x, int y, float var1)
    {
        if (taskListGui == null)
        {
            taskListGui = new GuiTaskList((this.width - this.xSize) / 2 + 20, (this.height - this.ySize) / 2 + 40);
        }
        super.drawBackgroundLayer(x, y, var1);
        FMLClientHandler.instance().getClient().renderEngine.bindTexture(TEXTURE_CODE_BACK);

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

        int containerWidth = (this.width - this.xSize) / 2;
        int containerHeight = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(containerWidth, containerHeight, 0, 0, this.xSize, this.ySize);
        taskListGui.drawConsole(this.fontRenderer);
    }

}
