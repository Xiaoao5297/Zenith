# Zenith 审计修复 — 剩余人工验证指南

> 适用分支：`fix/audit-critical-1`（基线 commit `966346b`，以 `git log -1` 为准）
> 关联文档：`docs/audit-2026-11-01.md`（问题清单与四段式说明）
> 目的：自动化脚本覆盖不到的修复项，按本文在测试服逐项验证。
> **所有步骤只在测试服执行，不要在生产服试。**

---

## 0. 准备

### 0.1 先跑自动化回归

```bash
cd /path/to/Incore-Pro
php tests/run-tests.php
```

预期输出结尾：

```
519/519 passed
```

- 数字随用例增加而增长，以实际输出为准（`feat/incore-port` 分支当前为 519 个用例）。

- 零依赖，只需要 PHP CLI（7.3 / 7.4），不需要开服务器、不需要 Composer/PHPUnit。
- 运行器自动扫描 `tests/*Test.php`，新增测试文件无需改 `run-tests.php`。
- 有 FAIL 时退出码为 1，输出会带上具体用例名与失败原因。
- 如果只有 31 个用例，说明跑的是旧副本（测试服目录里的 `tests/` 未同步）。

### 0.2 测试服准备

- 用本分支构建（源码目录或打包后的 phar 均可）启动测试服。
- 首次启动会在**服务器根目录**（不是 `config/` 下）生成：
  - `anticheat.yml` —— 反作弊总配置
  - `anticheat_messages.yml` —— 违规提示文案
- 备份配置在 `config/backup.yml`。
- 高级开关在 `gameplay.yml`（`server.*` 段读取）。

### 0.3 配置文件总览

| 文件 | 内容 |
|---|---|
| `server.yml` | 基础（语言、自动保存）、内存、网络、`ticks-per`、命令别名 |
| `world.yml` | 反矿透、世界规则、区块发送/计时/生成、刷怪上限、世界列表、天气/火焰/闪电、下界/末地/空岛 |
| `gameplay.yml` | 玩家（饥饿/回血/经验）、Genisys 玩法开关（铁砧、傀儡、经验缓存）、红石、AI |
| `debug.yml` | 调试等级、`/status`、`/gc` 等调试命令 |
| `anticheat.yml` / `anticheat_messages.yml` | 反作弊阈值与提示文案 |
| `config/backup.yml` | 自动备份 |
| `raklib.yml` | UDP 底层 |

旧版本的 `pocketmine.yml` 与 `genisys.yml` 会在启动时自动拆分迁移，原文件改名为 `*.yml.old`；
迁移只补缺失键，不覆盖已存在的新配置。确认无误后可删除 `.old` 文件。

模板按语言放在 `src/pocketmine/resources/<语言缩写>/`（`gameplay.yml`、`world.yml`），
启动时按 `server.yml` 的 `settings.language` 选择，取不到就回退根目录的单份模板。
目前 `chs` 为中文注释，`eng` 为英文注释，其余语言为英文注释加少量常用键本地化。
- 打开观察：

```text
/ac status            # 看 enabled / mode / debug / 每日上限 / 检测器数量
/ac debug on          # 打开逐条违规日志（本次运行有效）
/ac player <玩家名>    # 看该玩家的 buffer 与各检测器违规数（别名 /ac p）
/ac checks            # 列出 12 个检测器与 max-violations
```

- 默认 `settings.mode: alert`：只记录/提示，不踢人、不回退。确认没有误报后再切 `punish`。
- `/ac mode punish` 只对本次运行有效，重启后回到 `anticheat.yml` 里的值。
- `fpacheat.bypass` 权限的玩家不参与检测（做对照测试时注意）。

### 0.3 测试工具需求

| 项目 | 需要什么 |
|---|---|
| BUG-002 飞行/速度/攻击距离/进食限速 | 改包客户端（或能伪造协议包的插件） |
| SEC-006 合成缺格 | 改包客户端发 `CraftingEventPacket`（旧协议）/ 窗口事务（0.15） |
| SEC-009 库存 | 测试插件（调用 `setItem` 写入非法物品） |
| SEC-017 告示牌 | 测试插件（监听 `SignChangeEvent` 并抛异常） |
| 备份调度 | 无需插件，清理 `.state` 后重启即可 |

附录 A 给了测试插件骨架。改包类项目如果没有现成工具，可以先跳过，标注「未验证：缺改包工具」。

---

## 1. 自动化已覆盖（无需手工重复）

| 测试类 | 用例数 | 覆盖内容 |
|---|---|---|
| `BackupScheduleTest` | 10 | 启动不立即备份、state 初始化完整 interval、过期不补跑、各 tier 相位对齐、每 tick 最多一个 |
| `BanExpiryTest` | 16 | SEC-011 过期判断无副作用、序列化往返、坏时间字段 |
| `BanListFormatTest` | 14 | 封禁名单竖线转义、坏行、大小写 |
| `BatchCodecTest` | 19 | SEC-001 批包 4MB 上限、zlib/gzip/raw 三编码、压缩炸弹、截断流拒绝、帧切分、V11 遍历 |
| `NbtLimitTest` | 7 | SEC-007 10 万标签上限、小结构往返 |

另外一处生产加固已随测试一起完成：`BatchCodec::streamingDecompress()` 在收尾前检查 `inflate_get_status()`，截断的 zlib 流会返回 `false`，不再把半截数据交给上层。

---

## 2. 逐项人工验证

### 2.1 BUG-002 反作弊 —— 误报回归（优先级最高）

这是「反作弊经常误报」修复的核心，建议先跑这一节。

#### 2.1.1 普通移动不被回弹（forceMovement 阈值）

**前置**：`/ac debug on`；网络正常或人为加 200–300ms 延迟。

