package com.pocky.inv.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.item.Items;

import java.io.File;
import java.util.concurrent.atomic.AtomicInteger;

public class InventoryCommand {

    private static final InventoryCommand command = new InventoryCommand();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("inventory")
                .requires(cs -> cs.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .setInventory(context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                .then(Commands.literal("view")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .view(context.getSource(),
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

    public int setInventory(CommandSourceStack source, ServerPlayer target, String date) throws CommandSyntaxException {

        InventoryData invData = JsonFileHandler.load("inventory/" + target.getUUID() + "/", date, InventoryData.class);

        if (invData == null) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cOops! File not found or was corrupted."),
                    false
            );
            return 0;
        }

        target.getInventory().replaceWith(invData.getInventory(target));
        target.displayClientMessage(
                Component.literal("§aSuccess! Your inventory has been replaced with " + date),
                false
        );
        return 1;
    }

    public int list(CommandSourceStack source, ServerPlayer target, String approximateName) throws CommandSyntaxException {

        File folder = new File("InventoryLog/inventory/" + target.getUUID() + "/");
        File[] listOfFiles = folder.listFiles();


        source.getPlayerOrException()
                .displayClientMessage(Component.literal("§aHere is your list of files:"), false);

        boolean isFound = false;

        for (File file : listOfFiles) {
            if (file.isFile() && file.getName().startsWith(approximateName)) {
                isFound = true;
                source.getPlayerOrException()
                        .displayClientMessage(Component.literal("§3" + file.getName()
                                .replace(".json", "")), false);
            }
        }

        if (!isFound) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cOops! No file was found."),
                    false
            );
        }

        return 1;
    }

    public int view(CommandSourceStack source, ServerPlayer target, String date) throws CommandSyntaxException {

        InventoryData invData = JsonFileHandler.load("inventory/" + target.getUUID() + "/", date, InventoryData.class);

        ServerPlayer player = source.getPlayer();
        if (player == null) return 0;

        if (invData == null) {
            source.getPlayerOrException().displayClientMessage(
                    Component.literal("§cOops! File not found or was corrupted."),
                    false
            );
            return 0;
        }

        Container chestContainer = new SimpleContainer(54);

        AtomicInteger slotId = new AtomicInteger();
        invData.decode().forEach((i, e) -> {
            chestContainer.setItem(slotId.get(), e);
            slotId.getAndIncrement();
        });

        MenuProvider chestMenuProvider = new SimpleMenuProvider(
                (id, playerInv, playerEntity) -> new ChestFakeMenu(MenuType.GENERIC_9x6, id, playerInv, chestContainer, 6),
                Component.literal("View inventory")
        );

        player.openMenu(chestMenuProvider);

        return 1;
    }

    private static class ChestFakeMenu extends ChestMenu {

        public ChestFakeMenu(MenuType<?> p_39229_, int p_39230_, Inventory p_39231_, Container p_39232_, int p_39233_) {
            super(p_39229_, p_39230_, p_39231_, p_39232_, p_39233_);
        }

        private ChestFakeMenu(MenuType<?> p_39224_, int p_39225_, Inventory p_39226_, int p_39227_) {
            this(p_39224_, p_39225_, p_39226_, new SimpleContainer(9 * p_39227_), p_39227_);
        }

        @Override
        public boolean stillValid(Player player) {
            return true;  // Игрок может продолжать смотреть инвентарь
        }

        @Override
        public ItemStack quickMoveStack(Player player, int index) {
            return ItemStack.EMPTY;  // Отключаем возможность перемещения предметов shift-кликом
        }

        @Override
        protected Slot addSlot(Slot slot) {
            // Делаем все слоты только для просмотра (запрещаем перемещение)
            return super.addSlot(new Slot(slot.container, slot.getSlotIndex(), slot.x, slot.y) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return false;  // Запрещаем класть предметы
                }

                @Override
                public boolean mayPickup(Player player) {
                    return false;  // Запрещаем забирать предметы
                }
            });
        }
    }
}
