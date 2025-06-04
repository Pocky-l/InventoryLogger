package com.foxycraft.invbackup.neoforge;

import com.foxycraft.invbackup.Invbackup;
import net.neoforged.fml.common.Mod;

@Mod(Invbackup.MOD_ID)
public final class InvbackupNeoForge {
    public InvbackupNeoForge() {
        // Run our common setup.
        Invbackup.init();
    }
}
