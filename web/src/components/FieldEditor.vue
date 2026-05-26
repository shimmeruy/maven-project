<template>
  <div class="field-editor">
    <div style="margin-bottom: 12px;">
      <el-button type="primary" size="small" @click="handleAdd">新增字段</el-button>
    </div>
    <el-table :data="fields" border stripe size="small">
      <el-table-column prop="name" label="字段名称" width="120" />
      <el-table-column prop="code" label="字段编码" width="120" />
      <el-table-column prop="fieldType" label="字段类型" width="90">
        <template #default="{ row }">
          <el-tag size="small">{{ row.fieldType }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="isRequired" label="必填" width="60">
        <template #default="{ row }">{{ row.isRequired ? '是' : '否' }}</template>
      </el-table-column>
      <el-table-column prop="sortOrder" label="排序" width="60" />
      <el-table-column label="操作" width="120">
        <template #default="{ row }">
          <el-button link size="small" @click="handleEdit(row)">编辑</el-button>
          <el-button link size="small" type="danger" @click="handleDelete(row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑字段' : '新增字段'" width="550px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="字段名称" required>
          <el-input v-model="form.name" placeholder="如：建筑名称" />
        </el-form-item>
        <el-form-item label="字段编码" required>
          <el-input v-model="form.code" placeholder="如：building_name" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="字段类型" required>
          <el-select v-model="form.fieldType" :disabled="isEdit" @change="onTypeChange">
            <el-option label="文本 (TEXT)" value="TEXT" />
            <el-option label="数值 (NUMBER)" value="NUMBER" />
            <el-option label="日期 (DATE)" value="DATE" />
            <el-option label="图片 (IMAGE)" value="IMAGE" />
            <el-option label="子表 (SUB_TABLE)" value="SUB_TABLE" />
          </el-select>
        </el-form-item>
        <el-form-item label="是否必填">
          <el-switch v-model="form.isRequired" />
        </el-form-item>
        <el-form-item label="排序号">
          <el-input-number v-model="form.sortOrder" :min="0" />
        </el-form-item>
        <el-form-item label="默认值">
          <el-input v-model="form.defaultValue" />
        </el-form-item>
        <template v-if="form.fieldType === 'SUB_TABLE'">
          <el-divider>子表字段定义</el-divider>
          <div v-for="(sf, idx) in subFields" :key="idx" style="display:flex;gap:8px;margin-bottom:8px;align-items:center">
            <el-input v-model="sf.name" placeholder="名称" size="small" style="width:100px" />
            <el-input v-model="sf.code" placeholder="编码" size="small" style="width:100px" />
            <el-select v-model="sf.type" size="small" style="width:100px">
              <el-option label="TEXT" value="TEXT" />
              <el-option label="NUMBER" value="NUMBER" />
              <el-option label="DATE" value="DATE" />
              <el-option label="IMAGE" value="IMAGE" />
            </el-select>
            <el-switch v-model="sf.required" size="small" active-text="必填" />
            <el-button link type="danger" size="small" @click="subFields.splice(idx,1)">删</el-button>
          </div>
          <el-button size="small" @click="subFields.push({name:'',code:'',type:'TEXT',required:false})">+子字段</el-button>
        </template>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitField">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { createField, updateField, deleteField } from '../api/field'

const props = defineProps({ tableId: String, fields: { type: Array, default: () => [] } })
const emit = defineEmits(['refresh'])
const dialogVisible = ref(false)
const isEdit = ref(false)
const editingField = ref(null)
const form = ref({ name: '', code: '', fieldType: 'TEXT', isRequired: false, sortOrder: 0, defaultValue: '' })
const subFields = ref([])

function onTypeChange() {
  if (form.value.fieldType === 'SUB_TABLE') {
    subFields.value = [{ name: '', code: '', type: 'TEXT', required: false }]
  } else {
    subFields.value = []
  }
}

function handleAdd() {
  isEdit.value = false; editingField.value = null
  form.value = { name: '', code: '', fieldType: 'TEXT', isRequired: false, sortOrder: (props.fields || []).length, defaultValue: '' }
  subFields.value = []
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true; editingField.value = row
  form.value = {
    name: row.name, code: row.code, fieldType: row.fieldType,
    isRequired: row.isRequired || false, sortOrder: row.sortOrder || 0,
    defaultValue: row.defaultValue || ''
  }
  if (row.fieldType === 'SUB_TABLE' && row.fieldConfig && row.fieldConfig.sub_fields) {
    subFields.value = row.fieldConfig.sub_fields.map(sf => ({ ...sf }))
  } else {
    subFields.value = []
  }
  dialogVisible.value = true
}

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定删除字段"${row.name}"及其数据？`, '确认删除', { type: 'warning' })
    await deleteField(row.id)
    ElMessage.success('删除成功')
    emit('refresh')
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

async function submitField() {
  if (!form.value.name || !form.value.code) {
    ElMessage.warning('请填写字段名称和编码')
    return
  }
  const payload = { ...form.value }
  delete payload.fieldConfig
  if (form.value.fieldType === 'SUB_TABLE') {
    payload.fieldConfig = JSON.stringify({ sub_fields: subFields.value.filter(sf => sf.name) })
  }
  try {
    if (isEdit.value) {
      await updateField(editingField.value.id, payload)
      ElMessage.success('编辑成功')
    } else {
      await createField(props.tableId, payload)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    emit('refresh')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}
</script>
