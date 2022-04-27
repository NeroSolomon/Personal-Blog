## git 命令

### 学习网站
[学习网站](https://learngitbranching.js.org/)

### git rebase
合并多次commit

### git revert
新建一个commit，删除已提交的某些commit中的更改
```
git revert 168a2cd // 删除168a2cd的提交
```

当某个commit是merge的时侯，直接git revert commitid会有问题，因为merge的时侯其实会生成三个commitid（可以使用git log查看），直接revert的话git不知道你需要revert哪个<br>
所以需要
```
git revert 168a2cd -m 1 // 删除168a2cd的提交下的第一个
```

revert多个
```
git revert commitB^..commitA
```

### git fetch --prune origin 清除fetch的缓存，用于远程分支delete但git branch仍能看到的情况

### 通过tag拉新分支
git branch [newBranch] [tag]

### ~
通过git branch -f [branch] [hash/HEAD~^]不改动HEAD，索引和副本
git checkout 、 reset则会改变三者

### rebase
rebase 会让树结构更易看，功能上和merge差不多

### git fetch
更新所有数据、pull只更新当前分支

### git pull --rebase
用git pull --rebase代替git pull，会使得graph更容易查看