**步骤**：
1. 在地面行走、疾跑、跳跃、上下台阶/半砖各 2 分钟。
2. 坐船、爬梯子、游泳、被水冲、黏液块弹跳各 2 分钟。
3. 期间观察视角是否被拉回（橡皮筋）。

**预期**：
- 不出现被拉回原位的情况。
- `/ac player <名字>` 里 Speed/Teleport 的 buffer 不持续上涨。

**失败排查**：
- 回弹阈值在 `Player.php` 的 `MOVE_PLAYER` 分支，语义是 `distanceSquared > 0.25`（= 0.5 格真实距离）。
- 确认没有其它移动类插件同时在纠正位置。
- 传送后第一个移动包被拉回是设计行为（`teleportPosition` 门控），不是 bug；但只在传送完成后的一小段时间内出现。

#### 2.1.2 飞行检测

**步骤**：
1. 正常跳跃、从高处摔落、黏液块连续弹跳、跳跃药水、坐船、爬梯子、游泳 —— 共 5 分钟。
2. 用飞行挂：离地 4 格以上静止悬停 10 秒。

**预期**：
- 正常场景无 Fly 违规。
- 悬停超过 3 格或滞空超过 40 tick 触发 Fly 告警（debug 日志，或 OP 收到提示）。

**失败排查**：`anticheat.yml` → `checks.fly.max-air-ticks`（默认 40）、`max-hover-height`（默认 3.0）。旧的内联反飞已删除，飞行只由 FlyCheck 判定，不会直接踢人。

#### 2.1.3 移动速度

**步骤**：
1. 正常疾跑 + 跳跃 5 分钟（约 5.6 格/秒）。
2. 开启加速挂跑 30 秒（约 10 格/秒）。

**预期**：
- 1 不告警；2 触发 Speed 告警。
- 网络抖动导致的单次大位移不应告警（滑动窗口取平均）。

**失败排查**：`checks.speed.max-sprint-speed`（默认 8.0）、`max-walk-speed`（6.0）。窗口长度可在 `checks.speed` 下加 `window-ticks`（代码默认 10）。

#### 2.1.4 摔落伤害规避（NoFall）

**步骤**：
1. 从 10 格高自由落体到地面（正常应受到摔落伤害）。
2. 用无摔落伤害挂重复同样落差。

**预期**：
- 1 不告警。
- 2 在落地后约 3 tick（宽限）触发 NoFall 告警。

**失败排查**：宽限是为消除「服务端伤害比客户端落地晚 1–2 tick」造成的误报；`checks.nofall.min-fall-damage`（默认 4.0）。死亡时状态会被重置，重生后不应残留旧下落距离。

#### 2.1.5 受伤/击退豁免

**步骤**：
1. 用 TNT 把自己炸飞，或 PvP 中被击退。
2. 观察随后 0.5 秒（10 tick）内是否出现 Speed/Teleport 告警。

**预期**：不告警（击退位移不算玩家主动加速）。

#### 2.1.6 alert / punish 模式

**步骤**：
1. 保持 `alert`，触发一次飞行。
2. `/ac mode punish`，再触发一次飞行。

**预期**：
- alert：只有日志/提示，玩家位置不被回退，不被踢。
- punish：累计到 `punishments.threshold`（默认 3）后执行回退/踢出。
- 重启服务器后 `/ac status` 显示回到 `anticheat.yml` 的值。

---

### 2.2 SEC-006 合成缺格绕过

**前置**：改包客户端或测试插件，能发送自定义 `CraftingEventPacket`。

**步骤**：
1. 用完整材料合成一次：粘土块（4 粘土）、荧石块（4 荧石粉）、工作台（4 木板）。
2. 用改包客户端只上报 1 个格子、1 份材料，请求同一配方。
3. 0.15 客户端走窗口事务路径再测一次，旧协议客户端也测一次。

**预期**：
- 1 合成成功，且材料正好扣 4 个。
- 2 请求被拒绝，服务端回滚背包（`sendContents`），不产生产物。
- 3 两种协议路径行为一致。

**失败排查**：`Player.php` 的 `CRAFTING_EVENT_PACKET` 分支。必需格子必须非空且与配方 `deepEquals`；扣除按服务端配方的材料表，而不是客户端上报的格子。

---

### 2.3 SEC-009 库存物品检测接入

**前置**：测试插件（见附录 A），`/ac debug on`。

**步骤**：
1. 执行 `/actest give100`，往自己背包第 0 格写 100 个钻石。
2. `/ac player <名字>` 查看 item 检测器。
3. 用 `/actest give64` 写 64 个钻石（合法数量）做对照。
4. 打开一个箱子，执行 `/actest chestbad` 往箱子里写入非法物品。
5. 把模式切到 `punish`，重复步骤 1，观察原有合法钻石堆叠是否被误删。

**预期**：
- 1：`setItem` 直接返回 false，物品不进入背包（`count > 64` 硬上限），同时 ItemCheck 记录一次违规 —— 「可见而不是静默拒绝」。
- 3：无违规。
- 4：不把违规归到开箱子的玩家身上（tile 库存不按 viewer 归因）。
- 5：堆叠类违规只告警，不执行 `removeItem`，玩家已有的合法同 ID 堆叠必须还在。

**失败排查**：`inventory/BaseInventory.php::setItem()`、`inventory/PlayerInventory.php::setItem()`、`anticheat/AntiCheat.php::onItemSet()`。`count > 64` 的判断必须在 `getMaxStackSize()` 拒绝之前，否则工具/盔甲（单堆叠上限 1）会绕过上报。

---

### 2.4 SEC-013 攻击面

**前置**：带 Fire Aspect 的剑；改包客户端（用于超距攻击）；两头成年牛、小麦/胡萝卜/种子。

#### 2.4.1 Fire Aspect 不再提前点燃

