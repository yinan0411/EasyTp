<h1>1.4</h1>
Optimized the tps command functionality. You can now approve or deny teleportation requests. Enter /tps followed by the player name: /tps true (Allow all current players' teleportation requests) /tps false (Deny all current players' teleportation requests) /tps true [player] (Allow the specified player to teleport when multiple requests exist) /tps false [player] (Deny the specified player's teleportation when multiple requests exist)

优化了tps命令的方法 现在可以进行同意 或者是拒绝传送 输入 /tps 对应玩家 /tps true(允许当前所有玩家传送请求) /tps false(拒绝当前所有玩家传送请求) /tps true [玩家] /tps false [玩家] 允许某人的传送和拒绝某人的传送
<h1>1.3</h1>

New server cleanup feature added (for server administrators only) Cleans based on loaded blocks Command: /PascalCase true Enable service /PascalCase false Disable service /PascalCase time [/minutes] Set cleanup interval in minutes

新增服务器清理功能（仅限服务器管理员使用）基于已加载区块进行清理命令：/PascalCase true 启用服务/PascalCase false 禁用服务/PascalCase time [/minutes] 设置清理间隔（单位：分钟）
<h1>1.2</h1>

Added player suicide functionality (Note: If suicide is disabled in the base game, death will not trigger item drops; players will die normally according to game rules). Command: /killme Added command help: /EasyTpHelp displays currently available commands.

新增玩家自杀功能（注：若基础游戏中禁用自杀机制，死亡将不会触发物品掉落；玩家仍将按游戏规则正常死亡）。命令：/killme新增命令帮助：/EasyTpHelp 显示当前可用命令列表。
<h1>1.1</h1>

Added TP teleportation prompt messages (to prevent players from being startled while mining).

新增TP传送提示信息（防止玩家在采矿时受到惊吓）。

<h1>1.0</h1>

No permissions required—player-to-player teleportation is now possible. Command: /tps [player]
无需权限——玩家间传送现已实现。命令：/tps [玩家]