import request from './index'

export function getGraphConfigs() {
  return request.get('/graph/configs')
}

export function createGraphConfig(data) {
  return request.post('/graph/configs', data)
}

export function updateGraphConfig(id, data) {
  return request.put(`/graph/configs/${id}`, data)
}

export function deleteGraphConfig(id) {
  return request.delete(`/graph/configs/${id}`)
}

export function getGraphNodes(sourceTableId) {
  return request.get('/graph/nodes', { params: { sourceTableId } })
}

export function getGraphNodeDetail(id) {
  return request.get(`/graph/nodes/${id}`)
}

export function getGraphEdges() {
  return request.get('/graph/edges')
}

export function createGraphEdge(data) {
  return request.post('/graph/edges', data)
}

export function updateGraphEdge(id, data) {
  return request.put(`/graph/edges/${id}`, data)
}

export function deleteGraphEdge(id) {
  return request.delete(`/graph/edges/${id}`)
}

export function getFullGraph() {
  return request.get('/graph/full')
}
