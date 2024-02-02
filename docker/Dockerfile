# syntax=docker/dockerfile:1
# ^ 魔法句式需要放在第一行

FROM node:18-alpine

WORKDIR /home/gpx/app

RUN sed -i 's/dl-cdn.alpinelinux.org/mirrors.aliyun.com/g' /etc/apk/repositories \
    && yarn config set registry 'https://registry.npmmirror.com' \
    && npm config set registry 'https://registry.npmmirror.com'

RUN apk update && \
    apk add --no-cache nginx bash

COPY yarn.lock package.json /home/gpx/app/

RUN sed -i 's#registry.yarnpkg.com#registry.npmmirror.com#g' yarn.lock

RUN cd /home/gpx/app && yarn install

COPY . /home/gpx/app/

RUN cd /home/gpx/app && yarn build

COPY conf/nginx/nginx.conf /etc/nginx

RUN mkdir -p /etc/nginx/conf.d

COPY conf/nginx/geo.conf /etc/nginx/conf.d

COPY conf/nginx/custom/*.conf /tmp/

COPY scripts/service.sh /home/gpx/sh/

RUN chmod +x /home/gpx/sh/*.sh

ENTRYPOINT ["/bin/bash"] 

CMD ["/home/gpx/sh/service.sh"]