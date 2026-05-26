import request from './index'

export function getCategoryTree() {
  return request.get('/categories/tree')
}

export function getCategoryDetail(id) {
  return request.get(`/categories/${id}`)
}

export function createCategory(data) {
  return request.post('/categories', data)
}

export function updateCategory(id, data) {
  return request.put(`/categories/${id}`, data)
}

export function deleteCategory(id) {
  return request.delete(`/categories/${id}`)
}

export function updateCategorySort(id, sortOrder) {
  return request.put(`/categories/${id}/sort`, null, { params: { sortOrder } })
}
