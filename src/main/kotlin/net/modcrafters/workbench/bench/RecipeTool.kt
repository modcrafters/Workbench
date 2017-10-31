package net.modcrafters.workbench.bench

import com.google.gson.JsonObject
import net.minecraft.item.crafting.IRecipe
import net.minecraftforge.common.crafting.IRecipeFactory
import net.minecraftforge.common.crafting.JsonContext
import net.minecraftforge.common.crafting.CraftingHelper.ShapedPrimer
import net.minecraft.util.ResourceLocation
import net.modcrafters.workbench.MOD_ID

import net.minecraftforge.oredict.ShapedOreRecipe
import net.minecraft.item.ItemStack
import net.minecraftforge.common.ForgeHooks
import net.minecraftforge.event.ForgeEventFactory
import net.minecraft.client.Minecraft
import net.minecraft.init.Items
import net.minecraft.util.NonNullList
import net.minecraft.inventory.InventoryCrafting
import net.minecraft.item.crafting.Ingredient
import net.minecraft.world.World
import net.minecraftforge.registries.IForgeRegistryEntry
import net.minecraft.util.JsonUtils
import javax.annotation.Nullable





class RecipeTool : IRecipeFactory {

    override fun parse(context: JsonContext?, json: JsonObject?): IRecipe {
        val recipe = ShapedOreRecipe.factory(context, json)
        val primer = ShapedPrimer()

        primer.width = recipe.width
        primer.height = recipe.height
        primer.mirrored = JsonUtils.getBoolean(json, "mirrored", true)
        primer.input = recipe.getIngredients()


        return ShapedToolRecipe(ResourceLocation(MOD_ID, "shaped_tool_recipe"), recipe.getRecipeOutput(), primer)
    }

    class ShapedToolRecipe(group: ResourceLocation, private val recipeOutput: ItemStack, primer: ShapedPrimer) : IForgeRegistryEntry.Impl<IRecipe>(), IRecipe {
        val recipeItems: List<Ingredient>
        private val recipeWidth = 3
        private val recipeHeight = 3

        /**
         * Returns the size of the recipe area
         */
        val recipeSize: Int
            get() = this.recipeItems.size

        init {
            this.recipeItems = primer.input
        }

        @Nullable
        override fun getRecipeOutput(): ItemStack {
            return this.recipeOutput
        }


        /**
         * Used to check if a recipe matches current crafting inventory
         */
        override fun matches(inv: InventoryCrafting, worldIn: World): Boolean {
            for (i in 0..3 - this.recipeWidth) {
                for (j in 0..3 - this.recipeHeight) {
                    if (this.checkMatch(inv, i, j, true)) {
                        return true
                    }

                    if (this.checkMatch(inv, i, j, false)) {
                        return true
                    }
                }
            }

            return false
        }

        /**
         * Checks if the region of a crafting inventory is match for the recipe.
         */
        private fun checkMatch(p_77573_1_: InventoryCrafting, p_77573_2_: Int, p_77573_3_: Int, p_77573_4_: Boolean): Boolean {
            for (i in 0..2) {
                for (j in 0..2) {
                    val k = i - p_77573_2_
                    val l = j - p_77573_3_
                    var ingredient = Ingredient.EMPTY

                    if (k >= 0 && l >= 0 && k < this.recipeWidth && l < this.recipeHeight) {

                        when(p_77573_4_){
                            true -> ingredient = this.recipeItems[this.recipeWidth - k - 1 + l * this.recipeWidth]
                            false -> ingredient = this.recipeItems[k + l * this.recipeWidth]
                        }
                    }

                    if (!ingredient.apply(p_77573_1_.getStackInRowAndColumn(i, j))) {
                        return false
                    }
                }
            }

            return true
        }

        /**
         * Returns an Item that is the result of this recipe
         */
        @Nullable
        override fun getCraftingResult(inv: InventoryCrafting): ItemStack {
            return this.recipeOutput.copy()
        }

        override fun getRemainingItems(inv: InventoryCrafting): NonNullList<ItemStack> {

            val aitemstack = NonNullList.withSize(inv.sizeInventory, ItemStack.EMPTY)

            for (i in aitemstack.indices) {
                val itemstack = inv.getStackInSlot(i)
                if (!itemstack.isEmpty) {
                    if (itemstack.item === Items.SHEARS) {
                        val shearscopy = itemstack.copy()

                        if (shearscopy.attemptDamageItem(1, Minecraft.getMinecraft().world.rand, null)) {
                            ForgeEventFactory.onPlayerDestroyItem(ForgeHooks.getCraftingPlayer(), itemstack, null)
                            aitemstack[i] = ItemStack.EMPTY
                        } else {
                            aitemstack[i] = shearscopy
                        }
                    }
                } else {
                    aitemstack[i] = itemstack
                }
            }

            return aitemstack
        }

        override fun canFit(width: Int, height: Int): Boolean {
            return width * height >= 1
        }
    }


}
