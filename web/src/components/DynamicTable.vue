<template>
  <div class="dynamic-table">
    <div class="table-toolbar">
      <div class="toolbar-left">
        <el-button type="primary" @click="$emit('add')">新增数据</el-button>
        <el-button type="danger" :disabled="!selectedIds.length" @click="handleBatchDelete">批量删除</el-button>
        <el-button v-if="table.enableMap" type="success" @click="$router.push(`/map/${table.id}`)">地图视图</el-button>
      </div>
    </div>
    <el-table :data="dataList" border stripe v-loading="loading"
              @selection-change="onSelectionChange" style="width:100%">
      <el-table-column type="selection" width="50" />
      <template v-for="field in displayFields" :key="field.code">
        <el-table-column :prop="field.code" :label="field.name" :min-width="120">
          <template #default="{ row }">
            <template v-if="field.fieldType === 'IMAGE'">
              <el-image v-if="row.jsonbData[field.code]" :src="row.jsonbData[field.code]"
                        style="width:50px;height:50px" fit="cover" preview-teleported />
              <span v-else>-</span>
            </template>
            <template v-else-if="field.fieldType === 'SUB_TABLE'">
              <el-tag size="small">{{ (row.jsonbData[field.code] || []).length }} 行</el-tag>
            </template>
            <template v-else>
              {{ row.jsonbData[field.code] ?? '-' }}
            </template>
          </template>
        </el-table-column>
      </template>
      <el-table-column label="更新时间" width="160">
        <template #default="{ row }">{{ row.updatedAt }}</template>
      </el-table-column>
      <el-table-column label="操作" width="140" fixed="right">
        <template #default="{ row }">
          <el-button link size="small" @click="$emit('view', row)">查看</el-button>
          <el-button link size="small" @click="$emit('edit', row)">编辑</el-button>
          <el-button link size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      v-model:current-page="currentPage" :page-size="pageSize"
      :total="total" layout="total, prev, pager, next"
      @current-change="onPageChange" style="margin-top:16px;justify-content:flex-end" />
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { deleteData, batchDeleteData } from '../api/data'

const props = defineProps({
  table: Object, fields: Array, dataList: Array,
  loading: Boolean, total: Number, pageSize: Number
})
const emit = defineEmits(['page-change', 'add', 'edit', 'view', 'refresh'])
const currentPage = ref(1)
const selectedIds = ref([])

const displayFields = computed(() => {
  return (props.fields || []).filter(f => f.fieldType !== 'SUB_TABLE')
})

function onSelectionChange(selection) {
  selectedIds.value = selection.map(s => s.id)
}

function onPageChange(page) {
  currentPage.value = page
  emit('page-change', page - 1)
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm('确定删除该数据？', '确认删除', { type: 'warning' })
    await deleteData(row.id)
    ElMessage.success('删除成功')
    emit('refresh')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

async function handleBatchDelete() {
  try {
    await ElMessageBox.confirm(`确定删除选中的 ${selectedIds.value.length} 条数据？`, '确认删除', { type: 'warning' })
    await batchDeleteData(props.table.id, selectedIds.value)
    ElMessage.success('删除成功')
    selectedIds.value = []
    emit('refresh')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}
</script>

<style scoped>
.dynamic-table { display: flex; flex-direction: column; flex: 1; }
.table-toolbar { display: flex; justify-content: space-between; margin-bottom: 12px; }
</style>
