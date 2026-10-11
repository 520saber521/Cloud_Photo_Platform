<template>
  <AuthLayout ref="authLayoutRef">
    <p class="eyebrow auth-eyebrow">加入我们 · Join Us</p>
    <h1 class="auth-title">
      创建账号，收藏第一份<span class="accent">灵感</span>
    </h1>
    <p class="auth-desc">注册后即可创建图库空间，上传与协同编辑图片</p>

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

      <a-form-item
        name="checkPassword"
        :rules="[
          { required: true, message: '请输入确认密码' },
          { min: 8, message: '确认密码长度不能小于 8 位' },
        ]"
      >
        <template #label><span class="field-no">03</span>确认密码</template>
        <a-input-password
          v-model:value="formState.checkPassword"
          placeholder="请再次输入密码"
          size="large"
        />
      </a-form-item>

      <!-- 印刷勘误式错误提示（替代 toast） -->
      <div v-if="errata" class="errata">
        <p class="errata-label">勘误 · Errata</p>
        <p class="errata-text">{{ errata }}</p>
      </div>

      <a-button type="primary" html-type="submit" size="large" block class="submit-btn">
        注 册
      </a-button>

      <p class="auth-switch">
        已有账号？<RouterLink to="/user/login">去登录</RouterLink>
      </p>
    </a-form>
  </AuthLayout>
</template>

<script lang="ts" setup>
import { reactive, ref, watch } from 'vue'
import { userRegisterUsingPost } from '@/api/userController.ts'
import { message } from 'ant-design-vue'
import router from '@/router'
import AuthLayout from '@/layouts/AuthLayout.vue'

// 用于接受表单输入的值
const formState = reactive<API.UserRegisterRequest>({
  userAccount: '',
  userPassword: '',
  checkPassword: '',
})

// 版式组件的引用，用于播放注册成功后的离场转场
const authLayoutRef = ref()
// 勘误文案（注册失败时在表单内联展示）
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
  // 校验两次输入的密码是否一致
  if (values.userPassword !== values.checkPassword) {
    errata.value = '两次输入的密码不一致，请重新确认'
    return
  }
  try {
    const res = await userRegisterUsingPost(values)
    // 注册成功，跳转到登录页面
    if (res.data.code === 0 && res.data.data) {
      message.success('注册成功，请登录')
      await authLayoutRef.value?.playExit()
      router.push({
        path: '/user/login',
        replace: true,
      })
    } else {
      errata.value = res.data.message || '注册失败，请稍后重试'
    }
  } catch (e) {
    errata.value = '注册请求失败，请确认后端服务已启动（http://localhost:8123）'
  }
}
</script>

<style scoped>
.auth-eyebrow {
  margin-bottom: 14px;
}

.auth-title {
  font-size: clamp(28px, 2.9vw, 36px);
  font-weight: 700;
  line-height: 1.28;
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
  margin-bottom: 34px;
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
