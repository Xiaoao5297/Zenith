# Bot 系统使用教程

本目录是 LY Core 的 PVP Bot 配置目录。Bot 是服务器内的 AI 实体，不需要安装额外插件。

这份教程面向第一次使用服务器的管理者。按顺序操作即可创建、配置和管理 Bot。

## 开始前

1. 启动服务器一次。服务器会创建 `bot/config.yml` 和 `bot/skindata/`。
2. 在游戏内使用具有 OP 权限的账号。`/bot` 命令权限为 `pocketmine.command.bot`，默认仅 OP 拥有。
3. 进入要生成 Bot 的世界，并确认脚下是安全位置。Bot 会自动尝试寻找可站立的位置，但不要在虚空、岩浆或密闭空间附近生成。

输入下面的命令可随时查看内置帮助：

```text
/bot help
```

## 最快创建方式

只想马上放一个 PVP Bot，不需要先创建类型：

```text
/bot add pvpbot1
```

这会在你当前位置附近生成一个难度 1 的 Bot。难度可使用 `1` 到 `6`：

```text
/bot add pvpbot1
/bot add pvpbot2
/bot add pvpbot3
/bot add pvpbot4
/bot add pvpbot5
/bot add pvpbot6
```

难度越高，Bot 的默认装备和战斗资源越强。`pvpbot1` 使用皮革装和石剑，`pvpbot2` 使用铁装和铁剑，`pvpbot3`、`pvpbot4` 使用钻石装和钻石剑，`pvpbot5`、`pvpbot6` 使用附魔钻石装备。

也可以指定坐标和世界：

```text
/bot add pvpbot3 100 65 -20 world
```

参数含义：

```text
/bot add <类型> [x y z] [世界名]
```

- 不填坐标时，使用你当前所处位置。
- 不填世界名时，使用你当前所在世界；控制台执行时使用默认世界。
- 世界未加载时，系统会尝试加载它。世界名必须与服务器世界文件夹名一致。

## 从零完成一个陪练 Bot

下面是一套完整流程。示例类型名为 `training`，会创建一个原地战斗、死亡不掉落的陪练 Bot。

1. 进入目标世界，先把要交给 Bot 的武器、食物和其他物品放入自己的背包；要给 Bot 穿的盔甲请先穿在自己身上。
2. 依次执行：

```text
/bot create training
/bot edit training name §a新手陪练
/bot edit training bag copy
/bot edit training walk false
/bot edit training nodrops true
/bot edit training playerfriendly false
/bot add training
```

3. 输入 `/bot display` 显示管理 ID，再输入 `/bot list` 确认 Bot 已经生成。

这套配置的含义是：Bot 使用你复制进去的装备，固定在生成位置，主动攻击附近玩家，死亡时不掉落物品。

想把它改成大厅展示 NPC，只需要再执行：

```text
/bot edit training playerfriendly true
/bot edit training mobfriendly true
/bot edit training animalfriendly true
/bot edit training walk false
```

## 难度与类型的选择

| 使用场景 | 建议做法 |
| --- | --- |
| 临时测试 AI 或战斗 | 直接使用 `/bot add pvpbot1-6` |
| 需要固定名字、皮肤或背包 | 创建自定义类型后使用 `/bot add <类型>` |
| 要生成多个配置相同的 Bot | 只创建一个类型，反复使用 `/bot add <类型>` |
| 需要不同装备或不同性格 | 分别创建多个类型，例如 `novice`、`archer`、`guard` |

内置 Bot 从难度 3 开始可使用弓箭；难度 4 以上会获得更强的战斗补给；难度 5 和 6 的默认装备附魔更强。自定义类型设置过背包后，配置的背包会替换该类型 Bot 的默认难度装备，因此推荐用 `bag copy` 明确配置所需物品。

## 创建自己的 Bot 类型

“类型”相当于 Bot 模板。同一类型可生成多个 Bot；以后修改类型后，已生成的该类型 Bot 会立即刷新配置。

以下以 `arena` 为例：

```text
/bot create arena
```

类型名称只能使用中文、英文、数字、下划线和连字符，最长 32 个字符。例如：`arena`、`新手陪练`、`pvp-1`。

创建完成后，按以下顺序配置。

### 1. 设置显示名称

```text
/bot edit arena name 竞技场陪练
```

显示名称最长 64 个字符。可以使用 Minecraft 颜色代码，例如 `§a竞技场陪练`。

