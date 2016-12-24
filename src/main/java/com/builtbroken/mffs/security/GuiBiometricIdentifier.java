package com.builtbroken.mffs.security;

import com.builtbroken.mc.client.SharedAssets;
import com.builtbroken.mffs.base.GuiMFFS;
import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.entity.player.EntityPlayer;

public class GuiBiometricIdentifier extends GuiMFFS<TileBiometricIdentifier>
{
    public GuiBiometricIdentifier(EntityPlayer player, TileBiometricIdentifier tile)
    {
        super(new ContainerBiometricIdentifier(player, tile), tile);
        this.baseTexture = SharedAssets.GUI_BASE;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int x, int y)
    {
        drawStringCentered(tile.getInventoryName(), this.xSize / 2, 6);
        drawStringCentered(ChatFormatting.AQUA + "id and Group Cards", this.xSize / 2, 25);
        drawString("Frequency", 40, 118);
        drawFortronText(x, y);
        super.drawGuiContainerForegroundLayer(x, y);
    }
}