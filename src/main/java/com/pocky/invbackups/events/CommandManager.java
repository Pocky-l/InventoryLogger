package com.pocky.invbackups.events;

import com.mojang.brigadier.CommandDispatcher;
import com.pocky.invbackups.commands.InventoryCommand;
import net.minecraft.commands.CommandSourceStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.bus.api.SubscribeEvent;

public class CommandManager {

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();

        InventoryCommand.register(commandDispatcher);
    }
}
