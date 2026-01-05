package com.jiejie.server;

import com.jiejie.domin.Tp;
import com.jiejie.domin.playerTp;
import com.jiejie.util.GlobalVariables;
import com.jiejie.util.Teleportation;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

//1.0版本创建tps
//1.4新增同意 加优化tps指令 重写tps指令功能
public class EasyTpTp {

    private List<playerTp> toRemoveList = new ArrayList<>();
    public void easyTp() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("tps")
                    .then(CommandManager.literal("list")
                            .executes(this::listTp))
                    .then(CommandManager.argument("boolean", BoolArgumentType.bool())
                            .executes(this::isAgree)
                            .then(CommandManager.argument("player", EntityArgumentType.players())
                                    .executes(this::isAgreePlayer)))
                    .then(CommandManager.argument("target", EntityArgumentType.player())
                            .executes(this::easyTpPlayer)
                    ));
        });
    }

    public int easyTpPlayer(CommandContext<ServerCommandSource> context) {
        //1.4新添加
        ServerCommandSource source = context.getSource();
        try {
            ServerPlayerEntity playerTpTo = EntityArgumentType.getPlayer(context, "target");
            ServerPlayerEntity playerTo = source.getPlayer();
            if (Teleportation.isNullNot(playerTo)) {
                if (playerTo.getUuid().equals(playerTpTo.getUuid())) {
                    source.sendError(Text.literal("你不能传送自己"));
                    return 0;
                }
                playerTp playerTp = new playerTp(playerTo, playerTpTo);
                List<playerTp> playerList = GlobalVariables.PLAYER_TP_MAP.get(playerTpTo.getUuid());
                if (!playerList.contains(playerTp)) {
                    playerList.add(playerTp);
                    source.sendMessage(Text.literal("发送请求成功等待同意中").formatted(Formatting.GREEN));
                    playerTpTo.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD)
                            .append(Text.literal("玩家 ").formatted(Formatting.WHITE)
                                    .append(Text.literal(playerTo.getName().getString()).formatted(Formatting.GREEN)
                                            .append(Text.literal(" 向你发送了一个传送请求 输入").formatted(Formatting.WHITE)
                                                    .append(Text.literal("/tps true [对应的玩家名字]").styled(style -> style.withColor(Formatting.GREEN)
                                                            .withClickEvent(new ClickEvent(
                                                                    ClickEvent.Action.SUGGEST_COMMAND,
                                                                    "/tps ture " + playerTo.getName().getString() + " "
                                                            ))
                                                            .withHoverEvent(new HoverEvent(
                                                                    HoverEvent.Action.SHOW_ENTITY,
                                                                    new HoverEvent.EntityContent(
                                                                            playerTo.getType(),
                                                                            playerTo.getUuid(),
                                                                            playerTo.getDisplayName()
                                                                    )
                                                            ))
                                                            .withInsertion(playerTo.getName().getString())
                                                    ).formatted(Formatting.RED)
                                                            .append(Text.literal("和").formatted(Formatting.WHITE)
                                                                    .append(Text.literal("/tps false [对应的玩家名字]").styled(style -> style.withColor(Formatting.GREEN)
                                                                            .withClickEvent(new ClickEvent(
                                                                                    ClickEvent.Action.SUGGEST_COMMAND,
                                                                                    "/tps false " + playerTo.getName().getString() + " "
                                                                            ))
                                                                            .withHoverEvent(new HoverEvent(
                                                                                    HoverEvent.Action.SHOW_ENTITY,
                                                                                    new HoverEvent.EntityContent(
                                                                                            playerTo.getType(),
                                                                                            playerTo.getUuid(),
                                                                                            playerTo.getDisplayName()
                                                                                    )
                                                                            ))
                                                                            .withInsertion(playerTo.getName().getString())
                                                                    ).formatted(Formatting.RED)
                                                                            .append(Text.literal("来接受请求").formatted(Formatting.WHITE)))))))
                            ));
                    return 1;
                } else {
                    source.sendError(Text.literal("当前已经有传送请求请勿重新发送"));
                    return 0;
                }
            } else {
                source.sendError(Text.literal("玩家无实体"));
                return 0;
            }
        } catch (Exception e) {
            source.sendError(Text.literal("玩家不在线"));
            return 0;
        }
        //=============================================
    }

    public int isAgree(CommandContext<ServerCommandSource> context) {
        boolean isConfirm = BoolArgumentType.getBool(context, "boolean");
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (Teleportation.isNullNot(player)) {
            List<playerTp> playerList = GlobalVariables.PLAYER_TP_MAP.get(player.getUuid());
            if (Teleportation.isRequest(source.getPlayer())) {
                if (isConfirm) {
                    source.sendMessage(Text.literal("已同意所有传送请求").formatted(Formatting.GREEN));
                    playerList.forEach(playerTp -> {
                        Tp tp = Teleportation.easyTpPlayerUp(playerTp.getPlayerTo());
                        ServerPlayerEntity player1 = playerTp.getPlayer();
                        ServerPlayerEntity playerTo = playerTp.getPlayerTo();
                        player1.teleport(tp.getWorld(), tp.getDestX(), tp.getDestY(), tp.getDestZ(), tp.getYaw(), tp.getPitch());
                        Teleportation.printText(player1, playerTo);
                        toRemoveList.add(playerTp);
                    });
                    playerList.removeAll(toRemoveList);
                    return 1;
                } else {
                    source.sendMessage(Text.literal("已拒绝所有传送请求").formatted(Formatting.GREEN));
                    playerList.forEach(playerTp -> {
                        ServerPlayerEntity player1 = playerTp.getPlayer();
                        ServerPlayerEntity playerTo = playerTp.getPlayerTo();
                        player1.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD)
                                .append(Text.literal("玩家 ").formatted(Formatting.WHITE)
                                        .append(Text.literal(playerTo.getName().getString()).formatted(Formatting.GREEN)
                                                .append(Text.literal(" 已拒绝你的传送请求").formatted(Formatting.WHITE)))));
                        toRemoveList.add(playerTp);
                    });
                    playerList.removeAll(toRemoveList);
                    return 1;
                }
            }
        } else {
            source.sendError(Text.literal("获取玩家失败"));
        }
        return 0;
    }

    public int isAgreePlayer(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        try {
            ServerPlayerEntity playerTo = EntityArgumentType.getPlayer(context, "player");
            boolean isConfirm = BoolArgumentType.getBool(context, "boolean");
            if (Teleportation.isNullNot(player)) {
                List<playerTp> playerTps = GlobalVariables.PLAYER_TP_MAP.get(player.getUuid());
                if (Teleportation.isRequest(player,playerTo)) {
                    if (isConfirm) {
                        playerTps.stream().filter(playerTp -> {
                                    // 过滤条件：玩家非空且UUID匹配
                                    ServerPlayerEntity player1 = playerTp.getPlayer();
                                    return player1.getUuid().equals(playerTo.getUuid());
                                })
                                .findFirst()
                                .ifPresent(playerTp -> {
                                    ServerPlayerEntity player1 = playerTp.getPlayer();
                                    ServerPlayerEntity playerTo1 = playerTp.getPlayerTo();
                                    Tp tp = Teleportation.easyTpPlayerUp(playerTp.getPlayerTo());
                                    player1.teleport(tp.getWorld(), tp.getDestX(), tp.getDestY(), tp.getDestZ(), tp.getYaw(), tp.getPitch());
                                    Teleportation.printText(player1, playerTo1);
                                    playerTps.remove(playerTp);
                                });
                    } else {
                        playerTps.remove(new playerTp(playerTo,player));
                        playerTo.sendMessage(Text.literal("玩家 ").formatted(Formatting.WHITE)
                                .append(Text.literal(player.getName().getString()).formatted(Formatting.GREEN)
                                        .append(Text.literal(" 已拒绝你的传送请求").formatted(Formatting.WHITE))));
                        source.sendMessage(Text.literal("已拒绝玩家 ").formatted(Formatting.WHITE)
                                .append(Text.literal(playerTo.getName().getString()).formatted(Formatting.GREEN)
                                        .append(Text.literal(" 的传送请求").formatted(Formatting.WHITE))));
                        return 1;
                    }
                    return 1;
                }
            }
        } catch (Exception e) {
            source.sendError(Text.literal("玩家未发送请求或者未在线"));
            return 0;
        }
        return 0;
    }


    public int listTp(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        if (Teleportation.isNullNot(player)) {
            List<playerTp> playerList = GlobalVariables.PLAYER_TP_MAP.get(player.getUuid());
            if (Teleportation.isRequest(player)) {
                source.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD)
                        .append(Text.literal("当前请求共").formatted(Formatting.WHITE)
                                .append(Text.literal(String.valueOf(playerList.size())).formatted(Formatting.RED)
                                        .append(Text.literal("个").formatted(Formatting.WHITE)
                                                .append(Text.literal("输入指令").formatted(Formatting.WHITE)
                                                        .append(Text.literal("/tps true | false [对应玩家名字]").formatted(Formatting.RED)
                                                                .append(Text.literal("来 同意|拒绝 某玩家请求").formatted(Formatting.WHITE))))))));
                source.sendMessage(Text.literal(GlobalVariables.EASY_TP).formatted(Formatting.GOLD));
                playerList.forEach(playerTp -> {
                    source.sendMessage(Text.literal("玩家 ").formatted(Formatting.WHITE)
                            .append(Text.literal(playerTp.getPlayer().getName().getString()).formatted(Formatting.GREEN)
                                    .append(Text.literal(" 想传送到你当前位置").formatted(Formatting.WHITE))));
                });
            }
            return 1;
        } else {
            return 0;
        }
    }


}
