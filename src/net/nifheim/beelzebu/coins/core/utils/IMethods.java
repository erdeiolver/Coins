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
package net.nifheim.beelzebu.coins.core.utils;

import java.io.File;
import java.io.InputStream;
import java.util.UUID;

/**
 *
 * @author Beelzebu
 */
public interface IMethods {
    
    Object getPlugin();
    
    IConfiguration getConfig();
    
    MessagesManager getMessages(String lang);
    
    void runAsync(Runnable rn);
    
    void runAsync(Runnable rn, Long timer);

    void runSync(Runnable rn);

    void executeCommand(String cmd);
    
    void log(Object log);
    
    String getNick(Object player);
    
    String getNick(UUID uuid);
    
    UUID getUUID(Object player);
    
    UUID getUUID(String player);
    
    Object getConsole();
    
    void sendMessage(Object CommandSender, String msg);
    
    File getDataFolder();
    
    InputStream getResource(String filename);
}
