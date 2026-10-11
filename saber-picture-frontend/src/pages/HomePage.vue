<template>
  <div id="homePage">
    <!-- ① 精选大图轮播 -->
    <HeroSlider :pictures="heroPictures" />

    <!-- ② 今日推荐 -->
    <section v-if="todayPictures.length" class="block">
      <SectionHeading en="Today's Pick" cn="今日推荐" desc="编辑部每天挑出的几张，先看这一组。" />
      <div class="grid grid--4">
        <PictureCard v-for="pic in todayPictures" :key="pic.id" :picture="pic" />
      </div>
    </section>

    <!-- ③ 最新入库（网格 + 热门标签） -->
    <section class="block">
      <SectionHeading en="What's New" cn="最新入库">
        <template #side>
          <div class="kw-row">
            <span class="kw-label">热门标签</span>
            <button
              v-for="tag in hotTags"
              :key="tag"
              class="kw"
              type="button"
              @click="filterByTag(tag)"
            >
              #{{ tag }}
            </button>
          </div>
        </template>
      </SectionHeading>

      <div v-if="loading" class="grid grid--3">
        <div v-for="i in 6" :key="i" class="skel" />
      </div>
      <div v-else-if="newPictures.length" class="grid grid--3">
        <PictureCard v-for="pic in newPictures" :key="pic.id" :picture="pic" />
      </div>
      <a-empty v-else description="还没有图片，去上传第一张吧" />
    </section>

    <!-- ④ 专题合集（横向滑动行） -->
    <section v-if="topics.length" class="block">
      <SectionHeading en="Features" cn="专题合集" desc="按分类聚合的图片合集。" />
      <CardScroller :total="topics.length">
        <article
          v-for="topic in topics"
          :key="topic.name"
          class="topic"
          @click="filterByCategory(topic.name)"
        >
          <div class="topic-media">
            <img v-if="topic.cover" :src="topic.cover" :alt="topic.name" loading="lazy" />
            <div v-else class="topic-media-empty" />
          </div>
          <p class="topic-name">{{ topic.name }}</p>
          <p class="topic-count">共 {{ topic.count }} 张</p>
        </article>
      </CardScroller>
    </section>

    <!-- ⑤ 分类浏览：左说明 + 右搜索 / 分类 / 标签排行 -->
    <section class="block">
      <div class="browse-grid">
        <div class="browse-intro">
          <SectionHeading en="Category" cn="分类浏览" />
          <p class="browse-desc">
            输入关键词，或按分类与标签筛选。选中的条件会应用到下方「图库检索」的结果。
          </p>
          <div class="browse-search">
            <a-input-search
              v-model:value="searchParams.searchText"
              placeholder="搜索图片名称或简介"
              enter-button="搜索"
              size="large"
              @search="applyFilter"
            />
          </div>
        </div>

        <div class="browse-panels">
          <div class="panel">
            <p class="panel-title">分类</p>
            <div class="chips">
              <button
                v-for="cate in categoryList"
                :key="cate"
                class="chip"
                :class="{ active: searchParams.category === cate }"
                type="button"
                @click="filterByCategory(cate)"
              >
                {{ cate }}
              </button>
            </div>
          </div>

          <div class="panel">
            <p class="panel-title">标签排行</p>
            <ol class="tag-rank">
              <li v-for="(tag, index) in rankTags" :key="tag">
                <span class="tag-rank-no">{{ index + 1 }}</span>
                <button class="tag-rank-btn" type="button" @click="filterByTag(tag)">
                  #{{ tag }}
                </button>
              </li>
            </ol>
          </div>
        </div>
      </div>
    </section>

    <!-- ⑥ 编辑精选：1 大 + 3 小 -->
    <section v-if="pickLarge" class="block">
      <SectionHeading en="Pick Up" cn="编辑精选" desc="按体量挑出的大图，细节最完整。" />
      <div class="pickup">
        <PictureCard :picture="pickLarge" variant="large" badge="精选" />
        <div class="pickup-side">
          <PictureCard v-for="pic in pickSmall" :key="pic.id" :picture="pic" variant="row" />
        </div>
      </div>
    </section>

    <!-- ⑦ 榜单：Tab + 编号横向行 -->
    <section class="block">
      <SectionHeading en="Ranking" cn="榜单" :desc="rankDesc" />
      <div class="rank-tabs">
        <button
          class="rank-tab"
          :class="{ active: rankTab === 'latest' }"
          type="button"
          @click="rankTab = 'latest'"
        >
          最新入库
        </button>
        <button
          class="rank-tab"
          :class="{ active: rankTab === 'size' }"
          type="button"
          @click="rankTab = 'size'"
        >
          体量最大
        </button>
      </div>

      <CardScroller v-if="rankPictures.length" :total="rankPictures.length">
        <PictureCard
          v-for="(pic, index) in rankPictures"
          :key="pic.id"
          :picture="pic"
          :rank="index + 1"
        />
      </CardScroller>
      <a-empty v-else description="暂无数据" />
    </section>

    <!-- ⑧ 上传入口 CTA -->
    <section class="block">
      <div class="cta-inner">
        <div>
          <p class="cta-en">Upload</p>
          <h3 class="cta-title">把你的图片放进来</h3>
          <p class="cta-desc">
            上传到私有空间做个人相册，或开通团队空间与成员实时协同编辑。
          </p>
        </div>
        <div class="cta-actions">
          <router-link to="/add_picture" class="cta-btn">立即上传</router-link>
          <router-link to="/add_space?type=1" class="cta-btn cta-btn--ghost">创建团队</router-link>
        </div>
      </div>
    </section>

    <!-- ⑨ 图库检索：搜索 / 分类 / 标签 + 分页网格 -->
    <section id="gallery" class="block">
      <SectionHeading en="Library" cn="图库检索" :desc="`共 ${total} 张图片`">
        <template #side>
          <button v-if="hasFilter" class="clear-btn" type="button" @click="clearFilter">
            清空筛选
          </button>
        </template>
      </SectionHeading>

      <div class="filter-line">
        <span v-if="searchParams.category" class="filter-pill">
          分类：{{ searchParams.category }}
        </span>
        <span v-for="tag in searchParams.tags" :key="tag" class="filter-pill">#{{ tag }}</span>
        <span v-if="searchParams.searchText" class="filter-pill">
          关键词：{{ searchParams.searchText }}
        </span>
        <span v-if="!hasFilter" class="filter-hint">当前为全部公开图片</span>
      </div>

      <PictureList :dataList="gridList" :loading="gridLoading" :showOp="false" />

      <a-pagination
        class="pagination"
        v-model:current="searchParams.current"
        v-model:pageSize="searchParams.pageSize"
        :total="total"
        :show-size-changer="false"
        @change="onPageChange"
      />
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, reactive, ref } from 'vue'
import {
  listPictureTagCategoryUsingGet,
  listPictureVoByPageUsingPost,
} from '@/api/pictureController.ts'
import { message } from 'ant-design-vue'
import PictureList from '@/components/PictureList.vue'
import HeroSlider from '@/components/home/HeroSlider.vue'
import SectionHeading from '@/components/home/SectionHeading.vue'
import PictureCard from '@/components/home/PictureCard.vue'
import CardScroller from '@/components/home/CardScroller.vue'

