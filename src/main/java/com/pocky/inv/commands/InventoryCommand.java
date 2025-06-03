package com.pocky.inv.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.pocky.inv.utils.Suggestors;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import com.pocky.inv.data.InventoryData;
import com.pocky.inv.io.JsonFileHandler;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.io.File;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static com.pocky.inv.utils.Suggestors.PLAYER_SUGGESTIONS;

public class InventoryCommand {

    private static final InventoryCommand command = new InventoryCommand();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {
        commandDispatcher.register(Commands.literal("inventory")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", StringArgumentType.word()).suggests(PLAYER_SUGGESTIONS)
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command.setInventory(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "target"),
                                                StringArgumentType.getString(context, "date"))))))
                .then(Commands.literal("view")
                        .then(Commands.argument("target", StringArgumentType.word()).suggests(PLAYER_SUGGESTIONS)
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command.view(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "target"),
                                                StringArgumentType.getString(context, "date"))))))
                .then(Commands.literal("list")
                        .then(Commands.argument("target", StringArgumentType.word()).suggests(PLAYER_SUGGESTIONS)
                                .executes(context -> command.list(
                                        context.getSource(),
                                        StringArgumentType.getString(context, "target"),
                                        "")))
                        .then(Commands.argument("target", StringArgumentType.word()).suggests(PLAYER_SUGGESTIONS)
                                .then(Commands.argument("yyyy-MM-dd-HH-mm", StringArgumentType.string())
                                        .executes(context -> command.list(
                                                context.getSource(),
                                                StringArgumentType.getString(context, "target"),
                                                StringArgumentType.getString(context, "yyyy-MM-dd-HH-mm"))))))
        );
    }

    public int setInventory(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        UUID uuid = resolveUUID(source.getServer(), targetName);
        if (uuid == null) {
            source.sendFailure(Component.literal("§cPlayer not found."));
            return 0;
        }

        ServerPlayer target = source.getServer().getPlayerList().getPlayer(uuid);
        if (target == null) {
            source.sendFailure(Component.literal("§cPlayer must be online to set inventory."));
            return 0;
        }

        InventoryData invData = JsonFileHandler.load("inventory/" + uuid + "/", date, InventoryData.class);
        if (invData == null) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cOops! File not found or was corrupted."), false);
            return 0;
        }

        target.getInventory().replaceWith(invData.getInventory(target));
        target.displayClientMessage(
                Component.literal("§aSuccess! Your inventory has been replaced with " + date), false);
        return 1;
    }

    public int list(CommandSourceStack source, String targetName, String approximateName) throws CommandSyntaxException {
        UUID uuid = resolveUUID(source.getServer(), targetName);
        if (uuid == null) {
            source.sendFailure(Component.literal("§cPlayer not found."));
            return 0;
        }

        File folder = new File("InventoryLog/inventory/" + uuid + "/");
        File[] files = folder.listFiles();

        if (files == null || files.length == 0) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cNo saved inventories found."), false);
            return 0;
        }

        Arrays.sort(files, Comparator.comparing(File::getName));

        boolean foundAny = false;
        source.getPlayerOrException().displayClientMessage(
                Component.literal("§aHere is your list of files:"), false);

        for (File file : files) {
            if (!file.isFile() || !file.getName().startsWith(approximateName)) continue;
            String nameWithoutExtension = file.getName().replace(".json", "");

            MutableComponent fileComponent = Component.literal("§3" + nameWithoutExtension + " ");
            MutableComponent viewButton = Component.literal("§6[View]").withStyle(Style.EMPTY
                    .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                            "/inventory view " + targetName + " " + nameWithoutExtension))
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                            Component.literal("§7Click to view this inventory"))));

            source.getPlayerOrException().displayClientMessage(fileComponent.append(viewButton), false);
            foundAny = true;
        }

        if (!foundAny) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cOops! No file was found."), false);
        }

        return 1;
    }

    public int view(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        UUID uuid = resolveUUID(source.getServer(), targetName);
        if (uuid == null) {
            source.sendFailure(Component.literal("§cPlayer not found."));
            return 0;
        }

        InventoryData invData = JsonFileHandler.load("inventory/" + uuid + "/", date, InventoryData.class);
        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        if (invData == null) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cOops! File not found or was corrupted."), false);
            return 0;
        }

        Container chestContainer = new SimpleContainer(54);
        AtomicInteger slotId = new AtomicInteger();

        invData.decode().forEach((i, itemStack) -> {
            chestContainer.setItem(slotId.getAndIncrement(), itemStack);
        });

        MenuProvider chestMenuProvider = new SimpleMenuProvider(
                (id, playerInv, playerEntity) -> new ChestFakeMenu(MenuType.GENERIC_9x6, id, playerInv, chestContainer, 6),
                Component.literal("View inventory"));

        player.openMenu(chestMenuProvider);
        return 1;
    }

    private static UUID resolveUUID(MinecraftServer server, String name) {
        Optional<GameProfile> profile = server.getProfileCache().get(name);
        return profile.map(GameProfile::getId).orElse(null);
    }

    private static class ChestFakeMenu extends ChestMenu {
        public ChestFakeMenu(MenuType<?> menuType, int containerId, Inventory playerInventory, Container container, int rows) {
            super(menuType, containerId, playerInventory, container, rows);
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;
        }

        @Override
        protected Slot addSlot(Slot slot) {
            return super.addSlot(new Slot(slot.container, slot.getSlotIndex(), slot.x, slot.y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;
                }

                @Override
                public boolean mayPickup(Player player) {
                    return false;
                }
            });
        }
    }

}
