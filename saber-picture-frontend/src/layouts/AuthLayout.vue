<template>
  <div class="auth-shell" :class="{ exiting }">
    <!-- ========== 左页：图版（杂志图片页） ========== -->
    <aside class="plate">
      <!-- 印刷裁切标记（四角细十字线） -->
      <span class="crop crop-tl" />
      <span class="crop crop-tr" />
      <span class="crop crop-bl" />
      <span class="crop crop-br" />

      <header class="plate-top">
        <router-link to="/" class="plate-brand">
          <img class="plate-logo" src="../assets/logo-mark.png" alt="logo" />
          <span class="plate-brand-name">Saber 云图库</span>
        </router-link>
        <span class="plate-issue">SABER PICTURE · No.01</span>
      </header>

      <!-- 有图：印刷拼贴 -->
      <div v-if="pictures.length >= 3" class="collage">
        <figure
          v-for="(pic, index) in pictures"
          :key="pic.id"
          class="collage-item"
          :class="'shape-' + (index % 5)"
        >
          <img :src="pic.thumbnailUrl ?? pic.url ?? ''" :alt="pic.name ?? ''" loading="lazy" />
        </figure>
      </div>

      <!-- 无图：排版降级（细线网格 + 色块 + 大字号标语） -->
      <div v-else class="collage-fallback">
        <div class="fallback-grid" />
        <div class="fallback-block" />
        <p class="fallback-quote">
          收 藏<br />
          <span class="fallback-accent">灵 感</span>
        </p>
      </div>

      <footer class="plate-bottom">
        <div class="plate-lead">
          <p class="plate-slogan">发现灵感，收藏美好</p>
          <p class="plate-caption">
            图 / 公共图库 ·
            {{ pictures.length ? pictures.length + ' 幅精选' : 'SABER PICTURE LIBRARY' }}
          </p>
        </div>
        <span class="plate-page">P.01</span>
      </footer>
    </aside>

    <!-- ========== 右页：纸面表单 ========== -->
    <section class="paper">
      <div class="paper-inner">
        <p class="paper-kicker">账号入口 · ACCOUNT</p>
        <slot />
      </div>
      <div class="paper-foot">
        <span>© 2026 Saber 云图库 · 智能协同云图库平台</span>
        <span class="paper-page">P.02</span>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { onMounted, ref } from 'vue'
import { listPictureVoByPageUsingPost } from '@/api/pictureController.ts'

// 左页图版用的公共图库图片（取不到就优雅降级为排版版式）
const pictures = ref<API.PictureVO[]>([])

const fetchPictures = async () => {
  try {
    const res = await listPictureVoByPageUsingPost({
      current: 1,
      pageSize: 7,
      sortField: 'createTime',
      sortOrder: 'descend',
    })
    if (res.data.code === 0 && res.data.data?.records) {
      pictures.value = res.data.data.records
    }
  } catch (e) {
    // 图版只是装饰，失败时静默降级，不影响登录
    pictures.value = []
  }
}

onMounted(() => {
  fetchPictures()
})

// ---------- 登录成功后的离场转场 ----------
const exiting = ref(false)

/**
 * 播放离场动画：右页表单淡出 → 左页图版放大推进 → 整页淡出
 * 返回 Promise，动画结束后再跳转路由，保证过渡连贯
 */
const playExit = (): Promise<void> => {
  if (exiting.value) {
    return Promise.resolve()
  }
  // 尊重系统的"减少动态效果"设置
  const reduced =
    typeof window !== 'undefined' &&
    typeof window.matchMedia === 'function' &&
    window.matchMedia('(prefers-reduced-motion: reduce)').matches
  if (reduced) {
    return Promise.resolve()
  }
  exiting.value = true
  return new Promise<void>((resolve) => {
    window.setTimeout(resolve, 820)
  })
}

defineExpose({ playExit })
</script>

<style scoped>
/* ================= 页面骨架：55 / 45 硬分栏 ================= */
.auth-shell {
  display: grid;
  grid-template-columns: 1.16fr 1fr;
  min-height: 100vh;
  background: var(--bg);
}

/* ================= 印刷裁切标记 ================= */
.crop {
  position: absolute;
  width: 17px;
  height: 17px;
  pointer-events: none;
  opacity: 0.55;
}

.crop::before,
.crop::after {
  content: '';
  position: absolute;
  background: rgba(243, 237, 225, 0.5);
}

.crop::before {
  left: 0;
  right: 0;
  top: 50%;
  height: 1px;
}

.crop::after {
  top: 0;
  bottom: 0;
  left: 50%;
  width: 1px;
}

.crop-tl {
  top: 15px;
  left: 15px;
}
.crop-tr {
  top: 15px;
  right: 15px;
}
.crop-bl {
  bottom: 15px;
  left: 15px;
}
.crop-br {
  bottom: 15px;
  right: 15px;
}