// ---------- 主数据（一次多取，供多个区块复用）----------
const mainList = ref<API.PictureVO[]>([])
const loading = ref(true)

// ---------- 检索区 ----------
const gridList = ref<API.PictureVO[]>([])
const gridLoading = ref(true)
const total = ref(0)
const searchParams = reactive<API.PictureQueryRequest>({
  current: 1,
  pageSize: 12,
  sortField: 'createTime',
  sortOrder: 'descend',
  tags: [],
})

// ---------- 分类 / 标签 ----------
const categoryList = ref<string[]>([])
const tagList = ref<string[]>([])

// ---------- 专题（分类聚合）----------
const topics = ref<{ name: string; cover?: string; count: number }[]>([])

// ---------- 榜单 ----------
const rankTab = ref<'latest' | 'size'>('latest')
const sizeRankList = ref<API.PictureVO[]>([])

const rankDesc = computed(() =>
  rankTab.value === 'latest'
    ? '按入库时间排序（加入浏览量字段后可切换为「本周热门」）。'
    : '按图片体积排序，便于找高清原图。',
)

// ---------- 区块切片 ----------
const heroPictures = computed(() => mainList.value.slice(0, 6))
const todayPictures = computed(() => mainList.value.slice(6, 10))
const newPictures = computed(() => mainList.value.slice(10, 16))
const pickLarge = computed(() => mainList.value[16])
const pickSmall = computed(() => mainList.value.slice(17, 20))

