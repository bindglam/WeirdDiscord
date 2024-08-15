package io.github.bindglam.weirddiscord.listeners;

import io.github.bindglam.weirddiscord.WeirdDiscord;
import io.github.bindglam.weirddiscord.models.PlayerData;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.sql.SQLException;
import java.util.Objects;
import java.util.UUID;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        User user = event.getAuthor();
        String message = event.getMessage().getContentDisplay();

        if(event.isFromType(ChannelType.PRIVATE) && message.contains("!인증")){
            WeirdDiscord.INSTANCE.getLogger().info(user.getName() + "님이 연결 시도중");

            String code = message.split(" ")[1];

            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("인증 코드 확인중...")
                    .setColor(Color.YELLOW)
                    .setDescription("조금만 기다려주세요...").build()).queue();

            if(!WeirdDiscord.VERIFY_CODES.containsKey(code)){
                WeirdDiscord.INSTANCE.getLogger().info(user.getName() + "님이 계정 연결에 실패했습니다.");
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("인증 코드가 알맞지 않습니다!")
                        .setColor(Color.RED)
                        .setDescription("코드를 다시 확인해주세요...").build()).queue();
                return;
            }

            event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                    .setTitle("인증 코드를 통해 연결중...")
                    .setColor(Color.CYAN)
                    .setDescription("거의 다 왔습니다...!").build()).queue();
            try {
                UUID uuid = WeirdDiscord.VERIFY_CODES.get(code);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);

                PlayerData playerData = WeirdDiscord.DB.getPlayerDataFromDatabase(offlinePlayer.getUniqueId());
                playerData.setId(user.getId());
                WeirdDiscord.DB.updatePlayerStats(playerData);

                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle(offlinePlayer.getName() + "님 계정과 성공적으로 연결되었습니다!")
                        .setColor(Color.GREEN)
                        .setDescription("감사합니다")
                        .setImage("https://mc-heads.net/avatar/" + offlinePlayer.getUniqueId()).build()).queue();
                WeirdDiscord.VERIFY_CODES.remove(code);
                WeirdDiscord.INSTANCE.getLogger().info(user.getName() + "님이 계정 연결에 성공했습니다. ( " + offlinePlayer.getName() + " )");
            } catch (SQLException e) {
                //throw new RuntimeException(e);
                event.getChannel().sendMessageEmbeds(new EmbedBuilder()
                        .setTitle("오류가 발생했습니다!")
                        .setColor(Color.RED)
                        .setDescription("다시 시도해주세요...").build()).queue();

                try {
                    WeirdDiscord.DB.createConnection();
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }
}
