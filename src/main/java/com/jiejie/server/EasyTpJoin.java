package com.jiejie.server;

import com.jiejie.util.GlobalVariables;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;

public class EasyTpJoin {

    public void playerJoin() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            GlobalVariables.PLAYER_TP_MAP.put(player.getUuid(),new ArrayList<>());
            player.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD).append(Text.literal("EasyTp已加载作者:杰杰大王").formatted(Formatting.GREEN)));
            player.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD).append(Text.literal("输入指令/EasyTpHelp可查看所有帮助").styled(style -> style.withColor(Formatting.GREEN)
                    .withClickEvent(new ClickEvent(
                            ClickEvent.Action.SUGGEST_COMMAND,
                            "/EasyTpHelp"
                    )))
                 ));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            ServerPlayerEntity player = handler.getPlayer();
            GlobalVariables.PLAYER_TP_MAP.remove(player.getUuid());
        });
    }

}
