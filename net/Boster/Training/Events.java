package net.Boster.Training;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {
	
	Main plugin;
	Utils utils = new Utils();

	public Events(Main plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onMove(PlayerMoveEvent e) {
		Player p = e.getPlayer();
		if(plugin.onArena(p)) {
			Arena arena = Arena.getArena(p);
			if(arena.inLocation(p.getLocation(), "region")) {
				if(arena.isTimerStarted() == false) {
					arena.setTimerStarted(true);
					SBoard.scoreboardAddPlayer(p, true);
				}
				return;
			}
			if(p.getLocation().getY() <= arena.voidY()) {
				utils.moneyActions(p, Utils.EconomyAction.DEATH_ACTIONS);
				Utils.preparePlayer(p);
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onManipulate(PlayerArmorStandManipulateEvent e) {
		if(!e.getRightClicked().isVisible()) {
            e.setCancelled(true);
        }
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBreak(BlockBreakEvent e) {
		if(plugin.onArena(e.getPlayer())) {
			if(!Arena.getArena(e.getPlayer()).isPlacedBlock(e.getBlock())) e.setCancelled(true); return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if(plugin.onArena(p)) {
			Arena arena = Arena.getArena(p);
			if(arena.inLocation(e.getBlock().getLocation(), "region") == false) {
				e.setCancelled(true);
				p.sendMessage(utils.toColorAndPrefix(plugin.getConfig().getString("Messages.isNotInRegion")));
			} else if(e.getBlockReplacedState().getType() != Material.AIR) {
				e.setCancelled(true);
				return;
			} else {
				arena.addBlock(e.getBlock());
				return;
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onItemFrameBreak(HangingBreakByEntityEvent e) {
		if(!(e.getRemover() instanceof Player)) return;
		if(plugin.onArena(((Player) e.getRemover()))) {
			if(e.getEntity() instanceof ItemFrame) {e.setCancelled(true);}	
		}
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDamage(EntityDamageEvent e) {
		if(e.getEntity() instanceof Player && plugin.onArena(((Player) e.getEntity()))) {e.setCancelled(true);}
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onCommandProcess(PlayerCommandPreprocessEvent e) {
		Player p = e.getPlayer();
		if(plugin.onArena(p) && !e.getMessage().equalsIgnoreCase("/training leave")) {
			e.setCancelled(true);
			String s = plugin.getConfig().getString("Messages.commandsDenied");
			p.sendMessage(utils.toColorAndPrefix(s));
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onDropItem(PlayerDropItemEvent e) {
		if(plugin.onArena(e.getPlayer())) {e.setCancelled(true);}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void entityInteractEvent(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if(plugin.onArena(p) && e.getRightClicked() instanceof ItemFrame) {e.setCancelled(true);}
    }
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInterract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if(plugin.onArena(p)) {
			if(e.getItem() != null && (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) && e.getItem().hasItemMeta()) {
				if(!e.getItem().getItemMeta().hasDisplayName()) return;
				if(!e.getItem().getItemMeta().hasLore()) return;
				if(e.getItem().equals(Utils.LEAVE)) {
					e.setCancelled(true);
					utils.playerFromArena(p);
					return;
				}
			}
			if(e.getClickedBlock() == null) return;
			if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
				if(e.getClickedBlock().getType() == Material.BED_BLOCK) {
					e.setCancelled(true);
				}
				if(e.getClickedBlock().getType() == Material.BED) {
					e.setCancelled(true);
				}
				if(p.getInventory().getItemInMainHand().getType() == Material.DIAMOND_HOE) {
					e.setCancelled(true);
				}
				if(e.getClickedBlock().getType() == Material.FLOWER_POT || e.getClickedBlock().getType() == Material.ITEM_FRAME || e.getClickedBlock().getType() == Material.WORKBENCH || e.getClickedBlock().getType() == Material.CHEST || e.getClickedBlock().getType() == Material.ENDER_CHEST ||
						e.getClickedBlock().getType() == Material.FURNACE || e.getClickedBlock().getType() == Material.TRAP_DOOR || e.getClickedBlock().getType() == Material.ANVIL) {
					e.setCancelled(true);
				}
			}
			if(e.getClickedBlock().getType() == Material.ITEM_FRAME) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		if(plugin.onArena(p)) {utils.playerFromArena(p);}
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void clickEvent(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if(e.getInventory() == null) return;
		if(e.getClickedInventory() == null) return;
		if(e.getView().getTitle() == null) return;
		
		//Arenas
		if(ArenasMenu.getMenu(p) != null && e.getView().getTitle().equalsIgnoreCase(Utils.toColor(plugin.getConfig().getString("ArenasMenu.Title").replace("%page%", Integer.toString(ArenasMenu.getMenu(p).getPageNumber()))))) {
			if(e.getCursor() == null) return;
			if(e.getCurrentItem() == null) return;
			
			if(e.getClickedInventory().getHolder() instanceof Player) return;
			
			e.setCancelled(true);
			if(e.getCurrentItem().getType() == Material.AIR) return;
			ArenasMenu menu = ArenasMenu.getMenu(p);
			if(e.getSlot() == 53 && e.getCurrentItem().getType() != Material.STAINED_GLASS_PANE) {
				if(menu.getPages() == 1) return;
				if(menu.getPageNumber() < menu.getPages()) {
					menu.newPage();
					menu.open();
					return;
				}
			} else if(e.getSlot() == 45 && e.getCurrentItem().getType() != Material.STAINED_GLASS_PANE) {
				if(menu.getPages() == 1) return;
				if(menu.getPageNumber() > 1) {
					menu.pastPage();
					menu.open();
					return;
				}
			} else if(e.getCurrentItem().getType() == Material.STAINED_GLASS_PANE) {
				return;
			} else if(e.getCurrentItem().getType() == Material.BARRIER) {
				p.closeInventory();
				return;
			} else {
				String arena = plugin.arenas.get(e.getSlot() + menu.getActualArenasFrom() - 9);
				p.playSound(p.getLocation(), Arena.getArena(arena) == null ? Sound.BLOCK_NOTE_CHIME : Sound.ENTITY_GHAST_SCREAM, 10, 1);
				p.performCommand("training join " + arena);
				return;
			}
		}
	}

}
