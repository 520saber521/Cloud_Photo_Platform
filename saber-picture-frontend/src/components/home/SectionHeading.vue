<template>
  <div class="sec-head" :class="[`sec-head--${size}`]">
    <div class="sec-head-main">
      <h2 class="sec-title">
        <span class="sec-title-en">{{ en }}</span>
        <span class="sec-title-cn">{{ cn }}</span>
      </h2>
      <p v-if="desc" class="sec-desc">{{ desc }}</p>
    </div>
    <div class="sec-head-side">
      <slot name="side" />
      <router-link v-if="moreTo" :to="moreTo" class="sec-more">
        {{ moreText }}<span class="sec-more-arrow">→</span>
      </router-link>
    </div>
  </div>
</template>

<script setup lang="ts">
/**
 * 区块双标题：英文主标题 + 中文副标题（纵向堆叠）
 * 版式规范借鉴 LEE 的「hdg1-pt1」：大字号 + 常规字重（400）的衬线主标，
 * 靠字号、行高与留白撑气场，而不是靠加粗。
 */
interface Props {
  /** 英文主标题 */
  en: string
  /** 中文副标题 */
  cn: string
  /** 可选说明文字 */
  desc?: string
  /** 「更多」跳转地址，不传则不显示 */
  moreTo?: string
  /** 「更多」文案 */
  moreText?: string
  /** 尺寸变体：md 常规（32px）/ sm 小号（18px） */
  size?: 'md' | 'sm'
}

withDefaults(defineProps<Props>(), {
  moreText: '查看全部',
  size: 'md',
})
</script>

<style scoped>
.sec-head {
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  padding-bottom: 14px;
  border-bottom: 1px solid var(--line);
  margin-bottom: 22px;
}

/* 主标与副标纵向堆叠 */
.sec-title {
  display: flex;
  flex-direction: column;
}

.sec-title-en {
  font-family: var(--font-display);
  font-size: 32px;
  font-weight: 400;
  line-height: 1.6;
  letter-spacing: 0.06em;
  color: var(--ink);
}

.sec-title-cn {
  font-size: 12px;
  letter-spacing: 0.1em;
  color: var(--muted);
}

/* 小号变体（对应原站 ver-s） */
.sec-head--sm .sec-title-en {
  font-size: 18px;
}

.sec-head--sm .sec-title-cn {
  margin-top: 2px;
  font-size: 10px;
}

.sec-desc {
  margin-top: 6px;
  font-size: 13px;
  line-height: 1.65;
  letter-spacing: 0.08em;
  color: var(--ink-soft);
  max-width: 52ch;
}

.sec-head-side {
  display: flex;
  align-items: center;
  gap: 14px;
  flex-shrink: 0;
  padding-bottom: 3px;
}

.sec-more {
  font-size: 12px;
  letter-spacing: 0.08em;
  color: var(--brand);
  text-decoration: none;
  white-space: nowrap;
}

.sec-more-arrow {
  margin-left: 5px;
  transition: transform var(--dur) var(--ease);
  display: inline-block;
}

.sec-more:hover .sec-more-arrow {
  transform: translateX(3px);
}

@media (max-width: 768px) {
  .sec-title-en {
    font-size: 24px;
  }

  .sec-head {
    align-items: flex-start;
    flex-direction: column;
    gap: 10px;
  }
}
</style>
