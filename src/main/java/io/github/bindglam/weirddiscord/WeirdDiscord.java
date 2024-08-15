package io.github.bindglam.weirddiscord;

import dev.jorel.commandapi.CommandAPI;
import dev.jorel.commandapi.CommandAPIBukkitConfig;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.OfflinePlayerArgument;
import io.github.bindglam.weirddiscord.db.Database;
import io.github.bindglam.weirddiscord.listeners.MessageListener;
import io.github.bindglam.weirddiscord.listeners.PlayerListener;
import io.github.bindglam.weirddiscord.utils.PlaceholderExpansion;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.bukkit.OfflinePlayer;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

public class WeirdDiscord extends JavaPlugin {
    public static WeirdDiscord INSTANCE;
    public static JDA JDA;
    public static Database DB;

    public static final WeakHashMap<String, UUID> VERIFY_CODES = new WeakHashMap<>();

    @Override
    public void onLoad() {
        CommandAPI.onLoad(new CommandAPIBukkitConfig(this));
        registerCommands();
    }

    @Override
    public void onEnable() {
        CommandAPI.onEnable();

        saveDefaultConfig();

        INSTANCE = this;

        if(getConfig().getString("token") != null && !Objects.equals(getConfig().getString("token"), "")) {
            JDA = JDABuilder.createDefault(getConfig().getString("token"))
                    .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                    .build();
            JDA.addEventListener(new MessageListener());
        }

        try {
            DB = new Database();
            DB.initializeDatabase();
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

        if (getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderExpansion().register();
        }

        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable() {
        JDA.shutdown();

        CommandAPI.onDisable();

        saveConfig();
    }

    private void registerCommands(){
        new CommandAPICommand("weirddiscord")
                .withAliases("wdiscord")
                .withPermission(CommandPermission.OP)
                .withSubcommands(
                        new CommandAPICommand("deletedata")
                                .withArguments(new OfflinePlayerArgument("target"))
                                .executesPlayer((player, args) -> {
                                    OfflinePlayer target = (OfflinePlayer) args.get("target");

                                    try {
                                        DB.deletePlayerData(DB.getPlayerDataFromDatabase(Objects.requireNonNull(target).getUniqueId()));
                                    } catch (SQLException e) {
                                        //throw new RuntimeException(e);
                                        try {
                                            WeirdDiscord.DB.createConnection();
                                        } catch (SQLException ex) {
                                            throw new RuntimeException(ex);
                                        }
                                        return;
                                    }
                                    player.sendMessage("성공적으로 초기화했습니다.");
                                })
                )
                .register();
    }
}
