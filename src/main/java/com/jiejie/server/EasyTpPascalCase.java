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
        if (server == null || isTaskRegistered) { // 已注册则直接返回，避免重复注册任务
            return;
        }
        this.server = server;
        this.currentTick = 0;
        this.isTaskEnabled = true;
        this.isTaskRegistered = true; // 标记为已注册
        // 仅注册一次定时任务
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    public void onServerTick(MinecraftServer server) {
        if (!isTaskEnabled || server.getPlayerManager() == null) {
            return;
        }
        long targetTickInterval = PERIOD_TICKS * 60L * 20L;
        // 计算5秒对应的Tick数（1秒=20Tick，5秒=100Tick）
        long fiveSecondsBeforeTick = targetTickInterval - 5 * 20L;
        // 清理前5秒发送提醒（仅触发一次，服务器全局提醒）
        if (currentTick == fiveSecondsBeforeTick) {
            server.getPlayerManager().broadcast(
                    Text.literal(GlobalVariables.EASY_TP)
                            .formatted(Formatting.GOLD)
                            .append(Text.literal("将在5秒后清理已加载区块的掉落物，请及时拾取！").formatted(Formatting.YELLOW)),
                    false // 仅玩家可见，不发送到控制台
            );
        }
        // 每Tick递增计数器，达到目标间隔后执行清理（服务器全局只执行一次清理）
        if (++currentTick >= targetTickInterval) {
            clearDroppedItemsInLoadedChunks();
            currentTick = 0; // 重置计数器，开始下一轮计时
        }
    }

    public void stopCleanupTask() {
        this.isTaskEnabled = false;
        this.currentTick = 0;
        this.isTaskRegistered = false; // 重置注册标记，下次开启可重新注册
    }

    private void clearDroppedItemsInLoadedChunks() {
        // 防护：判空服务器实例
        if (server == null) {
            return;
        }

        // 用于存储所有已加载区块的掉落物（自动去重，避免跨世界/重复实体）
        Set<ItemEntity> itemsToClear = new HashSet<>();
        // 统计服务器总已加载区块数量（调用公有方法getLoadedChunkCount()）
        int totalLoadedChunks = 0;


        // 步骤1：遍历服务器的所有世界（主世界、下界、末地，ServerWorld的公有遍历）
        for (ServerWorld world : server.getWorlds()) {
            // 步骤1.1：获取当前世界的ServerChunkManager（ServerWorld的公有方法getChunkManager()）
            ServerChunkManager chunkManager = world.getChunkManager();
            // 步骤1.2：累加当前世界的已加载区块数量（ServerChunkManager的公有方法）
            totalLoadedChunks += chunkManager.getLoadedChunkCount();

            // 步骤2：创建覆盖Minecraft最大坐标范围的Box（确保包含所有已加载区块的坐标）
            // MC的坐标限制：X/Z为±30000000（超过会被限制），Y为世界的最低(bottomY)到最高(topY)高度
            Box worldWideBox = new Box(
                    -30000000.0D, // 最小X（MC最大负坐标）
                    world.getBottomY(), // 世界最低Y（ServerWorld的公有方法）
                    -30000000.0D, // 最小Z
                    30000000.0D,  // 最大X（MC最大正坐标）
                    world.getTopY(),    // 世界最高Y（ServerWorld的公有方法）
                    30000000.0D   // 最大Z
            );

            // 步骤3：调用ServerWorld的公有方法getEntitiesByType（匹配源码中的方法签名）
            // TypeFilter.instanceOf(ItemEntity.class)：过滤出所有掉落物实体
            // worldWideBox：限定查询范围（覆盖所有已加载区块）
            // (ItemEntity entity) -> !entity.isRemoved()：过滤掉已标记为移除的实体（公有方法isRemoved()）
            List<ItemEntity> itemList = world.getEntitiesByType(
                    TypeFilter.instanceOf(ItemEntity.class),
                    worldWideBox,
                    (ItemEntity entity) -> !entity.isRemoved()
            );

            // 步骤4：将当前世界的掉落物加入Set（自动去重）
            itemsToClear.addAll(itemList);
        }

        // 步骤5：统一清理所有已加载区块的掉落物（统计实际清理数量）
        int totalCleared = 0;
        for (ItemEntity item : itemsToClear) {
            // 双重防护：确保实体未被移除且属于服务器世界（公有API判断）
            if (!item.isRemoved() && item.getWorld() instanceof ServerWorld) {
                item.kill(); // 移除实体（ItemEntity的公有方法）
                totalCleared++;
            }
        }

        // 步骤6：广播清理结果（整合已加载区块数量，保留你的文案风格）
        server.getPlayerManager().broadcast(
                Text.literal("EasyTp") // 你的模组名
                        .formatted(Formatting.GOLD)
                        .append(Text.literal("服务器当前已加载区块总数：").formatted(Formatting.GREEN))
                        .append(Text.literal(String.valueOf(totalLoadedChunks)).formatted(Formatting.RED))
                        .append(Text.literal("已清理所有已加载区块的").formatted(Formatting.GREEN))
                        .append(Text.literal(String.valueOf(totalCleared)).formatted(Formatting.RED))
                        .append(Text.literal("个掉落物").formatted(Formatting.GREEN)),
                false // 不发送到服务器控制台
        );

    }
}