### 2. 设置背包和装备

先把希望 Bot 使用的物品放进你自己的背包。盔甲也请穿在身上，再执行：

```text
/bot edit arena bag copy
```

该命令会用你当前的完整背包和已穿戴盔甲覆盖该类型的装备。自定义名、附魔和 NBT 数据会一同保存。

只添加手持物品，而不改动现有背包：

```text
/bot edit arena bag add
```

清空该类型的背包和装备：

```text
/bot edit arena bag clear
```

`bag copy`、`bag clear` 和 `bag add` 只能由游戏内 OP 执行。

### 3. 设置移动方式

关闭移动，让 Bot 保持原地：

```text
/bot edit arena walk false
```

重新开启移动：

```text
/bot edit arena walk true
```

设置为自由移动：

```text
/bot edit arena walk random
```

设置为限定区域移动：

```text
/bot edit arena walk area
```

执行 `walk area` 后，按聊天提示完成三步：

1. 破坏第一个角落的方块。
2. 破坏对角的第二个方块。
3. 右键第三个方块，作为 Bot 越界后的回传点。

三个点必须在同一个世界。移动区域只限制 Bot 的日常移动；它离开区域后会被安全传送回第三个点附近。

### 4. 设置死亡后重生

重生只适用于通过 `/bot create <类型>` 创建并用 `/bot add <类型>` 生成的自定义类型 Bot。直接使用 `/bot add pvpbot1-6` 生成的内置 Bot 没有可保存的重生配置。

为 `arena` 开启重生：

```text
/bot edit arena respawn true
```

执行后按聊天提示完成两步：

1. 破坏一个方块，系统会将该方块的位置记录为重生点。
2. 不要输入命令，直接在聊天栏发送等待秒数，例如 `10`。可填写范围为 `0` 到 `86400`；`0` 表示死亡后立即重生。

Bot 死亡后，会等待设定的时间，在重生点附近寻找安全站立位置，然后满血重生并重新加载该类型的名称、皮肤、装备和行为设置。

关闭已配置的重生：

```text
/bot edit arena respawn false
```

注意：`/bot remove`、`/bot remove all` 和 `/bot remove worldall` 是管理删除，不是死亡事件，被这样删除的 Bot 不会触发重生。要彻底撤销一个会重生的 Bot，请先关闭重生或删除类型，再使用删除命令。

### 5. 设置行为开关

通用格式：

```text
/bot edit <类型> <设置名> true
/bot edit <类型> <设置名> false
```

`true` 表示开启该限制或友好行为，`false` 表示关闭。示例：

```text
/bot edit arena playerfriendly true
/bot edit arena nodrops true
/bot edit arena noshoot true
```

| 设置名 | 开启后的效果 |
| --- | --- |
| `nodrops` | Bot 死亡时不掉落物品 |
| `playerfriendly` | 不主动攻击玩家 |
| `mobfriendly` | 不主动攻击敌对怪物 |
| `animalfriendly` | 不主动攻击动物 |
| `nobreak` | 不破坏方块 |
| `noplace` | 不放置方块 |
| `noconsume` | 不消耗物品 |
| `noeat` | 不进食 |
| `noshoot` | 不使用弓箭射击 |
| `noteleport` | 不使用传送能力 |
| `nopickup` | 不拾取地面物品 |
| `nosplash` | 不使用喷溅药水 |

查看某个类型的当前配置：

```text
/bot edit arena
```

### 6. 生成已配置类型

```text
/bot add arena
```

或者指定位置和世界：

```text
/bot add arena 100 65 -20 world
```

`/bot add arena` 生成的实体会使用 `arena` 的名称、背包、皮肤、移动和行为配置。

## 设置自定义皮肤

1. 将皮肤图片放入 `bot/skindata/`。
2. 图片必须是 PNG、JPG 或 JPEG，尺寸只能是 `64 x 32` 或 `64 x 64`。
3. 打开 `bot/config.yml`，找到类型的 `skin` 字段，填写文件名。例如：

```yaml
types:
  arena:
    skin: arena.png
```

4. 保存文件。已生成的该类型 Bot 会自动检测配置变化并刷新；也可以重新生成 Bot。

不要填写完整磁盘路径或子目录，例如不要写 `skindata/arena.png`，只写 `arena.png`。

如果皮肤没有生效，请检查图片尺寸、扩展名和服务器 PHP 的 GD 图像扩展是否可用。皮肤加载失败时，Bot 会使用默认皮肤。

