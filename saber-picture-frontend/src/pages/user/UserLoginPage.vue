<template>
  <AuthLayout ref="authLayoutRef">
    <p class="eyebrow auth-eyebrow">欢迎回来 · Welcome Back</p>
    <h1 class="auth-title">
      登录，开启你的<span class="accent">灵感</span>库
    </h1>
    <p class="auth-desc">使用账号密码进入你的图片空间</p>

    <a-form
      :model="formState"
      name="basic"
      layout="vertical"
      autocomplete="off"
      @finish="handleSubmit"
    >
      <a-form-item name="userAccount" :rules="[{ required: true, message: '请输入账号' }]">
        <template #label><span class="field-no">01</span>账号</template>
        <a-input v-model:value="formState.userAccount" placeholder="请输入账号" size="large" />
      </a-form-item>

      <a-form-item
        name="userPassword"
        :rules="[
          { required: true, message: '请输入密码' },
          { min: 8, message: '密码长度不能小于 8 位' },
        ]"
      >
        <template #label><span class="field-no">02</span>密码</template>
        <a-input-password
          v-model:value="formState.userPassword"
          placeholder="请输入密码"
          size="large"
        />
      </a-form-item>

      <!-- 印刷勘误式错误提示（替代 toast） -->
      <div v-if="errata" class="errata">
        <p class="errata-label">勘误 · Errata</p>
        <p class="errata-text">{{ errata }}</p>
      </div>

      <a-button type="primary" html-type="submit" size="large" block class="submit-btn">
        登 录
      </a-button>

      <p class="auth-switch">
        没有账号？<RouterLink to="/user/register">去注册</RouterLink>
      </p>
    </a-form>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { userLoginUsingPost } from '@/api/userController.ts'
import { useLoginUserStore } from '@/stores/useLoginUserStore.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import AuthLayout from '@/layouts/AuthLayout.vue'

// 用于接受表单输入的值
const formState = reactive<API.UserLoginRequest>({
  userAccount: '',
  userPassword: '',
})

const loginUserStore = useLoginUserStore()
// 版式组件的引用，用于播放登录成功后的离场转场
const authLayoutRef = ref()
// 勘误文案（登录失败时在表单内联展示）
const errata = ref('')

// 用户改动输入时清掉勘误提示
watch(formState, () => {
  if (errata.value) {
    errata.value = ''
  }
})

/**
 * 提交表单
 * @param values
 */
const handleSubmit = async (values: any) => {
  errata.value = ''
  try {
    const res = await userLoginUsingPost(values)
    // 登录成功，把登录态保存到全局状态中
    if (res.data.code === 0 && res.data.data) {
      await loginUserStore.fetchLoginUser()
      message.success('登录成功')
      // 右页表单淡出 → 左页图版推进 → 整页淡出，再进入首页
      await authLayoutRef.value?.playExit()
      router.push({
        path: '/',
        replace: true,
      })
    } else {
      errata.value = res.data.message || '账号或密码不正确，请重新输入'
    }
  } catch (e) {
    errata.value = '登录请求失败，请确认后端服务已启动（http://localhost:8123）'
  }
}
</script>

<style scoped>
.auth-eyebrow {
  margin-bottom: 14px;
}

.auth-title {
  font-size: clamp(30px, 3.1vw, 38px);
  font-weight: 700;
  line-height: 1.26;
  letter-spacing: 0.08em;
  margin-bottom: 12px;
}

.auth-title .accent {
  color: var(--brand);
}

.auth-desc {
  font-size: 13.5px;
  color: var(--ink-soft);
  letter-spacing: 0.08em;
  margin-bottom: 40px;
}

.submit-btn {
  margin-top: 8px;
}

.auth-switch {
  margin-top: 26px;
  padding-top: 15px;
  border-top: 1px solid var(--line-soft);
  font-size: 12.5px;
  letter-spacing: 0.08em;
  color: var(--muted);
}

.auth-switch a {
  color: var(--brand);
  font-weight: 600;
}
</style>
