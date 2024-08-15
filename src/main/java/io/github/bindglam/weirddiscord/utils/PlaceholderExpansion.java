package io.github.bindglam.weirddiscord.utils;

import io.github.bindglam.weirddiscord.WeirdDiscord;
import io.github.bindglam.weirddiscord.models.PlayerData;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.SQLException;

public class PlaceholderExpansion extends me.clip.placeholderapi.expansion.PlaceholderExpansion {
    @Override
    public @NotNull String getIdentifier() {
        return "weirddiscord";
    }

    @Override
    public @NotNull String getAuthor() {
        return "bindglam";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onRequest(OfflinePlayer player, @NotNull String params) {
        PlayerData playerData;
        try {
            playerData = WeirdDiscord.DB.getPlayerDataFromDatabase(player.getUniqueId());
        } catch (SQLException e) {
            //throw new RuntimeException(e);
            try {
                WeirdDiscord.DB.createConnection();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            return null;
        }

        if(params.equalsIgnoreCase("user_id")){
            return playerData.getId();
        }
        return null;
    }
}
