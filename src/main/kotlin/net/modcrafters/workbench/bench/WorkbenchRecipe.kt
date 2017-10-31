package net.modcrafters.workbench.bench

import net.minecraft.item.ItemStack
import net.minecraft.world.WorldServer
import net.minecraftforge.common.util.FakePlayerFactory
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.items.IItemHandlerModifiable
import net.ndrei.teslacorelib.utils.extractFromCombinedInventory

class WorkbenchRecipe(
    val input: Map<Pair<Int, Int>, ItemStack>,
    val tool: ItemStack,
    val extra: List<ItemStack>,
    val result: List<Pair<ItemStack, Float>>
) {
    fun extractFromInventories(world: WorldServer, input: IItemHandlerModifiable, middle: IItemHandlerModifiable, bottom: IItemHandlerModifiable): Boolean {
        if (this.extractStuff(input, bottom, true)) {
            if (!this.tool.isEmpty && middle.getStackInSlot(0).isItemEqualIgnoreDurability(this.tool)) {
                middle.getStackInSlot(0).damageItem(1, FakePlayerFactory.getMinecraft(world))
                middle.setStackInSlot(0, middle.getStackInSlot(0).copy()) // to force a sync event
            }
            else if (!this.tool.isEmpty) {
                // tool not found ?!?
                return false
            }

            this.extractStuff(input, bottom, false)
            return true
        }
        return false
    }

    private fun extractStuff(input: IItemHandlerModifiable, bottom: IItemHandlerModifiable, simulate: Boolean): Boolean {
        if (this.input.any { (coords, stack) ->
            val slot = (coords.first + coords.second * 3).coerceIn(0 until input.slots)
            val extracted = input.extractItem(slot, stack.count, simulate)
            (extracted.count != stack.count)
        }) {
            return false
        }

        if (this.extra.any { stack ->
            val extracted = bottom.extractFromCombinedInventory(stack, stack.count, simulate)
            (extracted != stack.count)
        }) {
            return false
        }

        return true
    }
}