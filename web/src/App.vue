<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="header-left">
          <h2 @click="$router.push('/')" class="logo">知识管理系统</h2>
        </div>
        <div class="header-right">
          <el-menu mode="horizontal" :default-active="activeMenu" router>
            <el-menu-item index="/">知识管理</el-menu-item>
            <el-menu-item index="/search">全局搜索</el-menu-item>
            <el-menu-item index="/graph">知识图谱</el-menu-item>
          </el-menu>
        </div>
      </el-header>
      <el-main>
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activeMenu = computed(() => {
  if (route.path.startsWith('/search')) return '/search'
  if (route.path.startsWith('/graph')) return '/graph'
  return '/'
})
</script>
