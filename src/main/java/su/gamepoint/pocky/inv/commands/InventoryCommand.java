package su.gamepoint.pocky.inv.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import su.gamepoint.pocky.inv.data.InventoryData;
import su.gamepoint.pocky.inv.io.JsonFileHandler;

import java.io.File;

public class InventoryCommand {

    private static final InventoryCommand command = new InventoryCommand();

    public static void register(CommandDispatcher<CommandSource> commandDispatcher) {

        commandDispatcher.register(Commands.literal("inventory")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .setInventory(context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))
                .then(Commands.literal("list")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> command.list(context.getSource(),
                                        EntityArgument.getPlayer(context, "target"),
                                        ""))
                        )
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("yyyy-MM-dd-HH-mm", StringArgumentType.string())
                                        .executes(context -> command.list(context.getSource(),
                                                EntityArgument.getPlayer(context, "target"),
                                                StringArgumentType.getString(context, "yyyy-MM-dd-HH-mm")))
                                )
                        )
                )
        );

    }

    public int setInventory(CommandSource source, ServerPlayerEntity target, String date) throws CommandSyntaxException {

        InventoryData invData = JsonFileHandler.load("inventory/" + target.getUUID() + "/", date, InventoryData.class);

        if (invData == null) {
            source.getPlayerOrException().displayClientMessage(
                    new StringTextComponent("§cOops! File not found or was corrupted."),
                    false
            );
            return 0;
        }

        target.inventory.replaceWith(invData.getInventory(target));
        target.displayClientMessage(
                new StringTextComponent("§aSuccess! Your inventory has been replaced with " + date).setStyle(Style.EMPTY),
                false
        );
        return 1;
    }

    public int list(CommandSource source, ServerPlayerEntity target, String approximateName) throws CommandSyntaxException {

        File folder = new File("InventoryLog/inventory/" + target.getUUID() + "/");
        File[] listOfFiles = folder.listFiles();


        source.getPlayerOrException()
                .displayClientMessage(new StringTextComponent("§aHere is your list of files:"), false);

        boolean isFound = false;

        assert listOfFiles != null;
        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(approximateName)) {
                isFound = true;
                source.getPlayerOrException()
                        .displayClientMessage(new StringTextComponent("§3" + file.getName()
                                .replace(".json", "")), false);
            }
        }

        if (!isFound) {
            source.getPlayerOrException().displayClientMessage(
                    new StringTextComponent("§cOops! No file was found."),
                    false
            );
        }

        return 1;
    }
}
