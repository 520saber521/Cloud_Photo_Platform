<template>
  <div id="basicLayout">
    <!-- 登录 / 注册：使用独立的杂志跨页版式，不套站点头尾 -->
    <router-view v-if="isAuthLayout" />

    <a-layout v-else>
      <a-layout-header class="header">
        <GlobalHeader />
      </a-layout-header>
      <a-layout class="body">
        <GlobalSider class="sider" />
        <a-layout-content class="content">
          <div class="content-inner g-row">
            <router-view v-slot="{ Component }">
              <transition name="page" mode="out-in">
                <component :is="Component" />
              </transition>
            </router-view>
          </div>
        </a-layout-content>
      </a-layout>
      <a-layout-footer class="footer">
        <span class="footer-mark">Saber 云图库</span>
        <span class="footer-divider">·</span>
        <span>智能协同云图库平台</span>
      </a-layout-footer>
    </a-layout>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import GlobalHeader from '@/components/GlobalHeader.vue'
import GlobalSider from "@/components/GlobalSider.vue";

const route = useRoute()
// 路由 meta 标记为 auth 的页面（登录 / 注册）走独立版式
const isAuthLayout = computed(() => route.meta?.layout === 'auth')
</script>

<style scoped>
#basicLayout .header {
  position: sticky;
  top: 0;
  z-index: 100;
  height: 72px;
  line-height: 72px;
  padding-inline: 0;
  background: rgba(253, 250, 243, 0.82);
  backdrop-filter: blur(14px) saturate(1.5);
  -webkit-backdrop-filter: blur(14px) saturate(1.5);
  border-bottom: 1px solid var(--line-soft);
  margin-bottom: 0;
}

#basicLayout .body {
  background: transparent;
}

#basicLayout .sider {
  background: transparent;
  padding-top: 28px;
}

#basicLayout .content {
  min-height: calc(100vh - 72px);
  padding: 0;
  background: transparent;
}

#basicLayout .content-inner {
  padding-block: 40px 72px;
}

/* 内容区最大宽度 1280px（居中与留白由 .g-row 负责） */
#basicLayout .content-inner :deep(> *) {
  max-width: var(--container);
  margin-left: auto;
  margin-right: auto;
}

#basicLayout .footer {
  background: transparent;
  border-top: 1px solid var(--line-soft);
  padding: 20px 24px;
  text-align: center;
  color: var(--muted);
  font-size: 13px;
  letter-spacing: 0.08em;
}

#basicLayout .footer a {
  color: var(--ink-soft);
}

#basicLayout .footer a:hover {
  color: var(--brand);
}

.footer-mark {
  font-family: var(--font-display);
  color: var(--ink-soft);
  font-weight: 600;
}

.footer-divider {
  margin: 0 10px;
  color: var(--line);
}

/* 窄屏下内容留白收紧 */
@media (max-width: 768px) {
  #basicLayout .content {
    padding: 20px 16px 48px;
  }
}
</style>
