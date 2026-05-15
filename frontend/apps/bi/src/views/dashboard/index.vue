<template>
  <div class="dashboard-container">
    <div class="iframe-wrapper" v-loading="loading">
      <iframe
        ref="dashboardIframe"
        :src="currentUrl"
        frameborder="0"
        width="100%"
        height="100%"
        @load="handleIframeLoad"
      ></iframe>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

// 预设燕鑫系列各子系统的前端看板URL
const systems = [
  { id: 'zhaoxianbao', name: '招贤宝', url: 'http://localhost:3001/admin/dashboard' },
  { id: 'xueyuan', name: '燕鑫学院', url: 'http://localhost:3002/dashboard' },
  { id: 'anxinbao', name: '安心保', url: 'http://localhost:3003/dashboard' },
  { id: 'keyingmen', name: '客盈门', url: 'http://localhost:3004/dashboard' },
  { id: 'zhijian', name: '慧质检', url: 'http://localhost:3005/dashboard' },
  { id: 'zhipaidan', name: '智派单', url: 'http://localhost:3006/dashboard' },
  { id: 'suoguanjia', name: '锁管家', url: 'http://localhost:3007/dashboard' }
];

const loading = ref(true);
const dashboardIframe = ref<HTMLIFrameElement | null>(null);

const currentUrl = computed(() => {
  const systemId = (route.query.system as string) || systems[0].id;
  const sys = systems.find(s => s.id === systemId);
  return sys ? sys.url : systems[0].url;
});

watch(currentUrl, () => {
  loading.value = true;
});

const handleIframeLoad = () => {
  loading.value = false;
};
</script>

<style scoped>
.dashboard-container {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.iframe-wrapper {
  flex: 1;
  background-color: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  overflow: hidden;
  position: relative;
}

iframe {
  display: block;
}
</style>
