# 贡献指南 / Contributing Guide

感谢你对 Zenith 核心的关注！请阅读以下指南后再提交 Issue 或 PR。
Thanks for your interest in contributing to the Zenith core! Please read this guide before opening issues or PRs.

---

## 提交 Issue / Filing Issues

请使用 GitHub 的 Issue 模板，不要提交空白 issue：
Please use the GitHub issue templates instead of blank issues:

| 模板 / Template | 用途 / Purpose |
|---|---|
| Bug 报告 / Bug Report | Bug 与崩溃，请附复现步骤和日志 / Bugs and crashes, with repro steps and logs |
| 功能建议 / Feature Request | 新功能或改进建议 / New features or improvements |

提交前请：
Before filing:

1. **搜索重复 issue** / Search for duplicates first
2. 附上核心版本、PHP 版本（7.3/7.4）和插件列表 / Include core version, PHP version (7.3/7.4), and plugin list
3. 崩溃问题请附 `crash.log` 关键片段 / For crashes, include the relevant part of `crash.log`
4. 安全问题不要公开发布，请直接联系维护者 / Do not disclose security issues publicly — contact maintainers directly

## 标签约定 / Label Conventions

维护者会使用以下标签管理 issue（欢迎了解含义，方便跟进）：
Maintainers use these labels to triage issues (useful to know when following up):

### 优先级 / Priority

| 标签 / Label | 含义 / Meaning |
|---|---|
| `P0` | 紧急：崩溃、安全问题、核心不可用 / Critical: crashes, security, core unusable |
| `P1` | 高：主要功能异常 / High: major functionality broken |
| `P2` | 中：一般 bug、普通改进 / Medium: regular bugs, normal improvements |
| `P3` | 低：细节优化 / Low: minor polish and nice-to-haves |

### 类型 / Type

`bug`、`crash`、`enhancement`、`question`、`documentation`、`plugin-compat`（插件兼容性 / plugin compatibility）、`performance`

### 流程状态 / Workflow

`triage`（待分类 / awaiting triage）→ `confirmed`（已确认 / confirmed）或 `needs-repro`（需要复现步骤 / repro steps needed）→ 修复后关闭 / closed once fixed（`wontfix` 表示不打算修复 / won't be fixed）

标记 `good first issue` 的问题适合新贡献者上手 / Issues labeled `good first issue` are good entry points for new contributors.

## 提交 PR / Submitting PRs

1. 从 `main` 拉出新分支，分支名建议：`fix/...`、`feat/...`、`docs/...`
   Branch off `main`; suggested names: `fix/...`, `feat/...`, `docs/...`
2. 使用 [PR 模板](.github/PULL_REQUEST_TEMPLATE.md) 填写变更说明和自测清单
   Fill in the description and self-check list using the [PR template](.github/PULL_REQUEST_TEMPLATE.md)
3. 确保服务器能正常启动、无新增报错（见模板自测清单）
   Make sure the server starts normally with no new errors (see the PR self-check list)
4. 保持改动聚焦——一个 PR 解决一件事 / Keep changes focused — one PR, one concern
5. 关联对应 issue（`Closes #123`）/ Link the related issue (`Closes #123`)

## 代码风格 / Code Style

- 遵循项目现有的代码风格与命名习惯 / Follow the existing code style and naming conventions
- 兼容 PHP 7.3 语法（不要使用 7.4 以上特性，除非明确说明）/ Stay compatible with PHP 7.3 syntax (no 7.4+ features unless explicitly agreed)

---

有任何问题欢迎在 [Discussions](https://github.com/Xiaoao5297/Incore-Pro/discussions) 提出！
Questions? Ask in [Discussions](https://github.com/Xiaoao5297/Incore-Pro/discussions)!