## 手动编辑配置文件

配置文件是 `bot/config.yml`。通常建议优先使用 `/bot edit`，这样不容易写错格式。

最小类型示例：

```yaml
version: 1
types:
  arena:
    type: pvpbot
    name: "竞技场陪练"
    skin: default.png
    walk:
      enabled: true
      mode: random
      area: null
    respawn:
      enabled: true
      delay: 10
      point:
        world: world
        x: 100
        y: 65
        z: -20
    settings:
      nodrops: true
      playerfriendly: false
      mobfriendly: false
      animalfriendly: false
      nobreak: false
      noplace: false
      noconsume: false
      noeat: false
      noshoot: false
      noteleport: false
      nopickup: false
      nosplash: false
    equipment:
      copied: false
      armor_copied: false
      armor: []
      inventory: []
```

说明：

- `type` 必须为 `pvpbot`。
- `name` 为空时，Bot 使用默认名称。
- `skin: default.png` 表示默认皮肤；这个文件名不需要实际存在。
- `respawn.enabled` 只有在 `respawn.point` 已填写时才会生效。`delay` 的单位是秒，范围为 `0` 到 `86400`。
- 手动填写重生点时，`world` 必须是世界名，`x`、`y`、`z` 必须是整数。推荐优先使用 `/bot edit <类型> respawn true` 自动写入。
- `equipment` 建议通过 `bag copy` 或 `bag add` 写入。手动编辑装备时必须保留完整 NBT 数据，否则物品可能无法正确还原。
- 修改配置后请保存为 UTF-8 文本。不要使用 Tab 缩进，YAML 请使用空格。

## 配置何时生效

- 使用 `/bot edit` 修改类型后，当前已生成的同类型 Bot 会立即刷新。
- 手动保存 `bot/config.yml` 后，当前已生成的同类型 Bot 会定期检测文件变化，通常最多约一秒后刷新。
- 皮肤变更会让当前 Bot 重新发送实体外观，因此周围玩家会看到皮肤更新。
- 为避免覆盖彼此的改动，编辑 `bot/config.yml` 时不要同时执行会修改同一类型的 `/bot create`、`/bot del` 或 `/bot edit` 命令。

## 管理已生成的 Bot

先列出 Bot：

```text
/bot list
/bot list world
```

每个 Bot 都有一个管理 ID，范围为 `0` 到 `9999`。开启 ID 显示：

```text
/bot display
```

该显示只对 OP 可见。常用管理命令：

```text
/bot look <ID>       # 查看 Bot 信息
/bot tp <ID>         # 传送到 Bot
/bot tphere <ID>     # 把 Bot 传送到你身边
/bot wand            # 获得 Bot 查阅器
/bot remove <ID>     # 删除一个 Bot 实体
/bot remove all      # 删除当前世界的全部 Bot 实体
/bot remove worldall # 删除所有已加载世界的全部 Bot 实体
```

`remove` 只删除当前存在的 Bot 实体，不会删除类型配置。删除类型配置使用：

```text
/bot del arena
```

删除类型后，已生成的同类型 Bot 不会再从该类型读取配置。建议先执行 `/bot remove all` 或 `/bot remove worldall`，再删除类型。

## 常见问题

### 提示没有权限

请使用 OP 账号，或向账号授予 `pocketmine.command.bot` 权限。

### 提示类型不存在

先执行：

```text
/bot create 类型名
```

再执行 `/bot add 类型名`。类型名必须完全一致。

### Bot 没有使用我设置的装备

确认你是在游戏内使用 OP 身份执行了：

```text
/bot edit 类型名 bag copy
```

然后重新生成 Bot，或修改类型后等待已生成 Bot 刷新。请确认背包中不是空气物品。

### Bot 不攻击玩家

检查 `playerfriendly` 是否为 `true`。此外，Bot 只会攻击生存或冒险模式中、位于侦测范围内的玩家。

### Bot 不能移动或一直回传

检查移动是否开启：

```text
/bot edit 类型名 walk true
```

若使用区域模式，请重新执行 `/bot edit 类型名 walk area`，并确保三个点选在同一个世界、回传点周围有可站立空间。

### Bot 生成失败或位置不对

请在实体可站立的位置生成，至少保证脚下有可站立方块，身体和头部空间没有被方块堵住。也可以直接指定安全坐标和世界。