**步骤**：
1. 站在目标 7 格外，用改包客户端发攻击包（会被距离检查取消）。
2. 站在 4 格内正常攻击。
3. 连续快速点击攻击（触发冷却丢弃）。

**预期**：
- 1、3：目标不点燃。
- 2：目标点燃。

**失败排查**：`Player.php` 的 `INTERACT_PACKET` 分支。`setOnFire` 已移到攻击落地且事件未被取消之后，冷却 `break`、超距取消、事件取消三条路径都到不了点燃逻辑。

#### 2.4.2 攻击距离

**步骤**：站在 7 格外攻击。

**预期**：攻击被取消（同时 ReachCheck 也可能告警）。

**说明**：服务端距离上限从 8 格收紧到 6 格（`canInteract($target, 6)`）。

#### 2.4.3 繁殖需要对应食物

**步骤**：
1. 空手右键成年牛。
2. 手持泥土右键成年牛。
3. 手持小麦右键成年牛（生存模式）。
4. 创造模式重复步骤 3。
5. 右键幼年牛。
6. 对已经冒爱心的牛再喂一次小麦。

**预期**：
- 1、2：无爱心，不消耗物品。
- 3：出现爱心，小麦减 1。
- 4：出现爱心，小麦不减少。
- 5：小牛不瞬间长大。
- 6：不再消耗小麦（已处于求爱状态）。

**食物映射**：羊/牛/蘑菇牛 = 小麦，猪 = 胡萝卜，鸡 = 种子。

---

### 2.5 SEC-014 防快吃

**前置**：`/ac debug on` 不必开；需要能连续发送 `USE_ITEM` 的改包客户端或测试插件。

**步骤**：
1. 连发 100 次进食包（间隔远小于 1.5 秒）。
2. 正常长按吃完一个食物，再吃第二个。
3. 创造模式吃蘑菇煲。
4. 生存模式吃蘑菇煲。
5. 在 `gameplay.yml` 设置 `server.anti-fast-eat: false` 后重复步骤 1。

**预期**：
- 1：最多每 1.5 秒生效一次，被拒绝的包服务端回滚背包（`sendContents`）。
- 2：正常进食完全不受影响。
- 3：食物不减少，也不返还空碗。
- 4：食物减 1，返还 1 个空碗。
- 5：恢复旧行为（限速关闭），可作对照。

**失败排查**：`Player::EAT_MIN_INTERVAL = 1.5`，检查点在 `eatFoodInHand()` 的消耗分支；限速记录按成功进食时间，被拒绝的包不更新记录（不会卡死）。`anti-fast-eat` 是 `gameplay.yml` 里的 `server.anti-fast-eat`。

---

### 2.6 SEC-017 告示牌异常不再被吞

**前置**：测试插件注册一个 `SignChangeEvent` 监听器，直接 `throw new \RuntimeException("sign-test")`。

**步骤**：编辑一块告示牌并确认。

**预期**：
- 服务器日志出现该异常与堆栈（`logException`）。
- 玩家不会被踢，其它插件继续正常工作。

**失败排查**：`Player.php` 的 `BLOCK_ENTITY_DATA` 分支 `catch(\Throwable $e)` 内应有 `$this->server->getLogger()->logException($e)`。

---

### 2.7 备份调度（启动不再连续备份）

**步骤**：
1. 停服，清空 `backups/.state/`（保留或删除已有快照均可）。
2. 启动服务器，观察前 3 分钟。
3. 停服 2 小时后再启动，观察前 3 分钟。
4. 检查 Android 上的备份日志（`链接:0 复制:1555`）。

**预期**：
- 2：不会出现 1min→5min→30min→1h→1day→1month 的连续备份。若启动时存在已过期的时段，控制台会出现一条「启动对齐：已错过的时段 [...] 不补跑，按原周期继续」info；全新状态或无过期时段时不输出该行。
- 3：错过的时段不补跑；按原周期相位继续（例如 1min 档在启动后约 30 秒对齐）。
- 4：本机 `link()` 返回 Permission denied 属于文件系统限制，每次都是完整复制；建议删除或调大 `1min` 时段。

**说明**：
- `tick: 30` 是调度检查粒度（每 30 秒扫一次哪些时段到期），不是「每 30 秒备份一次」。
- 例行「开始备份 / 备份完成」日志已降为 debug，默认不可见；失败仍是 error，磁盘不足仍是 warning。
- 单元测试 `tests/BackupScheduleTest.php` 已覆盖对齐算法的纯逻辑，这里验证的是真实启动行为。

---

### 2.8 其它低危项

| 项目 | 步骤 | 预期 |
|---|---|---|
| `/timings paste` | 执行命令 | 提示上传功能已移除，改用 `/timings report`；不再静默无响应 |
| `Player::getInAirTicks()` | 测试插件调用并打印 | 恒为 0（旧反飞删除后无递增点），调用不报错 |
| 已删语言键 | 触发对应旧路径 | 旧反飞、旧秒吃提示已不存在，不会出现缺翻译报错 |

---

## 3. 勾选清单

