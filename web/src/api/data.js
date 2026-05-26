import request from './index'

export function getDataPage(tableId, page = 0, size = 20) {
  return request.get(`/tables/${tableId}/data`, { params: { page, size } })
}

export function getDataDetail(id) {
  return request.get(`/data/${id}`)
}

export function createData(tableId, data) {
  return request.post(`/tables/${tableId}/data`, data)
}

export function updateData(id, data) {
  return request.put(`/data/${id}`, data)
}

export function deleteData(id) {
  return request.delete(`/data/${id}`)
}

export function batchDeleteData(tableId, ids) {
  return request.delete(`/tables/${tableId}/data/batch`, { data: ids })
}

export function searchData(keyword, page = 0, size = 20) {
  return request.get('/data/search', { params: { keyword, page, size } })
}

export function uploadImage(file) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/data/upload-image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}
