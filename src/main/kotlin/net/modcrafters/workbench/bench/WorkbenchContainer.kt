package net.modcrafters.workbench.bench

import net.minecraft.entity.player.EntityPlayer
import net.ndrei.teslacorelib.containers.BasicTeslaContainer

class WorkbenchContainer(entity: WorkbenchEntity, player: EntityPlayer?)
    : BasicTeslaContainer<WorkbenchEntity>(entity, player) {

    override val showPlayerExtraSlots get() = false

    override val inventoryOffsetX get() = 8
    override val inventoryOffsetY get() = 115
    override val inventoryQuickBarOffsetY get() = 173
}