/* ================= 左页 · 图版 ================= */
.plate {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 40px 46px 34px;
  background: #221c15;
  color: #f3ede1;
  overflow: hidden;
  transform-origin: 44% 50%;
}

.plate-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(243, 237, 225, 0.16);
}

.plate-brand {
  display: flex;
  align-items: center;
  gap: 11px;
  text-decoration: none;
}

.plate-logo {
  height: 30px;
  border-radius: 10px;
}

.plate-brand-name {
  font-family: var(--font-display);
  font-size: 17px;
  font-weight: 700;
  color: #f7f2e8;
  letter-spacing: 0.08em;
}

.plate-issue {
  font-size: 10px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(243, 237, 225, 0.5);
}

/* —— 图片拼贴 —— */
.collage {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  grid-auto-rows: 92px;
  gap: 11px;
  align-content: center;
  margin: 30px 0;
}

.collage-item {
  margin: 0;
  overflow: hidden;
  border-radius: 10px;
  background: #342a20;
  animation: plate-rise 0.7s ease both;
}

.collage-item:nth-child(1) {
  animation-delay: 0.04s;
}
.collage-item:nth-child(2) {
  animation-delay: 0.1s;
}
.collage-item:nth-child(3) {
  animation-delay: 0.16s;
}
.collage-item:nth-child(4) {
  animation-delay: 0.22s;
}
.collage-item:nth-child(5) {
  animation-delay: 0.28s;
}
.collage-item:nth-child(6) {
  animation-delay: 0.34s;
}
.collage-item:nth-child(7) {
  animation-delay: 0.4s;
}

.collage-item img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: saturate(0.94) contrast(1.02);
  transition: transform 0.3s ease, filter 0.3s ease;
}

.collage-item:hover img {
  transform: scale(1.05);
  filter: saturate(1.06) contrast(1.02);
}

/* 杂志式错落尺寸 */
.shape-0 {
  grid-column: span 2;
  grid-row: span 2;
}
.shape-1 {
  grid-row: span 2;
}
.shape-2 {
  grid-row: span 1;
}
.shape-3 {
  grid-column: span 2;
  grid-row: span 1;
}
.shape-4 {
  grid-row: span 1;
}

/* —— 无图降级版式 —— */
.collage-fallback {
  position: relative;
  flex: 1;
  margin: 30px 0;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.fallback-grid {
  position: absolute;
  inset: 0;
  background-image: linear-gradient(rgba(243, 237, 225, 0.07) 1px, transparent 1px),
    linear-gradient(90deg, rgba(243, 237, 225, 0.07) 1px, transparent 1px);
  background-size: 46px 46px;
}

.fallback-block {
  position: absolute;
  left: 0;
  bottom: 0;
  width: 38%;
  height: 26%;
  background: var(--brand);
  opacity: 0.9;
}

.fallback-quote {
  position: relative;
  font-family: var(--font-display);
  font-size: clamp(34px, 4.6vw, 58px);
  line-height: 1.24;
  letter-spacing: 0.12em;
  color: #f7f2e8;
  animation: plate-rise 0.7s ease both;
}

.fallback-accent {
  color: var(--gold);
}

/* —— 左页页脚：标语 + 页码 —— */
.plate-bottom {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 20px;
  padding-top: 16px;
  border-top: 1px solid rgba(243, 237, 225, 0.16);
}

.plate-slogan {
  font-family: var(--font-display);
  font-size: clamp(20px, 2.1vw, 27px);
  font-weight: 700;
  letter-spacing: 0.08em;
  color: #f7f2e8;
  margin-bottom: 8px;
}

.plate-caption {
  font-size: 10.5px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: rgba(243, 237, 225, 0.52);
}

.plate-page {
  font-family: var(--font-display);
  font-size: 13px;
  letter-spacing: 0.12em;
  color: rgba(243, 237, 225, 0.62);
  padding-bottom: 2px;
}

/* ================= 右页 · 纸面表单 ================= */
.paper {
  position: relative;
  display: flex;
  flex-direction: column;
  justify-content: center;
  padding: 56px 60px 96px;
  background: var(--bg);
  border-left: 1px solid var(--line-soft);
}

.paper-inner {
  width: 100%;
  max-width: 372px;
  margin: 0 auto;
  animation: rise 0.55s ease both;
}

.paper-kicker {
  font-size: 10.5px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--muted);
  padding-bottom: 16px;
  border-bottom: 1px solid var(--line);
  margin-bottom: 38px;
}

.paper-foot {
  position: absolute;
  left: 60px;
  right: 60px;
  bottom: 26px;
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 16px;
  padding-top: 13px;
  border-top: 1px solid var(--line-soft);
  font-size: 11px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

.paper-page {
  font-family: var(--font-display);
  font-size: 12.5px;
  letter-spacing: 0.12em;
  color: var(--ink-soft);
  flex-shrink: 0;
}

/* ================= 印刷表单语言 ================= */

/* 字段标签：编号 + 小号大写 */
.paper :deep(.ant-form-item-label) {
  padding-bottom: 4px;
}

.paper :deep(.ant-form-item-label > label) {
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--muted);
  height: auto;
}

