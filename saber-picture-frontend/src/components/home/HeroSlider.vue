<template>
  <section v-if="pictures.length" class="hero">
    <div class="hero-stage">
      <transition-group name="hero-fade">
        <div
          v-for="(pic, index) in pictures"
          v-show="index === current"
          :key="pic.id ?? index"
          class="hero-slide"
          @click="goDetail(pic)"
        >
          <img :src="pic.url ?? pic.thumbnailUrl ?? ''" :alt="pic.name ?? ''" />
          <div class="hero-veil" />
          <div class="hero-caption">
            <p class="hero-meta">
              <span class="hero-cate">{{ pic.category ?? '未分类' }}</span>
              <span v-if="isNew(pic)" class="hero-new">New</span>
            </p>
            <h2 class="hero-title">{{ pic.name ?? '未命名' }}</h2>
            <p class="hero-sub">{{ pic.introduction || 'Saber 云图库精选' }}</p>
            <p class="hero-date">{{ dateText(pic) }}</p>
          </div>
        </div>
      </transition-group>

      <!-- 印刷式页码：1 | N -->
      <div class="hero-bar">
        <button class="hero-btn" type="button" aria-label="上一张" @click="prev">‹</button>
        <span class="hero-pager">
          <span class="hero-now">{{ current + 1 }}</span>
          <span class="hero-sep">|</span>
          <span class="hero-all">{{ pictures.length }}</span>
        </span>
        <button class="hero-btn" type="button" aria-label="下一张" @click="next">›</button>
        <router-link to="/?category=all" class="hero-all-link">查看全部精选</router-link>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { onBeforeUnmount, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'

/**
 * 精选大图轮播（对应原站首屏 トップスライダー）
 */
interface Props {
  pictures: API.PictureVO[]
}

const props = defineProps<Props>()

const router = useRouter()
const current = ref(0)
let timer: number | undefined

const goDetail = (pic: API.PictureVO) => {
  if (pic.id) {
    router.push(`/picture/${pic.id}`)
  }
}

const next = () => {
  if (props.pictures.length) {
    current.value = (current.value + 1) % props.pictures.length
  }
}

const prev = () => {
  if (props.pictures.length) {
    current.value = (current.value - 1 + props.pictures.length) % props.pictures.length
  }
}

const isNew = (pic: API.PictureVO) => {
  const t = pic.createTime
  if (!t) {
    return false
  }
  const diff = Date.now() - new Date(t).getTime()
  return diff >= 0 && diff < 7 * 24 * 3600 * 1000
}

const dateText = (pic: API.PictureVO) => {
  if (!pic.createTime) {
    return ''
  }
  const d = new Date(pic.createTime)
  return `${d.getFullYear()}.${String(d.getMonth() + 1).padStart(2, '0')}.${String(d.getDate()).padStart(2, '0')}`
}

const startTimer = () => {
  timer = window.setInterval(next, 5000)
}

const stopTimer = () => {
  if (timer) {
    window.clearInterval(timer)
    timer = undefined
  }
}

onMounted(() => {
  if (props.pictures.length > 1) {
    startTimer()
  }
})

onBeforeUnmount(stopTimer)
</script>

<style scoped>
.hero-stage {
  position: relative;
  border: 1px solid var(--line-soft);
  /* 签名式对角圆角 */
  border-radius: var(--radius-diagonal);
  overflow: hidden;
  background: #221c15;
}

.hero-slide {
  position: relative;
  cursor: pointer;
}

.hero-slide img {
  display: block;
  width: 100%;
  aspect-ratio: 16 / 7;
  object-fit: cover;
}

.hero-veil {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    to top,
    rgba(24, 18, 12, 0.82) 0%,
    rgba(24, 18, 12, 0.42) 38%,
    rgba(24, 18, 12, 0.08) 68%,
    transparent 100%
  );
}

.hero-caption {
  position: absolute;
  left: 40px;
  right: 40px;
  bottom: 34px;
  color: #f7f2e8;
  animation: rise 0.6s ease both;
}

.hero-meta {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 12px;
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.hero-cate {
  color: var(--gold);
  font-weight: 600;
}

.hero-new {
  padding: 2px 8px;
  background: var(--brand);
  border-radius: var(--radius);
  color: #fff;
  letter-spacing: 0.12em;
}

.hero-title {
  font-family: var(--font-display);
  font-size: clamp(24px, 3.2vw, 42px);
  font-weight: 700;
  line-height: 1.24;
  letter-spacing: 0.06em;
  max-width: 22ch;
  margin-bottom: 10px;
}

.hero-sub {
  font-size: 13.5px;
  line-height: 1.7;
  color: rgba(247, 242, 232, 0.78);
  max-width: 46ch;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hero-date {
  margin-top: 14px;
  font-size: 11px;
  letter-spacing: 0.08em;
  color: rgba(247, 242, 232, 0.6);
}

/* 控制条 */
.hero-bar {
  position: absolute;
  right: 24px;
  top: 24px;
  display: flex;
  align-items: center;
  gap: 4px;
  background: rgba(34, 28, 21, 0.5);
  border-radius: var(--radius);
  padding: 5px 6px;
  backdrop-filter: blur(4px);
}

.hero-btn {
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: #f7f2e8;
  font-size: 16px;
  line-height: 1;
  cursor: pointer;
  border-radius: var(--radius-sm);
  transition: background var(--dur) var(--ease);
}

.hero-btn:hover {
  background: rgba(247, 242, 232, 0.16);
}

.hero-pager {
  font-family: var(--font-display);
  font-size: 12.5px;
  letter-spacing: 0.12em;
  color: #f7f2e8;
  padding: 0 6px;
}

.hero-sep {
  margin: 0 6px;
  opacity: 0.5;
}

.hero-all {
  opacity: 0.65;
}

.hero-all-link {
  margin-left: 6px;
  padding: 0 10px;
  font-size: 11.5px;
  letter-spacing: 0.08em;
  color: #f7f2e8;
  text-decoration: none;
  border-left: 1px solid rgba(247, 242, 232, 0.24);
}

.hero-all-link:hover {
  color: var(--gold);
}

/* 切换动画 */
.hero-fade-enter-active,
.hero-fade-leave-active {
  transition: opacity var(--dur) var(--ease);
}

.hero-fade-enter-from,
.hero-fade-leave-to {
  opacity: 0;
}

.hero-fade-leave-active {
  position: absolute;
  inset: 0;
}

@media (max-width: 768px) {
  .hero-caption {
    left: 20px;
    right: 20px;
    bottom: 22px;
  }

  .hero-bar {
    right: 12px;
    top: 12px;
  }

  .hero-slide img {
    aspect-ratio: 4 / 3;
  }
}
</style>
