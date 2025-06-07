package com.foxycraft.invbackup.command;


import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;

public class CommandInit {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        InventoryListCommand.register(dispatcher);
    }
}
