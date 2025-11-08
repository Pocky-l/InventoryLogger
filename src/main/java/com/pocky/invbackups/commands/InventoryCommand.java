package com.pocky.invbackups.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import com.pocky.invbackups.data.InventoryData;
import com.pocky.invbackups.data.EnderChestData;
import com.pocky.invbackups.io.JsonFileHandler;
import com.pocky.invbackups.ui.ChatUI;
import com.pocky.invbackups.utils.PlayerResolver;
import com.pocky.invbackups.utils.EnderChestUtil;
import com.pocky.invbackups.config.InventoryConfig;
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
import java.util.Optional;

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

                // /inventory player <player> - View/edit current inventory
                .then(Commands.literal("player")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .executes(context -> command
                                        .viewCurrentInventory(context.getSource(),
                                                StringArgumentType.getString(context, "target")))))

                // /inventory set <player> <backup>
                .then(Commands.literal("set")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .setInventory(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /inventory view <player> <backup>
                .then(Commands.literal("view")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .view(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /inventory copy <player> <backup>
                .then(Commands.literal("copy")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .copyInventory(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /inventory list <player> [filter] [page]
                .then(Commands.literal("list")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .executes(context -> command.list(context.getSource(),
                                        StringArgumentType.getString(context, "target"),
                                        "", 1))
                                .then(Commands.argument("filter", StringArgumentType.string())
                                        .executes(context -> command.list(context.getSource(),
                                                StringArgumentType.getString(context, "target"),
                                                StringArgumentType.getString(context, "filter"), 1))
                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(context -> command.list(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "filter"),
                                                        IntegerArgumentType.getInteger(context, "page")))))
                        )
                )
        );

        // Ender Chest commands
        commandDispatcher.register(Commands.literal("enderchest")
                .requires(cs -> cs.hasPermission(2))

                // /enderchest help
                .executes(context -> {
                    ServerPlayer player = context.getSource().getPlayerOrException();
                    ChatUI.showEnderChestHelp(player);
                    return 1;
                })

                // /enderchest player <player> - View/edit current ender chest
                .then(Commands.literal("player")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .executes(context -> command
                                        .viewCurrentEnderChest(context.getSource(),
                                                StringArgumentType.getString(context, "target")))))

                // /enderchest set <player> <backup>
                .then(Commands.literal("set")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .setEnderChest(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /enderchest view <player> <backup>
                .then(Commands.literal("view")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .viewEnderChest(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /enderchest copy <player> <backup>
                .then(Commands.literal("copy")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .then(Commands.argument("date", StringArgumentType.string())
                                        .executes(context -> command
                                                .copyEnderChest(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "date"))))))

                // /enderchest list <player> [filter] [page]
                .then(Commands.literal("list")
                        .then(Commands.argument("target", StringArgumentType.string())
                                .executes(context -> command.listEnderChest(context.getSource(),
                                        StringArgumentType.getString(context, "target"),
                                        "", 1))
                                .then(Commands.argument("filter", StringArgumentType.string())
                                        .executes(context -> command.listEnderChest(context.getSource(),
                                                StringArgumentType.getString(context, "target"),
                                                StringArgumentType.getString(context, "filter"), 1))
                                        .then(Commands.argument("page", IntegerArgumentType.integer(1))
                                                .executes(context -> command.listEnderChest(context.getSource(),
                                                        StringArgumentType.getString(context, "target"),
                                                        StringArgumentType.getString(context, "filter"),
                                                        IntegerArgumentType.getInteger(context, "page")))))
                        )
                )
        );

    }

    public int setInventory(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        // Resolve player (online or offline)
        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        InventoryData invData = JsonFileHandler.load("inventory/" + resolved.getUuid() + "/", date, InventoryData.class);

        if (invData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        // Only restore if player is online
        if (!resolved.isOnline()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_offline_cannot_restore", resolved.getName()).getString());
            return 0;
        }

        ServerPlayer target = resolved.getOnlinePlayer();
        target.getInventory().replaceWith(invData.getInventory(target));
        ChatUI.showSuccess(executor, Component.translatable("invbackups.success.restored",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE),
                Component.literal(target.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        ChatUI.showInfo(target, Component.translatable("invbackups.info.inventory_restored",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        return 1;
    }

    public int copyInventory(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        // Resolve player (online or offline)
        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        InventoryData invData = JsonFileHandler.load("inventory/" + resolved.getUuid() + "/", date, InventoryData.class);

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
                Component.literal(resolved.getName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        return 1;
    }

    public int list(CommandSourceStack source, String targetName, String filter, int page) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        // Resolve player (online or offline)
        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        ChatUI.showBackupList(executor, resolved.getUuid(), resolved.getName(), filter, page);
        return 1;
    }

    public int view(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        // Resolve player (online or offline)
        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        InventoryData invData = JsonFileHandler.load("inventory/" + resolved.getUuid() + "/", date, InventoryData.class);

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
                Component.translatable("invbackups.preview.title", resolved.getName(), date)
                        .withStyle(style -> style.withColor(net.minecraft.ChatFormatting.GOLD))
        );

        executor.openMenu(chestMenuProvider);
        ChatUI.showInfo(executor, Component.translatable("invbackups.info.viewing",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());

        return 1;
    }

    /**
     * View and edit current inventory of a player (online only)
     */
    public int viewCurrentInventory(CommandSourceStack source, String targetName) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        // Resolve player (online or offline)
        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();

        // Only works for online players
        if (!resolved.isOnline()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_offline_cannot_view", resolved.getName()).getString());
            return 0;
        }

        ServerPlayer target = resolved.getOnlinePlayer();

        // Create an editable chest menu with player's inventory
        Container playerInventory = new SimpleContainer(54);

        // Copy player's inventory to the chest
        AtomicInteger slotId = new AtomicInteger();

        // Main inventory (0-35)
        for (int i = 0; i < target.getInventory().items.size() && slotId.get() < 36; i++) {
            playerInventory.setItem(slotId.getAndIncrement(), target.getInventory().items.get(i).copy());
        }

        // Armor slots (36-39)
        for (int i = 0; i < 4; i++) {
            playerInventory.setItem(slotId.getAndIncrement(), target.getInventory().getArmor(i).copy());
        }

        // Offhand (40)
        playerInventory.setItem(slotId.getAndIncrement(), target.getInventory().offhand.get(0).copy());

        MenuProvider chestMenuProvider = new SimpleMenuProvider(
                (id, playerInv, playerEntity) -> new ChestEditableMenu(
                        MenuType.GENERIC_9x6, id, playerInv, playerInventory, 6, target),
                Component.translatable("invbackups.player.title", target.getScoreboardName())
                        .withStyle(style -> style.withColor(net.minecraft.ChatFormatting.AQUA))
        );

        executor.openMenu(chestMenuProvider);
        ChatUI.showInfo(executor, Component.translatable("invbackups.info.viewing_player",
                Component.literal(target.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());

        return 1;
    }

    // ==================== ENDER CHEST COMMANDS ====================

    public int setEnderChest(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        if (!InventoryConfig.general.enderChestEnabled.get()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.enderchest_disabled").getString());
            return 0;
        }

        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        EnderChestData ecData = JsonFileHandler.load("enderchest/" + resolved.getUuid() + "/", date, EnderChestData.class);

        if (ecData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        if (!resolved.isOnline()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_offline_cannot_restore", resolved.getName()).getString());
            return 0;
        }

        ServerPlayer target = resolved.getOnlinePlayer();
        EnderChestUtil.restoreEnderChest(target, ecData.decode(executor.level().registryAccess()));
        ChatUI.showSuccess(executor, Component.translatable("invbackups.success.enderchest_restored",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE),
                Component.literal(target.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        ChatUI.showInfo(target, Component.translatable("invbackups.info.enderchest_restored",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        return 1;
    }

    public int copyEnderChest(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        if (!InventoryConfig.general.enderChestEnabled.get()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.enderchest_disabled").getString());
            return 0;
        }

        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        EnderChestData ecData = JsonFileHandler.load("enderchest/" + resolved.getUuid() + "/", date, EnderChestData.class);

        if (ecData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        Inventory executorInv = executor.getInventory();
        ecData.decode(executor.level().registryAccess()).forEach((index, itemStack) -> {
            if (!itemStack.isEmpty()) {
                if (!executorInv.add(itemStack.copy())) {
                    executor.drop(itemStack.copy(), false);
                }
            }
        });

        ChatUI.showSuccess(executor, Component.translatable("invbackups.success.enderchest_copied",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE),
                Component.literal(resolved.getName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
        return 1;
    }

    public int listEnderChest(CommandSourceStack source, String targetName, String filter, int page) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        if (!InventoryConfig.general.enderChestEnabled.get()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.enderchest_disabled").getString());
            return 0;
        }

        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        ChatUI.showEnderChestBackupList(executor, resolved.getUuid(), resolved.getName(), filter, page);
        return 1;
    }

    public int viewEnderChest(CommandSourceStack source, String targetName, String date) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        if (!InventoryConfig.general.enderChestEnabled.get()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.enderchest_disabled").getString());
            return 0;
        }

        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();
        EnderChestData ecData = JsonFileHandler.load("enderchest/" + resolved.getUuid() + "/", date, EnderChestData.class);

        if (ecData == null) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.backup_not_found", date).getString());
            return 0;
        }

        Container chestContainer = ecData.toContainer(executor.level().registryAccess());

        MenuProvider chestMenuProvider = new SimpleMenuProvider(
                (id, playerInv, playerEntity) -> new ChestFakeMenu(MenuType.GENERIC_9x3, id, playerInv, chestContainer, 3),
                Component.translatable("invbackups.enderchest.preview.title", resolved.getName(), date)
                        .withStyle(style -> style.withColor(net.minecraft.ChatFormatting.DARK_PURPLE))
        );

        executor.openMenu(chestMenuProvider);
        ChatUI.showInfo(executor, Component.translatable("invbackups.info.viewing_enderchest",
                Component.literal(date).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());

        return 1;
    }

    public int viewCurrentEnderChest(CommandSourceStack source, String targetName) throws CommandSyntaxException {
        ServerPlayer executor = source.getPlayerOrException();

        if (!InventoryConfig.general.enderChestEnabled.get()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.enderchest_disabled").getString());
            return 0;
        }

        Optional<PlayerResolver.ResolvedPlayer> resolvedOpt = PlayerResolver.resolvePlayer(
                source.getServer(), targetName);

        if (resolvedOpt.isEmpty()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_not_found", targetName).getString());
            return 0;
        }

        PlayerResolver.ResolvedPlayer resolved = resolvedOpt.get();

        if (!resolved.isOnline()) {
            ChatUI.showError(executor, Component.translatable("invbackups.error.player_offline_cannot_view", resolved.getName()).getString());
            return 0;
        }

        ServerPlayer target = resolved.getOnlinePlayer();
        SimpleContainer targetEnderChest = target.getEnderChestInventory();

        // Create a copy for editing
        SimpleContainer editableEnderChest = new SimpleContainer(27);
        for (int i = 0; i < 27; i++) {
            editableEnderChest.setItem(i, targetEnderChest.getItem(i).copy());
        }

        MenuProvider chestMenuProvider = new SimpleMenuProvider(
                (id, playerInv, playerEntity) -> new EnderChestEditableMenu(
                        MenuType.GENERIC_9x3, id, playerInv, editableEnderChest, 3, target),
                Component.translatable("invbackups.enderchest.player.title", target.getScoreboardName())
                        .withStyle(style -> style.withColor(net.minecraft.ChatFormatting.DARK_PURPLE))
        );

        executor.openMenu(chestMenuProvider);
        ChatUI.showInfo(executor, Component.translatable("invbackups.info.viewing_enderchest_player",
                Component.literal(target.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());

        return 1;
    }

    /**
     * Editable ender chest menu that syncs changes back to player's ender chest
     */
    private static class EnderChestEditableMenu extends ChestMenu {
        private final ServerPlayer targetPlayer;

        public EnderChestEditableMenu(MenuType<?> menuType, int containerId, Inventory playerInv,
                                     Container container, int rows, ServerPlayer targetPlayer) {
            super(menuType, containerId, playerInv, container, rows);
            this.targetPlayer = targetPlayer;
        }

        @Override
        public void removed(Player player) {
            super.removed(player);

            if (targetPlayer != null && !targetPlayer.isRemoved()) {
                SimpleContainer targetEnderChest = targetPlayer.getEnderChestInventory();

                // Sync all items
                for (int i = 0; i < 27; i++) {
                    targetEnderChest.setItem(i, this.getContainer().getItem(i).copy());
                }

                ChatUI.showSuccess((ServerPlayer) player, Component.translatable("invbackups.success.enderchest_updated",
                        Component.literal(targetPlayer.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return targetPlayer != null && !targetPlayer.isRemoved();
        }
    }

    /**
     * Editable chest menu that syncs changes back to player's inventory
     */
    private static class ChestEditableMenu extends ChestMenu {
        private final ServerPlayer targetPlayer;

        public ChestEditableMenu(MenuType<?> menuType, int containerId, Inventory playerInv,
                                Container container, int rows, ServerPlayer targetPlayer) {
            super(menuType, containerId, playerInv, container, rows);
            this.targetPlayer = targetPlayer;
        }

        @Override
        public void removed(Player player) {
            super.removed(player);

            // When menu is closed, sync the changes back to the target player
            if (targetPlayer != null && !targetPlayer.isRemoved()) {
                Inventory targetInv = targetPlayer.getInventory();

                // Sync main inventory (slots 0-35)
                for (int i = 0; i < 36; i++) {
                    targetInv.items.set(i, this.getContainer().getItem(i).copy());
                }

                // Sync armor (slots 36-39)
                for (int i = 0; i < 4; i++) {
                    targetInv.armor.set(i, this.getContainer().getItem(36 + i).copy());
                }

                // Sync offhand (slot 40)
                targetInv.offhand.set(0, this.getContainer().getItem(40).copy());

                // Mark inventory as changed
                targetPlayer.inventoryMenu.broadcastChanges();

                ChatUI.showSuccess((ServerPlayer) player, Component.translatable("invbackups.success.player_inventory_updated",
                        Component.literal(targetPlayer.getScoreboardName()).withStyle(net.minecraft.ChatFormatting.WHITE)).getString());
            }
        }

        @Override
        public boolean stillValid(Player player) {
            return targetPlayer != null && !targetPlayer.isRemoved();
        }
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
