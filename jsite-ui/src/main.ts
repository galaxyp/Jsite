import { createApp } from 'vue'
import { createPinia } from 'pinia'
import piniaPluginPersistedstate from 'pinia-plugin-persistedstate'
import Antd from 'ant-design-vue'
import App from './App.vue'
import router from './router'

// 样式
import 'ant-design-vue/dist/reset.css'
import './assets/styles/index.less'

// 创建应用
const app = createApp(App)

// 状态管理
const pinia = createPinia()
pinia.use(piniaPluginPersistedstate)
app.use(pinia)

// 路由
app.use(router)

// UI框架
app.use(Antd)

// 注册自定义指令
import { setupPermissionDirective } from './directives/permission'
setupPermissionDirective(app)

// 挂载应用
app.mount('#app')
