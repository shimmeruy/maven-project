<template>
  <div class="graph-page">
    <!-- left config panel -->
    <div class="graph-sidebar">
      <div class="sidebar-header">
        <h4>图谱配置</h4>
        <el-button size="small" type="primary" @click="openAddConfig">新增配置</el-button>
      </div>
      <div v-for="cfg in configs" :key="cfg.id" class="config-item">
        <div class="config-name">
          <el-tag>{{ cfg.tableName }}</el-tag>
        </div>
        <div class="config-info">
          <span>节点字段: {{ cfg.nodeNameField }}</span>
          <span v-if="cfg.nodeLabelField">标签字段: {{ cfg.nodeLabelField }}</span>
        </div>
        <div class="config-actions">
          <el-button link size="small" @click="syncConfig(cfg)">同步</el-button>
          <el-button link size="small" type="danger" @click="deleteConfig(cfg)">删除</el-button>
        </div>
      </div>
    </div>
    <!-- graph canvas -->
    <div class="graph-main">
      <GraphCanvas ref="graphCanvas" :nodes="nodes" :edges="edges" :tables="tables"
                   @add-edge="openAddEdge" @export="doExport" />
    </div>

    <!-- add config dialog -->
    <el-dialog v-model="configDialogVisible" title="新增图谱配置" width="500px">
      <el-form :model="configForm" label-width="120px">
        <el-form-item label="选择知识表" required>
          <el-select v-model="configForm.tableId" placeholder="选择知识表" @change="onConfigTableChange">
            <el-option v-for="t in allTables" :key="t.id" :label="t.name" :value="t.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点名称字段" required>
          <el-select v-model="configForm.nodeNameField" placeholder="选择字段">
            <el-option v-for="f in configTableFields" :key="f.code" :label="f.name" :value="f.code" />
          </el-select>
        </el-form-item>
        <el-form-item label="节点标签字段">
          <el-select v-model="configForm.nodeLabelField" placeholder="选择字段（可选）" clearable>
            <el-option v-for="f in configTableFields" :key="f.code" :label="f.name" :value="f.code" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="configDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitConfig">确定</el-button>
      </template>
    </el-dialog>

    <!-- add edge dialog -->
    <el-dialog v-model="edgeDialogVisible" title="手动建立关系" width="500px">
      <el-form :model="edgeForm" label-width="100px">
        <el-form-item label="起始节点" required>
          <el-select v-model="edgeForm.sourceNodeId" placeholder="选择起始节点" filterable>
            <el-option v-for="n in nodes" :key="n.id" :label="n.name" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="目标节点" required>
          <el-select v-model="edgeForm.targetNodeId" placeholder="选择目标节点" filterable>
            <el-option v-for="n in nodes" :key="n.id" :label="n.name" :value="n.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="关系名称" required>
          <el-input v-model="edgeForm.relationName" placeholder="如：关联、属于、依赖" />
        </el-form-item>
        <el-form-item label="关系方向">
          <el-radio-group v-model="edgeForm.relationType">
            <el-radio label="DIRECTED">单向</el-radio>
            <el-radio label="UNDIRECTED">双向</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="edgeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitEdge">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import GraphCanvas from '../components/GraphCanvas.vue'
import {
  getGraphConfigs, createGraphConfig, deleteGraphConfig,
  getFullGraph, createGraphEdge
} from '../api/graph'
import { getTablesByCategory, getTableDetail } from '../api/table'
import { getCategoryTree } from '../api/category'

const graphCanvas = ref(null)
const configs = ref([])
const nodes = ref([])
const edges = ref([])
const tables = ref([])
const allTables = ref([])
const configTableFields = ref([])

const configDialogVisible = ref(false)
const configForm = ref({ tableId: '', nodeNameField: '', nodeLabelField: '' })

const edgeDialogVisible = ref(false)
const edgeForm = ref({ sourceNodeId: '', targetNodeId: '', relationName: '', relationType: 'DIRECTED' })

onMounted(async () => {
  await loadAll()
})

async function loadAll() {
  try {
    const categories = await getCategoryTree()
    for (const cat of categories) {
      const tbls = await getTablesByCategory(cat.id)
      allTables.value.push(...tbls)
      tables.value.push(...tbls)
    }
    configs.value = await getGraphConfigs()
    const fullGraph = await getFullGraph()
    nodes.value = fullGraph.nodes || []
    edges.value = fullGraph.edges || []
  } catch (e) {
    ElMessage.error('加载图谱数据失败')
  }
}

async function onConfigTableChange(tableId) {
  try {
    const detail = await getTableDetail(tableId)
    configTableFields.value = (detail.fields || []).filter(f => f.fieldType === 'TEXT')
  } catch (e) {
    configTableFields.value = []
  }
}

function openAddConfig() {
  configForm.value = { tableId: '', nodeNameField: '', nodeLabelField: '' }
  configTableFields.value = []
  configDialogVisible.value = true
}

async function submitConfig() {
  if (!configForm.value.tableId || !configForm.value.nodeNameField) {
    ElMessage.warning('请选择知识表和节点名称字段')
    return
  }
  try {
    await createGraphConfig(configForm.value)
    ElMessage.success('配置成功，节点已自动生成')
    configDialogVisible.value = false
    await loadAll()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

async function syncConfig(cfg) {
  ElMessage.info('节点同步中...')
  await loadAll()
  ElMessage.success('同步完成')
}

async function deleteConfig(cfg) {
  try {
    await ElMessageBox.confirm('确定删除该图谱配置？节点也将被删除', '确认删除', { type: 'warning' })
    await deleteGraphConfig(cfg.id)
    ElMessage.success('删除成功')
    await loadAll()
  } catch (e) {
    if (e !== 'cancel') ElMessage.error(e.message || '删除失败')
  }
}

function openAddEdge() {
  edgeForm.value = { sourceNodeId: '', targetNodeId: '', relationName: '', relationType: 'DIRECTED' }
  edgeDialogVisible.value = true
}

async function submitEdge() {
  if (!edgeForm.value.sourceNodeId || !edgeForm.value.targetNodeId || !edgeForm.value.relationName) {
    ElMessage.warning('请填写完整关系信息')
    return
  }
  try {
    await createGraphEdge(edgeForm.value)
    ElMessage.success('关系创建成功')
    edgeDialogVisible.value = false
    await loadAll()
  } catch (e) {
    ElMessage.error(e.message || '操作失败')
  }
}

function doExport() {
  if (graphCanvas.value) graphCanvas.value.exportImage()
}
</script>

<style scoped>
.graph-page { display: flex; height: 100%; }
.graph-sidebar {
  width: 260px; min-width: 260px; border-right: 1px solid #ebeef5;
  overflow-y: auto; padding: 12px;
}
.sidebar-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.config-item {
  border: 1px solid #ebeef5; border-radius: 6px; padding: 10px; margin-bottom: 8px;
}
.config-name { margin-bottom: 4px; }
.config-info { font-size: 12px; color: #909399; margin-bottom: 8px; }
.config-info span { display: block; }
.config-actions { text-align: right; }
.graph-main { flex: 1; }
</style>