| 编号 | 项目 | 需要的工具 | 预计耗时 | 结果 |
|---|---|---|---|---|
| BUG-002-1 | 普通移动不回弹 | 无 | 15 min | [ ] |
| BUG-002-2 | 飞行检测（正/反例） | 飞行挂 | 10 min | [ ] |
| BUG-002-3 | 速度检测（正/反例） | 加速挂 | 10 min | [ ] |
| BUG-002-4 | NoFall（正/反例） | 无摔落挂 | 10 min | [ ] |
| BUG-002-5 | 击退/爆炸豁免 | TNT 或 PvP | 5 min | [ ] |
| BUG-002-6 | alert/punish 切换 | 飞行挂 | 5 min | [ ] |
| SEC-006 | 合成缺格拒绝 | 改包/测试插件 | 20 min | [ ] |
| SEC-009 | 库存检测与不误删 | 测试插件 | 20 min | [ ] |
| SEC-013-1 | Fire Aspect 时序 | 改包 | 10 min | [ ] |
| SEC-013-2 | 攻击距离 6 格 | 改包 | 5 min | [ ] |
| SEC-013-3 | 繁殖食物/幼崽/重复喂 | 无 | 10 min | [ ] |
| SEC-014 | 进食限速 | 改包/测试插件 | 15 min | [ ] |
| SEC-017 | 告示牌异常入日志 | 测试插件 | 5 min | [ ] |
| BACKUP | 启动不连续备份 | 无 | 15 min + 停机 2h | [ ] |
| LOW | timings paste / getInAirTicks | 无/测试插件 | 5 min | [ ] |

---

## 4. 记录模板

```text
验证日期：
验证人：
构建来源（分支/commit）：
服务器环境（系统 / PHP 版本 / 客户端版本）：

| 编号 | 结果 | 证据（日志片段 / 命令输出 / 截图） | 备注 |
|---|---|---|---|
| BUG-002-1 | PASS / FAIL / SKIP | | |
| ... | | | |

遗留问题：
```

判定说明：
- **PASS**：按「预期」全部符合。
- **FAIL**：与预期不符，附最小复现步骤与日志。
- **SKIP**：缺少工具（如改包客户端），注明原因，后续补测。

---

## 附录 A：测试插件骨架

`plugins/AuditProbe/plugin.yml`：

```yaml
name: AuditProbe
main: AuditProbe\Main
version: 1.0.0
api: [2.0.0]
commands:
  actest:
    description: audit verification helper
    usage: "/actest <give100|give64|chestbad|syncthrow|airticks>"
```

`plugins/AuditProbe/src/AuditProbe/Main.php`：

```php
<?php
namespace AuditProbe;

use pocketmine\command\Command;
use pocketmine\command\CommandSender;
use pocketmine\event\block\SignChangeEvent;
use pocketmine\event\Listener;
use pocketmine\item\Item;
use pocketmine\Player;
use pocketmine\plugin\PluginBase;

class Main extends PluginBase implements Listener{

    public function onEnable(){
        $this->getServer()->getPluginManager()->registerEvents($this, $this);
    }

    public function onCommand(CommandSender $sender, Command $command, $label, array $args){
        if(!($sender instanceof Player)) return true;
        $cmd = isset($args[0]) ? $args[0] : "";
        switch($cmd){
            case "give100":
                $ok = $sender->getInventory()->setItem(0, Item::get(Item::DIAMOND, 0, 100));
                $sender->sendMessage("setItem(100) = " . ($ok ? "true" : "false"));
                break;
            case "give64":
                $ok = $sender->getInventory()->setItem(0, Item::get(Item::DIAMOND, 0, 64));
                $sender->sendMessage("setItem(64) = " . ($ok ? "true" : "false"));
                break;
            case "chestbad":
                // 请先打开一个箱子，再把非法物品写进它
                foreach($sender->getLevel()->getTiles() as $tile){
                    if($tile instanceof \pocketmine\tile\Chest and $sender->distance($tile) < 5){
                        $tile->getInventory()->setItem(0, Item::get(Item::DIAMOND, 0, 100));
                        $sender->sendMessage("chest setItem(100) done");
                        return true;
                    }
                }
                $sender->sendMessage("no chest nearby");
                break;
            case "syncthrow":
                $sender->sendMessage("sign listener armed");
                break;
            case "airticks":
                $sender->sendMessage("getInAirTicks() = " . $sender->getInAirTicks());
                break;
            default:
                $sender->sendMessage("/actest <give100|give64|chestbad|syncthrow|airticks>");
        }
        return true;
    }

    public function onSignChange(SignChangeEvent $event){
        // 用于 SEC-017：确认异常被 logException 记录且玩家不被踢
        throw new \RuntimeException("sign-test");
    }
}
```

注意：
- `syncthrow` 只是提示；真正的触发是编辑告示牌（监听器一直生效）。测试完请禁用插件，避免正常编辑告示牌时报错。
- 协议层项目（伪造 `CraftingEventPacket`、连发 `USE_ITEM`、7 格攻击）插件无法完全模拟，需要改包客户端；插件只能覆盖服务端 API 层（`setItem`、`getInAirTicks`、事件监听）。


---

## 5. InCore-II 移植项人工验证（分支 `feat/incore-port`）

> 本节只覆盖从 `docs/incore-II/` 移植过来的模块。移植文件保留上游 LGPLv3 版权头，行为差异见各小节说明。
> 离线用例已覆盖解析/算法层，下面这些必须在真实服务器上确认。

### 5.1 A1 反矿透（Anti-Xray）

配置文件：`src/pocketmine/resources/world.yml` 顶层 `anti-xray:` 节（旧版为 `pocketmine.yml`，启动时自动迁移）。

```yaml
anti-xray:
 enabled: true
 mode: obfuscator        # obfuscator=伪矿, hidden=只隐藏真矿
 scan-chunk-height-limit: 4
 overworld-fake-block: 1 # STONE
 nether-fake-block: 87   # NETHERRACK
 worlds: [world]
 ores: []
 filters: []
```

验证步骤：

1. 只把 `worlds` 设成主世界名，`enabled: true`，重启服务器。
2. 用客户端在主世界地下挖一条直隧道，观察新生成的区块：
   - `mode: obfuscator`：隧道两侧会周期性出现金/铁/煤/青金石/钻石/红石/绿宝石/石英矿纹理，挖掉后变回石头类方块（假矿）。
   - `mode: hidden`：真实矿物在客户端不可见，挖到该位置才显示。
