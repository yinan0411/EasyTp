package com.jiejie.util;

import com.jiejie.domin.Tp;
import com.jiejie.domin.playerTp;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.List;

//1.4新增传送请求工具类
public class Teleportation {

    public static Tp easyTpPlayerUp(ServerPlayerEntity player) {
        return new Tp(player.getServerWorld(), player.getX(), player.getY(), player.getZ(), player.getYaw(), player.getPitch());
    }


    public static boolean isRequest(ServerPlayerEntity player) {
        if (isNullNot(player)) {
            List<playerTp> playerList = GlobalVariables.PLAYER_TP_MAP.get(player.getUuid());
            if (playerList.isEmpty()) {
                player.sendMessage(Text.literal("当前没有传送请求").formatted(Formatting.RED));
                return false;
            } else {
                return true;
            }
        }
        return false;
    }

    public static boolean isRequest(ServerPlayerEntity player, ServerPlayerEntity playerTo) {
        if (isNullNot(player)) {
            List<playerTp> playerList = GlobalVariables.PLAYER_TP_MAP.get(player.getUuid());
            if (playerList.isEmpty()) {
                player.sendMessage(Text.literal("当前没有传送请求").formatted(Formatting.RED));
                return false;
            }
            if (player.getUuid().equals(playerTo.getUuid())){
                player.sendMessage(Text.literal("你不能传送自己").formatted(Formatting.RED));
            }
            // anyMatch是短路操作：找到第一个匹配元素后立即返回true，终止遍历，性能最优
            boolean isMatch = playerList.stream()
                    .filter(playerTp -> {
                        // 先做非空判断，避免空指针（关键：防止playerTp的属性为null导致崩溃）
                        return playerTp != null
                                && playerTp.getPlayerTo() != null
                                && playerTp.getPlayer() != null
                                && playerTo != null;
                    })
                    .anyMatch(playerTp -> {
                        // 你的条件判断：满足则返回true
                        return playerTp.getPlayerTo().getUuid().equals(player.getUuid())
                                && playerTp.getPlayer().getUuid().equals(playerTo.getUuid());
                    });
            // 4. 根据是否匹配返回对应布尔值
            if(isMatch){
                return true;
            }
            player.sendMessage(Text.literal("此玩家当前未给你发送传送请求"));
            return false;
        }
        return false;
    }

    public static boolean isNull(ServerPlayerEntity player) {
        return player == null;
    }


    public static boolean isNullNot(ServerPlayerEntity player) {
        return player != null;
    }

    public static void printText(ServerPlayerEntity player, ServerPlayerEntity playerTo) {
        player.sendMessage(Text.literal("传送成功").formatted(Formatting.GREEN));
        playerTo.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD)
                .append(Text.literal("已将玩家").formatted(Formatting.WHITE)
                        .append(Text.literal(player.getName().getString()).formatted(Formatting.GREEN)
                                .append(Text.literal("传送至玩家").formatted(Formatting.WHITE)
                                        .append(Text.literal(playerTo.getName().getString()).formatted(Formatting.GREEN))))));
    }

}
