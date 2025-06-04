package com.foxycraft.invbackup.forge;

import com.foxycraft.invbackup.Invbackup;
import dev.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Invbackup.MOD_ID)
public final class InvbackupForge {
    public InvbackupForge() {
        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(Invbackup.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());

        // Run our common setup.
        Invbackup.init();
    }
}
