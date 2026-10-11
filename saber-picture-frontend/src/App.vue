<template>
  <div id="app" class="grain">
    <a-config-provider :locale="zhCN" :theme="antdTheme">
      <BasicLayout />
    </a-config-provider>
  </div>
</template>

<script setup lang="ts">
import zhCN from 'ant-design-vue/es/locale/zh_CN';
import dayjs from 'dayjs';
import 'dayjs/locale/zh-cn';
dayjs.locale('zh-cn');

import BasicLayout from '@/layouts/BasicLayout.vue'

// 编辑杂志 · 奶油纸感 —— Ant Design 主题令牌
const antdTheme = {
  token: {
    colorPrimary: '#b0542e',
    colorInfo: '#b0542e',
    colorLink: '#b0542e',
    colorLinkHover: '#8f4220',
    colorSuccess: '#56714b',
    colorWarning: '#c98a2b',
    colorError: '#c0392b',
    colorBgLayout: '#f3ede1',
    colorBgContainer: '#fdfaf3',
    colorBgElevated: '#fffdf7',
    colorText: '#2a241c',
    colorTextSecondary: '#6b6152',
    colorTextTertiary: '#9a8f7d',
    colorBorder: '#e3d9c6',
    colorBorderSecondary: '#ece4d3',
    borderRadius: 10,
    borderRadiusLG: 16,
    borderRadiusSM: 6,
    fontSize: 14,
    fontFamily:
      "-apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', 'Noto Sans SC', 'Helvetica Neue', Arial, sans-serif",
    controlHeight: 36,
  },
  components: {
    Button: {
      borderRadius: 10,
      primaryShadow: '0 8px 20px -8px rgba(176, 84, 46, 0.55)',
      fontWeight: 500,
    },
    Layout: {
      headerBg: 'transparent',
      siderBg: 'transparent',
      headerPadding: '0',
    },
    Menu: {
      itemBg: 'transparent',
      subMenuItemBg: 'transparent',
      itemColor: '#6b6152',
      itemHoverColor: '#b0542e',
      itemSelectedColor: '#b0542e',
      itemHoverBg: 'rgba(176, 84, 46, 0.06)',
      itemSelectedBg: 'rgba(176, 84, 46, 0.1)',
      activeBarBorderWidth: 0,
    },
    Card: {
      borderRadiusLG: 16,
    },
    Modal: {
      borderRadiusLG: 16,
    },
    Tabs: {
      inkBarColor: '#b0542e',
      itemSelectedColor: '#b0542e',
      itemHoverColor: '#8f4220',
      titleFontSize: 15,
    },
    Progress: {
      remainingColor: '#ece4d3',
    },
    Table: {
      headerBg: '#f6f0e3',
      headerColor: '#6b6152',
      rowHoverBg: '#faf5ea',
    },
    Pagination: {
      itemActiveBg: '#f3e2d6',
    },
    Dropdown: {
      borderRadiusLG: 10,
    },
  },
}
</script>

<style scoped></style>
