/*
 * This file is part of Coins.
 *
 * Copyright © 2017 Beelzebu
 * Coins is licensed under the GNU General Public License.
 *
 * Coins is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Coins is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.nifheim.beelzebu.coins.listener;

import java.util.UUID;
import net.nifheim.beelzebu.coins.Main;
import net.nifheim.beelzebu.coins.utils.gui.BaseGUI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Beelzebu
 */
public class GUIListener implements Listener {

    private final Main plugin;

    public GUIListener(Main main) {
        plugin = main;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) {
            return;
        }
        Player p = (Player) e.getWhoClicked();

        UUID inventoryUUID = BaseGUI.getOpenInventories().get(p.getUniqueId());
        if (inventoryUUID != null) {
            e.setCancelled(true);
            BaseGUI gui = BaseGUI.getInventoriesByUUID().get(inventoryUUID);
            BaseGUI.GUIAction action = gui.getActions().get(e.getSlot());

            if (action != null) {
                action.click(p);
            }
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        Player player = (Player) e.getPlayer();
        UUID playerUUID = player.getUniqueId();

        BaseGUI.getOpenInventories().remove(playerUUID);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        UUID playerUUID = player.getUniqueId();
        BaseGUI.getOpenInventories().remove(playerUUID);
    }
}
