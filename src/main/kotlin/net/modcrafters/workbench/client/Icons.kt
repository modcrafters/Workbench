package net.modcrafters.workbench.client

import net.ndrei.teslacorelib.gui.IGuiIcon
import net.ndrei.teslacorelib.gui.IGuiTexture

enum class Icons(override val texture: IGuiTexture, override val left: Int, override val top: Int, override val width: Int, override val height: Int): IGuiIcon {
    ARROW_OFF(Textures.WORKBENCH, 186, 12, 22, 15),
    ARROW_ON(Textures.WORKBENCH, 212, 12, 22, 15),
    BIG_SLOT(Textures.WORKBENCH, 184, 31, 26, 26)
}