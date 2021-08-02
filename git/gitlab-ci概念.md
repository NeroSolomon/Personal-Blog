## CI/CD pipeline

GitLab 中默认开启了 Gitlab CI/CD 的支持，且使用 YAML 文件 .gitlab-ci.yml 来管理项目构建配置。该文件需要存放于项目仓库的根目录（默认路径，可在 [GitLab Pipeline settings](https://docs.gitlab.com/ee/ci/pipelines/settings.html#custom-ci-configuration-path) 中修改），它定义该项目的 CI/CD 如何配置。所以，我们只需要在.gitlab-ci.yml配置文件中定义流水线的各个阶段，以及各个阶段中的若干作业（任...
1CI/CD pipeline
GitLab 中默认开启了 Gitlab CI/CD 的支持，且使用 YAML 文件 .gitlab-ci.yml 来管理项目构建配置。该文件需要存放于项目仓库的根目录（默认路径，可在 GitLab Pipeline settings 中修改），它定义该项目的 CI/CD 如何配置。所以，我们只需要在.gitlab-ci.yml配置文件中定义流水线的各个阶段，以及各个阶段中的若干作业（任务）即可。

示例：

# 定义 stages
stages:
    - test
    - variables

# 定义 job
job1:
    stage: test
    script:
        - echo "I am in test stage"

job2:
    stage: variables
    script:
        - echo "I am in variables" 
以上配置中，用 stages 关键字来定义 Pipeline 中的各个构建阶段，然后用一些非关键字来定义 jobs。每个 job 中可以可以再用 stage 关键字来指定该 job 对应哪个 stage。job 里面的script关键字是每个 job 中必须要包含的，它表示每个 job 要执行的命令。

2yaml文件
（1）概念
.gitlab-ci.yml文件定义了一系列带有约束说明的任务（job）。这些任务都是以任务名开始并且至少包含script部分，每一个任务执行一条不同的命令，每条命令都会被runner拿到runner的环境下被执行，每个job会与其他job分开，独立进行。

Jobs：

定义了约束，指出应在什么条件下执行。
具有任意名称的顶级元素，并且必须至少包含script子句。
不限制可以定义多少个。
例如：

 job1:
    script: "execute-script-for-job1"
 job2:
    script: "execute-script-for-job2"
如果要检查.gitlab-ci.yml文件内容，可以使用 CI Lint工具 来验证。

注意：缩进始终使用空格，不要使用制表符。

（2）关键字
每个 jobs 必须有一个唯一的名字，且名字不能是下面列出的保留字段：

image：使用的docker镜像
services：使用的docker服务
stages：定义构建场景
types：stages的别名（已弃用）
before_script：定义每个任务的脚本启动前所需执行的命令
after_script：定义每个任务的脚本执行结束后所需执行的命令
variables：定义构建变量
cache：定义哪些文件需要缓存，让后续执行可用
include：允许包含外部YAML文件
job的保留字（定义 jobs 的行为）：

关键字	说明
script	必须包含，runner执行的命令或脚本，可以包含多条命令。
image	docker镜像，runner执行时会使用该镜像做为基础环境，执行对应job中的内容。
services	docker中的服务，runner执行时会关联该服务的镜像做为基础环境，执行对应job中的内容。
before_script	执行job之前执行的一组命令。
after_script	执行job后执行的一组命令。
stages	定义一个pipeline中所有阶段的执行顺序。
stage	定义job归属哪个stage，默认为test。
only	限制何时创建job，即限制触发job创建的条件，可用only：refs、only：kubernetes、only：variables和only：changes进行限制。
except	限制何时不创建job，即限制不会触发job创建的条件。和only关键字的作用相反。 可用except：refs、except：kubernetes、except：variables和except：changes进行限制。
tags	用于选择哪个Runner来执行job的标签列表，这个标签必须和注册runner时定义的tags一致。
allow_failure	允许失败的job可以正常提交状态。有时候一个完整的pipeline会有多个stage，有时候为了不阻塞该stage后面的stage执行，会设置该job允许失败。
when	定义何时开始job，可以是on_success，on_failure，always或者manual。注意它不是限制创建job的条件，限制创建job的条件参数是only或者except。 有时候某个job的执行你想手工点击确认才执行，可用该参数。
environment	用于定义作业部署到特定环境，比如部署到production正式环境或者testing测试环境。
cache	后续运行时应缓存的文件列表。主要用于存储project的依赖项，它可以在同个project中的不同pipelines或者jobs中共享使用。比如避免重复下载npm packages, Go vendor packages等，减少pipeline的执行时间。
artifacts	成功附加到job的文件和目录列表，主要用于同个pipeline中不同stages之间进行加速，比如后一个stage需要用到上一个stage中生成的image。它存储的内容不能在不同的pipeline中共享使用。关于cache和artifices的详细区别可参考官网说明。
dependencies	定义job依赖关系，以便您可以在它们之间互相传递artifacts。
coverage	对给定作业(job)的代码覆盖率进行设置。
retry	定义在发生故障时可以自动重试作业的时间和次数。
trigger	定义下游管道触发器，比如在该stage中触发其他项目的pipeline。注意和triggers进行区分。
include	允许此作业(job)包括的外部YAML文件列表，可用include：local、include：file，include：template和include：remote关键字参数。类似nginx配置文件的include设置。
extends	此作业(job)将要继承的配置条目。
pages	上传作业(job)结果以用于GitLab的pages静态页面。
variables	定义job级别的变量。
parallel	定义应并行运行的job数。
（3）重要关键字解析
extends

定义了一个使用 extends 的 job 将继承的条目名称

.tests:
  script: rake test
  stage: test
  only:
    refs:
      - branches

rspec:
  extends: .tests
  script: rake rspec
  only:
    variables:
      - $RSPEC
在上面的示例中，rspec 继承自 .tests 模板。 GitLab 将根据keys执行反向深度合并：

将 rspec 内容以递归方式合并到 .tests 中。
不合并keys的值。
实际生成的rspec job：

rspec:
  script: rake rspec
  stage: test
  only:
    refs:
      - branches
    variables:
      - $RSPEC
注意：script: rake test 已被覆盖 script: rake rspec。

image和services

这两个关键字允许使用一个自定义的 docker 镜像和一系列的服务，并且可以用于整个 job 周期。
详情见官方文档 Docker integration

before_script 和 after_script

before_script 用来定义所有 job 之前运行的命令，after_script 用来定义所有 job 之后运行的命令。它们可以是一个数组或者是多行字符串。
before_script 和 script 在一个上下文中是串行执行的，after_script 是独立执行的。

stages

stages 用于定义作业可以使用的阶段，并且是全局定义的。
stages中的元素顺序决定了对应job的执行顺序：

相同 stage 的 job 可以并行执行；
下一个 stage 的 job 会在前一个 stage 的 job 成功后开始执行；
例如：

stages:
 - build
 - test
 - deploy
首先，所有 build 的 jobs 都是并行执行的。
build 任务成功后，test 并行执行。
所有的 jobs 都执行成功，commit 才会标记为 success。
任何一个前置的 jobs 失败了，commit 会标记为 failed 并且下一个 stages 的 jobs 都不会执行。
特殊的例子：

如果.gitlab-ci.yml中没有定义stages，那么作业阶段会默认定义为build，test 和 deploy；
如果一个 job 没有指定 stage，那么这个任务会分配到 test stage。
only和except

only和except两个参数说明了job什么时候将会被执行：

only定义了job需要执行的所在分支或者标签
except定义了job不会执行的所在分支或者标签
使用规则：

only 和 except 可同时使用，如果only和except在一个 job 配置中同时存在，则以 only 为准，跳过 except
only 和 except 允许使用正则表达式
only 和 except 允许使用特殊的关键字：branches，tags和triggers
only和except允许使用指定仓库地址，但是不forks仓库
例：
1） job 将只会运行以issue-开始的refs(分支)，而except中设置将被跳过。

job:
  # use regexp
  only:
    - /^issue-.*$/
  # use special keyword
  except:
    - branches
2）job只会在打了tag的分支，或者被api所触发，或者每日构建任务上运行，

job:
    # use special keywords
    only:
        - tags      # tag 分支 commit 之后触发
        - triggers  # API 触发
        - schedules # 每日构建触发
3）job将会为所有的分支执行job，但 master 分支除外.

job:
    only:
        - branches@gitlab-org/gitlab-ce
    except:
        - master@gitlab-org/gitlab-ce
4)在计划被触发时或者master分支被push时触发，并且先决条件是kubernetes服务(执行器)是活跃的

