<template>
  <a-result status="403" title="401" sub-title="抱歉，您没有权限访问此页面">
    <template #extra>
      <a-space>
        <a-button type="primary" @click="$router.push('/')">返回首页</a-button>
        <a-button @click="$router.push('/login')">重新登录</a-button>
      </a-space>
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
