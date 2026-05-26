<template>
  <div class="home-page">
    <!-- left sidebar -->
    <div class="left-sidebar">
      <CategoryTree
        :tree-data="categories"
        @refresh="loadCategories"
        @select="onCategorySelect" />
    </div>
    <!-- right content -->
    <div class="right-content">
      <!-- empty state -->
      <div v-if="!selectedCategory" class="empty-state">
        <el-empty description="请从左侧选择一个分类" />
      </div>
      <!-- table list -->
      <template v-else>
        <div class="content-header">
          <h3>{{ selectedCategory.name }} - 知识表</h3>
          <el-button type="primary" @click="openAddTable">新增知识表</el-button>
        </div>
        <el-row :gutter="16">
          <el-col v-for="t in tables" :key="t.id" :span="8">
            <el-card class="table-card" shadow="hover" @click="selectTable(t)">
              <div class="card-title">{{ t.name }}</div>
              <div class="card-info">
                <span>{{ t.dataCount || 0 }} 条数据</span>
                <span>{{ t.updatedAt || t.createdAt }}</span>
              </div>
              <div class="card-actions" @click.stop>
                <el-button link size="small" @click="openEditTable(t)">编辑</el-button>
                <el-button link size="small" @click="selectTable(t)">查看数据</el-button>
                <el-button link size="small" type="danger" @click="handleDeleteTable(t)">删除</el-button>
              </div>
            </el-card>
          </el-col>
        </el-row>
        <!-- data table view -->
        <div v-if="activeTable" class="data-section">
          <el-divider />
          <div class="data-header">
            <h4>{{ activeTable.name }} — 数据列表</h4>
            <el-button link @click="activeTable = null">← 返回</el-button>
          </div>
          <DynamicTable
            :table="activeTable"
            :fields="activeFields"
            :data-list="dataList"
            :loading="dataLoading"
            :total="dataTotal"
            :page-size="20"
            @add="openAddData"
            @edit="openEditData"
            @view="openViewData"
            @page-change="loadData"
            @refresh="loadData"
          />
        </div>
      </template>
    </div>

    <!-- dialogs -->
    <TableDesigner
      v-model:visible="tableDialogVisible"
      :is-edit="tableIsEdit"
      :table-id="tableIsEdit ? editingTable?.id : ''"
      :category-id="selectedCategory?.id"
      :category-name="selectedCategory?.name"
      @saved="onTableSaved" />

    <DataForm
      v-model:visible="dataDialogVisible"
      :is-edit="dataIsEdit"
      :table-id="activeTable?.id"
      :data-id="dataIsEdit ? editingData?.id : ''"
      :fields="activeFields"
      :initial-data="editingData"
      @saved="loadData" />

    <el-dialog v-model="viewDialogVisible" title="数据详情" width="600px">
      <el-descriptions :column="1" border v-if="viewingData">
        <template v-for="f in activeFields" :key="f.code">
          <el-descriptions-item v-if="f.fieldType !== 'SUB_TABLE'" :label="f.name">
            <template v-if="f.fieldType === 'IMAGE'">
              <el-image v-if="viewingData.jsonbData[f.code]"
                        :src="viewingData.jsonbData[f.code]" style="width:100px;height:100px" fit="cover" />
              <span v-else>-</span>
            </template>
            <span v-else>{{ viewingData.jsonbData[f.code] ?? '-' }}</span>
          </el-descriptions-item>
        </template>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import CategoryTree from '../components/CategoryTree.vue'
import TableDesigner from '../components/TableDesigner.vue'
import DataForm from '../components/DataForm.vue'
import DynamicTable from '../components/DynamicTable.vue'
import { getCategoryTree } from '../api/category'
import { getTablesByCategory, deleteTable } from '../api/table'
import { getDataPage } from '../api/data'

const categories = ref([])
const selectedCategory = ref(null)
const tables = ref([])
const activeTable = ref(null)
const activeFields = ref([])
const dataList = ref([])
const dataTotal = ref(0)
const dataLoading = ref(false)

const tableDialogVisible = ref(false)
const tableIsEdit = ref(false)
const editingTable = ref(null)

const dataDialogVisible = ref(false)
const dataIsEdit = ref(false)
const editingData = ref(null)

const viewDialogVisible = ref(false)
const viewingData = ref(null)

loadCategories()

async function loadCategories() {
  try {
    categories.value = await getCategoryTree()
  } catch (e) {
    ElMessage.error('加载分类失败')
  }
}

async function onCategorySelect(node) {
  selectedCategory.value = node
  activeTable.value = null
  dataList.value = []
  try {
    tables.value = await getTablesByCategory(node.id)
  } catch (e) {
    tables.value = []
  }
}

async function selectTable(table) {
  activeTable.value = table
  try {
    const detail = await getTablesByCategory(selectedCategory.value.id)
    const found = detail.find(t => t.id === table.id)
    if (found) {
      activeTable.value = found
      activeFields.value = found.fields || []
    }
    await loadData()
  } catch (e) {
    activeFields.value = []
  }
}

async function loadData(page = 0) {
  if (!activeTable.value) return
  dataLoading.value = true
  try {
    const res = await getDataPage(activeTable.value.id, page)
    dataList.value = res.content || []
    dataTotal.value = res.totalElements || 0
  } catch (e) {
    ElMessage.error('加载数据失败')
  } finally {
    dataLoading.value = false
  }
}

function openAddTable() {
  tableIsEdit.value = false; editingTable.value = null; tableDialogVisible.value = true
}

function openEditTable(table) {
  tableIsEdit.value = true; editingTable.value = table; tableDialogVisible.value = true
}

async function handleDeleteTable(table) {
  try {
    await ElMessageBox.confirm(`确定删除知识表"${table.name}"及其所有数据？`, '确认删除', { type: 'warning' })
    await deleteTable(table.id)
    ElMessage.success('删除成功')
    await onCategorySelect(selectedCategory.value)
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

async function onTableSaved() {
  if (selectedCategory.value) {
    tables.value = await getTablesByCategory(selectedCategory.value.id)
  }
}

function openAddData() { dataIsEdit.value = false; editingData.value = null; dataDialogVisible.value = true }
function openEditData(row) { dataIsEdit.value = true; editingData.value = row; dataDialogVisible.value = true }
function openViewData(row) { viewingData.value = row; viewDialogVisible.value = true }
</script>

<style scoped>
.home-page { display: flex; height: 100%; }
.left-sidebar { width: 260px; min-width: 260px; border-right: 1px solid #ebeef5; overflow-y: auto; }
.right-content { flex: 1; padding: 16px; overflow-y: auto; }
.empty-state { display: flex; align-items: center; justify-content: center; height: 100%; }
.content-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.table-card { cursor: pointer; margin-bottom: 12px; }
.table-card:hover { border-color: #409eff; }
.card-title { font-size: 15px; font-weight: 600; margin-bottom: 8px; }
.card-info { font-size: 12px; color: #909399; display: flex; justify-content: space-between; margin-bottom: 8px; }
.card-actions { text-align: right; border-top: 1px solid #ebeef5; padding-top: 8px; }
.data-section { margin-top: 8px; }
.data-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
</style>
