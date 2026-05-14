import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

const app = createApp(App)

for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.use(router)
app.use(ElementPlus)
app.mount('#app')
// --- Iframe Adapter for BI System ---
if (window.self !== window.top) {
  document.documentElement.classList.add('is-iframe');
  const style = document.createElement('style');
  style.textContent = `
    html.is-iframe .aside,
    html.is-iframe .header,
    html.is-iframe .aside-panel,
    html.is-iframe .header-bar {
      display: none !important;
    }
    html.is-iframe .main,
    html.is-iframe .main-area {
      padding: 10px !important;
      height: 100vh !important;
    }
  `;
  document.head.appendChild(style);
}
// ----------------------------------
