## 使用git时出线的一些问题及解决方法

1.当远程仓库和本地仓库没有建立起链接时，报
```
There is no tracking information for the current branch.

Please specify which branch you want to merge with.

See git-pull(1) for details

    git pull <remote> <branch>

If you wish to set tracking information for this branch you can do so with:

    git branch --set-upstream-to=origin/<branch> merged
```

解决命令
```
git branch --set-upstream-to=origin/远程分支的名字 本地分支的名字
```