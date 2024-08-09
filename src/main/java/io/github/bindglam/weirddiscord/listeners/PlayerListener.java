package io.github.bindglam.weirddiscord.listeners;

import io.github.bindglam.weirddiscord.WeirdDiscord;
import io.github.bindglam.weirddiscord.db.Database;
import io.github.bindglam.weirddiscord.models.PlayerData;
import io.github.bindglam.weirddiscord.utils.CodeUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;

public class PlayerListener implements Listener {
    private final Database database = WeirdDiscord.DB;

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();

        PlayerData playerData;
        try {
            playerData = database.getPlayerDataFromDatabase(player.getUniqueId());
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        if(playerData.getId().isEmpty()){
            if(WeirdDiscord.VERIFY_CODES.containsValue(player.getUniqueId())){
                for(String code : WeirdDiscord.VERIFY_CODES.keySet()){
                    if(WeirdDiscord.VERIFY_CODES.get(code).equals(player.getUniqueId())){
                        WeirdDiscord.VERIFY_CODES.remove(code);
                        break;
                    }
                }
            }
            String verifyCode = CodeUtil.getRandomCode(WeirdDiscord.INSTANCE.getConfig().getInt("verify-code-length"));

            player.kick(MiniMessage.miniMessage().deserialize(
                    "<white>안녕하세요! " + player.getName() + "님!\n" +
                            "<white>지금보니 <aqua><bold>디스코드 <reset><white>계정에 연결되어있지 않은 것 같군요!\n" +
                            "<white>다음 주소로 접속하여 계정 연결을 완료해주세요!\n" +
                            "<yellow><bold>감사합니다!\n" +
                            "\n" +
                            "<aqua><bold>인증 코드: " + verifyCode + "\n" +
                            "<aqua><bold><u>" + WeirdDiscord.INSTANCE.getConfig().getString("discord-address")
            ));
            WeirdDiscord.VERIFY_CODES.put(verifyCode, player.getUniqueId());
        }
    }
}
