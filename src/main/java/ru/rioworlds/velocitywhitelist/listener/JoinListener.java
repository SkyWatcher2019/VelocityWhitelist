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

package ru.rioworlds.velocitywhitelist.listener;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PreLoginEvent;
import com.velocitypowered.api.event.connection.PreLoginEvent.PreLoginComponentResult;
import java.util.Locale;
import ru.rioworlds.velocitywhitelist.Config;
import ru.rioworlds.velocitywhitelist.VelocityWhitelist;

public class JoinListener {

  private final VelocityWhitelist plugin;
  private final PreLoginComponentResult result;

  public JoinListener(VelocityWhitelist plugin) {
    this.plugin = plugin;
    this.result = PreLoginComponentResult.denied(VelocityWhitelist.getSerializer().deserialize(Config.IMP.MESSAGES.KICK));
  }

  @Subscribe
  public void onPlayerJoin(PreLoginEvent event) {
    if (!this.plugin.whitelist.contains(event.getUsername().toLowerCase(Locale.ROOT))) {
      event.setResult(this.result);
    }
  }
}
