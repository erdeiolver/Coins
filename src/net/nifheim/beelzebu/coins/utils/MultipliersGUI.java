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
package net.nifheim.beelzebu.coins.utils;

import net.nifheim.beelzebu.coins.CoinsAPI;
import net.nifheim.beelzebu.coins.Main;
import net.nifheim.beelzebu.coins.multiplier.MultiplierType;
import net.nifheim.beelzebu.coins.utils.gui.BaseGUI;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Beelzebu
 */
public class MultipliersGUI extends BaseGUI {

    private final Player p;

    public MultipliersGUI(Player player, String name) {
        super(54, name);
        p = player;
        setItems();
    }

    private void setItems() {
        if (p != null && CoinsAPI.getMultiplier(null).getMultipliersFor(p).size() > 0) {
            for (int i = 0; i < CoinsAPI.getMultiplier(null).getMultipliersFor(p).size() && i < 37; i++) {
                final int k = i;
                CoinsAPI.getMultiplier(null).getMultipliersFor(p).forEach(j -> {
                    ItemStack item = new ItemStack(Material.NETHER_STAR);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("" + j);
                    item.setItemMeta(meta);
                    setItem(k, item, player -> {
                        CoinsAPI.getMultiplier().useMultiplier(player, j, MultiplierType.SERVER);
                        player.sendMessage("Has usado el multiplicador con id: " + j);
                    });
                });
                CoinsAPI.getMultiplier().getMultipliersFor(p).forEach((j) -> {
                    Main.getInstance().log(j);
                });
            }
        }
        for (int i = 36; i < 45; i++) {
            setItem(i, new ItemStack(Material.WOOD_STEP, 1, Short.parseShort("1")));
        }
        setItem(49, new ItemStack(Material.REDSTONE_BLOCK), p -> {
            p.closeInventory();
        });
    }
}
