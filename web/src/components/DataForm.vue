<template>
  <el-dialog :model-value="visible" :title="isEdit ? '编辑数据' : '新增数据'"
             width="650px" @update:model-value="$emit('update:visible', $event)" @open="onOpen">
    <el-form :model="form" label-width="120px">
      <template v-for="field in fields" :key="field.code">
        <!-- TEXT -->
        <el-form-item v-if="field.fieldType === 'TEXT'" :label="field.name" :required="field.isRequired">
          <el-input v-model="form[field.code]" :placeholder="'请输入' + field.name" />
        </el-form-item>
        <!-- NUMBER -->
        <el-form-item v-if="field.fieldType === 'NUMBER'" :label="field.name" :required="field.isRequired">
          <el-input-number v-model="form[field.code]" :precision="getPrecision(field)" style="width:100%" />
        </el-form-item>
        <!-- DATE -->
        <el-form-item v-if="field.fieldType === 'DATE'" :label="field.name" :required="field.isRequired">
          <el-date-picker v-model="form[field.code]" type="date" placeholder="选择日期" style="width:100%"
                          value-format="YYYY-MM-DD" />
        </el-form-item>
        <!-- IMAGE -->
        <el-form-item v-if="field.fieldType === 'IMAGE'" :label="field.name" :required="field.isRequired">
          <div>
            <el-upload :auto-upload="false" :show-file-list="false" :on-change="(f) => onUpload(f, field.code)"
                       accept="image/*">
              <el-button size="small">选择图片</el-button>
            </el-upload>
            <div v-if="form[field.code]" style="margin-top:8px">
              <el-image :src="form[field.code]" style="width:120px;height:120px" fit="cover" />
              <el-button link type="danger" size="small" @click="form[field.code] = ''">移除</el-button>
            </div>
          </div>
        </el-form-item>
        <!-- SUB_TABLE -->
        <template v-if="field.fieldType === 'SUB_TABLE'">
          <el-divider>{{ field.name }}</el-divider>
          <div v-for="(row, idx) in (form[field.code] || [])" :key="idx" style="display:flex;gap:8px;margin-bottom:8px;align-items:center">
            <template v-for="sf in getSubFields(field)" :key="sf.code">
              <el-input v-if="sf.type === 'TEXT'" v-model="row[sf.code]" :placeholder="sf.name" size="small" style="width:100px" />
              <el-input-number v-if="sf.type === 'NUMBER'" v-model="row[sf.code]" :placeholder="sf.name" size="small" style="width:100px" />
              <el-date-picker v-if="sf.type === 'DATE'" v-model="row[sf.code]" type="date" size="small" style="width:140px" value-format="YYYY-MM-DD" />
            </template>
            <el-button link type="danger" size="small" @click="removeSubRow(field.code, idx)">删</el-button>
          </div>
          <el-button size="small" @click="addSubRow(field.code, field)">+ 添加行</el-button>
        </template>
      </template>
    </el-form>
    <template #footer>
      <el-button @click="$emit('update:visible', false)">取消</el-button>
      <el-button type="primary" @click="submitForm" :loading="submitting">确定</el-button>
    </template>
  </el-dialog>
</template>

<script setup>
import { ref } from 'vue'
import { ElMessage } from 'element-plus'
import { createData, updateData, uploadImage } from '../api/data'

const props = defineProps({
  visible: Boolean, isEdit: Boolean,
  tableId: String, dataId: String,
  fields: { type: Array, default: () => [] },
  initialData: Object
})
const emit = defineEmits(['update:visible', 'saved'])
const form = ref({})
const submitting = ref(false)

function onOpen() {
  form.value = {}
  for (const f of props.fields) {
    if (f.fieldType === 'SUB_TABLE') {
      form.value[f.code] = []
    } else if (f.fieldType === 'NUMBER') {
      form.value[f.code] = null
    } else {
      form.value[f.code] = f.defaultValue || ''
    }
  }
  if (props.isEdit && props.initialData && props.initialData.jsonbData) {
    Object.assign(form.value, props.initialData.jsonbData)
  }
}

function getPrecision(field) {
  if (field.fieldConfig && field.fieldConfig.precision != null) return field.fieldConfig.precision
  return 0
}

function getSubFields(field) {
  if (field.fieldConfig && field.fieldConfig.sub_fields) return field.fieldConfig.sub_fields
  return []
}

function addSubRow(code, field) {
  if (!form.value[code]) form.value[code] = []
  const row = {}
  for (const sf of getSubFields(field)) {
    row[sf.code] = sf.type === 'NUMBER' ? null : ''
  }
  form.value[code].push(row)
}

function removeSubRow(code, idx) {
  form.value[code].splice(idx, 1)
}

async function onUpload(fileWrapper, code) {
  try {
    const res = await uploadImage(fileWrapper.raw)
    form.value[code] = res.url
    ElMessage.success('上传成功')
  } catch (e) {
    ElMessage.error('上传失败: ' + (e.message || ''))
  }
}

async function submitForm() {
  for (const f of props.fields) {
    if (f.isRequired && !form.value[f.code] && form.value[f.code] !== 0) {
      ElMessage.warning(`请填写必填字段: ${f.name}`)
      return
    }
  }
  submitting.value = true
  try {
    const payload = {}
    for (const f of props.fields) {
      if (form.value[f.code] !== undefined) payload[f.code] = form.value[f.code]
    }
    if (props.isEdit) {
      await updateData(props.dataId, payload)
    } else {
      await createData(props.tableId, payload)
    }
    ElMessage.success(props.isEdit ? '编辑成功' : '新增成功')
    emit('update:visible', false)
    emit('saved')
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  } finally {
    submitting.value = false
  }
}
</script>
