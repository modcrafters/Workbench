package net.modcrafters.workbench.bench

import net.minecraft.block.material.Material
import net.modcrafters.workbench.MOD_ID
import net.modcrafters.workbench.WorkbenchMod
import net.ndrei.teslacorelib.annotations.AutoRegisterBlock
import net.ndrei.teslacorelib.blocks.OrientedBlock

@Suppress("unused")
@AutoRegisterBlock
object WorkbenchBlock : OrientedBlock<WorkbenchEntity>(MOD_ID, WorkbenchMod.creativeTab, "workbench",
    WorkbenchEntity::class.java, Material.WOOD)