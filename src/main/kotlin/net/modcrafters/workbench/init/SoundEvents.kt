package net.modcrafters.workbench.init

import net.minecraft.util.ResourceLocation
import net.minecraft.util.SoundEvent
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.modcrafters.workbench.Workbench

//Setting up for sounds later
@EventBusSubscriber
object SoundEvents {
    val TOOL_SCRAPE = simply("tool_scrape")

    private fun simply(name: String): SoundEvent {
        val resourceLocation = ResourceLocation(Workbench.MODID, name)
        return SoundEvent(resourceLocation).setRegistryName(resourceLocation)
    }

    @JvmStatic
    @SubscribeEvent
    fun register(event: RegistryEvent.Register<SoundEvent>) {
        val registry = event.registry
        registry.register(TOOL_SCRAPE)
    }
}
