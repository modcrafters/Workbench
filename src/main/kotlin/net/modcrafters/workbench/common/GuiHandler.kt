package net.modcrafters.workbench.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.inventory.ContainerChest
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler
import net.modcrafters.workbench.client.GuiWorkbench

class GuiHandler : IGuiHandler {

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {

        if (ID == 0) {
            val xyz = BlockPos(x, y, z)
            val tileEntity = world.getTileEntity(xyz)
            if (tileEntity is TileEntityWorkbench) {
                val tileEntityWorkbench = tileEntity as TileEntityWorkbench?
                return ContainerWorkbench(player.inventory, tileEntityWorkbench!!)
            }
        }

        return null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        if (ID == 0) {
            val xyz = BlockPos(x, y, z)
            val tileEntity = world.getTileEntity(xyz)
            if (tileEntity is TileEntityWorkbench) {
                val tileEntityWorkbench = tileEntity as TileEntityWorkbench?
                return GuiWorkbench(player.inventory, tileEntityWorkbench!!)
            }
        }

        return null
    }
}

