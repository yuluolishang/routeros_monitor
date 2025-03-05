## RouterOS 监控项目

这是一个在 Copilot 帮助下开发的 Android 项目。代码质量究极一般，请多多包涵。该应用仅支持横屏模式，并在 Android 7.0 上进行了测试。

### 功能：
1. 运行时间
2. 系统版本
3. CPU 使用率
4. 内存使用率
5. 防火墙信息
6. 上传和下载流量信息

### 使用说明：
1. 在 RouterOS 用户管理中创建一个新组，并授予 `read` 和 `api` 权限。
2. 创建一个新用户，并将新创建的组分配给该用户。
3. 点击应用程序左下角打开设置界面。
   ![设置界面](https://github.com/user-attachments/assets/635718ab-756c-4027-85cb-17fb185f4c30)
4. 配置路由器 IP、API 端口、用户名和密码。
   ![配置](https://github.com/user-attachments/assets/29d90b0c-f4f0-448d-a53d-743838a8af76)
5. 享受应用吧！
   ![应用界面](https://github.com/user-attachments/assets/b883a995-2006-4def-98fb-cd837a7047cf)

### 致谢：
感谢以下开源项目：
- [mikrotik_java](https://github.com/GideonLeGrange/mikrotik-java)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [CircularProgressIndicator](https://github.com/antonKozyriatskyi/CircularProgressIndicator)