const rankPictures = computed(() =>
  rankTab.value === 'latest' ? mainList.value.slice(0, 10) : sizeRankList.value,
)

const hotTags = computed(() => tagList.value.slice(0, 8))
const rankTags = computed(() => tagList.value.slice(0, 10))

const hasFilter = computed(
  () =>
    !!searchParams.category || !!searchParams.searchText || (searchParams.tags?.length ?? 0) > 0,
)

// ---------- 数据加载 ----------
/** 主列表：多取一些，供 hero / 今日 / 最新 / 精选复用（后端限制 pageSize ≤ 20） */
const fetchMainList = async () => {
  loading.value = true
  try {
    const res = await listPictureVoByPageUsingPost({
      current: 1,
      pageSize: 20,
      sortField: 'createTime',
      sortOrder: 'descend',
    })
    if (res.data.code === 0 && res.data.data) {
      mainList.value = res.data.data.records ?? []
    }
  } catch (e) {
    // 装饰性区块失败不影响检索区
  } finally {
    loading.value = false
  }
}

/** 体量榜 */
const fetchSizeRank = async () => {
  try {
    const res = await listPictureVoByPageUsingPost({
      current: 1,
      pageSize: 10,
      sortField: 'picSize',
      sortOrder: 'descend',
    })
    if (res.data.code === 0 && res.data.data) {
      sizeRankList.value = res.data.data.records ?? []
    }
  } catch (e) {
    sizeRankList.value = []
  }
}

/** 分类与标签选项 */
const fetchTagCategory = async () => {
  const res = await listPictureTagCategoryUsingGet()
  if (res.data.code === 0 && res.data.data) {
    tagList.value = res.data.data.tagList ?? []
    categoryList.value = res.data.data.categoryList ?? []
  }
}

/** 专题：按分类各取 1 条拿封面 + 总数 */
const fetchTopics = async () => {
  const names = categoryList.value.slice(0, 6)
  const results = await Promise.all(
    names.map(async (name) => {
      try {
        const res = await listPictureVoByPageUsingPost({
          current: 1,
          pageSize: 1,
          category: name,
          sortField: 'createTime',
          sortOrder: 'descend',
        })
        const rec = res.data.data?.records?.[0]
        return {
          name,
          cover: rec?.thumbnailUrl ?? rec?.url,
          count: Number(res.data.data?.total ?? 0),
        }
      } catch (e) {
        return { name, cover: undefined, count: 0 }
      }
    }),
  )
  topics.value = results.filter((t) => t.count > 0)
}

/** 图库检索区 */
const fetchGrid = async () => {
  gridLoading.value = true
  try {
    const res = await listPictureVoByPageUsingPost({ ...searchParams })
    if (res.data.code === 0 && res.data.data) {
      gridList.value = res.data.data.records ?? []
      total.value = Number(res.data.data.total ?? 0)
    } else {
      message.error('获取数据失败，' + res.data.message)
    }
  } catch (e) {
    message.error('获取数据失败，请检查后端服务是否已启动')
  } finally {
    gridLoading.value = false
  }
}

// ---------- 交互 ----------
const scrollToGallery = () => {
  document.getElementById('gallery')?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

/** 应用筛选并滚动到检索区 */
const applyFilter = () => {
  searchParams.current = 1
  fetchGrid()
  scrollToGallery()
}

const filterByCategory = (category: string) => {
  searchParams.category = searchParams.category === category ? undefined : category
  applyFilter()
}

const filterByTag = (tag: string) => {
  const tags = searchParams.tags ?? []
  searchParams.tags = tags.includes(tag) ? tags.filter((t) => t !== tag) : [...tags, tag]
  applyFilter()
}

const clearFilter = () => {
  searchParams.category = undefined
  searchParams.searchText = undefined
  searchParams.tags = []
  applyFilter()
}

const onPageChange = (page: number, pageSize: number) => {
  searchParams.current = page
  searchParams.pageSize = pageSize
  fetchGrid()
}

onMounted(async () => {
  // 检索区优先，保证首屏可用
  fetchGrid()
  fetchMainList()
  fetchSizeRank()
  await fetchTagCategory()
  fetchTopics()
})
</script>

<style scoped>
#homePage {
  padding-bottom: 24px;
}

