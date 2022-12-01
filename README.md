# find-author

![Build](https://github.com/LinWanCen/find-author/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/20557-find-author.svg)](https://plugins.jetbrains.com/plugin/20557-find-author)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/20557-find-author.svg)](https://plugins.jetbrains.com/plugin/20557-find-author)

## Plugin description 插件介绍

<!-- Plugin description -->
- Find authors of multiple files or lines from Git
- export find window as tsv table with authors (TODO)
- Find authors from comment (TODO)

## How to Use

1. Open Author ToolWindow at Right
2. Paste file:lineNum in <kbd>input</kbd>
3. click <kbd>Git Author</kbd>

### input
file support: fileName/filePath/classSimpleName
```
file
file:lineNum
xxx(file:lineNum)
```
### ignore
- LineNum blame not support <kbd>msg key word</kbd>, you can use <kbd>key word -> Rev ID</kbd>
- File log only select one of the <kbd>leave-user</kbd>, <kbd>msg key word</kbd>, <kbd>Rev ID</kbd>

# 中文说明

- 从 Git 中查找多个文件或行的作者
- 以带作者的表格形式导出搜索窗口（未完成）
- 从注释获取作者（未完成）

## 用法

1. 右边的打开<kbd>Author</kbd>工具栏
2. 粘贴 文件:行号 到 <kbd>输入</kbd>
3. 点击 <kbd>Git 作者</kbd>

### 输入
文件 支持：文件名/文件路径/类简称
```
文件
文件:行号
xxx(文件:行号)
```

### 忽略
- 行作者不支持<kbd>信息关键字</kbd>过滤，可以用<kbd>关键字获取参考ID</kbd>按钮
- 文件作者只能选择<kbd>离职用户</kbd>、<kbd>信息关键字</kbd>、<kbd>参考ID</kbd>其中一个过滤

如果对你有所帮助，别忘了给 [本项目 GitHub 主页][find-author] 一个 Start，您的支持是项目前进的动力。
<!-- Plugin description end -->

## Installation

- Using IDE built-in plugin system:
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "find-author"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/LinWanCen/find-author/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>

[Changelog 更新说明](CHANGELOG.md)

---
Enhanced for my old project [GitTool][GitTool]

Plugin based on the [IntelliJ Platform Plugin Template][template].

[find-author]: https://github.com/LinWanCen/find-author
[GitTool]: https://github.com/LinWanCen/GitTool
[template]: https://github.com/JetBrains/intellij-platform-plugin-template
