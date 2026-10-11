<template>
  <div class="picture-list">
    <!-- 加载中：骨架屏 -->
    <div v-if="loading" class="masonry" aria-busy="true">
      <div v-for="i in 12" :key="i" class="m-item">
        <div class="skel" :style="{ height: skelHeight(i) }" />
      </div>
    </div>

    <!-- 图片瀑布流 -->
    <div v-else-if="dataList.length" class="masonry">
      <div v-for="picture in dataList" :key="picture.id" class="m-item">
        <figure class="pic-card" @click="doClickPicture(picture)">
          <div class="pic-media">
            <img
              :src="picture.thumbnailUrl ?? picture.url ?? ''"
              :alt="picture.name"
              loading="lazy"
            />
            <!-- 悬停操作层 -->
            <div v-if="showOp" class="pic-actions" @click.stop>
              <button class="act" title="分享" @click="doShare(picture, $event)">
                <ShareAltOutlined />
              </button>
              <button class="act" title="找相似" @click="doSearch(picture, $event)">
                <SearchOutlined />
              </button>
              <button v-if="canEdit" class="act" title="编辑" @click="doEdit(picture, $event)">
                <EditOutlined />
              </button>
              <button
                v-if="canDelete"
                class="act danger"
                title="删除"
                @click="doDelete(picture, $event)"
              >
                <DeleteOutlined />
              </button>
            </div>
          </div>
          <figcaption class="pic-meta">
            <div class="pic-name" :title="picture.name">{{ picture.name }}</div>
            <div class="pic-tags">
              <span class="mini-tag cat">{{ picture.category ?? '默认' }}</span>
              <span v-for="tag in picture.tags" :key="tag" class="mini-tag">{{ tag }}</span>
            </div>
          </figcaption>
        </figure>
      </div>
    </div>

    <a-empty v-else description="暂无图片" />

    <ShareModal ref="shareModalRef" :link="shareLink" />
  </div>
</template>

<script setup lang="ts">
import { useRouter } from 'vue-router'
import {
  DeleteOutlined,
  EditOutlined,
  SearchOutlined,
  ShareAltOutlined,
} from '@ant-design/icons-vue'
import { deletePictureUsingPost } from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import ShareModal from '@/components/ShareModal.vue'
import { ref } from 'vue'

interface Props {
  dataList?: API.PictureVO[]
  loading?: boolean
  showOp?: boolean
  canEdit?: boolean
  canDelete?: boolean
  onReload?: () => void
}

const props = withDefaults(defineProps<Props>(), {
  dataList: () => [],
  loading: false,
  showOp: false,
  canEdit: false,
  canDelete: false,
})

const router = useRouter()

// 骨架屏高度交错变化，营造杂志排版感
const skelHeight = (i: number) => `${160 + ((i * 47) % 130)}px`

// 跳转至图片详情页
const doClickPicture = (picture: API.PictureVO) => {
  router.push({
    path: `/picture/${picture.id}`,
  })
}

// 搜索
const doSearch = (picture: API.PictureVO, e: any) => {
  // 阻止冒泡
  e.stopPropagation()
  // 打开新的页面
  window.open(`/search_picture?pictureId=${picture.id}`)
}

// 编辑
const doEdit = (picture: API.PictureVO, e: any) => {
  // 阻止冒泡
  e.stopPropagation()
  // 跳转时一定要携带 spaceId
  router.push({
    path: '/add_picture',
    query: {
      id: picture.id,
      spaceId: picture.spaceId,
    },
  })
}

// 删除数据
const doDelete = async (picture: API.PictureVO, e: any) => {
  // 阻止冒泡
  e.stopPropagation()
  const id = picture.id
  if (!id) {
    return
  }
  const res = await deletePictureUsingPost({ id })
  if (res.data.code === 0) {
    message.success('删除成功')
    props.onReload?.()
  } else {
    message.error('删除失败')
  }
}

// ----- 分享操作 ----
const shareModalRef = ref()
// 分享链接
const shareLink = ref<string>()
// 分享
const doShare = (picture: API.PictureVO, e: any) => {
  // 阻止冒泡
  e.stopPropagation()
  shareLink.value = `${window.location.protocol}//${window.location.host}/picture/${picture.id}`
  if (shareModalRef.value) {
    shareModalRef.value.openModal()
  }
}
</script>

<style scoped>
/* —— 瀑布流 —— */
.masonry {
  columns: 250px;
  column-gap: 18px;
  animation: rise 0.5s ease both;
}

.m-item {
  break-inside: avoid;
  margin-bottom: 18px;
}

/* —— 图片卡片 —— */
.pic-card {
  margin: 0;
  background: var(--surface);
  border: 1px solid var(--line-soft);
  border-radius: 16px;
  overflow: hidden;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.pic-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--shadow-md);
}

.pic-media {
  position: relative;
  overflow: hidden;
  background: var(--bg-deep);
}

.pic-media img {
  display: block;
  width: 100%;
  height: auto;
  transition: transform 0.3s ease;
}

.pic-card:hover .pic-media img {
  transform: scale(1.05);
}

/* —— 悬停操作层 —— */
.pic-actions {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  padding: 30px 12px 12px;
  background: linear-gradient(to top, rgba(24, 17, 9, 0.55), transparent);
  opacity: 0;
  transform: translateY(8px);
  transition: opacity 0.3s ease, transform 0.3s ease;
}

.pic-card:hover .pic-actions {
  opacity: 1;
  transform: none;
}

/* 触屏设备始终展示操作 */
@media (hover: none) {
  .pic-actions {
    opacity: 1;
    transform: none;
  }
}

.act {
  width: 34px;
  height: 34px;
  border: none;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.92);
  color: #3a3228;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 15px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.18);
  transition: background 0.3s ease, color 0.3s ease, transform 0.3s ease;
}

.act:hover {
  background: var(--brand);
  color: #fff;
  transform: translateY(-2px);
}

.act.danger:hover {
  background: #c0392b;
}

/* —— 卡片信息 —— */
.pic-meta {
  padding: 12px 14px 14px;
}

.pic-name {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  color: var(--ink);
  letter-spacing: 0.08em;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  margin-bottom: 8px;
}

.pic-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.mini-tag {
  font-size: 11px;
  color: var(--ink-soft);
  background: #f1e9d8;
  padding: 2px 9px;
  border-radius: 10px;
}

.mini-tag.cat {
  background: var(--brand-soft);
  color: var(--brand-deep);
  font-weight: 600;
}

/* —— 骨架屏 —— */
.skel {
  border-radius: 16px;
  background: linear-gradient(100deg, #ece3d1 40%, #f6f0e2 50%, #ece3d1 60%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
}
</style>
