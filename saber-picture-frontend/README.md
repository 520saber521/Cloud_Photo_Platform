# Saber 云图库 · 前端

基于 Vue 3 + TypeScript + Vite + Ant Design Vue 的云图库前端。

## 开发

```sh
npm install      # 安装依赖
npm run dev      # 启动开发服务（默认 http://localhost:5173）
npm run build    # 类型检查 + 生产构建
npm run lint     # ESLint 校验并修复
npm run format   # Prettier 格式化
npm run openapi  # 根据后端接口文档重新生成 src/api 下的请求代码
```

后端地址默认是 `http://localhost:8123`，在 `src/request.ts` 中修改。

## 目录说明

```
src
├── api            # OpenAPI 自动生成的请求代码与类型定义
├── components     # 通用组件（导航、图片列表、上传、分析图表、协同编辑等）
├── layouts        # 基础布局（顶栏 + 侧栏 + 内容区）
├── pages          # 页面（首页、空间、图片详情、后台管理等）
├── router         # 路由与权限校验
├── stores         # Pinia 状态管理
├── styles         # 全局样式与设计令牌
└── utils          # 工具函数（含 WebSocket 协同编辑客户端）
```

## 主题定制

界面风格为「编辑杂志 · 奶油纸感」，配色与圆角集中在两处，改主题只需动这里：

- `src/styles/global.css`：CSS 变量（`--bg` / `--ink` / `--brand` 等）、全局质感与 Ant Design 细节覆写
- `src/App.vue` 的 `antdTheme`：Ant Design 的 token 与组件级 token

品牌资源：`src/assets/logo.svg`（顶栏与登录页）、`public/favicon.svg`（站点图标）。

## 推荐 IDE

VSCode + [Volar](https://marketplace.visualstudio.com/items?itemName=Vue.volar)（并禁用 Vetur）。