job:
only:
    refs:
        - master
        - schedules
    kubernetes: active
variables
GItLab CI 允许在.gitlab-ci.yml文件中添加变量，并在 job 环境中起作用。因为这些配置是存储在 git 仓库中，所以最好是存储项目的非敏感配置。这些变量可以被后续的命令和脚本使用。

variables:
DATABASE_URL: "postgres://postgres@postgres/my_database"
# 注意:整数和字符串一样，对于设置变量名和变量值来说都是合法的，但浮点数是非法的。
除了在.gitlab-ci.yml中设置变量外，runner 也可以定义它自己的变量，也可以通过 GitLab 的界面上设置私有变量。更多variables介绍

cache

cache: path
使用 path 指令选择要缓存的文件或目录，也可以使用通配符。
如果 cache 定义在 jobs 的作用域之外，那么它就是全局缓存，所有 jobs 都可以使用该缓存。
例：
1）缓存binaries和.config中的所有文件：

rspec:
  script: test
  cache:
    paths:
    - binaries/
    - .config
2）缓存git中没有被跟踪的文件：

rspec:
  script: test
  cache:
    untracked: true
3）缓存binaries下没有被git跟踪的文件：

rspec:
  script: test
  cache:
    untracked: true
    paths:
    - binaries/
4）job 中优先级高于全局，下面这个rspec job中将只会缓存binaries/下的文件：

cache:
  paths:
  - my/files

rspec:
  script: test
  cache:
    key: rspec
    paths:
    - binaries/
