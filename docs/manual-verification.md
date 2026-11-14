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
66/66 passed
```

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
- 高级开关在 `genisys.yml`（`server.*` 段读取）。
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
5. 在 `genisys.yml` 设置 `server.anti-fast-eat: false` 后重复步骤 1。

**预期**：
- 1：最多每 1.5 秒生效一次，被拒绝的包服务端回滚背包（`sendContents`）。
- 2：正常进食完全不受影响。
- 3：食物不减少，也不返还空碗。
- 4：食物减 1，返还 1 个空碗。
- 5：恢复旧行为（限速关闭），可作对照。

**失败排查**：`Player::EAT_MIN_INTERVAL = 1.5`，检查点在 `eatFoodInHand()` 的消耗分支；限速记录按成功进食时间，被拒绝的包不更新记录（不会卡死）。`anti-fast-eat` 是 `genisys.yml` 里的 `server.anti-fast-eat`。

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
