/*
 * Copyright (C) 2022 SkyWatcher_2019
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package ru.rioworlds.velocitywhitelist;

import net.elytrium.java.commons.config.YamlConfig;

public class Config extends YamlConfig {

  @Ignore
  public static final Config IMP = new Config();

  @Create
  public MESSAGES MESSAGES;

  public static class MESSAGES {

    public String KICK = "You aren't whitelisted";
    public String NO_PERMISSION = "No permission";
    public String USAGE = "/whitelist <add/remove> <player>";
    public String RELOAD = "Whitelist reloaded";
    public String ADDED = "Added {0} to whitelist";
    public String REMOVED = "Removed {0} from whitelist";
    public String ALREADY_WHITELISTED = "{0} already whitelisted";
    public String NOT_WHITELISTED = "{0} isn't in whitelisted";
    public String REMOVED_KICK = "You have been deleted from whitelist!";
  }
}
