package net.modcrafters.workbench.bench

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.EnumDyeColor
import net.minecraftforge.items.IItemHandlerModifiable
import net.modcrafters.workbench.client.Icons
import net.ndrei.teslacorelib.gui.BasicTeslaGuiContainer
import net.ndrei.teslacorelib.gui.MachineNameGuiPiece
import net.ndrei.teslacorelib.gui.PlayerInventoryBackground
import net.ndrei.teslacorelib.inventory.BoundingRectangle
import net.ndrei.teslacorelib.tileentities.SidedTileEntity

class WorkbenchEntity : SidedTileEntity(425) {
    private lateinit var left : IItemHandlerModifiable
    private lateinit var right : IItemHandlerModifiable
    private lateinit var middle : IItemHandlerModifiable
    private lateinit var bottom : IItemHandlerModifiable

    override fun initializeInventories() {
        super.initializeInventories()

        this.left = this.addSimpleInventory(9, "left_inventory", EnumDyeColor.GREEN, "Left Inventory",
            BoundingRectangle.slots(17, 17, 3, 3),
            { stack, slot -> true }, // TODO: filter by recipes, maybe
            { _, _-> false }, // we never allow automation to output from this inventory
            true)

        this.right = this.addSimpleInventory(6, "right_inventory", EnumDyeColor.RED, "Right Inventory",
            BoundingRectangle.slots(125, 17, 2, 3),
            { _, _ -> false }, // we never allow automation to insert into this slot
            { _, _-> true } // automation can always extract from here
            )

        this.middle = this.addSimpleInventory(1, "middle_inventory", EnumDyeColor.BROWN, "Tool",
            BoundingRectangle.slots(89, 45, 1, 1),
            { stack, slot -> true }, // TODO: filter by recipes, maybe
            { _, _-> false }, // we never allow automation to output from this inventory
            true)

        this.bottom = this.addSimpleInventory(9, "bottom_inventory", EnumDyeColor.BLUE, "Secondary Ingredients",
            BoundingRectangle.slots(7, 83, 9, 1),
            { stack, slot -> true }, // TODO: filter by recipes, maybe
            { _, _-> false }, // we never allow automation to output from this inventory
            true)
    }

    override fun getContainer(id: Int, player: EntityPlayer) =
        WorkbenchContainer(this, player)

    override fun getGuiContainer(id: Int, player: EntityPlayer) =
        WorkbenchGuiContainer(this.getContainer(id, player), this)

    override fun getGuiContainerPieces(container: BasicTeslaGuiContainer<*>) =
        super.getGuiContainerPieces(container).also {
            // first, remove some stuff we don't want from core lib
            // make mental note to make a switch for it
            it.removeIf { (it is MachineNameGuiPiece) || (it is PlayerInventoryBackground) }

            // second, add out own pieces
            it.add(Icons.BIG_SLOT.getStaticPiece(85, 41))
            it.add(Icons.ARROW_OFF.getStaticPiece(86, 21))
        }

    override fun canBePaused() = false
    override val showRedstoneControlPiece get() = false
    override val showSideConfiguratorPiece get() = false
    override fun supportsAddons() = false

    override fun innerUpdate() {
        // TODO: maybe automate this?
    }
}