注意，缓存是在 jobs 之前进行共享的。如果你不同的 jobs 缓存不同的文件路径，必须设置不同的cache: key，否则缓存内容将被重写。

cache: key
key 指令允许我们定义缓存的作用域，可以是所有 jobs 的单个缓存，也可以是每个 job，也可以是每个分支或者是任何你认为合适的地方。它也可以让你很好的调整缓存，允许你设置不同 jobs 的缓存，甚至是不同分支的缓存。
cache: key 可以使用任何的预定义变量，默认key是默认设置的这个项目缓存。
例：
1）缓存每个job

cache:
  key: "$CI_JOB_NAME"
  untracked: true
缓存每个分支
cache:
  key: "$CI_COMMIT_REF_NAME"
  untracked: true
3）缓存每个 job 且每个分支

cache:
  key: "$CI_JOB_NAME/$CI_COMMIT_REF_NAME"
  untracked: true
4）缓存每个分支且每个stage

cache:
  key: "$CI_JOB_STAGE/$CI_COMMIT_REF_NAME"
  untracked: true
5）如果使用的Windows Batch(windows批处理)来跑脚本需要用 % 替代 $

cache:
  key: "%CI_JOB_STAGE%/%CI_COMMIT_REF_NAME%"
  untracked: true
allow_failure
artifacts
artifacts 被用于在 job 作业成功、失败或终止后将制定列表里的文件或文件夹附加到 job 上，传递给下一个 job ，如果要在两个 job 之间传递 artifacts，必须设置dependencies
例：
1）传递所有binaries和.config：

artifacts:
    paths:
        - binaries/
        - .config
2）传递所有git没有追踪的文件

artifacts:
    untracked: true
3）传递binaries文件夹里所有内容和git没有追踪的文件

artifacts:
    untracked: true
    paths:
        - binaries/
4）使用空的依赖项定义job，以禁止传递来的artifact：

job:
    stage: build
    script: make build
    dependencies: []
artifacts: name
name指令允许你对artifacts压缩包重命名，你可以为每个artifect压缩包都指定一个特别的名字

job:
    artifacts:
        name: "$CI_JOB_NAME"
artifacts: when
用于job失败或者未失败时使用：

on_success 这个值是默认的，当job成功时上传artifacts
on_failure 当job执行失败时，上传artifacts
always 不管失败与否，都上传
job:
	artifacts:
		when: on_failure    #当失败时上传artifacts
artifacts:expose_as
用于在合并请求 UI中公开Job artifacts。

test:
    script: ["echo 'test' > file.txt"]
    artifacts:
        expose_as: 'artifact 1'
        paths: ['file.txt']
使用此配置，GitLab将在指向的相关合并请求中添加链接file1.txt。

请注意以下几点：

使用artifacts:paths变量定义时，artifacts不会显示在合并请求UI中。
每个合并请求最多可以公开10个作业工件。
不支持glob模式。
如果指定了目录，那么如果目录中有多个文件，则该链接将指向作业工件浏览器。
对于暴露的单一文件的工件.html，.htm，.txt，.json，.xml，和.log扩展，如果GitLab页数是：
启用后，GitLab会自动渲染工件。
未启用，文件显示在工件浏览器中。
3使用实践
（1）artifacts
artifacts用于指定在成功，失败或始终执行作业时应附加到该文件和目录的列表。作业完成后，工件将被发送到GitLab，并可在GitLab UI中下载。

编写 .gitlab-ci.yml文件

stages:
    - release
    
job:
    stage: release
    script:
        - echo "download artifacts"
    artifacts:
        paths:
         - binaries/
    tags:
        - docker-dind

CI/CD 构建后可以看到下载按钮

请在这里输入图片描述

请在这里输入图片描述

（2）使用模板
在新建.gitlab-ci.yml文件时，可以使用模板来创建。
请在这里输入图片描述

例如，选择Docker template：

# This file is a template, and might need editing before it works on your project.
# Official docker image.
image: docker:latest

services:
  - docker:dind

before_script:
  - docker login -u "$CI_REGISTRY_USER" -p "$CI_REGISTRY_PASSWORD" $CI_REGISTRY

build-master:
  stage: build
  script:
    - docker build --pull -t "$CI_REGISTRY_IMAGE" .
    - docker push "$CI_REGISTRY_IMAGE"
  only:
    - master

build:
  stage: build
  script:
    - docker build --pull -t "$CI_REGISTRY_IMAGE:$CI_COMMIT_REF_SLUG" .
    - docker push "$CI_REGISTRY_IMAGE:$CI_COMMIT_REF_SLUG"
  except:
    - master
更多GitLab CI / CD示例参考[官方文档](https://docs.gitlab.com/ee/ci/yaml/)。

4资料参考
[GitLab CI/CD pipeline configuration reference](https://docs.gitlab.com/ee/ci/yaml/)
[DevOps之Gitlab-CICD实践](https://km.netease.com/topics/topic/3673/item/38018)
[GitLabCI系列之流水线语法第五部分](https://cloud.tencent.com/developer/article/1631779)
[Gitlab CI yaml官方配置文件翻译](https://segmentfault.com/a/1190000010442764)