package net.modcrafters.workbench.client

import net.minecraft.util.ResourceLocation
import net.modcrafters.workbench.MOD_ID
import net.ndrei.teslacorelib.gui.IGuiTexture

enum class Textures(path: String) : IGuiTexture {
    WORKBENCH("textures/gui/gui_workbench.png");

    override val resource by lazy { ResourceLocation(MOD_ID, path) }
}