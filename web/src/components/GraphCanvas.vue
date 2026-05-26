<template>
  <div class="graph-canvas">
    <div class="graph-toolbar">
      <el-button-group>
        <el-button size="small" @click="zoomIn">放大</el-button>
        <el-button size="small" @click="zoomOut">缩小</el-button>
        <el-button size="small" @click="fitView">适应画布</el-button>
      </el-button-group>
      <el-select v-model="filterTableId" placeholder="按知识表筛选" clearable size="small" style="width:180px" @change="applyFilter">
        <el-option v-for="t in tables" :key="t.id" :label="t.name" :value="t.id" />
      </el-select>
      <el-button size="small" @click="$emit('add-edge')">手动建立关系</el-button>
      <el-button size="small" @click="$emit('export')">导出图片</el-button>
    </div>
    <div ref="container" class="graph-container"></div>
    <el-drawer v-model="nodeDrawerVisible" title="节点详情" size="350px">
      <template v-if="selectedNode">
        <el-descriptions :column="1" border>
          <el-descriptions-item label="节点名称">{{ selectedNode.name }}</el-descriptions-item>
          <el-descriptions-item label="标签">{{ selectedNode.label || '-' }}</el-descriptions-item>
          <el-descriptions-item label="来源表">{{ selectedNode.sourceTableName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="类型">{{ selectedNode.nodeType === 'AUTO' ? '自动生成' : '手动创建' }}</el-descriptions-item>
        </el-descriptions>
      </template>
    </el-drawer>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import G6 from '@antv/g6'

const props = defineProps({
  nodes: { type: Array, default: () => [] },
  edges: { type: Array, default: () => [] },
  tables: { type: Array, default: () => [] }
})
const emit = defineEmits(['add-edge', 'export'])

const container = ref(null)
const nodeDrawerVisible = ref(false)
const selectedNode = ref(null)
const filterTableId = ref('')
let graph = null

function initGraph() {
  if (!container.value) return
  const width = container.value.offsetWidth
  const height = container.value.offsetHeight

  graph = new G6.Graph({
    container: container.value,
    width, height,
    layout: {
      type: 'force',
      preventOverlap: true,
      linkDistance: 150,
      nodeStrength: -100,
      edgeStrength: 0.2
    },
    defaultNode: {
      size: 40,
      labelCfg: { style: { fill: '#333', fontSize: 12 } }
    },
    defaultEdge: {
      style: { endArrow: true, stroke: '#aaa' },
      labelCfg: { autoRotate: true, style: { fill: '#666', fontSize: 10 } }
    },
    modes: {
      default: ['drag-canvas', 'zoom-canvas', 'drag-node']
    }
  })

  graph.on('node:click', (e) => {
    const model = e.item.getModel()
    selectedNode.value = model._data || model
    nodeDrawerVisible.value = true
  })
}

function renderGraph() {
  if (!graph) return
  const colorMap = {}
  const colors = ['#409eff', '#67c23a', '#e6a23c', '#f56c6c', '#909399', '#b37feb']
  let colorIdx = 0

  const gNodes = props.nodes.map(n => {
    const key = n.sourceTableId || 'default'
    if (!colorMap[key]) { colorMap[key] = colors[colorIdx++ % colors.length] }
    return {
      id: n.id,
      label: n.name,
      style: { fill: colorMap[key] },
      _data: n
    }
  })
  const gEdges = props.edges.map(e => ({
    source: e.sourceNodeId,
    target: e.targetNodeId,
    label: e.relationName
  }))
  graph.data({ nodes: gNodes, edges: gEdges })
  graph.render()
  graph.fitView(40)
}

function applyFilter() {
  if (!graph) return
  graph.getNodes().forEach(node => {
    const model = node.getModel()
    if (!filterTableId.value || (model._data && model._data.sourceTableId === filterTableId.value)) {
      node.show()
    } else {
      node.hide()
    }
  })
}

function zoomIn() { if (graph) { const z = graph.getZoom(); graph.zoomTo(z * 1.2) } }
function zoomOut() { if (graph) { const z = graph.getZoom(); graph.zoomTo(z / 1.2) } }
function fitView() { if (graph) graph.fitView(40) }

function exportImage() {
  if (graph) graph.downloadFullImage('knowledge-graph', 'image/png')
}

defineExpose({ renderGraph, exportImage, applyFilter })

onMounted(() => {
  nextTick(() => {
    initGraph()
    renderGraph()
  })
})

onUnmounted(() => {
  if (graph) graph.destroy()
})

watch(() => [props.nodes, props.edges], () => {
  nextTick(() => renderGraph())
}, { deep: true })

window.addEventListener('resize', () => {
  if (graph && container.value) {
    graph.changeSize(container.value.offsetWidth, container.value.offsetHeight)
  }
})
</script>

<style scoped>
.graph-canvas { display: flex; flex-direction: column; height: 100%; }
.graph-toolbar {
  display: flex; gap: 8px; align-items: center;
  padding: 8px 16px; border-bottom: 1px solid #ebeef5;
}
.graph-container { flex: 1; }
</style>
