/**
 * This file is part of Coins
 *
 * Copyright (C) 2017 Beelzebu
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package net.nifheim.beelzebu.coins.bungee;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import net.nifheim.beelzebu.coins.bungee.listener.PluginMessageListener;
import net.nifheim.beelzebu.coins.bungee.utils.Configuration;

import net.nifheim.beelzebu.coins.core.Core;
import net.nifheim.beelzebu.coins.core.executor.ExecutorManager;
import net.nifheim.beelzebu.coins.core.utils.IConfiguration;
import net.nifheim.beelzebu.coins.core.utils.IMethods;

/**
 *
 * @author Beelzebu
 */
public class Main extends Plugin {

    private static Main instance;
    private IConfiguration config;
    private IMethods methods = new BungeeMethods();

    public static Main getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        methods = new BungeeMethods();
        config = new Configuration();
        Core.getInstance().setup(methods);
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PluginMessageListener());
        ProxyServer.getInstance().registerChannel("Coins");
    }

    public void execute(String executorid, ProxiedPlayer p) {
        ExecutorManager ex = new ExecutorManager();
        if (ex.getExecutor(executorid) != null) {
            /*
            if (CoinsAPI.getCoins(p.getName()) >= ex.getExecutor(executorid).getCost()) {
            }
             */
        }
    }

    public IConfiguration getConfiguration() {
        return config;
    }
}