.paper :deep(.ant-form-item-label > label::after) {
  display: none;
}

/* 下划线输入框 */
.paper :deep(.ant-input-affix-wrapper),
.paper :deep(.ant-input),
.paper :deep(.ant-input-password) {
  background: transparent;
  border: none;
  border-bottom: 1px solid var(--line);
  border-radius: 0;
  padding: 8px 0;
  font-size: 15px;
  color: var(--ink);
  box-shadow: none;
  transition: border-color 0.3s ease;
}

.paper :deep(.ant-input-affix-wrapper:hover),
.paper :deep(.ant-input:hover),
.paper :deep(.ant-input-affix-wrapper-focused),
.paper :deep(.ant-input:focus) {
  border-bottom-color: var(--brand);
  box-shadow: none;
}

.paper :deep(.ant-input::placeholder) {
  color: #b5aa97;
}

.paper :deep(.ant-form-item) {
  margin-bottom: 26px;
}

.paper :deep(.ant-form-item-explain-error) {
  font-size: 12px;
  padding-top: 4px;
}

/* 方角实底按钮 + 压印位移 */
.paper :deep(.ant-btn-primary) {
  border-radius: 10px;
  height: 48px;
  font-size: 14px;
  letter-spacing: 0.12em;
  box-shadow: 4px 4px 0 rgba(42, 36, 28, 0.14);
  transition: transform 0.3s ease, box-shadow 0.3s ease, filter 0.3s ease;
}

.paper :deep(.ant-btn-primary:not(:disabled):hover) {
  transform: translate(-2px, -2px);
  box-shadow: 7px 7px 0 rgba(42, 36, 28, 0.18);
  filter: brightness(1.04);
}

.paper :deep(.ant-btn-primary:not(:disabled):active) {
  transform: translate(0, 0);
  box-shadow: 2px 2px 0 rgba(42, 36, 28, 0.18);
}

/* 链接 */
.paper :deep(a) {
  border-bottom: 1px solid transparent;
  transition: border-color 0.3s ease;
}

.paper :deep(a:hover) {
  border-bottom-color: currentColor;
}

/* ================= 离场转场：右页淡出 → 左页推进 → 整页淡出 ================= */
.auth-shell.exiting .paper {
  opacity: 0;
  transform: translateX(30px);
  transition: opacity 0.32s ease, transform 0.46s cubic-bezier(0.4, 0, 0.2, 1);
}

.auth-shell.exiting .paper-inner {
  opacity: 0;
  transform: translateY(-12px);
  transition: opacity 0.24s ease, transform 0.3s ease;
}

.auth-shell.exiting .plate {
  transform: scale(1.18);
  transition: transform 0.84s cubic-bezier(0.22, 0.72, 0.28, 1);
}

.auth-shell.exiting .plate-top,
.auth-shell.exiting .plate-bottom {
  opacity: 0;
  transition: opacity 0.3s ease;
}

.auth-shell.exiting .crop {
  opacity: 0;
  transition: opacity 0.25s ease;
}

.auth-shell.exiting .collage-item img {
  transform: scale(1.07);
  transition: transform 0.9s ease;
}

.auth-shell.exiting {
  animation: auth-shell-out 0.84s ease forwards;
}

@keyframes auth-shell-out {
  0%,
  52% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

/* ================= 动效 ================= */
@keyframes plate-rise {
  from {
    opacity: 0;
    transform: translateY(16px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* ================= 响应式 ================= */
@media (max-width: 1000px) {
  .auth-shell {
    grid-template-columns: 1fr;
  }

  .plate {
    padding: 24px 24px 22px;
    min-height: 260px;
  }

  .collage {
    grid-auto-rows: 62px;
    margin: 20px 0;
  }

  .collage-fallback {
    margin: 18px 0;
    min-height: 130px;
  }

  .fallback-quote {
    font-size: 30px;
  }

  .plate-slogan {
    font-size: 19px;
  }

  .paper {
    padding: 40px 24px 88px;
    border-left: none;
    border-top: 1px solid var(--line-soft);
  }

  .paper-foot {
    left: 24px;
    right: 24px;
    bottom: 20px;
  }

  .paper-kicker {
    margin-bottom: 28px;
  }
}

/* ================= 减少动态效果 ================= */
@media (prefers-reduced-motion: reduce) {
  .auth-shell.exiting {
    animation: none;
  }

  .auth-shell.exiting .plate,
  .auth-shell.exiting .collage-item img {
    transform: none;
  }

  .collage-item,
  .paper-inner {
    animation: none;
  }
}
</style>
