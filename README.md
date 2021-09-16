# AndroidUtils
# app-cores

#### 规范
**--->Commit message 的格式：<type>: <subject>**

```
fix:修复xxx
feat:新增xxx功能
doc:提供提交源代码更改记录的提交模板
```

**type(必须)**

用于说明 git commit 的类别，只允许使用下面的标识。

1.  feat：新功能（feature）。
2.  fix/to：修复bug，可以是QA发现的BUG，也可以是研发自己发现的BUG。
    fix：产生diff并自动修复此问题。适合于一次提交直接修复问题
    to：只产生diff不自动修复此问题。适合于多次提交。最终修复问题提交时使用fix
3.  docs：文档（documentation）。
4.  style：格式（不影响代码运行的变动）。
5.  refactor：重构（即不是新增功能，也不是修改bug的代码变动）。
6.  perf：优化相关，比如提升性能、体验。
7.  test：增加测试。
8.  chore：构建过程或辅助工具的变动。
9.  revert：回滚到上一个版本。
10.  merge：代码合并。
11.  sync：同步主线或分支的Bug。

**subject(必须)**

subject是commit目的的简短描述，不超过50个字符。

**--->Module 打包 aar 包**
./gradlew :<ModuleName>:assembleRelease
如
```
./gradlew :lib-alog:assembleRelease
```
生成路径：lib-alog/build/outputs/aar/lib-alog-release.aar


#### 软件架构
软件架构说明

#### 使用说明

1.  xxxx
2.  xxxx
3.  xxxx