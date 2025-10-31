package com.pocky.invbackups.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.io.JsonFileHandler;
import com.pocky.invbackups.ui.ChatUI;
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

import java.util.concurrent.atomic.AtomicInteger;

public class InventoryCommand {

    private static final InventoryCommand command = new InventoryCommand();

    public static void register(CommandDispatcher<CommandSourceStack> commandDispatcher) {

        commandDispatcher.register(Commands.literal("inventory")
                .requires(cs -> cs.hasPermission(2))

                // /inventory help
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ChatUI.showHelp(player);
                    return 1;
                })

                // /inventory set <player> <backup>
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .setInventory(context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /inventory view <player> <backup>
                .then(Commands.literal("view")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .view(context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /inventory copy <player> <backup>
                .then(Commands.literal("copy")
                        .then(Commands.argument("target", EntityArgument.player())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .copyInventory(context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /inventory list <player> [filter] [page]
                .then(Commands.literal("list")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(context -> command.list(context.getSource(),
                                        EntityArgument.getPlayer(context, "target"),
                                        "", 1))
                                .then(Commands.argument("filter", StringArgumentType.string())
                                        .executes(context -> command.list(context.getSource(),
                                                EntityArgument.getPlayer(context, "target"),
                                                StringArgumentType.getString(context, "filter"), 1))
                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(context -> command.list(context.getSource(),
                                                        EntityArgument.getPlayer(context, "target"),
                                                        StringArgumentType.getString(context, "filter"),
                                                        IntegerArgumentType.getInteger(context, "page")))))
                        )
                )
        );

    }

    public int setInventory(CommandSourceStack source, ServerPlayer target, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();
        InventoryData invData = JsonFileHandler.load("inventory/" + target.getUUID() + "/", date, InventoryData.class);

        if (invData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        target.getInventory().replaceWith(invData.getInventory(target));
        ChatUI.showSuccess(executor, Component.translatable("invbackups.success.restored",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE),
                Component.literal(target.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        ChatUI.showInfo(target, Component.translatable("invbackups.info.inventory_restored",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        return 1;
    }

    public int copyInventory(CommandSourceStack source, ServerPlayer target, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();
        InventoryData invData = JsonFileHandler.load("inventory/" + target.getUUID() + "/", date, InventoryData.class);

        if (invData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        // Load the backup items into executor's inventory
        Inventory executorInv = executor.getInventory();
        invData.decode(executor.level().registryAccess()).forEach((index, itemStack) -> {
            if (!itemStack.isEmpty()) {
                // Try to add item to inventory, drop if full
                if (!executorInv.add(itemStack.copy())) {
                    executor.drop(itemStack.copy(), false);
                }
            }
        });

        ChatUI.showSuccess(executor, Component.translatable("invbackups.success.copied",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE),
                Component.literal(target.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        return 1;
    }

    public int list(CommandSourceStack source, ServerPlayer target, String filter, int page) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();
        ChatUI.showBackupList(executor, target, filter, page);
        return 1;
    }

    public int view(CommandSourceStack source, ServerPlayer target, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();
        InventoryData invData = JsonFileHandler.load("inventory/" + target.getUUID() + "/", date, InventoryData.class);

        if (invData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        Container chestContainer = new SimpleContainer(54);

        AtomicInteger slotId = new AtomicInteger();
        invData.decode(executor.level().registryAccess()).forEach((i, e) -> {
            chestContainer.setItem(slotId.get(), e);
            slotId.getAndIncrement();
        });

        MenuProvider chestMenuProvider = new SimpleMenuProvider(
                (id, playerInv, playerEntity) -> new ChestFakeMenu(MenuType.GENERIC_9x6, id, playerInv, chestContainer, 6),
                Component.translatable("invbackups.preview.title", target.getScoreboardName(), date)
                        .withStyle(style -> style.withColor(net.minecraft.ChatFormatting.GOLD))
        );

        executor.openMenu(chestMenuProvider);
        ChatUI.showInfo(executor, Component.translatable("invbackups.info.viewing",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());

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
