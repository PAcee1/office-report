## 报表在线预览

Java + OpenOffice + jodconverter + easypoi 实现

具体功能：

- 通过easypoi，将报表模板进行数据填充
- 通过openOffice实现Excel转html，Excel转PDF
- Html在线预览
- Excel，PDF导出

**模板：**

![image-20210118143307332](image/image-20210118143307332.png)

**预览：**

![image-20210118143045423](image/image-20210118143045423.png)

**Excel导出：**

![image-20210118143139532](image/image-20210118143139532.png)

**Pdf导出：**

![image-20210118143124354](image/image-20210118143124354.png)

## Linux安装OpenOffice

1.获取openOffice压缩包

- wget下载：`wget https://kkfileview.keking.cn/Apache_OpenOffice_4.1.6_Linux_x86-64_install-rpm_zh-CN.tar.gz`
- 直接使用提供的

2.解压压缩包

```
tar -zxvf Apache_OpenOffice_4.1.6_Linux_x86-64_install-rpm_zh-CN.tar.gz
```

3.进入压缩包安装rpm

```
cd /opt/softwares/zh-CN/RPMS
yum localinstall *.rpm
yum localinstall openoffice4.1.5-redhat-menus-4.1.5-9788.noarch.rpm
```

4.启动

```
nohup /opt/openoffice4/program/soffice -headless -accept="socket,host=127.0.0.1,port=8100;urp;" -nofirststartwizard &
```

5.查看启动成功

```
ps -ef|grep openoffice
netstat -lnp |grep 8100
```