.block {
  margin-top: 40px;
}

@media (min-width: 768px) {
  .block {
    margin-top: 60px;
  }
}

/* 网格 */
.grid {
  display: grid;
  gap: 18px;
}

.grid--3 {
  grid-template-columns: repeat(3, 1fr);
}

.grid--4 {
  grid-template-columns: repeat(4, 1fr);
}

.skel {
  height: 230px;
  border-radius: var(--radius);
  background: linear-gradient(100deg, #ece3d1 40%, #f6f0e2 50%, #ece3d1 60%);
  background-size: 200% 100%;
  animation: shimmer 1.4s infinite linear;
}

/* 关键词 chips */
.kw-row {
  display: flex;
  align-items: center;
  gap: 8px;
  flex-wrap: wrap;
  justify-content: flex-end;
}

.kw-label {
  font-size: 10.5px;
  letter-spacing: 0.12em;
  color: var(--muted);
  text-transform: uppercase;
}

.kw {
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--ink-soft);
  font-size: 11.5px;
  padding: 3px 10px;
  border-radius: var(--radius);
  cursor: pointer;
  font-family: inherit;
  transition: all var(--dur) var(--ease);
}

.kw:hover {
  border-color: var(--brand);
  color: var(--brand);
}

/* 专题卡 */
.topic {
  cursor: pointer;
}

.topic-media {
  aspect-ratio: 4 / 3;
  overflow: hidden;
  border-radius: var(--radius);
  background: var(--bg-deep);
  border: 1px solid var(--line-soft);
}

.topic-media img {
  display: block;
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--dur) var(--ease);
}

.topic:hover .topic-media img {
  transform: scale(1.05);
}

.topic-media-empty {
  width: 100%;
  height: 100%;
  background-image: linear-gradient(45deg, var(--bg-deep) 25%, transparent 25%),
    linear-gradient(-45deg, var(--bg-deep) 25%, transparent 25%);
  background-size: 16px 16px;
}

.topic-name {
  font-family: var(--font-display);
  font-size: 15px;
  font-weight: 600;
  letter-spacing: 0.08em;
  color: var(--ink);
  margin-top: 10px;
}

.topic-count {
  font-size: 11px;
  letter-spacing: 0.08em;
  color: var(--muted);
  margin-top: 2px;
}

/* 分类浏览 */
.browse-grid {
  display: grid;
  grid-template-columns: 1fr 1.1fr;
  gap: 40px;
  align-items: start;
}

.browse-desc {
  font-size: 13px;
  line-height: 1.7;
  color: var(--ink-soft);
  max-width: 40ch;
}

.browse-search {
  margin-top: 18px;
  max-width: 420px;
}

.browse-panels {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
}

.panel-title {
  font-size: 10.5px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--muted);
  padding-bottom: 9px;
  border-bottom: 1px solid var(--line);
  margin-bottom: 13px;
}

.chips {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.chip {
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--ink-soft);
  font-size: 12.5px;
  padding: 5px 14px;
  border-radius: var(--radius);
  cursor: pointer;
  font-family: inherit;
  transition: all var(--dur) var(--ease);
}

.chip:hover {
  border-color: var(--brand);
  color: var(--brand);
}

.chip.active {
  background: var(--brand);
  border-color: var(--brand);
  color: #fff;
}

.tag-rank {
  list-style: none;
  padding: 0;
  margin: 0;
  display: flex;
  flex-direction: column;
  gap: 7px;
}

.tag-rank li {
  display: flex;
  align-items: center;
  gap: 10px;
}

.tag-rank-no {
  font-family: var(--font-display);
  font-size: 13px;
  color: var(--brand);
  width: 16px;
  text-align: right;
}