3. 走出 `worlds` 列表的世界（或把 `worlds` 改成别的名字重启），确认没有伪矿，说明世界过滤生效。
4. 观察 `scan-chunk-height-limit`：设为 1 时只有最下面两层（y≤31）被处理，设为 15 时覆盖到 y=126。
5. 性能：10 人以上在线时看 TPS，首次进服/新区块生成会有额外 CPU，但缓存命中后不再重复计算。

注意：
- 混淆发生在 `Level::chunkRequestCallback()` 写入 chunkCache 之前，缓存里存的就是混淆后的包；改配置后需要重启（配置按世界懒加载缓存）。
- 只处理 x/z 1..14 的内部方块，区块边界不处理，这是上游行为。
- 末地维度在 Zenith 不存在，`nether-fake-block` 只对 `DIMENSION_NETHER` 生效。

### 5.2 A2 配置文件编码修复（Config）

覆盖场景（每次改完 `server.properties` 都要重启服务器）：

1. **GBK 中文**：用记事本/GBK 编辑器写 `motd=我的服务器`，保存为 GBK。启动后 `/say` 或看服务器列表 MOTD，应为正确中文，且 `server.properties` 不会被写成乱码。
2. **UTF-8 BOM**：用工具给 `server.properties` 加 BOM，启动后第一个键不应变成 `\ufeffmotd`。
3. **UTF-16**：另存为 UTF-16 LE/BE，启动后中文应正常（会被转成 UTF-8 落盘）。
4. **`\uXXXX` 转义**：手写 `motd=\u4f60\u597d`，启动后应显示「你好」。
5. **不反复重写**：启动后连续 `/reload` 或等自动保存，`server.properties` 的修改时间不应每次变化（内容没变就不写盘）。
6. **非法 UTF-8**：塞入乱码字节，服务器不应崩溃，应保留原内容并记录告警。

已知启发式风险：某些“恰好也是合法 GBK 的 UTF-8 文本”可能被当成乱码修复，验证时留意中文是否被改动。

### 5.3 A4 目标选择器（`/tp` `/kill` `/effect`）

前置：OP 账号；`server.yml` 的 `settings.player-tp` 保持默认。

| 命令 | 预期 |
|---|---|
| `/tp @s 100 70 100` | 自己传送到坐标 |
| `/tp @a 0 70 0` | 所有玩家（含自己）传送到出生点附近 |
| `/tp @p 0 70 0` | 最近玩家传送 |
| `/tp @r 0 70 0` | 随机一名玩家传送 |
| `/tp @e[type=Zombie,r=10] 0 70 0` | 10 格内所有僵尸被传送 |
| `/kill @e[type=Zombie,r=20]` | 20 格内僵尸全部死亡，每个都有死亡消息 |
| `/kill @a[name=!Steve]` | 除 Steve 外全部死亡 |
| `/effect @a slowness 10 1` | 所有玩家获得 10 秒缓慢 II |
| `/effect @e[type=Zombie] clear` | 范围内僵尸效果清空 |
| `/tp @x 0 70 0` | 报错：未知选择器 |
| `/kill @e[scores={a=1}]` | 报错：不支持 scores |
| `/kill @e[tag=foo]` | 报错：不支持 tag |

权限相关：

1. 非 OP 玩家执行 `/tp @a 0 70 0`：在 `player-tp` 为 `op`/`false` 时应被拒绝；为 `true`（开放）时，选择器只能作用于自己，作用于他人应被拒绝。
2. 非 OP 执行 `/kill @a`、`/effect @a ...`：分别需要 `kill.other`、`effect.other`，默认应失败。

注意：
- 选择器只在命令执行者所在世界内匹配（控制台使用默认世界）。
- `@e` 会匹配非玩家实体，`/kill`、`/effect` 对它们同样生效；装了假设目标一定是 Player 的第三方插件时留意异常。
- `type=` 依赖 `NETWORK_ID`，插件自定义实体只能按类名匹配。

### 5.4 A3 洞穴与峡谷生成（Cave populator）

`src/pocketmine/level/generator/populator/Cave.php` 已整体换成 InCore-II 的 734 行实现（`normal` 生成器原本就注册了它，无需改 `Normal.php`）。

验证步骤：

1. 新建一个 `level-type=normal` 的世界，飞下去观察：
   - 洞穴应更曲折、有分支与房间，而不是单条直线隧道；
   - 沙漠地下应出现砂岩/石头过渡，而不是悬空沙子；
   - 峡谷（ravine）应成片出现，且不会切断地表到虚空。
2. 用 `/seed` 记录种子，删档重建同种子世界，确认洞穴形态一致（确定性）。
3. 观察液体：洞穴穿过水/岩浆时不应出现整片液体倒灌或方块残缺。
4. 性能：新区块生成在 `PopulationTask` 异步线程里跑，单区块实测约 60–150 ms（开发机），10 人同时探索时看 TPS 与区块生成队列是否堆积；低配主机若明显卡顿，可考虑把 `settings.async-workers` 调高。

注意：
- `Cave::populate()` 会重置传入的 `Random` 状态（上游行为）。当前它是 `generationPopulators` 的最后一个，不要在后面追加依赖随机序列的 populator。
- `gorigional` 生成器的 `MapGenCaves` 未改动，两套洞穴实现并存。

### 5.5 A5 下界传送门（NetherPortalHelper）

配置：`world.yml` 的 `nether:` 节（旧版为 `pocketmine.yml`）。

```yaml
nether:
 enabled: false      # 允许点燃传送门 + 传送
 allow-nether: false # 启动时加载/生成下界维度（旧键，逻辑与 enabled 相同地参与点燃判断）
 level-name: "nether"
```

