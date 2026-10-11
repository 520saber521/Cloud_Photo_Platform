import 'axios'

declare module 'axios' {
  export interface AxiosRequestConfig {
    /**
     * 兼容 OpenAPI 生成代码中携带的旧版 axios 配置项
     * （如 fileController.ts / pictureController.ts 中的 requestType: 'json' | 'form'）
     */
    requestType?: string
  }
}
