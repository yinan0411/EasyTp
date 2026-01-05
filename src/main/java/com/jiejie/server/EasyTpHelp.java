package com.jiejie.server;

import com.jiejie.util.GlobalVariables;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class EasyTpHelp {

    public void EasyTpHelpStart() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("EasyTpHelp").executes(this::EasyTpHelpStartPrint));
        });
    }

    public int EasyTpHelpStartPrint(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        source.sendMessage(
                Text.literal(GlobalVariables.EASY_TP)
                        .formatted(Formatting.GOLD)
                        .append(Text.literal("欢迎使用模组帮助\n1.无权限Tp:").formatted(Formatting.GREEN))
                        .append(Text.literal("\n/tps [对应玩家名字]\n/tps true(允许当前所有玩家传送请求)\n/tps false(拒绝当前所有玩家传送请求)\n/tps true [玩家](允许当前玩家传送到自己位置)\n/tps false [玩家](拒绝当前玩家传送到自己位置)").styled(style -> style.withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/tps"
                                ))).formatted(Formatting.RED))
                        .append(Text.literal("\n2.快速自杀:").formatted(Formatting.GREEN))
                        .append(Text.literal("\n/killme").styled(style -> style.withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/killme"
                                ))).formatted(Formatting.RED))
                        .append(Text.literal("\n3.服务器清理功能(开启者需要作弊权限):").formatted(Formatting.GREEN))
                        .append(Text.literal("\n/PascalCase true(开)\n/PascalCase false(关)\n/PascalCase time [分钟](设置几分钟打扫一次)").styled(style -> style.withColor(Formatting.GREEN)
                                .withClickEvent(new ClickEvent(
                                        ClickEvent.Action.SUGGEST_COMMAND,
                                        "/PascalCase"
                                ))).formatted(Formatting.RED)))
        ;
        return 1;
    }

}
