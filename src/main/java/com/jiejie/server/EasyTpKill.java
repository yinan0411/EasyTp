package com.jiejie.server;

import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

//1.2新添加自杀
public class EasyTpKill {
    public void killStart(){
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment)->{
            dispatcher.register(CommandManager.literal("killme").executes(this::easyTpKill));
        });
    }

    public int easyTpKill(CommandContext<ServerCommandSource> context){
        ServerCommandSource source = context.getSource();
        ServerPlayerEntity player = source.getPlayer();
        try {
            if (player != null) {
                player.kill();
            }else {
                source.sendError(Text.literal("自杀目标为空"));
            }
        }catch (Exception e){
            source.sendError(Text.literal("自杀失败"));
            return 0;
        }
        return 1;
    }

}
