/**
 * vue-cropper 本地类型垫片
 *
 * 该包自带的 typings（lib/typings/index.d.ts）会间接引用其 .ts 源码
 * （lib/index.ts），导致 vue-tsc 检查时报 TS7016
 * （无法为 './vue-cropper.vue' 找到声明文件）。本项目只通过
 * `app.use(VueCropper)` 全局注册使用，因此用本地声明覆盖即可。
 */
import type { Plugin } from 'vue'

declare const VueCropper: Plugin

export { VueCropper }
export default VueCropper
