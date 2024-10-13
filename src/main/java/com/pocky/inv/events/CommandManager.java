package com.pocky.inv.events;

import com.mojang.brigadier.CommandDispatcher;
import com.pocky.inv.commands.InventoryCommand;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CommandManager {

    @SubscribeEvent
    public void registerCommands(RegisterCommandsEvent event) {
        final CommandDispatcher<CommandSourceStack> commandDispatcher = event.getDispatcher();

        InventoryCommand.register(commandDispatcher);
    }
}
