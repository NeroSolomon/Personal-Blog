```conf
http {
  # set REMOTE_ADDR from any internal proxies
  # see http://nginx.org/en/docs/http/ngx_http_realip_module.html
  set_real_ip_from 127.0.0.1; # 真实服务器上一级代理的IP地址或者IP段,可以写多行
  set_real_ip_from 10.0.0.0/8;
  real_ip_header X-Forwarded-For; # 从哪个header头检索出要的IP地址
  real_ip_recursive on; # 递归排除IP地址,ip串从右到左开始排除set_real_ip_from里面出现的IP,如果出现了未出现这些ip段的IP，那么这个IP将被认为是用户的IP。

  # SSL configuration -- change these certs to match yours
  ssl_certificate      /etc/ssl/sentry.example.com.crt;
  ssl_certificate_key  /etc/ssl/sentry.example.com.key;

  # NOTE: These settings may not be the most-current recommended
  # defaults
  ssl_protocols TLSv1 TLSv1.1 TLSv1.2;
  ssl_ciphers ECDH+AESGCM:DH+AESGCM:ECDH+AES256:DH+AES256:ECDH+AES128:DH+AES:ECDH+3DES:DH+3DES:RSA+AESGCM:RSA+AES:RSA+3DES:!aNULL:!MD5:!DSS;
  ssl_prefer_server_ciphers on;
  ssl_session_cache shared:SSL:128m;
  ssl_session_timeout 10m;

  server {
    listen   80;
    server_name sentry.example.com;

    location / {
      if ($request_method = GET) {
        rewrite  ^ https://$host$request_uri? permanent;
      }
      return 405;
    }
  }

  server {
    listen   443 ssl;
    server_name sentry.example.com;

    proxy_set_header   Host                 $http_host;
    proxy_set_header   X-Forwarded-Proto    $scheme;
    proxy_set_header   X-Forwarded-For      $remote_addr;
    proxy_redirect     off;

    # keepalive + raven.js is a disaster
    keepalive_timeout 0;

    # use very aggressive timeouts
    proxy_read_timeout 5s;
    proxy_send_timeout 5s;
    send_timeout 5s;
    resolver_timeout 5s;
    client_body_timeout 5s;

    # buffer larger messages
    client_max_body_size 5m;
    client_body_buffer_size 100k;

    location / {
      proxy_pass        http://localhost:9000;

      add_header Strict-Transport-Security "max-age=31536000";
    }
  }
}
```