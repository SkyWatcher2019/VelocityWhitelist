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

package ru.rioworlds.velocitywhitelist.command;

import com.google.common.collect.ImmutableList;
import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import net.elytrium.java.commons.mc.serialization.Serializer;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.text.Component;
import ru.rioworlds.velocitywhitelist.Config;
import ru.rioworlds.velocitywhitelist.VelocityWhitelist;

public class WhitelistCommand implements SimpleCommand {

  private final VelocityWhitelist plugin;
  private final Serializer serializer;
  private final Component noPermission;
  private final Component usage;
  private final Component reload;
  private final Component kick;
  private final String added;
  private final String removed;
  private final String alreadyWhitelisted;
  private final String notWhitelisted;

  public WhitelistCommand(VelocityWhitelist plugin) {
    this.serializer = VelocityWhitelist.getSerializer();
    this.plugin = plugin;
    this.noPermission = this.serializer.deserialize(Config.IMP.MESSAGES.NO_PERMISSION);
    this.usage = this.serializer.deserialize(Config.IMP.MESSAGES.USAGE);
    this.reload = this.serializer.deserialize(Config.IMP.MESSAGES.RELOAD);
    this.added = Config.IMP.MESSAGES.ADDED;
    this.removed = Config.IMP.MESSAGES.REMOVED;
    this.alreadyWhitelisted = Config.IMP.MESSAGES.ALREADY_WHITELISTED;
    this.notWhitelisted = Config.IMP.MESSAGES.NOT_WHITELISTED;
    this.kick = this.serializer.deserialize(Config.IMP.MESSAGES.REMOVED_KICK);
  }

  @Override
  public List<String> suggest(Invocation invocation) {
    String[] args = invocation.arguments();
    if (args.length == 0) {
      return ImmutableList.of("add", "remove", "reload");
    } else {
      return ImmutableList.of();
    }
  }

  @Override
  public void execute(Invocation invocation) {
    CommandSource source = invocation.source();
    String[] args = invocation.arguments();
    if (!source.hasPermission("whitelist.admin")) {
      source.sendMessage(this.noPermission, MessageType.SYSTEM);
    } else if (args.length == 0) {
      source.sendMessage(this.usage, MessageType.SYSTEM);
    } else {
      String action = args[0].toLowerCase(Locale.ROOT);
      switch (action) {
        case "add":
          if (args.length != 2) {
            source.sendMessage(this.usage, MessageType.SYSTEM);
          } else {
            String message = this.plugin.add(args[1]) ? this.added : this.alreadyWhitelisted;
            source.sendMessage(this.serializer.deserialize(MessageFormat.format(message, args[1])), MessageType.SYSTEM);
          }
          break;
        case "remove":
          if (args.length != 2) {
            source.sendMessage(this.usage, MessageType.SYSTEM);
          } else {
            boolean result = this.plugin.remove(args[1]);
            if (result) {
              this.plugin.getServer().getPlayer(args[1]).ifPresent(player -> player.disconnect(this.kick));
            }
            source.sendMessage(this.serializer.deserialize(MessageFormat.format(result ? this.removed : this.notWhitelisted, args[1])), MessageType.SYSTEM);
          }
          break;
        case "reload":
          this.plugin.reload();
          source.sendMessage(this.reload, MessageType.SYSTEM);
          break;
        default:
          source.sendMessage(this.usage, MessageType.SYSTEM);
          break;
      }
    }
  }
}
