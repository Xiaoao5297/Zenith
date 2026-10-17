# MCPE 0.14.3 结构收尾（Phase 8）

## 8.1 丛林神庙

- `MapGenScatteredFeature` 的允许群系增加 21（Jungle）、22（Jungle Hills）、23（Jungle Edge）。
- `ScatteredFeatureStart` 增加 23 分支，23 与 21/22 一样生成 `JunglePyramid`。
- seed 71 的候选区块 `13,3` 生成丛林神庙，bbox `208,64,48 .. 222,73,59`，包含发射器 2、箱子 2、石砖 1006 等。

## 8.2 战利品

- `StructureComponentBase::createLoot()` 增加 `desert` 与 `jungle` 表。
- 沙漠神殿 4 个箱子使用 `desert` 表。
- 丛林神庙主箱和隐藏箱使用 `jungle` 表，不再只放空箱子。
- 要塞箱子继续使用 `stronghold` 表。

## 8.3 测试环境修复

- `GeneratorWorld::setChestContents()` / `setSpawnerEntity()` 先判断 provider 是否为 `LevelProvider`，再判断 provider 的 Level。
- 修复 `Chunk::getEmptyChunk()` 传入字符串 provider 时调用 `getLevel()` 导致的 fatal。

## 8.4 跨区块结构

- seed 30 村庄 bbox `216,64,195 .. 283,85,291`，跨 x=13..17、z=12..18 多个区块。
- 逐区块 populate 后，圆石、木板、砂砾路、台阶、栅栏、门、火把等方块分布在多个区块，确认跨区块写入正常。

## 8.5 村庄组件

- 保留 MCPE 的房屋优先、道路兜底的展开顺序。
- 暂不人为限制组件数量；现有 44 组件和 9 条道路仍在可接受范围。
