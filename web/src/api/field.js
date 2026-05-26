import request from './index'

export function getFields(tableId) {
  return request.get(`/tables/${tableId}/fields`)
}

export function createField(tableId, data) {
  return request.post(`/tables/${tableId}/fields`, data)
}

export function updateField(id, data) {
  return request.put(`/fields/${id}`, data)
}

export function deleteField(id) {
  return request.delete(`/fields/${id}`)
}

export function batchUpdateSort(data) {
  return request.put('/fields/sort', data)
}
