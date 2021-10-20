### 子模块
keywords: Submodule

references: 
1. https://git-scm.com/book/zh/v2/Git-%E5%B7%A5%E5%85%B7-%E5%AD%90%E6%A8%A1%E5%9D%97

git项目中又引入了一个git仓库，那么这个被引入的仓库就是前者的子模块

#### 引入子模块
```bash
git submodule add https://github.com/chaconinc/DbConnector
```
默认情况下，子模块会将子项目放到一个与仓库同名的目录中，本例中是 “DbConnector”。 如果你想要放到其他地方，那么可以在命令结尾添加一个不同的路径。

#### 初始化子模块
```bash
git submodule init
```

#### 更新子模块后如何同步父模块
1. cd到子模块目录
2. 切出一个分支修改，并提交
3. 这个时候，父模块会发生一个git change，表明关联子模块commit id改变
4. cd到父模块目录，提交