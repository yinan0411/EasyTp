package com.jiejie.server;

import com.jiejie.util.GlobalVariables;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerChunkManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

//1.3新添加服务器清理功能 每5分钟清理一次掉落物
public class EasyTpPascalCase {
    //手动设置value值 true 为开启 false 为不开启
    public static boolean isTaskEnabled = false;
    //初始设置时间* 60 * 20
    private Integer PERIOD_TICKS = 5;
    private long currentTick = 0;
    // 新增：标记任务是否已注册，避免重复注册ServerTickEvents（核心解决玩家进来重复执行的问题）
    private boolean isTaskRegistered = false;

    private MinecraftServer server;
    private static final Identifier CLEANUP_TASK_ID = new Identifier("easytp", "cleanup_task");

    public void PascalCaseStars() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            dispatcher.register(CommandManager.literal("PascalCase")
                    .requires(serverCommandSource -> (serverCommandSource.hasPermissionLevel(2)))
                    .then(CommandManager.argument("value", BoolArgumentType.bool())
                            .executes(this::PascalCase)
                    )
                    .then(CommandManager.literal("time")
                            .then(CommandManager.argument("minutes", IntegerArgumentType.integer(1)).executes(this::PascalCaseTime)
                            )
                    )
            );
        });
    }

    public int PascalCase(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        this.isTaskEnabled = BoolArgumentType.getBool(context, "value");
        this.server = source.getServer();
        if (this.isTaskEnabled) {
            startCleanupTask(server);
            source.sendMessage(Text.literal(GlobalVariables.EASY_TP)
                    .formatted(Formatting.GOLD)
                    .append(Text.literal("已开启服务器打扫功能,当前").formatted(Formatting.GREEN))
                    .append(Text.literal(String.valueOf(PERIOD_TICKS)).formatted(Formatting.RED))
                    .append(Text.literal("/分钟清理一次如需关闭请使用/PascalCase false,如需调节时间请使用/PascalCase time [需要设置时间/每分钟]").formatted(Formatting.GREEN))
            );
        } else {
            stopCleanupTask();
            source.sendError(Text.literal(GlobalVariables.EASY_TP)
                    .formatted(Formatting.GOLD)
                    .append(Text.literal("已关闭服务器打扫功能").formatted(Formatting.GREEN)));
        }
        return 1;
    }

    public int PascalCaseTime(CommandContext<ServerCommandSource> context) {
        ServerCommandSource source = context.getSource();
        this.PERIOD_TICKS = IntegerArgumentType.getInteger(context, "minutes");
        this.server = source.getServer();
        if (this.isTaskEnabled) {
            stopCleanupTask();
            startCleanupTask(server);
        }

        source.sendMessage(Text.literal(GlobalVariables.EASY_TP)
                .formatted(Formatting.GOLD)
                .append(Text.literal("已将打扫时间设置为").formatted(Formatting.GREEN))
                .append(Text.literal(String.valueOf(PERIOD_TICKS)).formatted(Formatting.RED))
                .append(Text.literal("/分钟").formatted(Formatting.GREEN))
        );
        return 1;
    }

    public void startCleanupTask(MinecraftServer server) {
        if (server == null || isTaskRegistered) {
            return;
        }
        this.server = server;
        this.currentTick = 0;
        this.isTaskEnabled = true;
        this.isTaskRegistered = true;
        // 仅注册一次定时任务
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    public void onServerTick(MinecraftServer server) {
        if (!isTaskEnabled || server.getPlayerManager() == null) {
            return;
        }
        long targetTickInterval = PERIOD_TICKS * 60L * 20L;

        long fiveSecondsBeforeTick = targetTickInterval - 5 * 20L;

        if (currentTick == fiveSecondsBeforeTick) {
            server.getPlayerManager().broadcast(
                    Text.literal(GlobalVariables.EASY_TP)
                            .formatted(Formatting.GOLD)
                            .append(Text.literal("将在5秒后清理已加载区块的掉落物，请及时拾取！").formatted(Formatting.YELLOW)),
                    false
            );
        }

        if (++currentTick >= targetTickInterval) {
            clearDroppedItemsInLoadedChunks();
            currentTick = 0;
        }
    }

    public void stopCleanupTask() {
        this.isTaskEnabled = false;
        this.currentTick = 0;
        this.isTaskRegistered = false;
    }

    private void clearDroppedItemsInLoadedChunks() {
        if (server == null) {
            return;
        }
        Set<ItemEntity> itemsToClear = new HashSet<>();
        int totalLoadedChunks = 0;


        for (ServerWorld world : server.getWorlds()) {
            ServerChunkManager chunkManager = world.getChunkManager();

            totalLoadedChunks += chunkManager.getLoadedChunkCount();

            Box worldWideBox = new Box(
                    -30000000.0D,
                    world.getBottomY(),
                    -30000000.0D,
                    30000000.0D,
                    world.getTopY(),
                    30000000.0D
            );


            List<ItemEntity> itemList = world.getEntitiesByType(
                    TypeFilter.instanceOf(ItemEntity.class),
                    worldWideBox,
                    (ItemEntity entity) -> !entity.isRemoved()
            );
            itemsToClear.addAll(itemList);
        }
        int totalCleared = 0;
        for (ItemEntity item : itemsToClear) {
            if (!item.isRemoved() && item.getWorld() instanceof ServerWorld) {
                item.kill();
                totalCleared++;
            }
        }

        server.getPlayerManager().broadcast(
                Text.literal("EasyTp")
                        .formatted(Formatting.GOLD)
                        .append(Text.literal("已清理所有已加载区块的").formatted(Formatting.GREEN))
                        .append(Text.literal(String.valueOf(totalCleared)).formatted(Formatting.RED))
                        .append(Text.literal("个掉落物").formatted(Formatting.GREEN)),
                false
        );

    }
}