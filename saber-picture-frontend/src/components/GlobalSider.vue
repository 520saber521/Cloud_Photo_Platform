<template>
  <div id="globalSider">
    <a-layout-sider
      v-if="loginUserStore.loginUser.id"
      width="232"
      breakpoint="lg"
      collapsed-width="0"
      class="sider-panel"
    >
      <nav class="side-nav">
        <router-link
          v-for="item in fixedMenuItems"
          :key="item.key"
          :to="item.key"
          class="side-item"
          :class="{ active: isActive(item.key) }"
        >
          <component :is="item.icon" class="side-icon" />
          <span>{{ item.label }}</span>
        </router-link>

        <!-- 团队空间分组 -->
        <template v-if="teamSpaceList.length">
          <div class="side-group">我的团队</div>
          <router-link
            v-for="spaceUser in teamSpaceList"
            :key="spaceUser.spaceId"
            :to="'/space/' + spaceUser.spaceId"
            class="side-item"
            :class="{ active: isActive('/space/' + spaceUser.spaceId) }"
          >
            <span class="side-dot" />
            <span class="side-text">{{ spaceUser.space?.spaceName }}</span>
          </router-link>
        </template>
      </nav>
    </a-layout-sider>
  </div>
</template>

<script lang="ts" setup>
import { ref, watchEffect } from 'vue'
import { PictureOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons-vue'
import { useRoute } from 'vue-router'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { SPACE_TYPE_ENUM } from '@/constants/space.ts'
import { listMyTeamSpaceUsingPost } from '@/api/spaceUserController.ts'
import { message } from 'ant-design-vue'

const loginUserStore = useLoginUserStore()

// 固定的菜单列表
const fixedMenuItems = [
  {
    key: '/',
    icon: PictureOutlined,
    label: '公共图库',
  },
  {
    key: '/my_space',
    icon: UserOutlined,
    label: '我的空间',
  },
  {
    key: '/add_space?type=' + SPACE_TYPE_ENUM.TEAM,
    icon: TeamOutlined,
    label: '创建团队',
  },
]

const teamSpaceList = ref<API.SpaceUserVO[]>([])

// 加载团队空间列表
const fetchTeamSpaceList = async () => {
  const res = await listMyTeamSpaceUsingPost()
  if (res.data.code === 0 && res.data.data) {
    teamSpaceList.value = res.data.data
  } else {
    message.error('加载我的团队空间失败，' + res.data.message)
  }
}

/**
 * 监听变量，改变时触发数据的重新加载
 */
watchEffect(() => {
  // 登录才加载
  if (loginUserStore.loginUser.id) {
    fetchTeamSpaceList()
  }
})

const route = useRoute()

// 判断菜单项是否激活（忽略 query 参数）
const isActive = (key: string) => {
  const path = key.split('?')[0]
  if (path === '/') {
    return route.path === '/'
  }
  return route.path === path || route.path.startsWith(path + '/')
}
</script>

<style scoped>
#globalSider .sider-panel {
  background: transparent;
  border-right: 1px solid var(--line-soft);
}

.side-nav {
  display: flex;
  flex-direction: column;
  gap: 4px;
  padding: 0 12px;
  position: sticky;
  top: 100px;
}

.side-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 14px;
  border-radius: 10px;
  color: var(--ink-soft);
  font-size: 14px;
  text-decoration: none;
  transition: background 0.3s ease, color 0.3s ease;
}

.side-item:hover {
  background: rgba(176, 84, 46, 0.06);
  color: var(--brand);
}

.side-item.active {
  background: var(--brand-soft);
  color: var(--brand-deep);
  font-weight: 600;
}

.side-icon {
  font-size: 16px;
}

.side-group {
  margin: 18px 14px 6px;
  font-size: 11px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--muted);
  font-weight: 600;
}

.side-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: var(--line);
  flex-shrink: 0;
}

.side-item.active .side-dot {
  background: var(--brand);
}

.side-text {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
