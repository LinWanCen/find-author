# find-author

![Build](https://github.com/LinWanCen/find-author/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/20557-find-author.svg)](https://plugins.jetbrains.com/plugin/20557-find-author)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/20557-find-author.svg)](https://plugins.jetbrains.com/plugin/20557-find-author)

## Plugin description 插件介绍

<!-- Plugin description -->
Multiple files => git author / doc comment, Export Find as tsv.

## How to Use

Author of mutil input files:
1. Open Author ToolWindow at Right
2. Paste file:lineNum in <kbd>input</kbd>
3. Click <kbd>Git Author</kbd>

Export Find Window:
1. <kbd>Ctrl + Shift + F</kbd> / select a method <kbd>Alt + F7</kbd> Find Usage
2. Click <kbd>Open in the Find Window</kbd> or use <kbd>Ctrl + Enter</kbd>
3. Right-click the Find Window, select<kbd>@ Export Tsv + Git Author + Comment</kbd>

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

## My Other Plugin
- Show doc comment at the Project view Tree, line End, json etc: [Show Comment]

# 中文说明

- 从 Git 中查找多个文件或行的作者
- 以带作者的表格形式导出搜索窗口
- 从注释获取作者

## 用法

多文件作者：
1. 右边的打开<kbd>Author</kbd>工具栏
2. 粘贴 文件:行号 到 <kbd>输入</kbd>
3. 点击 <kbd>Git 作者</kbd>

导出搜索：
1. <kbd>Ctrl + Shift + F</kbd> / 在方法上 <kbd>Alt + F7</kbd> 查找调用方
2. 单击<kbd>打开查找窗口</kbd> 或者用 <kbd>Ctrl + Enter</kbd>
3. 右键单击查找窗口，选择<kbd>@ 导出 tsv + Git 作者 + 注释</kbd>

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

## 我的其他项目
- 在文件树、行末、JSON、COBOL 显示注释：[Show Comment]

如果对你有所帮助，别忘了给 [本项目 GitHub 主页][Find Author] 一个 Star，您的支持是项目前进的动力。

[Show Comment]: https://plugins.jetbrains.com/plugin/18553-show-comment
[Find Author]: https://github.com/LinWanCen/find-author
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

[GitTool]: https://github.com/LinWanCen/GitTool
[template]: https://github.com/JetBrains/intellij-platform-plugin-template
