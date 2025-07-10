# 编解码工具
> 通过反射技术实现 _JavaBean_ 与 _二进制数据_ 之间的相互转换。
> 二进制数据的格式参考网络通讯协议中的数据包格式

## 1. 通信报文的二进制格式

| 2 byte    | 2 byte  | 4 byte    | 4 byte     | 4 byte     | n byte |
|-----------|---------|-----------|------------|------------|--------|
| ByteOrder | Version | CommandId | SequenceId | BodyLength | Body   |

### 1.1 ByteOrder （字节序）

- 大端序 （0xFEFF）(默认)
- 小端序 （0xFFFE）

### 1.2 Version （版本号）

- V1.0 (0x0100)

### 1.3 CommandId （命令字）

- 第一个 bit ： 0 表示用户自定义的类型； 1 表示系统内置的类型
- （optional）第一个 byte 可以有 127 个分类
- （optional）第二个 byte 可以在当前分类中有 256 种类型的数据包

### 1.4 SequenceId （序列号）

- 自增一
- 可以循环使用

### 1.5 BodyLength （数据长度）

- 数据部分的长度
- [0, 520] 或者 [0, 1444]
- 以太网 UDP 的报文长度 = 1500 - IP头(20) - UDP头(8) = 1472(Bytes)
- 以太网 TCP 的报文长度 = 1500 - IP头(20) - TCP头(20) = 1460(bytes)
- 因特网 MTU = 576

## 2. 对象的二进制格式

| 4 byte  | n byte     |
|---------|------------|
| ClassID | FieldsData |