package com.foxycraft.invbackup.gui;
//
//import com.foxycraft.invbackup.Invbackup;
//import com.foxycraft.invbackup.backup.InventoryBackupManager;
//import com.foxycraft.invbackup.backup.PlayerBackup;
//import net.minecraft.network.chat.Component;
//import net.minecraft.server.level.ServerPlayer;
//import net.minecraft.world.SimpleContainer;
//import net.minecraft.world.SimpleMenuProvider;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.inventory.Slot;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//
//import java.util.List;
//
//public class InventoryBackupMenu extends AbstractContainerMenu {
//
//    private final ServerPlayer player;
//    private final String targetPlayerName;
//    private int currentPage;
//    private String dateFilter;
//
//    private List<PlayerBackup> backups; // your backup list for the player filtered
//
//    public static final int ENTRIES_PER_PAGE = 7;
//
//    public InventoryBackupMenu(int id, Inventory playerInventory, ServerPlayer player, String targetPlayerName, int page, String dateFilter) {
//        super(Invbackup.INV_BACKUP_MENU, id);
//        this.player = player;
//        this.targetPlayerName = targetPlayerName;
//        this.currentPage = page;
//        this.dateFilter = dateFilter;
//
//        // load backups from your backup manager
//        this.backups = InventoryBackupManager.getBackupsForPlayer(targetPlayerName, dateFilter);
//
//        setupSlots();
//    }
//
//    private void setupSlots() {
//        // Clear existing slots
//        this.slots.clear();
//
//        // Add slots for backup entries as fake slots (or use custom slots if you want)
//        int startIndex = currentPage * ENTRIES_PER_PAGE;
//        int endIndex = Math.min(startIndex + ENTRIES_PER_PAGE, backups.size());
//
//        for (int i = startIndex; i < endIndex; i++) {
//            PlayerBackup backup = backups.get(i);
//
//            // Create a slot that shows a dummy item representing backup (e.g. a named paper item)
//            ItemStack displayItem = new ItemStack(Items.PAPER);
//            displayItem.setHoverName(Component.literal("Backup: " + backup.getBackupTime().toString()));
//
//            // Add slot at x,y on GUI (adjust coordinates)
//            this.addSlot(new Slot(new SimpleContainer(1) {{
//                setItem(0, displayItem);
//            }}, 0, 8, 20 + (i - startIndex) * 20) {
//                @Override
//                public boolean mayPickup(Player player) {
//                    return false; // prevent taking
//                }
//            });
//        }
//
//        // Add dummy slots for navigation buttons if needed (or handle buttons via packet clicks)
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//        return player == this.player;
//    }
//
//    @Override
//    public boolean clickMenuButton(Player player, int id) {
//        super.clickMenuButton(player, id);
//        // Use this method if you add buttons that send button IDs
//
//        switch (id) {
//            case 0: // Back button pressed
//                if (currentPage > 0) {
//                    reopenWithPage(currentPage - 1);
//                }
//                break;
//            case 1: // Forward button pressed
//                if ((currentPage + 1) * ENTRIES_PER_PAGE < backups.size()) {
//                    reopenWithPage(currentPage + 1);
//                }
//                break;
//            case 2: // Filter button pressed (implement as needed)
//                break;
//            case 3: // Search button pressed (maybe open chat or send message)
//                break;
//            default:
//                break;
//        }
//        return false;
//    }
//
//    @Override
//    public ItemStack quickMoveStack(Player player, int i) {
//        return null;
//    }
//
//    private void reopenWithPage(int page) {
//        this.currentPage = page;
//        this.backups = InventoryBackupManager.getBackupsForPlayer(targetPlayerName, dateFilter);
//        setupSlots();
//        // reopen container for player with updated page
//        player.closeContainer();
//        player.openMenu(new SimpleMenuProvider(
//                (id, inv, p) -> new InventoryBackupMenu(id, inv, (ServerPlayer) p, targetPlayerName, currentPage, dateFilter),
//                Component.literal("Inventory Backups for " + targetPlayerName)
//        ));
//    }
//}
//
