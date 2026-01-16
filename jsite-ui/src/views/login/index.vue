<template>
  <div class="login-container">
    <div class="login-box">
      <div class="login-title">
        <h1>JSite 快速开发平台</h1>
      </div>
      <a-form
        ref="loginFormRef"
        :model="loginForm"
        :rules="loginRules"
        class="login-form"
        @finish="handleLogin"
      >
        <a-form-item name="username">
          <a-input
            v-model:value="loginForm.username"
            size="large"
            placeholder="请输入账号"
          >
            <template #prefix>
              <UserOutlined />
            </template>
          </a-input>
        </a-form-item>

        <a-form-item name="password">
          <a-input-password
            v-model:value="loginForm.password"
            size="large"
            placeholder="请输入密码"
            @keyup.enter="handleLogin"
          >
            <template #prefix>
              <LockOutlined />
            </template>
          </a-input-password>
        </a-form-item>

        <a-form-item v-if="captchaEnabled" name="code">
          <div class="captcha-row">
            <a-input
              v-model:value="loginForm.code"
              size="large"
              placeholder="请输入验证码"
              style="flex: 1"
            >
              <template #prefix>
                <SafetyCertificateOutlined />
              </template>
            </a-input>
            <img
              :src="captchaUrl"
              class="captcha-img"
              @click="getCaptcha"
              alt="验证码"
            />
          </div>
        </a-form-item>

        <a-form-item>
          <a-checkbox v-model:checked="loginForm.rememberMe">记住密码</a-checkbox>
        </a-form-item>

        <a-form-item>
          <a-button
            type="primary"
            size="large"
            block
            :loading="loading"
            html-type="submit"
          >
            登 录
          </a-button>
        </a-form-item>
      </a-form>
    </div>

    <div class="login-footer">
      <span>Copyright &copy; 2024 JSite. All Rights Reserved.</span>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { message } from 'ant-design-vue'
import { UserOutlined, LockOutlined, SafetyCertificateOutlined } from '@ant-design/icons-vue'
import { useUserStore } from '@/store/modules/user'
import { getCaptcha as getCaptchaApi } from '@/api/login'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginFormRef = ref()
const loading = ref(false)
const captchaEnabled = ref(false)
const captchaUrl = ref('')

const loginForm = reactive({
  username: 'admin',
  password: 'admin123',
  code: '',
  uuid: '',
  rememberMe: true,
})

const loginRules = {
  username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  code: [{ required: captchaEnabled.value, message: '请输入验证码', trigger: 'blur' }],
}

/**
 * 获取验证码
 */
const getCaptcha = async () => {
  try {
    const res = await getCaptchaApi()
    captchaEnabled.value = res.data.captchaEnabled
    if (captchaEnabled.value) {
      captchaUrl.value = 'data:image/gif;base64,' + res.data.img
      loginForm.uuid = res.data.uuid
    }
  } catch (error) {
    console.error('获取验证码失败', error)
  }
}

/**
 * 登录
 */
const handleLogin = async () => {
  try {
    await loginFormRef.value?.validate()
    loading.value = true

    await userStore.login(loginForm)
    message.success('登录成功')

    // 跳转到首页或重定向地址
    const redirect = (route.query.redirect as string) || '/'
    router.push(redirect)
  } catch (error: any) {
    console.error('登录失败', error)
    // 刷新验证码
    if (captchaEnabled.value) {
      getCaptcha()
    }
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  getCaptcha()
})
</script>

<style lang="less" scoped>
.login-container {
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.login-box {
  width: 400px;
  padding: 40px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;

  h1 {
    font-size: 24px;
    font-weight: 600;
    color: #333;
    margin: 0;
  }
}

.login-form {
  .captcha-row {
    display: flex;
    gap: 10px;
  }

  .captcha-img {
    width: 120px;
    height: 40px;
    cursor: pointer;
    border: 1px solid #d9d9d9;
    border-radius: 4px;
  }
}

.login-footer {
  margin-top: 20px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 12px;
}
</style>
