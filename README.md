# GRpc Java 双向TLS认证示例

## 生成证书

### 变量

* 服务端域名：SERVER_CN=localhost

* 客户端域名：CLIENT_CN=localhost

### 步骤

1. 生成 CA 私钥

    `openssl genrsa -passout pass:1111 -des3 -out ca.key 4096`

2. 生成 CA 证书

    `openssl req -passin pass:1111 -new -x509 -days 365 -key ca.key -out ca.crt -subj "/CN=${SERVER_CN}"`

3. 生成服务端私钥

    `openssl genrsa -passout pass:2222 -des3 -out server.key 4096`

4. 生成服务端CSR

    `openssl req -passin pass:2222 -new -key server.key -out server.csr -subj "/CN=${SERVER_CN}"`

5. 生成服务端自签名证书

    `openssl x509 -req -passin pass:1111 -days 365 -in server.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out server.crt`

6. 移除服务端私钥密码

    `openssl rsa -passin pass:2222 -in server.key -out server.key`

7. 生成客户端私钥

    `openssl genrsa -passout pass:3333 -des3 -out client.key 4096`

8. 生成客户端CSR

    `openssl req -passin pass:3333 -new -key client.key -out client.csr -subj "/CN=${CLIENT_CN}"`

9. 生成客户端自签名证书

    `openssl x509 -passin pass:1111 -req -days 365 -in client.csr -CA ca.crt -CAkey ca.key -set_serial 01 -out client.crt`

10. 移除客户端私钥密码

    `openssl rsa -passin pass:3333 -in client.key -out client.key`

11. 私钥转换为 X.509

    `openssl pkcs8 -topk8 -nocrypt -in client.key -out client.pem`

    `openssl pkcs8 -topk8 -nocrypt -in server.key -out server.pem`


## 性能测试

本地重复一万次调用，计算总耗时(单位ms)

| 字符长度   | 50  | 100 |  200  | 400 | 800 | 1600 |3200|
|---------- |-----|-----|-------|-----|-----|-----|-----|
| 带TLS     |  8833  |  8911  |  7321    |  8083    |  7142   |   7361  |   9614  |
| 不带TLS   |  7570   |  7223    |   6621   |  6.840   |  7665   |  7436   |  6573   |

由以上数据可见，加TLS安全认证对GRpc响应不会有大的影响。