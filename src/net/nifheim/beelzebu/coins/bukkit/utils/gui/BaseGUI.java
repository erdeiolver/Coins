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
package net.nifheim.beelzebu.coins.bukkit.utils.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Beelzebu
 */
public abstract class BaseGUI {

    private final Inventory inv;
    private final Map<Integer, GUIAction> actions;
    private static final Map<UUID, BaseGUI> inventoriesByUUID = new HashMap<>();
    private static final Map<UUID, UUID> openInventories = new HashMap<>();
    private final UUID uuid;

    public BaseGUI(int size, String name) {
        inv = Bukkit.createInventory(null, size, name);
        actions = new HashMap<>();
        uuid = UUID.randomUUID();
        inventoriesByUUID.put(getUUID(), this);
    }

    public Inventory getInv() {
        return inv;
    }

    public interface GUIAction {

        void click(Player p);
    }

    public void setItem(int slot, ItemStack is, GUIAction action) {
        inv.setItem(slot, is);
        if (action != null) {
            actions.put(slot, action);
        }
    }

    public void setItem(int slot, ItemStack is) {
        setItem(slot, is, null);
    }

    public void open(Player p) {
        p.openInventory(inv);
        openInventories.put(p.getUniqueId(), getUUID());
    }

    private UUID getUUID() {
        return uuid;
    }

    public static Map<UUID, BaseGUI> getInventoriesByUUID() {
        return inventoriesByUUID;
    }

    public static Map<UUID, UUID> getOpenInventories() {
        return openInventories;
    }

    public Map<Integer, GUIAction> getActions() {
        return actions;
    }

    public void delete() {
        Bukkit.getOnlinePlayers().forEach((p) -> {
            UUID u = openInventories.get(p.getUniqueId());
            if (u.equals(getUUID())) {
                p.closeInventory();
            }
        });
        inventoriesByUUID.remove(getUUID());
    }
}
