<template>
  <a-result status="404" title="404" sub-title="抱歉，您访问的页面不存在">
    <template #extra>
      <a-button type="primary" @click="$router.push('/')">返回首页</a-button>
    </template>
  </a-result>
</template>

<script setup lang="ts">
</script>

<style lang="less" scoped>
.ant-result {
  height: 100vh;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
</style>
