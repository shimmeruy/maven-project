import request from './index'

export function getTablesByCategory(categoryId) {
  return request.get(`/categories/${categoryId}/tables`)
}

export function getTableDetail(id) {
  return request.get(`/tables/${id}`)
}

export function createTable(data) {
  return request.post('/tables', data)
}

export function updateTable(id, data) {
  return request.put(`/tables/${id}`, data)
}

export function deleteTable(id) {
  return request.delete(`/tables/${id}`)
}

export function updateMapConfig(id, data) {
  return request.put(`/tables/${id}/map-config`, data)
}