验证步骤（必须先 `allow-nether: true` 并重启，让下界维度真正存在）：

1. 主世界用黑曜石搭 4×5 最小框架（内部 2×3），打火石点燃 → 生成紫色传送门方块。
2. 站进去 4 秒左右 → 传送到下界，坐标按 1:8 缩放（主世界 x/z ÷ 8）。
3. 下界再点燃一个门、返回主世界 → 坐标放大 8 倍；若目标位置没有门，应落在安全落点而不是虚空/岩层里。
4. 破坏框架后门消失；框架不完整（缺角、内部尺寸不对）时打火石不应生成门。
5. `enabled: true` 但 `allow-nether: false` → 可以点燃，但进入后不传送，日志出现一行 `[NetherPortal]` 告警，玩家不被踢。
6. 主世界 `enabled: false` → 打火石对黑曜石无反应，原有 `/setblock` 等逻辑不受影响。

注意：
- 目标世界缺失时 helper 只警告一次并 no-op，不会抛异常；排查时先看 `nether.level-name` 是否与 `world.yml` / `server.properties` 里实际世界名一致。
- 传送门不会在目的地自动建门（与旧实现一致）。

### 5.6 B2 拴绳与马具

新增物品：拴绳 420、命名牌 421、鞍 329、胡萝卜钓竿 398、皮革/铁/金/钻石马铠 416–419。
已在 `Item::$list` 与 `Server::registerEntities()`（`LeashKnot`）注册。

验证步骤（客户端需 0.15+，`Lead` 内部有协议门控，0.14 客户端用不了）：

1. `/give <name> 420`（或创造模式背包）拿到拴绳，右键牛/羊/马 → 动物被牵引，走远会跟过来。
2. 手持拴绳右键栅栏 → 生成拴绳结，动物被固定在栅栏上；再右键动物或走远 → 解开。
3. 拴着的动物受伤/死亡 → 拴绳结自动消失；栅栏被拆 → 结与拴绳一起消失（掉落拴绳）。
4. 拴绳结重启服务器后仍在原位（NBT `Leashed`/`Leash` 持久化）。
5. 马铠：右键马给马穿装备，潜行右键取下（走 `Horse::onInteract`，B2 未改）。
6. 命名牌改名：**未实现**（需要文本输入 UI），手持命名牌右键只会走默认交互。

注意：0.14 客户端收到 420/421 物品 ID 会显示异常，别在混合版本服上发这些物品；`LeashKnot` 只有一根栅栏支撑时不会被误删。

### 5.7 B3 自然刷怪

新增 `entity/NaturalMobSpawnRules.php`、`entity/AgeableSpawnHelper.php`，`Level::doTick()` 中调用 `tickNaturalMobSpawning()`，只做**陆地**刷怪。

验证步骤：

1. 夜晚在草地（光照 ≤7）等待，应刷新僵尸/骷髅/蜘蛛/苦力怕；白天不应刷新敌对生物。
2. 平原/森林/沙漠生物群系刷被动生物（羊/牛/猪/鸡），数量受 `spawn-limits.animals` 限制。
3. `world.yml` 的 `spawn-limits.monsters` 改成 5 → 世界内敌对生物明显减少；改回 70 恢复。
4. 难度设为和平 → 不再刷敌对生物；简单/普通/困难的刷怪间隔分别约 600/360/240 tick。
5. `ticks-per.monster-spawns` 调小不会低于难度默认周期（有下限保护）。
6. 观察 `EntityGenerateEvent`：装了监听器的插件应能收到 `CAUSE_AI_HOLDER` 并取消刷怪。

注意：
- 水中/洞穴/下界刷怪未移植；下界仍依赖原有逻辑（如果有）。
- 只在已生成的区块内刷怪，新区块生成时不会刷。
- 若某插件依赖“世界不刷怪”，可用 `Server::isWorldNaturalMobSpawnDisabled` 或世界配置关闭。

### 5.8 B6 光照更新队列（LightUpdate / SkyLightUpdate）

新增 `level/light/LightUpdate.php`、`level/light/SkyLightUpdate.php`（InCore-II LGPLv3 头），
`Level::updateBlockSkyLight()` 由空实现改为入队，`Level::doTick()` 在 `tickChunks()` 之后调用
`executeQueuedLightUpdates()` 排空队列。

**默认关闭**：只有 `server.properties` / `world.yml`（也可用 CLI `--chunk-ticking.light-updates=true`）
设置 `chunk-ticking.light-updates: true` 时才走队列；关闭时 `updateBlockSkyLight()` 保持历史上的 no-op，
世界光照数据与升级前完全一致。

验证步骤（开启 `chunk-ticking.light-updates: true` 后重启）：

1. 白天在露天空地放一块 3x3 的石头，石头下方与侧面应出现阴影（亮度下降），拆掉后恢复 15。
2. 在屋顶下挖一个 1 格洞通到天空，洞底与周围 2-3 格应逐渐变亮；填回方块后恢复原亮度。
3. 在沙漠/雪原等不同光照条件下的地形边缘走动，观察是否有 15/0 硬边或“方块自带亮圈”残留。
4. 打开 F3 等效的调试（或插件读 `getBlockSkyLightAt`）对比客户端显示，确认服务端值 = 客户端值。
5. 大范围改动（爆炸、`/fill` 大区域）后等待 1-2 tick，不应出现长时间卡顿或光照不收敛。
6. 关闭开关重启 → 上述行为回到升级前（放方块不再改变天空光，与旧版一致）。

注意：
- 队列只覆盖**天空光**；方块光仍走 `Level::updateBlockLight()` 的即时传播，未改动。
- 未移植 InCore-II 的 `markLightChunkDirty()` / 光照 chunk 重发策略，改动后的天空光要等区块重新下发
  （玩家走远再回来、或重新登录）才会在客户端完整刷新；如果上服发现“服务端对、客户端不对”，优先查这里。
