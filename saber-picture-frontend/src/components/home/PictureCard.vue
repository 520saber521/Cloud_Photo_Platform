<template>
  <article class="pic-card" :class="[`pic-card--${variant}`]" @click="goDetail">
    <div class="pic-media">
      <img :src="picture.thumbnailUrl ?? picture.url ?? ''" :alt="picture.name ?? ''" loading="lazy" />
      <!-- 排行编号 -->
      <span v-if="rank" class="pic-rank">{{ rank }}</span>
      <!-- 角标（对应原站 txt-tag） -->
      <span v-if="badge" class="pic-badge">{{ badge }}</span>
    </div>

    <div class="pic-body">
      <p class="pic-meta-row">
        <span class="pic-cate">{{ picture.category ?? '未分类' }}</span>
        <span v-if="isNew" class="pic-new">New</span>
      </p>
      <h3 class="pic-title">{{ picture.name ?? '未命名' }}</h3>
      <p class="pic-foot">
        <span v-if="picture.userId" class="pic-author">作者 {{ shortUser }}</span>
        <span class="pic-date">{{ dateText }}</span>
      </p>
    </div>
  </article>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'

/**
 * 通用图片卡（对齐原站的卡片单元：图 + 角标 + 分类 + 标题 + 日期）
 */
interface Props {
  picture: API.PictureVO
  /** 排行编号，传入则左上角显示序号 */
  rank?: number
  /** 图片上的角标文案 */
  badge?: string
  /** 尺寸变体：grid 网格 / row 横向行 / large 大卡 */
  variant?: 'grid' | 'row' | 'large'
}

const props = withDefaults(defineProps<Props>(), {
  variant: 'grid',
})

const router = useRouter()

const goDetail = () => {
  if (props.picture.id) {
    router.push(`/picture/${props.picture.id}`)
  }
}

// 一周内视为新图
const isNew = computed(() => {
  const t = props.picture.createTime
  if (!t) {
    return false
  }
  const diff = Date.now() - new Date(t).getTime()
  return diff >= 0 && diff < 7 * 24 * 3600 * 1000
})

const dateText = computed(() => {
  const t = props.picture.createTime
  if (!t) {
    return ''
  }
  const d = new Date(t)
  const mm = String(d.getMonth() + 1).padStart(2, '0')
  const dd = String(d.getDate()).padStart(2, '0')
  return `${d.getFullYear()}.${mm}.${dd}`
})

const shortUser = computed(() => String(props.picture.userId ?? '').slice(-4))
</script>

<style scoped>
.pic-card {
  display: flex;
  flex-direction: column;
  background: var(--surface);
  border: 1px solid var(--line-soft);
  border-radius: var(--radius);
  overflow: hidden;
  cursor: pointer;
  transition: transform var(--dur) var(--ease), box-shadow var(--dur) var(--ease),
    border-color var(--dur) var(--ease);
}

.pic-card:hover {
  transform: translateY(-3px);
  border-color: var(--line);
  box-shadow: var(--shadow-md);
}

.pic-media {
  position: relative;
  overflow: hidden;
  background: var(--bg-deep);
  aspect-ratio: 4 / 3;
}

.pic-card--large .pic-media {
  aspect-ratio: 16 / 10;
}

.pic-card--row .pic-media {
  aspect-ratio: 3 / 2;
}

.pic-media img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--dur) var(--ease);
}

.pic-card:hover .pic-media img {
  transform: scale(1.045);
}

/* 排行编号：印刷式大号衬线数字 */
.pic-rank {
  position: absolute;
  top: 0;
  left: 0;
  min-width: 38px;
  padding: 6px 11px 8px;
  background: var(--brand);
  color: #fff;
  font-family: var(--font-display);
  font-size: 20px;
  font-weight: 700;
  line-height: 1;
  text-align: center;
}

/* 角标 */
.pic-badge {
  position: absolute;
  left: 10px;
  bottom: 10px;
  padding: 3px 10px;
  background: rgba(34, 28, 21, 0.82);
  color: #f7f2e8;
  font-size: 10.5px;
  letter-spacing: 0.12em;
  border-radius: var(--radius);
  backdrop-filter: blur(3px);
}

.pic-body {
  padding: 12px 13px 14px;
  display: flex;
  flex-direction: column;
  gap: 7px;
  flex: 1;
}

.pic-meta-row {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 10.5px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
}

.pic-cate {
  color: var(--brand);
  font-weight: 600;
}

.pic-new {
  color: var(--gold);
  font-weight: 600;
}

/* 卡片标题：上方一道主题色描边（借自 LEE「.txt-hdg」的签名细节） */
.pic-title {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  line-height: 1.5;
  letter-spacing: 0.08em;
  color: var(--ink);
  border-top: 1px solid var(--brand);
  padding-top: 9px;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.pic-card--large .pic-title {
  font-size: 19px;
}

.pic-foot {
  margin-top: auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 10px;
  font-size: 11px;
  color: var(--muted);
  letter-spacing: 0.08em;
  padding-top: 2px;
}
</style>
