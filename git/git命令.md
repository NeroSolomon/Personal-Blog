## git 命令

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

### git fetch --prune origin 清除fetch的缓存，用于远程分支delete但git branch仍能看到的情况