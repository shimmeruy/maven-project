<template>
  <div class="search-page">
    <div class="search-header">
      <el-input v-model="keyword" placeholder="输入关键字搜索所有知识表..." size="large"
                @keyup.enter="doSearch" clearable style="width:500px">
        <template #append>
          <el-button @click="doSearch" :loading="loading" icon="Search">搜索</el-button>
        </template>
      </el-input>
    </div>
    <div class="search-body" v-if="keyword">
      <div class="search-summary" v-if="total > 0">
        共找到 <strong>{{ total }}</strong> 条匹配结果
      </div>
      <div v-if="results.length === 0 && !loading && searched" class="empty-result">
        <el-empty description="未找到匹配结果" />
      </div>
      <div v-for="item in results" :key="item.dataId" class="search-item">
        <div class="item-header">
          <el-tag size="small" type="info">{{ item.tableName }}</el-tag>
          <span class="item-time">{{ item.updatedAt }}</span>
        </div>
        <div class="item-data">
          <template v-for="(value, key) in item.jsonbData" :key="key">
            <div class="data-row" v-if="typeof value === 'string'">
              <span class="data-key">{{ key }}:</span>
              <span class="data-value" v-html="highlight(value)"></span>
            </div>
            <div class="data-row" v-else-if="typeof value === 'number'">
              <span class="data-key">{{ key }}:</span>
              <span class="data-value">{{ value }}</span>
            </div>
          </template>
        </div>
      </div>
      <el-pagination
        v-if="total > pageSize"
        v-model:current-page="currentPage" :page-size="pageSize"
        :total="total" layout="total, prev, pager, next"
        @current-change="onPageChange"
        style="margin-top:20px;justify-content:center" />
    </div>
    <div class="search-empty" v-else>
      <el-empty description="请输入关键字开始搜索" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { searchData } from '../api/data'

const keyword = ref('')
const results = ref([])
const total = ref(0)
const loading = ref(false)
const searched = ref(false)
const currentPage = ref(1)
const pageSize = 20

async function doSearch(page = 0) {
  if (!keyword.value.trim()) return
  loading.value = true
  searched.value = true
  currentPage.value = page + 1
  try {
    const res = await searchData(keyword.value.trim(), page, pageSize)
    results.value = res.content || []
    total.value = res.totalElements || 0
  } catch (e) {
    results.value = []
  } finally {
    loading.value = false
  }
}

function onPageChange(page) {
  doSearch(page - 1)
}

function highlight(text) {
  if (!keyword.value || !text || typeof text !== 'string') return text
  const kw = keyword.value.trim()
  const re = new RegExp(`(${kw.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')})`, 'gi')
  return text.replace(re, '<mark>$1</mark>')
}
</script>

<style scoped>
.search-page { padding: 24px; max-width: 900px; margin: 0 auto; height: 100%; overflow-y: auto; }
.search-header { text-align: center; margin-bottom: 24px; }
.search-body { margin-top: 16px; }
.search-summary { margin-bottom: 16px; color: #909399; }
.empty-result, .search-empty { margin-top: 60px; }
.search-item {
  background: #fff; border: 1px solid #ebeef5; border-radius: 8px;
  padding: 16px; margin-bottom: 12px;
}
.item-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.item-time { font-size: 12px; color: #c0c4cc; }
.data-row { margin-bottom: 4px; font-size: 14px; }
.data-key { color: #909399; margin-right: 8px; }
.data-value :deep(mark) { background: #ffff00; padding: 1px 2px; }
</style>
