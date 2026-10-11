<template>
  <div class="scroller">
    <div ref="trackRef" class="scroller-track" @scroll="onScroll">
      <slot />
    </div>

    <div class="scroller-bar">
      <button
        class="sc-btn"
        type="button"
        aria-label="上一组"
        :disabled="atStart"
        @click="step(-1)"
      >
        ‹
      </button>
      <span class="sc-pager">
        <span class="sc-now">{{ pageLabel }}</span>
        <span class="sc-sep">|</span>
        <span class="sc-all">{{ total }}</span>
      </span>
      <button class="sc-btn" type="button" aria-label="下一组" :disabled="atEnd" @click="step(1)">
        ›
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, onBeforeUnmount, ref } from 'vue'

/**
 * 横向滑动行（对应原站的横向滚动区块：Features / Ranking / 专题等）
 * 自带左右翻页与「当前 | 总数」页码
 */
interface Props {
  /** 总条数，用于页码显示 */
  total: number
}

const props = defineProps<Props>()

const trackRef = ref<HTMLElement>()
const scrollLeft = ref(0)
const maxScroll = ref(0)

const updateMetrics = () => {
  const el = trackRef.value
  if (!el) {
    return
  }
  scrollLeft.value = el.scrollLeft
  maxScroll.value = Math.max(0, el.scrollWidth - el.clientWidth)
}

const onScroll = () => updateMetrics()

const atStart = computed(() => scrollLeft.value <= 4)
const atEnd = computed(() => scrollLeft.value >= maxScroll.value - 4)

// 页码：按「一屏」估算当前第几屏
const pageLabel = computed(() => {
  const el = trackRef.value
  if (!el || maxScroll.value <= 0) {
    return props.total > 0 ? 1 : 0
  }
  const perScreen = el.clientWidth
  const page = Math.floor(el.scrollLeft / perScreen) + 1
  const totalPages = Math.ceil(el.scrollWidth / perScreen)
  return Math.min(page, totalPages)
})

const step = (dir: number) => {
  const el = trackRef.value
  if (!el) {
    return
  }
  el.scrollBy({ left: dir * el.clientWidth * 0.85, behavior: 'smooth' })
}

let resizeObserver: ResizeObserver | undefined

onMounted(() => {
  updateMetrics()
  if (typeof ResizeObserver !== 'undefined' && trackRef.value) {
    resizeObserver = new ResizeObserver(() => updateMetrics())
    resizeObserver.observe(trackRef.value)
  }
  window.addEventListener('resize', updateMetrics)
})

onBeforeUnmount(() => {
  resizeObserver?.disconnect()
  window.removeEventListener('resize', updateMetrics)
})
</script>

<style scoped>
.scroller {
  position: relative;
}

.scroller-track {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  scroll-snap-type: x proximity;
  padding-bottom: 6px;
  /* 隐藏滚动条，靠左右按钮翻页 */
  scrollbar-width: none;
}

.scroller-track::-webkit-scrollbar {
  display: none;
}

.scroller-track > :deep(*) {
  flex: 0 0 232px;
  scroll-snap-align: start;
}

.scroller-bar {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  margin-top: 14px;
}

.sc-btn {
  width: 30px;
  height: 30px;
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--ink-soft);
  border-radius: 10px;
  cursor: pointer;
  font-size: 16px;
  line-height: 1;
  transition: all 0.3s ease;
}

.sc-btn:not(:disabled):hover {
  border-color: var(--brand);
  color: var(--brand);
}

.sc-btn:disabled {
  opacity: 0.35;
  cursor: default;
}

.sc-pager {
  font-family: var(--font-display);
  font-size: 12.5px;
  letter-spacing: 0.12em;
  color: var(--ink-soft);
  padding: 0 8px;
}

.sc-sep {
  margin: 0 6px;
  color: var(--line);
}

.sc-all {
  color: var(--muted);
}

@media (max-width: 768px) {
  .scroller-track > :deep(*) {
    flex: 0 0 190px;
  }
}
</style>
