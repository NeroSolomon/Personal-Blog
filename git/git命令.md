## git 命令

### git rebase
合并多次commit

### git revert
新建一个commit，删除已提交的某些commit中的更改
```
git revert 168a2cd // 删除168a2cd的提交
```

### git fetch --prune origin 清除fetch的缓存，用于远程分支delete但git branch仍能看到的情况