package net.modcrafters.workbench

import net.minecraft.block.Block
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.item.EnumDyeColor
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.registry.GameRegistry
import net.modcrafters.workbench.common.BlockWorkbench
import net.modcrafters.workbench.common.CommonProxy
import net.modcrafters.workbench.common.ItemBlockWorkbench
import net.modcrafters.workbench.common.TileEntityWorkbench
import org.apache.logging.log4j.Logger

@Mod.EventBusSubscriber
@Mod(modid = Workbench.MODID, version = Workbench.VERSION, useMetadata = true,
    name = Workbench.NAME,
    dependencies = "required-after:forgelin@[1.5.1,)",
    modLanguage = "kotlin", modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter")
object Workbench {
    const val MODID = "workbench"
    const val NAME = "Workbench"
    const val VERSION = "1.0"

    @SidedProxy(clientSide = "net.modcrafters.workbench.client.ClientProxy", serverSide = "net.modcrafters.workbench.server.ServerProxy")
    lateinit var proxy: CommonProxy

    lateinit var logger: Logger

    @JvmStatic
    val BENCH_TAB: CreativeTabs = object : CreativeTabs(MODID) {
        override fun getTabIconItem() = ItemStack(BLOCK_BENCH)
    }

    @JvmStatic
    val BLOCK_BENCH: BlockWorkbench = BlockWorkbench()

    @Mod.EventHandler
    fun preInit(event: FMLPreInitializationEvent) {
        this.logger = event.modLog

        this.logger.error("Hello from Kotlin!")
        this.setupBlock(BLOCK_BENCH, "workbench")

//        NetworkRegistry.INSTANCE.registerGuiHandler(Workbench, GuiHandler())
        this.proxy.registerMessageHandlers()
    }

    @SubscribeEvent
    @JvmStatic
    fun registerBlocks(ev: RegistryEvent.Register<Block>) {
        ev.registry.register(BLOCK_BENCH)
        GameRegistry.registerTileEntity(TileEntityWorkbench::class.java, BLOCK_BENCH.registryName!!.toString())
    }

    @SubscribeEvent
    @JvmStatic
    fun registerItems(ev: RegistryEvent.Register<Item>) {
        val itemBlock = ItemBlockWorkbench(BLOCK_BENCH)
        itemBlock.registryName = BLOCK_BENCH.registryName
        itemBlock.unlocalizedName = BLOCK_BENCH.registryName!!.resourceDomain + "." + BLOCK_BENCH.registryName!!.resourcePath
        ev.registry.register(itemBlock)
    }

    @SubscribeEvent
    @JvmStatic
    fun registerModels(ev: ModelRegistryEvent) {
        for (i in 0..15) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(BLOCK_BENCH), i,
                ModelResourceLocation(ResourceLocation(MODID, "workbench"), "color=" + EnumDyeColor.byMetadata(i).getName()))
        }
    }

    private fun setupBlock(block: Block, name: String): Block {
        block.setRegistryName(name)
        block.unlocalizedName = block.registryName?.resourceDomain + "." + name
//        ForgeRegistries.BLOCKS.register(block)
//        val itemBlock = ItemBlockWorkbench(block)
//        itemBlock.setRegistryName(name)
//        itemBlock.unlocalizedName = block.registryName?.resourceDomain + "." + name
//        ForgeRegistries.ITEMS.register(itemBlock)

        block.setCreativeTab(BENCH_TAB)
        return block
    }
}
