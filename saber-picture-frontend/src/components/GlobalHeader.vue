<template>
  <div id="globalHeader" class="header-inner g-row">
    <router-link to="/" class="brand">
      <img class="logo" src="../assets/logo-mark.png" alt="logo" />
      <span class="brand-text">
        <span class="brand-name">Saber 云图库</span>
        <span class="brand-sub">SABER PICTURE</span>
      </span>
    </router-link>

    <nav class="nav">
      <template v-for="item in items" :key="item.key">
        <router-link
          v-if="item.key.startsWith('/')"
          :to="item.key"
          class="nav-item"
          :class="{ active: isActive(item.key) }"
        >
          {{ item.label }}
        </router-link>
        <a v-else :href="item.href" target="_blank" class="nav-item external">
          {{ item.label }}
        </a>
      </template>
    </nav>

    <!-- 用户信息展示栏 -->
    <div class="user-area">
      <template v-if="loginUserStore.loginUser.id">
        <a-dropdown placement="bottomRight">
          <button class="user-chip" type="button">
            <a-avatar :src="loginUserStore.loginUser.userAvatar" :size="30" />
            <span class="user-name">{{ loginUserStore.loginUser.userName ?? '无名' }}</span>
            <DownOutlined class="user-chevron" />
          </button>
          <template #overlay>
            <a-menu>
              <a-menu-item key="mySpace">
                <router-link to="/my_space">
                  <UserOutlined />
                  我的空间
                </router-link>
              </a-menu-item>
              <a-menu-divider />
              <a-menu-item key="logout" @click="doLogout">
                <LogoutOutlined />
                退出登录
              </a-menu-item>
            </a-menu>
          </template>
        </a-dropdown>
      </template>
      <router-link v-else to="/user/login" class="login-btn">登录</router-link>
    </div>
  </div>
</template>

<script lang="ts" setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { DownOutlined, LogoutOutlined, UserOutlined } from '@ant-design/icons-vue'
import { message } from 'ant-design-vue'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { userLogoutUsingPost } from '@/api/userController.ts'

interface NavItem {
  key: string
  label: string
  href?: string
}

const loginUserStore = useLoginUserStore()

// 未经过滤的菜单项
const originItems: NavItem[] = [
  { key: '/', label: '主页' },
  { key: '/add_picture', label: '创建图片' },
  { key: '/admin/userManage', label: '用户管理' },
  { key: '/admin/pictureManage', label: '图片管理' },
  { key: '/admin/spaceManage', label: '空间管理' },
]

// 根据权限过滤菜单项（管理员才能看到 /admin 开头的菜单）
const items = computed(() =>
  originItems.filter((menu) => {
    if (menu.key.startsWith('/admin')) {
      const loginUser = loginUserStore.loginUser
      if (!loginUser || loginUser.userRole !== 'admin') {
        return false
      }
    }
    return true
  }),
)

const route = useRoute()
const router = useRouter()

// 判断菜单项是否处于激活状态
const isActive = (key: string) => {
  if (key === '/') {
    return route.path === '/'
  }
  return route.path === key || route.path.startsWith(key + '/')
}

// 用户注销
const doLogout = async () => {
  const res = await userLogoutUsingPost()
  if (res.data.code === 0) {
    loginUserStore.setLoginUser({
      userName: '未登录',
    })
    message.success('退出登录成功')
    await router.push('/user/login')
  } else {
    message.error('退出登录失败，' + res.data.message)
  }
}
</script>

<style scoped>
/* 栅格（max-width / 留白）由全局 .g-row 提供，与内容区严格对齐 */
.header-inner {
  height: 72px;
  display: flex;
  align-items: center;
  gap: 28px;
}

/* —— 品牌 —— */
.brand {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  flex-shrink: 0;
}

.logo {
  height: 42px;
  border-radius: 10px;
  /* logo 主体是奶油色，导航栏底色也接近奶油白，靠贴纸式投影把边界从纸面托起来。
     用 drop-shadow 而不是 border：它会沿 PNG 的实际透明轮廓走，不会在圆角处画出错位的描边。 */
  filter: drop-shadow(0 1px 1px rgba(42, 36, 28, 0.2))
    drop-shadow(0 2px 6px rgba(42, 36, 28, 0.14));
}

.brand-text {
  display: flex;
  flex-direction: column;
  line-height: 1.2;
}

.brand-name {
  font-family: var(--font-display);
  font-size: 19px;
  font-weight: 700;
  color: var(--ink);
  letter-spacing: 0.08em;
}

.brand-sub {
  font-size: 10px;
  letter-spacing: 0.12em;
  color: var(--muted);
  text-transform: uppercase;
}

/* —— 导航 —— */
.nav {
  display: flex;
  align-items: center;
  gap: 4px;
  margin-left: auto;
}

.nav-item {
  position: relative;
  padding: 6px 14px;
  border-radius: 10px;
  color: var(--ink-soft);
  font-size: 14.5px;
  text-decoration: none;
  transition: color 0.3s ease, background 0.3s ease;
}

.nav-item:hover {
  color: var(--brand);
  background: rgba(176, 84, 46, 0.06);
}

.nav-item.active {
  color: var(--brand-deep);
  font-weight: 600;
}

.nav-item.active::after {
  content: '';
  position: absolute;
  left: 16px;
  right: 16px;
  bottom: 1px;
  height: 2px;
  border-radius: 10px;
  background: var(--brand);
}

/* —— 用户区 —— */
.user-area {
  flex-shrink: 0;
}

.user-chip {
  display: flex;
  align-items: center;
  gap: 8px;
  border: 1px solid var(--line);
  background: var(--surface-raised);
  padding: 4px 12px 4px 4px;
  border-radius: 10px;
  cursor: pointer;
  font: inherit;
  color: var(--ink);
  box-shadow: var(--shadow-sm);
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
}

.user-chip:hover {
  border-color: var(--brand);
  box-shadow: var(--shadow-md);
}

.user-name {
  font-size: 14px;
  max-width: 120px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.user-chevron {
  font-size: 10px;
  color: var(--muted);
}

.login-btn {
  display: inline-flex;
  align-items: center;
  height: 38px;
  padding: 0 24px;
  border-radius: 10px;
  background: var(--brand);
  color: #fff;
  font-size: 14px;
  text-decoration: none;
  box-shadow: 0 8px 20px -8px rgba(176, 84, 46, 0.55);
  transition: transform 0.3s ease, filter 0.3s ease;
}

.login-btn:hover {
  color: #fff;
  filter: brightness(1.05);
  transform: translateY(-1px);
}

/* 窄屏隐藏顶部导航（保留品牌与用户区） */
@media (max-width: 900px) {
  .nav {
    display: none;
  }
}
</style>
