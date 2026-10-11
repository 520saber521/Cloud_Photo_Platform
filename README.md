# Saber 云图库

基于 **Vue 3 + Spring Boot + 腾讯云 COS + WebSocket** 的企业级智能协同云图库平台。

## 项目介绍

一个支持公有图库与私有空间的图片素材平台，核心能力分为 4 大类：

1. **公共图库** —— 所有用户都可以上传、检索、分享图片素材，快速找到需要的图片。可用作表情包网站、设计素材网站、壁纸网站等。
2. **审核与运营** —— 管理员可以上传、审核、管理图片，并对系统内的图片进行数据分析。
3. **私有空间** —— 个人用户可以把图片上传到私有空间，进行批量管理、多维检索、扫码分享、快速编辑和用量分析。可用作个人网盘、个人相册、作品集等。
4. **团队协同** —— 企业/团队可以开通团队空间并邀请成员，共享图片并**实时协同编辑**，提高协作效率。可用作企业活动相册、企业内部素材库等。

项目涉及文件存管、内容检索、权限控制、实时协同等主流业务场景，并运用多级缓存、分库分表、无锁队列、设计模式等优化手段。

## 技术选型

### 前端

- Vue 3（Composition API + `<script setup>`）
- Vite 构建工具
- TypeScript + ESLint + Prettier 前端工程化
- Ant Design Vue 组件库（已定制「编辑杂志 · 奶油纸感」主题）
- Pinia 全局状态管理、Vue Router
- ECharts + echarts-wordcloud 数据可视化
- vue-cropper 图片裁剪、vue3-colorpicker 颜色选择
- OpenAPI 自动生成前端请求代码

### 后端

- Java Spring Boot 框架
- MySQL 数据库 + MyBatis-Plus 框架
- Redis 分布式缓存 + Caffeine 本地缓存
- 腾讯云 COS 对象存储
- Sa-Token 权限控制 + Spring Session（Redis 存储）
- ShardingSphere 分库分表
- WebSocket 双向通信（实时协同编辑）
- Disruptor 高性能无锁队列、JUC 并发与异步编程
- 阿里云 AI 大模型接入（DashScope 图片扩图）
- Jsoup 数据抓取、Quartz 定时任务

## 项目结构

```
saber-picture-master
├── saber-picture-frontend      # 前端（Vue 3 + Vite + TS）
├── saber-picture-backend       # 后端（Spring Boot 分层架构）
└── saber-picture-backend-ddd   # 后端（DDD 领域驱动设计架构版本）
```

## 快速开始

### 环境依赖

| 依赖 | 版本 |
| --- | --- |
| JDK | 17+ |
| Maven | 3.9+ |
| Node.js | 18+ |
| MySQL | 8.x |
| Redis | 5.x+ |

### 1. 初始化数据库

创建数据库 `saber_picture`，导入建表脚本：

```bash
mysql -uroot -p saber_picture < saber-picture-backend/sql/create_table.sql
```

> ⚠️ 脚本包含**增量更新语句**（如会员字段 `vipExpireTime` / `vipCode` / `vipNumber`），
> 老库升级时也要执行脚本末尾的 `ALTER TABLE` 部分，否则登录会报「系统错误」（Unknown column）。

### 2. 配置后端

编辑 `saber-picture-backend/src/main/resources/application.yml`：

- **MySQL**：默认 `localhost:3306/saber_picture`（root / 123456）
- **Redis**：默认 `127.0.0.1:6379`
- **对象存储 `cos.client.*`**：腾讯云 COS 的 `secretId` / `secretKey` / `region` / `bucket` / `host`。
  **必须填写真实密钥**，否则上传、删除图片会失败（项目里默认是占位值）。
- **AI 扩图 `aliYunAi.apiKey`**：阿里云 DashScope 密钥，可选。未配置时 AI 扩图相关代码已停用。

### 3. 启动后端

```bash
cd saber-picture-backend
mvn package -DskipTests
java -jar target/saber-picture-backend-0.0.1-SNAPSHOT.jar
```

- 服务地址：http://localhost:8123/api
- 接口文档（Knife4j）：http://localhost:8123/api/doc.html

### 4. 启动前端

```bash
cd saber-picture-frontend
npm install
npm run dev
```

访问 http://localhost:5173 。前端默认请求 `http://localhost:8123`（见 `src/request.ts`）。

### 5. 创建管理员账号

项目没有内置账号，需要先注册一个普通账号，再手动提升为管理员：

```sql
UPDATE `user` SET userRole = 'admin' WHERE userAccount = '你的账号';
```

之后登录即可在顶部导航看到「用户管理 / 图片管理 / 空间管理」入口。

## 功能一览

| 模块 | 功能 |
| --- | --- |
| 图片 | 上传（文件 / URL 抓取 / 批量抓取）、编辑裁剪、AI 扩图、以图搜图、按颜色搜索、批量重命名与编辑 |
| 图库 | 分页检索、分类与标签筛选、缩略图、图片详情与分享（链接 + 二维码） |
| 空间 | 私有空间、团队空间、空间级别与配额、成员与角色管理（查看者 / 编辑者 / 管理员） |
| 分析 | 空间用量、分类分布、标签词云、大小分布、用户上传趋势、空间排行 |
| 系统 | 用户注册登录、RBAC 权限（user / vip / admin）、会员兑换码、图片审核、实时协同编辑 |

## 开发备注

- 密码加密方式为 `MD5(固定盐 + 密码)`，盐值定义在 `UserServiceImpl#getEncryptPassword`。修改盐值会导致已有账号无法登录。
- WebSocket 协同编辑地址见 `src/utils/pictureEditWebSocket.ts`。
- 前端主题令牌集中定义在 `src/styles/global.css` 与 `src/App.vue` 的 `antdTheme` 中，改配色只需改这两处。
