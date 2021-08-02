## 设定ci流程触发条件

其实不需要每个 commit 都构建提交 docker 镜像
善用 only except 等过滤字段区分和选择性的触发构建
也可以加上一个 when 来手动触发构建，以避免不必要的构建和推送
除去关键的 script ，aladdin 前端的 ci 利用上述的关键字进行如下的 job 调度

stages:
    - front-build
    - build


# 前端构建，主要运行 npm build 等工作
build-frontend:
  - 前端构建

# 三种不同情况下推送 docker 镜像逻辑

# 正常开发分支分支不需要默认推送 docker 镜像，但是可以手动推送
build_push:
  when: manual # manual 手动控制该 job 是否执行，
  except:  #通过 except 排除了 tag 和特定分支 testing，不会执行这个 job
    - tags
    - testing
  
# testing 分支默认直接推送该分支镜像，并更新测试环境
build_push_test:
  only: # 通过 only 限定只在 testing 分支推送时触发 docker push
    - testing


# ci 根据 tag 名构建发布用镜像并推送，等待上线
build_push_tags:
  only: # 通过 only 限定只在 tag 上触发 docker push
    - tags