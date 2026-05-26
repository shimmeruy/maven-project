<template>
  <div class="map-page">
    <div class="map-toolbar">
      <el-button @click="$router.back()">← 返回</el-button>
      <span class="map-title">{{ tableName }} — 地图视图</span>
      <span class="point-count">共 {{ markers.length }} 个点位</span>
    </div>
    <div ref="mapContainer" class="map-container"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue'
import { useRoute } from 'vue-router'
import L from 'leaflet'
import { getTableDetail } from '../api/table'
import { getDataPage } from '../api/data'

const route = useRoute()
const tableId = route.params.tableId
const tableName = ref('')
const markers = ref([])
const mapContainer = ref(null)
let map = null
let markerLayer = null

onMounted(async () => {
  // load table config
  const table = await getTableDetail(tableId)
  tableName.value = table.name
  const lngField = table.mapLongitudeField
  const latField = table.mapLatitudeField
  const labelField = table.mapLabelField
  const descField = table.mapDescField

  // load all data
  const res = await getDataPage(tableId, 0, 9999)

  await nextTick()
  initMap()
  markerLayer = L.featureGroup()

  for (const item of res.content) {
    const d = item.jsonbData
    const lng = parseFloat(d[lngField])
    const lat = parseFloat(d[latField])
    if (isNaN(lng) || isNaN(lat)) continue
    const label = d[labelField] || ''
    const desc = d[descField] || ''
    const marker = L.marker([lat, lng])
    marker.bindPopup(`<b>${label}</b><br/>${desc}`)
    markerLayer.addLayer(marker)
    markers.value.push({ lat, lng, label, desc })
  }
  markerLayer.addTo(map)
  if (markers.value.length > 0) {
    const bounds = L.latLngBounds(markers.value.map(m => [m.lat, m.lng]))
    map.fitBounds(bounds, { padding: [50, 50] })
  }
})

function initMap() {
  map = L.map(mapContainer.value).setView([35, 105], 5)
  L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
    attribution: '&copy; OpenStreetMap contributors'
  }).addTo(map)
}

onUnmounted(() => {
  if (map) map.remove()
})
</script>

<style scoped>
.map-page { display: flex; flex-direction: column; height: 100%; }
.map-toolbar {
  display: flex; gap: 16px; align-items: center;
  padding: 8px 16px; border-bottom: 1px solid #ebeef5;
}
.map-title { font-weight: 600; }
.point-count { color: #909399; font-size: 13px; }
.map-container { flex: 1; }
</style>