- 未移植 `computeSkyLightReduction()` / `computeSunAnglePercentage()`（昼夜对天空光的整体削减），
  即夜晚的天空光数值与白天相同，仅为队列机制。
- 每改一个方块会重算该列 128 格，单次改动开销明显高于旧版；若 TPS 下降应关闭该开关。

### 5.9 B4 结构补全

- **沙漠神殿**：改为 32x32 区块一个确定性候选点（原版 spacing 32），同一 seed 可复现；
  神殿基座取中位面并包含砂岩层。
- **村庄 / 掠夺者前哨站 / 林地府邸**：生成时写入的延迟生物标记现在会在区块生成后消费，
  生成村民（含职业）、铁傀儡、流浪者、掠夺者僵尸（钻石甲 + 保护 III + 名称“掠夺者”）、
  府邸替代生物（Evoker→女巫、Vindicator→僵尸、Allay→蝙蝠，本分支无原版对应实体）。
- **丛林神庙**：核对确认发射器陷阱（2 个发射器 + 绊线 + 2–8 支箭）原本已完整，未重复移植。

验证步骤：

1. 同一 seed 新开世界，在不同区域找到的沙漠神殿位置应稳定；`/seed` 不变时多次新建世界位置一致。
2. 进入沙漠神殿，踩绊线应触发射手箭；破坏发射器可见内部箭矢。
3. 新生成村庄应有村民（职业随机但同 seed 稳定）与铁傀儡；`/kill @e[type=villager]` 后重新生成区块不再补村民（标记已消费）。
4. 掠夺者前哨站应出现头顶“掠夺者”的僵尸，穿钻石头盔/胸甲/护腿/靴子且带保护 III。
5. 林地府邸内的替代生物为女巫/僵尸/蝙蝠，属已知差异。
6. 世界关闭自然刷怪（`spawn-limits` 或插件）时，结构生物也不再生成。

注意：沙漠神殿密度从旧逻辑（约 1/43 区块）降到 1/1024 区块，这是 InCore-II 的确定性布局语义；
若需要高密度可回退 `object/Temple.php` 的随机闸门，但重生成一致性会丢失。

### 5.10 B1 活塞

新增方块 29 粘性活塞 / 33 活塞 / 34 活塞头 / 36 移动方块，`PistonArm` tile，
以及 `BlockPistonEvent` 与推拉音效。受 `redstone.enable`（默认 false）总开关约束。

验证步骤：

1. 开启红石（`gameplay.yml` 的 `redstone.enable: true`）重启。
2. 放置活塞 + 拉杆：通电后活塞头伸出，断电收回；粘性活塞收回时把方块拉回。
3. 推 1–12 格方块链：超过 12 格不应推动并应发出失败音效（无动画）。
4. 黑曜石/基岩/刷怪笼等不可推方块：活塞不应推出方块头。
5. 斜向/上方/下方朝向：用 `/setblock` 或放置时的朝向验证六个方向。
6. 推动带容器方块（箱子）：方块瞬移，箱子内容物应随之迁移（NBT 复制）；若内容丢失请记录。
7. 活塞被破坏（`/setblock` 空气）后，用区块调试确认没有残留 `PistonArm` block entity。
8. 高频通断（1 tick 拉杆）观察 TPS 与发包量——本移植没有 InCore 的高频红石限流。

注意：
- 本移植**没有移动方块过渡动画**，被推方块是瞬移；`MovingBlock` 仅作为方块 ID 注册。
- MCPE 0.14 客户端对 33/34/36 与活塞臂 tile 的渲染需上服确认；0.12 及以下由兼容层映射为其他方块。
- 活塞臂靠 scheduled tile update 推进（每步 0.25，4 tick 完成），每个动作会多花约 1 tick。

### 5.11 0.11–0.13 旧版玩法补丁（Player）

覆盖：0.11 铁砧/附魔台会话、0.11 自动疾跑与深海探索者、0.11 合成包反查、0.11 直接进食、
0.13 配方克隆、0.12/0.13 村民交易关窗、0.11–0.13 延迟创造模式。

验证步骤（分别用 0.11 / 0.12 / 0.13 / 0.14 客户端各登录一次）：

1. 0.11 手持物品点击铁砧：空手应收到说明；手持附魔书再点目标物品应扣经验并附魔；
   手持同类物品/材料再点目标物品应修复并扣材料；铁砧有概率损坏/消失。
2. 0.11 点击附魔台：第一次显示报价，第二次才扣青金石与等级；换物品应提示重来。
3. 0.11 移动时不发疾跑动作也应自动疾跑；穿深海探索者靴子在水里速度应提升；
   0.14 客户端不得出现速度异常。
4. 0.11 打开工作台/切石机后合成，服务端应能按 5 槽或 10 槽包反查配方并扣材料；
   0.14 合成行为与改动前一致。
5. 0.11 长按食物应能正常恢复饥饿值，且无法高频快吃。
6. 0.12/0.13 与村民完成交易后窗口应关闭且不再自动重开；背包临时槽位应还原。
7. 0.11–0.13 创造模式玩家应先以生存进入世界，约 1 秒后切回创造；0.14 不得出现该延迟。

已知限制/需重点观察：

- 0.12/0.13 村民关窗复用 `VillagerTradeInventory::onClose()`：关闭包未显式设置
  `CHANNEL_WORLD_EVENTS`，且假方块移除是立即发送而非 InCore 的延迟 4 tick；
  若客户端残留交易方块或窗口关不掉，优先查这里。
- 自动疾跑当前固定开启，未移植 InCore 的 `/sprint` 开关与 NBT 持久化。
- 未移植 0.11 的 HUD/状态提示、手持附魔提示、潜行开关，以及 0.13 的隐藏容器物品
  拦截/配方输入归一化；这些不属于本次 Player 玩法补丁范围。
