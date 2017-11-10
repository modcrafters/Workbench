package net.modcrafters.workbench.client

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.inventory.GuiContainer
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.util.ResourceLocation
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import net.modcrafters.workbench.common.ContainerWorkbench
import net.modcrafters.workbench.common.TileEntityWorkbench

@SideOnly(Side.CLIENT)
class GuiWorkbench(invPlayer: InventoryPlayer, private val tileEntityWorkbench: TileEntityWorkbench)
    : GuiContainer(ContainerWorkbench(invPlayer, tileEntityWorkbench)) {

    init {
        // Set the width and height of the gui.  Should match the size of the texture!
        this.xSize = 176
        this.ySize = 197
    }

    // draw the background for the GUI - rendered first
    override fun drawGuiContainerBackgroundLayer(partialTicks: Float, x: Int, y: Int) {
        this.drawDefaultBackground()
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f)
        Minecraft.getMinecraft().textureManager.bindTexture(texture)

        this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize)
    }

    // draw the foreground for the GUI - rendered after the slots, but before the dragged items and tooltips
    // renders relative to the top left corner of the background
    override fun drawGuiContainerForegroundLayer(mouseX: Int, mouseY: Int) {
        val s: String = this.tileEntityWorkbench.displayName?.unformattedText ?: "Workbench"
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2 + 8, 6, 4210752)
    }

    companion object {
        // This is the resource location for the background image for the GUI
        private val texture = ResourceLocation("workbench", "textures/gui/gui_workbench.png")
    }
}
