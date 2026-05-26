<template>
  <el-dialog :model-value="visible" :title="isEdit ? '编辑知识表' : '新增知识表'"
             width="800px" @update:model-value="$emit('update:visible', $event)" @open="onOpen">
    <el-tabs v-model="activeTab">
      <el-tab-pane label="基本信息" name="basic">
        <el-form :model="form" label-width="100px">
          <el-form-item label="表名称" required>
            <el-input v-model="form.name" placeholder="请输入知识表名称" />
          </el-form-item>
          <el-form-item label="所属分类" required>
            <el-input :model-value="categoryName" disabled />
          </el-form-item>
          <el-form-item label="表描述">
            <el-input v-model="form.description" type="textarea" :rows="3" />
          </el-form-item>
          <el-form-item label="排序号">
            <el-input-number v-model="form.sortOrder" :min="0" />
          </el-form-item>
        </el-form>
      </el-tab-pane>
      <el-tab-pane v-if="isEdit" label="字段管理" name="fields">
        <FieldEditor :table-id="tableId" :fields="fields" @refresh="loadFields" />
      </el-tab-pane>
      <el-tab-pane v-if="isEdit" label="地图联动" name="map">
        <el-form :model="mapConfig" label-width="120px">
          <el-form-item label="启用地图">
            <el-switch v-model="mapConfig.enableMap" />
          </el-form-item>
          <el-form-item label="经度字段">
            <el-select v-model="mapConfig.mapLongitudeField" placeholder="选择经度字段">
              <el-option v-for="f in numberFields" :key="f.code" :label="f.name" :value="f.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="纬度字段">
            <el-select v-model="mapConfig.mapLatitudeField" placeholder="选择纬度字段">
              <el-option v-for="f in numberFields" :key="f.code" :label="f.name" :value="f.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="标注名称字段">
            <el-select v-model="mapConfig.mapLabelField" placeholder="选择标注名称字段" clearable>
              <el-option v-for="f in textFields" :key="f.code" :label="f.name" :value="f.code" />
            </el-select>
          </el-form-item>
          <el-form-item label="标注描述字段">
            <el-select v-model="mapConfig.mapDescField" placeholder="选择标注描述字段" clearable>
              <el-option v-for="f in textFields" :key="f.code" :label="f.name" :value="f.code" />
            </el-select>
          </el-form-item>
        </el-form>
      </el-tab-pane>
    </el-tabs>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="submitForm">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createTable, updateTable, updateMapConfig, getTableDetail } from '../api/table'
import FieldEditor from './FieldEditor.vue'

const props = defineProps({
  visible: Boolean,
  isEdit: Boolean,
  tableId: String,
  categoryId: String,
  categoryName: String
})
const emit = defineEmits(['update:visible', 'saved'])

const activeTab = ref('basic')
const form = ref({ name: '', description: '', sortOrder: 0 })
const mapConfig = ref({
  enableMap: false, mapLongitudeField: '', mapLatitudeField: '',
  mapLabelField: '', mapDescField: ''
})
const fields = ref([])

const numberFields = computed(() => fields.value.filter(f => f.fieldType === 'NUMBER'))
const textFields = computed(() => fields.value.filter(f => f.fieldType === 'TEXT'))

async function onOpen() {
  activeTab.value = 'basic'
  if (props.isEdit) {
    await loadFields()
    const table = await getTableDetail(props.tableId)
    form.value = {
      name: table.name,
      description: table.description || '',
      sortOrder: table.sortOrder || 0
    }
    mapConfig.value = {
      enableMap: table.enableMap || false,
      mapLongitudeField: table.mapLongitudeField || '',
      mapLatitudeField: table.mapLatitudeField || '',
      mapLabelField: table.mapLabelField || '',
      mapDescField: table.mapDescField || ''
    }
  } else {
    form.value = { name: '', description: '', sortOrder: 0 }
    mapConfig.value = { enableMap: false, mapLongitudeField: '', mapLatitudeField: '', mapLabelField: '', mapDescField: '' }
  }
}

async function loadFields() {
  try {
    const table = await getTableDetail(props.tableId)
    fields.value = table.fields || []
  } catch (e) {
    // ignore
  }
}

async function submitForm() {
  if (!form.value.name) {
    ElMessage.warning('请输入表名称')
    return
  }
  try {
    if (props.isEdit) {
      await updateTable(props.tableId, form.value)
      if (mapConfig.value.enableMap !== undefined) {
        await updateMapConfig(props.tableId, mapConfig.value)
      }
      ElMessage.success('编辑成功')
    } else {
      await createTable({
        name: form.value.name,
        categoryId: props.categoryId,
        description: form.value.description,
        sortOrder: form.value.sortOrder
      })
      ElMessage.success('新增成功')
    }
    emit('update:visible', false)
    emit('saved')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}
</script>