- 0.13 配方克隆函数已移植但当前无调用点：Zenith 由
  `ProtocolCompatibility::mapCraftingRecipeForProtocol()` 在 `Server.php` 统一处理配方映射。

### 5.12 安装向导：生成器 / 种子 / 超平坦预设

覆盖：`wizard/Installer::generateBaseConfig()` 新增的世界生成器菜单、`level-seed` 与超平坦预设。
离线部分由 `tests/InstallerTest.php`（37 项）验证，交互与真实生成需上服确认。

验证步骤（删掉 `config/server.properties` 后首次启动，或加 `--no-wizard` 前先手动删）：

1. 生成器直接回车 → `config/server.properties` 里 `level-type=gorigional`，世界按 1.7.10 风格地形生成。
2. 选 `2`（normal）→ `level-type=normal`，新世界按 PocketMine 经典地形生成，
   用于对比两种生成器的观感差异。
3. 选 `3`（flat）→ 预设先输入乱码应提示格式错误并重新询问；输入
   `2;7,2x3,2;1;` 后 `generator-settings` 写入该值，世界为经典三层超平坦 + 草方块。
4. 选 `4`（void）→ `level-type=void`，世界只有出生平台。
5. 输入合法世界名但对应 `worlds/<name>` 已存在 → 应提示「生成器与种子只对新建世界生效」。
6. 种子留空 → 不写 `level-seed`；填 `12345` → 写入且重开服务器后地形一致。
7. 语言切到 `eng`，上述菜单与提示应显示英文（未被翻译的语言回退英文）。

已知限制：

- 向导只写 `config/server.properties`；`world.yml` 的 `worlds.<name>.generator` 优先级更高，
  若该文件里已给同名世界配了生成器，会覆盖向导选择。
- 已存在的世界读取存档 `level.dat` 里的生成器，向导改 `level-type` 不会改变它。
- `gorigional` 的高度场已按 Java 1.7.10 对齐（128 高截断），数值与 MCPE 0.14 原版不同，
  差异对照见 `docs/gorigional-1.7.10.md`。**已用旧 gorigional 生成过的世界，新老区块会有高度接缝**，
  要统一观感需要新建世界。

### 5.13 gorigional：洞穴 / 峡谷 / 基岩 / 村庄（对齐 1.7.10）

覆盖：`gorigional` 的 A/B/C 三批改动（详见 `docs/gorigional-1.7.10.md`）。
离线部分由 `tests/GorigionalConstantsTest.php`（10 项）与 `tests/GorigionalChunkTest.php`（14 项）验证。

1. 新建 `level-type=gorigional` 的世界，挖到基岩：每列基岩应为 **1..5 层**（旧版是 2..5 层）。
2. 地下探索：洞穴应是细长隧道 + 少量大厅，**约每 7 个节点才 1 个真正生成**（旧版约 1/15 且算法不同），
   洞穴不应整体穿透到基岩以下，y<10 的洞底应为岩浆。
3. 地表：应能看到明显的峡谷（ravine），开口率约 1/50 候选原点；旧版完全不生成。
4. 平原/沙漠/热带草原上寻找村庄：间距应比旧版更密（32 区块网格 / 8 间隔，旧版 40/28）。
5. 同一 `level-seed` 连开两次新世界，出生点附近地形、洞穴、村庄位置应完全一致（确定性）。
6. 生物群系交界处地表方块正常（草/沙/砂砾），水面结冰只出现在寒冷群系。
7. 观察 y=120 以上：不应出现悬空草方块（洞穴 y 上限钳到 120）。
8. 若用旧 gorigional 世界的旧存档继续玩，新老区块交界会有高度/洞穴接缝，这是预期行为。

### 5.14 生物移动：台阶 / 贴墙 / 起跳

覆盖：`Mob::$stepHeight = 1.0` 与 `Mob::moveInDirection()` 的贴墙滑行修复
（详见 `docs/ai-java-mcpe.md` 第 10 节）。离线部分由 `tests/MobStepTest.php`（10 项）验证。

1. 让牛/猪/羊在平原上跟随手持小麦：**全程不应起跳**，走过 1 格高的土坎时应平滑抬升。
2. 在一格高的台阶（如石台阶）前观察：生物应直接走上去，不应出现「反复跳、原地回弹」。
3. 把生物引到 2 格高的墙边：生物应停在墙前或沿墙滑行，不应尝试起跳，也不应穿墙。
4. 观察行走速度：平地直行时步频应稳定，不应出现「走一下停一下」。
5. 让生物斜向撞墙：应沿墙滑行寻找缺口，而不是原地抖动。
6. 若开启了 AI（`gameplay.yml` 的 `ai.enabled`），关掉后生物仍应保持原来的静止行为。

### 5.15 末影人/生物贴脸时的黑色三角

覆盖：相机进入客户端模型导致的黑色长刺，以及重复 `AddEntityPacket`
（详见 `docs/enderman-render-issue.md`）。离线部分由 `tests/MobSeparationTest.php`（19 项）与
`tests/MetadataEncodingTest.php`（8 项）验证。

1. 主动走进末影人身体：不应出现从屏幕边缘汇聚到准心的黑色三角，生物应被缓慢推开。
2. 站着不动等末影人走到面前：相机不应进入其模型内部，不应出现大面积黑色几何。
3. 远处观察末影人：模型比例正常，不应躺倒、拉伸或出现“第二个残影”。
4. 反复走远再靠近同一只末影人、以及死亡重生后：不应出现多出来的黑色模型。
5. 用拴绳牵牛/马走一段：拴绳应正常显示（元数据 LONG 编码修复的回归验证）。
