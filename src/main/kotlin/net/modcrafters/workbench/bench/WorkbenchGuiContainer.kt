package net.modcrafters.workbench.bench

import net.modcrafters.workbench.client.Textures
import net.ndrei.teslacorelib.containers.BasicTeslaContainer
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer

class WorkbenchGuiContainer(container: BasicTeslaContainer<WorkbenchEntity>, entity: WorkbenchEntity)
    : BasicTeslaGuiContainer<WorkbenchEntity>(425, container, entity) {

    override val containerWidth get() = 176
    override val containerHeight get() = 197

    override fun drawGuiContainerBackground() {
        Textures.WORKBENCH.bind(this)
        this.drawTexturedModalRect(super.guiLeft, super.guiTop, 0, 0, super.getXSize(), super.getYSize())
    }
}