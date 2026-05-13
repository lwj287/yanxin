<template>
  <el-config-provider>
    <router-view />
  </el-config-provider>
</template>

<script setup lang="ts">
// Root Component
</script>

<style>
body {
  margin: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

/* 统一所有页面状态标签的视觉尺寸，避免切页时出现忽大忽小 */
.el-table .el-tag,
.level-tag {
  min-width: 64px;
  height: 24px;
  line-height: 22px;
  padding: 0 8px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  box-sizing: border-box;
  white-space: nowrap;
  transition: none !important;
  transform: none !important;
}

/* 分页切换时固定列宽计算，避免状态标签所在单元格出现拉伸感 */
.custom-table :is(.el-table__header, .el-table__body, .el-table__footer) {
  table-layout: fixed !important;
}

/* 关闭表格单元格过渡动画，避免分页切页时的视觉抖动 */
.custom-table .el-table__cell {
  transition: none !important;
}
</style>
