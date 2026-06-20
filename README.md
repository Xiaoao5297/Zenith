# Incore-PRO

> 这个核心还在基础阶段，如果您发现了 bug 或者有宝贵的建议，请提交 Issues！

## 介绍

Incore 是一个基于 PocketMine-MP 的核心，基于 SCAXE 和 Genisys。

特点：
- 支持 `PHP 7.3` - `7.4`
- 推荐使用 `PHP 7.3`
- 兼容 PocketMine-MP 插件生态
- 继承 axe 核心的优化思路，结合 Genisys 的架构设计

说明：
Incore 适合希望在 PocketMine-MP 上运行稳定服务器的开发者，尤其是需要兼容 PHP 7.3 的环境。

---

## 使用方法
下面给出快速上手与常见操作步骤：

1. 环境准备
	- `PHP 7.3` 或 `7.4`（推荐 `7.3`）
	- 安装并配置好 `Composer`（用于管理依赖，可选）
	- 建议在 Linux 系统或类 Unix 环境下部署

2. 获取 Incore
	- 通过 git 克隆仓库：
    ```sh
	  git clone https://github.com/Xiaoao5297/Incore-Pro.git
    ``` 
	- 进入目录：
	  `cd Incore-Pro`

3. 配置 PocketMine-MP
	- 下载并解压 PocketMine-MP 到服务器目录，确保 PHP 版本兼容。
	- 将 Incore 的核心文件放入 PocketMine-MP 的 `src` 目录或按项目 README 指示替换对应文件。

4. 启动服务器
	- 进入 PocketMine-MP 根目录，执行：
	  `./start.sh`
	- 若使用自定义启动脚本或 Supervisor，请按各自方式启动。

5. 常见操作
	- 更新 Incore：在仓库根目录执行 `git pull` 并重启服务器。
	- 安装插件：将插件放入 `plugins` 目录，重启或使用 `reload`（不推荐在生产服热加载）。

如需示例配置或更详细的部署流程，请查看仓库内其他文档或 issues。