.tag-rank-btn {
  border: none;
  background: none;
  padding: 0;
  font-family: inherit;
  font-size: 13px;
  color: var(--ink-soft);
  cursor: pointer;
  transition: color var(--dur) var(--ease);
}

.tag-rank-btn:hover {
  color: var(--brand);
}

/* 编辑精选 */
.pickup {
  display: grid;
  grid-template-columns: 1.25fr 1fr;
  gap: 18px;
}

.pickup-side {
  display: grid;
  grid-template-rows: repeat(3, 1fr);
  gap: 18px;
}

/* 榜单 tabs */
.rank-tabs {
  display: flex;
  gap: 8px;
  margin-bottom: 18px;
}

.rank-tab {
  border: 1px solid var(--line);
  background: var(--surface);
  color: var(--ink-soft);
  font-family: inherit;
  font-size: 12.5px;
  letter-spacing: 0.08em;
  padding: 6px 18px;
  border-radius: var(--radius);
  cursor: pointer;
  transition: all var(--dur) var(--ease);
}

.rank-tab:hover {
  border-color: var(--brand);
  color: var(--brand);
}

.rank-tab.active {
  background: var(--ink);
  border-color: var(--ink);
  color: #f7f2e8;
}

/* CTA */
.cta-inner {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 32px;
  padding: 34px 38px;
  background: #221c15;
  /* 签名式对角圆角 */
  border-radius: var(--radius-diagonal);
  color: #f7f2e8;
}

.cta-en {
  font-family: var(--font-display);
  font-size: 13px;
  letter-spacing: 0.12em;
  text-transform: uppercase;
  color: var(--gold);
  margin-bottom: 10px;
}

.cta-title {
  font-family: var(--font-display);
  font-size: 26px;
  font-weight: 400;
  letter-spacing: 0.06em;
  margin-bottom: 8px;
}

.cta-desc {
  font-size: 13px;
  line-height: 1.7;
  color: rgba(247, 242, 232, 0.72);
  max-width: 46ch;
}

.cta-actions {
  display: flex;
  gap: 12px;
  flex-shrink: 0;
}

.cta-btn {
  display: inline-flex;
  align-items: center;
  height: 44px;
  padding: 0 28px;
  background: var(--brand);
  color: #fff;
  border-radius: var(--radius);
  text-decoration: none;
  font-size: 14px;
  letter-spacing: 0.08em;
  transition: filter var(--dur) var(--ease), transform var(--dur) var(--ease);
}

.cta-btn:hover {
  color: #fff;
  filter: brightness(1.06);
  transform: translateY(-1px);
}

.cta-btn--ghost {
  background: transparent;
  border: 1px solid rgba(247, 242, 232, 0.35);
}

.cta-btn--ghost:hover {
  border-color: var(--gold);
  color: var(--gold);
}

/* 检索区 */
.clear-btn {
  border: none;
  background: none;
  font-family: inherit;
  font-size: 12px;
  letter-spacing: 0.08em;
  color: var(--brand);
  cursor: pointer;
}

.filter-line {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 18px;
  min-height: 24px;
}

.filter-pill {
  font-size: 11.5px;
  padding: 3px 11px;
  border-radius: var(--radius);
  background: var(--brand-soft);
  color: var(--brand-deep);
}

.filter-hint {
  font-size: 12px;
  color: var(--muted);
}

.pagination {
  text-align: right;
  margin-top: 28px;
}

/* 响应式 */
@media (max-width: 1024px) {
  .grid--4 {
    grid-template-columns: repeat(2, 1fr);
  }

  .browse-grid {
    grid-template-columns: 1fr;
    gap: 26px;
  }

  .pickup {
    grid-template-columns: 1fr;
  }

  .pickup-side {
    grid-template-rows: none;
    grid-template-columns: repeat(3, 1fr);
  }

  .cta-inner {
    flex-direction: column;
    align-items: flex-start;
  }
}

@media (max-width: 768px) {
  .block {
    margin-top: 40px;
  }

  .grid--3,
  .grid--4 {
    grid-template-columns: 1fr;
  }

  .browse-panels {
    grid-template-columns: 1fr;
    gap: 22px;
  }

  .pickup-side {
    grid-template-columns: 1fr;
  }

  .kw-row {
    justify-content: flex-start;
  }
}
</style>
