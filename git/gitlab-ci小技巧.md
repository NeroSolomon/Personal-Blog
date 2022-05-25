## 设定ci流程触发条件

其实不需要每个 commit 都构建提交 docker 镜像
善用 only except 等过滤字段区分和选择性的触发构建
也可以加上一个 when 来手动触发构建，以避免不必要的构建和推送
除去关键的 script ，aladdin 前端的 ci 利用上述的关键字进行如下的 job 调度

stages:
    - front-build
    - build


### 前端构建，主要运行 npm build 等工作
build-frontend:
  - 前端构建

### 三种不同情况下推送 docker 镜像逻辑

#### 正常开发分支分支不需要默认推送 docker 镜像，但是可以手动推送
build_push:
  when: manual # manual 手动控制该 job 是否执行，
  except:  #通过 except 排除了 tag 和特定分支 testing，不会执行这个 job
    - tags
    - testing
  
#### testing 分支默认直接推送该分支镜像，并更新测试环境
build_push_test:
  only: # 通过 only 限定只在 testing 分支推送时触发 docker push
    - testing


#### ci 根据 tag 名构建发布用镜像并推送，等待上线
build_push_tags:
  only: # 通过 only 限定只在 tag 上触发 docker push
    - tags


## gitlab-ci 缓存 node_modules

背景
ci 打包每次都需要运行 npm install 重新安装所有库，而且是有些库安装起来比较慢

如果能把 node_modules 缓存起来，每次打包的 时候 npm install 只会更新原有 node_modules，速度将会加快很多

过程

.gitlab-ci.yml 中加入 cache 字段

```yaml
build_push:
  cache:
    paths:
      node_modules/
```

Dockerfile 需要把对应目录挂载的 VOLUME

```dockerfile
FROM dockerhub.nie.netease.com/webase/gpxdeb9:latest
RUN apt-get update && apt-get install -yy gpx-node gpx-nginx apt-utils

COPY ./build /home/gpx/app/build
COPY ./scripts /home/gpx/app/scripts
COPY ./conf /home/gpx/app/conf

RUN cp /home/gpx/app/scripts/service.sh /home/gpx/sh/

WORKDIR /home/gpx/app/build

VOLUME ["/home/gpx/app/node_modules/"] #看这里

EXPOSE 4000

ENTRYPOINT ["/home/gpx/sh/service.sh"]

```

## artifacts
```yaml
  artifacts: # 存在gitlab的文件
    when: always
    paths:
      - cypress/videos/**/*.mp4
      - cypress/screenshots/**/*.png
      - cypress/report/
    expire_in: 1 day # 过期时间
```


## cache
```yaml
cache:
  key:
    files:
      - package.json # 此文件改变后更新cache
  paths:
    - node_modules/
    - cache/Cypress
```