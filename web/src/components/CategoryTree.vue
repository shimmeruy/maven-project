<template>
  <div class="category-tree">
    <div class="tree-header">
      <span>知识分类</span>
      <el-button type="primary" size="small" @click="handleAdd(null)">新增</el-button>
    </div>
    <el-tree
      :data="treeData"
      :props="defaultProps"
      node-key="id"
      default-expand-all
      highlight-current
      @node-click="onNodeClick"
    >
      <template #default="{ data }">
        <div class="tree-node">
          <span class="node-label">{{ data.name }}</span>
          <span class="node-actions">
            <el-button link size="small" @click.stop="handleAdd(data)">+</el-button>
            <el-button link size="small" @click.stop="handleEdit(data)">✎</el-button>
            <el-button link size="small" @click.stop="handleDelete(data)">✕</el-button>
          </span>
        </div>
      </template>
    </el-tree>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新增分类'" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称" required>
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类">
          <el-input :model-value="parentName" disabled />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '../api/category'

const props = defineProps({
  treeData: { type: Array, default: () => [] }
})
const emit = defineEmits(['refresh', 'select'])

const defaultProps = { children: 'children', label: 'name' }
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingNode = ref(null)
const parentNode = ref(null)
const form = ref({ name: '', sortOrder: 0, description: '' })

const parentName = computed(() => parentNode.value ? parentNode.value.name : '根节点')

function onNodeClick(data) {
  emit('select', data)
}

function handleAdd(parent) {
  isEdit.value = false
  editingNode.value = null
  parentNode.value = parent
  form.value = { name: '', sortOrder: 0, description: '' }
  dialogVisible.value = true
}

function handleEdit(data) {
  isEdit.value = true
  editingNode.value = data
  form.value = {
    name: data.name,
    sortOrder: data.sortOrder || 0,
    description: data.description || ''
  }
  dialogVisible.value = true
}

async function handleDelete(data) {
  try {
    await ElMessageBox.confirm(`确定删除分类"${data.name}"？`, '确认删除', { type: 'warning' })
    await deleteCategory(data.id)
    ElMessage.success('删除成功')
    emit('refresh')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

async function submitForm() {
  if (!form.value.name) {
    ElMessage.warning('请输入分类名称')
    return
  }
  try {
    if (isEdit.value) {
      await updateCategory(editingNode.value.id, form.value)
      ElMessage.success('编辑成功')
    } else {
      await createCategory({
        name: form.value.name,
        parentId: parentNode.value ? parentNode.value.id : null,
        sortOrder: form.value.sortOrder,
        description: form.value.description
      })
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('refresh')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}
</script>

<style scoped>
.category-tree { height: 100%; display: flex; flex-direction: column; }
.tree-header {
  display: flex; justify-content: space-between; align-items: center;
  padding: 12px 16px; border-bottom: 1px solid #ebeef5; font-weight: 600;
}
.tree-node {
  flex: 1; display: flex; justify-content: space-between; align-items: center;
  padding-right: 8px;
}
.node-actions { display: none; }
.tree-node:hover .node-actions { display: inline-flex; }
</style>
