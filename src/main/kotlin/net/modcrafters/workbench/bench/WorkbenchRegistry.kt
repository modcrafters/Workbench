package net.modcrafters.workbench.bench

import net.minecraft.init.Blocks
import net.minecraft.init.Items
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.fml.common.discovery.ASMDataTable
import net.minecraftforge.items.IItemHandler
import net.minecraftforge.registries.IForgeRegistry
import net.ndrei.teslacorelib.annotations.IRegistryHandler
import net.ndrei.teslacorelib.annotations.RegistryHandler
import net.ndrei.teslacorelib.utils.getCombinedInventory

@RegistryHandler
object WorkbenchRegistry: IRegistryHandler {
    private val recipes = mutableListOf<WorkbenchRecipe>()

    fun findRecipe(left: IItemHandler, tool: ItemStack, extra: IItemHandler) =
        this.recipes.firstOrNull {
            val extraCombined = extra.getCombinedInventory()
            (it.tool.isEmpty || it.tool.isItemEqualIgnoreDurability(tool)) &&
                it.input.all { input ->
                    val slot = (input.key.first + input.key.second * 3).coerceIn(0 until left.slots)
                    // todo: test stack size
                    left.getStackInSlot(slot).isItemEqualIgnoreDurability(input.value)
                } &&
                (it.extra.isEmpty() || it.extra.all { extraItem ->
                    extraCombined.any { testee ->
                        // todo: test stack size
                        testee.isItemEqualIgnoreDurability(extraItem)
                    }
                })
        }

    override fun registerRecipes(asm: ASMDataTable, registry: IForgeRegistry<IRecipe>) {
        recipes.add(WorkbenchRecipe(
            // recipe for a wooden pickaxe, using an axe as a tool
            mapOf(
                (0 to 0) to ItemStack(Blocks.PLANKS),
                (1 to 0) to ItemStack(Blocks.PLANKS),
                (2 to 0) to ItemStack(Blocks.PLANKS),
                (1 to 1) to ItemStack(Items.STICK),
                (1 to 2) to ItemStack(Items.STICK)
                ),
            ItemStack(Items.WOODEN_AXE),
            listOf(),
            listOf(ItemStack(Items.WOODEN_PICKAXE) to 1.0f, ItemStack(Items.STICK) to 0.25f)
        ))

        recipes.add(WorkbenchRecipe(
            // recipe for a stone pickaxe, using an axe as a tool, and requiring flint in extra inventory
            mapOf(
                (0 to 0) to ItemStack(Blocks.COBBLESTONE),
                (1 to 0) to ItemStack(Blocks.COBBLESTONE),
                (2 to 0) to ItemStack(Blocks.COBBLESTONE),
                (1 to 1) to ItemStack(Items.STICK),
                (1 to 2) to ItemStack(Items.STICK)
            ),
            ItemStack(Items.WOODEN_AXE),
            listOf(ItemStack(Items.FLINT)),
            listOf(ItemStack(Items.STONE_PICKAXE) to 1.0f, ItemStack(Items.FLINT) to 0.25f)
        ))
    }
}