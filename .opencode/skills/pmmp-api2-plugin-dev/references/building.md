# 构建与发布（API 2）

## 目录装配

一个可被服务端直接加载的插件源码目录结构：

```
PMPluginDemo/
├── plugin.yml
├── resources/
│   └── config.yml
└── src/
    └── Author/
        └── PMMPluginDemo/   # 注意：目录名应与命名空间一致
            ├── Main.php
            ├── EventListener.php
            ├── commands/
            │   └── GreetCommand.php
            └── tasks/
                └── BroadcastTask.php
```

服务端在 `plugins/` 下识别该目录即可热加载（开发期无需打包）。

## 打包成 .phar（用 DevTools）

1. 下载与 **API 2 服务端同代** 的 DevTools 插件（旧版 DevTools 对应 PMMP 2.x）。
2. 把 DevTools 放入服务端 `plugins/`，启动一次让其生成自身数据目录。
3. 将你的插件源码目录放入 `plugins/`。
4. 控制台执行：`/makeplugin PMPluginDemo`
5. 生成的 `PMPluginDemo.phar` 出现在 `plugins/` 下。

> ⚠️ DevTools 的 API 必须匹配你的目标 PMMP 版本。用于 API 2 的 DevTools 是旧版，
> 不要使用最新 DevTools（最新只支持 API 3+）。
> 若你手头只有新版 DevTools，可改用通用 phar 打包方式（见下）。

### 通用 phar 打包（不依赖 DevTools）

用 PHP 自带 `phar` 扩展打包，确保入口在 `plugin.yml` 的 `main` 可解析：

```bash
php -dphar.readonly=0 -r '
$phar = new Phar("PMPluginDemo.phar", 0, "PMPluginDemo.phar");
$phar->buildFromDirectory("PMPluginDemo");
$phar->setStub($phar->createDefaultStub("plugin.yml"));
'
```

（仅作示意；实际推荐仍用匹配版本的 DevTools 以保证 stub 与元数据正确。）

## 发布

- **Poggit**：现代 Poggit 仅接受 API 3+。API 2 插件通常**无法**通过 Poggit 发布，
  建议自行分发 `.phar` 或在对应社区（如旧版 PMMP 论坛/GitHub releases）托管。
- 分发时附上 `api` 与 `mcpe-protocol` 兼容说明，方便服主判断是否匹配其服务端。

## 调试建议

- 开发期直接放源码目录，改完重启服务端即可，省去打包。
- 开启服务端 `debug` 级别日志查看加载错误。
- 常见故障：`main` 类名/命名空间与 plugin.yml 不一致 → `Could not load plugin`；
  `api` 版本不符 → `Plugin requires API x but server has y`；缺依赖 → 检查 `depend`。
