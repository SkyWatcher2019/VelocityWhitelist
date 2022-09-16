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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import net.elytrium.java.commons.mc.serialization.Serializer;
import net.elytrium.java.commons.mc.serialization.Serializers;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.ComponentSerializer;
import org.slf4j.Logger;
import ru.rioworlds.velocitywhitelist.command.WhitelistCommand;
import ru.rioworlds.velocitywhitelist.listener.JoinListener;

@Plugin(
    id = "velocitywhitelist",
    name = "VelocityWhitelist",
    version = BuildConstants.VERSION
)
public class VelocityWhitelist {

  private static Logger LOGGER;
  private static Serializer SERIALIZER;
  public final Path dataDirectory;
  private final ProxyServer server;
  private final File configFile;
  private final File whitelistFile;
  public HashSet<String> whitelist = new HashSet<>();

  @Inject
  public VelocityWhitelist(Logger logger, ProxyServer server, @DataDirectory Path dataDirectory) {
    setLogger(logger);

    this.server = server;
    this.dataDirectory = dataDirectory;
    this.configFile = this.dataDirectory.resolve("config.yml").toFile();
    this.whitelistFile = this.dataDirectory.resolve("whitelist.json").toFile();
  }

  public static Logger getLogger() {
    return LOGGER;
  }

  private static void setLogger(Logger logger) {
    LOGGER = logger;
  }

  public static Serializer getSerializer() {
    return SERIALIZER;
  }

  private static void setSerializer(Serializer serializer) {
    SERIALIZER = serializer;
  }

  @Subscribe
  public void onProxyInitialization(ProxyInitializeEvent event) {
    Config.IMP.setLogger(LOGGER);
    this.reload();
  }

  public void reload() {
    Config.IMP.reload(this.configFile);
    this.load();
    ComponentSerializer<Component, Component, String> serializer = Serializers.MINIMESSAGE.getSerializer();
    if (serializer == null) {
      LOGGER.warn("The specified serializer could not be founded, using default. (LEGACY_AMPERSAND)");
      setSerializer(new Serializer(Objects.requireNonNull(Serializers.LEGACY_AMPERSAND.getSerializer())));
    } else {
      setSerializer(new Serializer(serializer));
    }

    CommandManager manager = this.server.getCommandManager();
    manager.unregister("whitelist");
    manager.register("whitelist", new WhitelistCommand(this), "wl");

    this.server.getEventManager().unregisterListeners(this);
    this.server.getEventManager().register(this, new JoinListener(this));
  }

  public ProxyServer getServer() {
    return this.server;
  }

  public void load() {
    if (this.whitelistFile.exists()) {
      try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(this.whitelistFile), StandardCharsets.UTF_8)) {
        Type whitelistSetType = new TypeToken<HashSet<String>>() {
        }.getType();
        this.whitelist = new Gson().fromJson(inputStreamReader, whitelistSetType);
      } catch (Exception e) {
        VelocityWhitelist.getLogger().error("Error loading whitelist.json");
        e.printStackTrace();
      }
    }
  }

  public void save() {
    try {
      FileWriter fileWriter = new FileWriter(this.whitelistFile);
      new Gson().toJson(this.whitelist, fileWriter);
      fileWriter.flush();
      fileWriter.close();
    } catch (Exception e) {
      VelocityWhitelist.getLogger().error("Error saving whitelist.json");
      e.printStackTrace();
    }
  }

  public boolean add(String nickname) {
    if (!this.check(nickname)) {
      this.whitelist.add(nickname.toLowerCase(Locale.ROOT));
      this.save();
      return true;
    }
    return false;
  }

  public boolean remove(String nickname) {
    if (this.check(nickname)) {
      this.whitelist.remove(nickname.toLowerCase(Locale.ROOT));
      this.save();
      return true;
    }
    return false;
  }

  public boolean check(String nickname) {
    return this.whitelist.contains(nickname.toLowerCase(Locale.ROOT));
  }
}
