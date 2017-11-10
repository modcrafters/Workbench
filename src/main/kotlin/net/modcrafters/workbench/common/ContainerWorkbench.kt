package net.modcrafters.workbench.common

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.InventoryPlayer
import net.minecraft.inventory.Container
import net.minecraft.inventory.Slot
import net.minecraft.item.ItemStack
import net.minecraftforge.items.SlotItemHandler

class ContainerWorkbench(invPlayer: InventoryPlayer, // Stores a reference to the tile entity instance for later use
                         private val tile: TileEntityWorkbench) : Container() {

    private val HOTBAR_SLOT_COUNT = 9
    private val PLAYER_INVENTORY_ROW_COUNT = 3
    private val PLAYER_INVENTORY_COLUMN_COUNT = 9
    private val PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_COLUMN_COUNT * PLAYER_INVENTORY_ROW_COUNT
    private val VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT

    private val VANILLA_FIRST_SLOT_INDEX = 0
    private val TE_INVENTORY_FIRST_SLOT_INDEX = VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT
    private val TE_INVENTORY_SLOT_COUNT = 25 // TODO: un-hardcode this

    init {
//        val inventory = this.tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH)

        addPlayerSlots(invPlayer)

//        if (TE_INVENTORY_SLOT_COUNT != tile.getSizeInventory()) {
//            System.err.println("Mismatched slot count in ContainerWorkbench(" + TE_INVENTORY_SLOT_COUNT +
//                    ") and TileInventory (" + tile.getSizeInventory() + ")")
//        }

        (0 until this.tile.inputItems.slots).forEach {
            addSlotToContainer(SlotItemHandler(this.tile.inputItems, it, 17 + 18 * (it % 3), 17 + 18 * (it / 3)))
        }
        (0 until this.tile.toolItem.slots).forEach {
            addSlotToContainer(SlotItemHandler(this.tile.toolItem, it, 89, 45))
        }
        (0 until this.tile.outputItem.slots).forEach {
            // no big slot yet, so using 1st output
            addSlotToContainer(SlotItemHandler(this.tile.outputItem, it, 125, 17))
        }
        (0 until this.tile.outputItems.slots).forEach {
            // no big slot yet, so using the outputs after the 1st one
            addSlotToContainer(SlotItemHandler(this.tile.outputItems, it, 125 + 18 * ((it + 1) % 2), 17 + 18 * ((it + 1) % 3)))
        }
        (0 until this.tile.extraItems.slots).forEach {
            addSlotToContainer(SlotItemHandler(this.tile.extraItems, it, 8 + 18 * it, 84))
        }

//        for (x in 0 until TE_INVENTORY_SLOT_COUNT) {
//// Add the secondary ingredients to the gui
//            if (x < 9) {
//                addSlotToContainer(Slot(tile, x, 8 + 18 * x, 84))
//            } else
//            // Add the crafting grid to the gui
//                if (x < 18) {
//                    addSlotToContainer(Slot(tile, x, 17 + 18 * ((x - 9) % 3), 17 + 18 * ((x - 9) / 3)))
//                } else
//                // Add the output grid to the gui
//                    if (x < 24) {
//                        //System.out.println(((x-18)%3));
//                        addSlotToContainer(SlotOutput(tile, x, 125 + 18 * ((x - 18) % 2), 17 + 18 * ((x - 18) % 3)))
//                    } else
//                    // Add the tool slot to the gui
//                        if (x == 24) {
//                            addSlotToContainer(Slot(tile, x, 89, 45))
//                        }
//        }
    }

    fun addPlayerSlots(invPlayer: InventoryPlayer){
        // Add the players hotbar to the gui - the [xpos, ypos] location of each item
        for (x in 0 until 9) {
            addSlotToContainer(Slot(invPlayer, x, 8 + 18 * x, 173))
        }

        // Add the rest of the players inventory to the gui
        for (y in 0 until 3) {
            for (x in 0 until 9) {
                val slotNumber = 9 + y * 9 + x
                val xpos = 8 + x * 18
                val ypos = 115 + y * 18
                addSlotToContainer(Slot(invPlayer, slotNumber, xpos, ypos))
            }
        }
    }

    // Vanilla calls this method every tick to make sure the player is still able to access the inventory, and if not closes the gui
    override fun canInteractWith(player: EntityPlayer): Boolean {
        // return tile.isUsableByPlayer(player)
        return true
    }

    override fun transferStackInSlot(player: EntityPlayer?, sourceSlotIndex: Int): ItemStack {
        val sourceSlot = inventorySlots[sourceSlotIndex] as Slot
        if ( !sourceSlot.hasStack) return ItemStack.EMPTY  //EMPTY_ITEM
        val sourceStack = sourceSlot.stack
        val copyOfSourceStack = sourceStack.copy()

        // Check if the slot clicked is one of the vanilla container slots
        if (sourceSlotIndex >= VANILLA_FIRST_SLOT_INDEX && sourceSlotIndex < VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot so merge the stack into the tile inventory
            if (!mergeItemStack(sourceStack, TE_INVENTORY_FIRST_SLOT_INDEX, TE_INVENTORY_FIRST_SLOT_INDEX + 9, false)) {
                return ItemStack.EMPTY  // EMPTY_ITEM
            }
        } else if (sourceSlotIndex >= TE_INVENTORY_FIRST_SLOT_INDEX && sourceSlotIndex < TE_INVENTORY_FIRST_SLOT_INDEX + TE_INVENTORY_SLOT_COUNT) {
            // This is a TE slot so merge the stack into the players inventory
            if (!mergeItemStack(sourceStack, VANILLA_FIRST_SLOT_INDEX, VANILLA_FIRST_SLOT_INDEX + VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY   // EMPTY_ITEM
            }
        } else {
            System.err.print("Invalid slotIndex:" + sourceSlotIndex)
            return ItemStack.EMPTY   // EMPTY_ITEM
        }

        // If stack size == 0 (the entire stack was moved) set slot contents to null
        if (sourceStack.count == 0) {  // getStackSize
            sourceSlot.putStack(ItemStack.EMPTY)  // EMPTY_ITEM
        } else {
            sourceSlot.onSlotChanged()
        }

        sourceSlot.onTake(player, sourceStack)  //onPickupFromSlot()
        return copyOfSourceStack
    }
}
