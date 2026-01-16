const TokenKey = 'jsite-token'

/**
 * 获取Token
 */
export function getToken(): string | null {
  return localStorage.getItem(TokenKey)
}

/**
 * 设置Token
 */
export function setToken(token: string): void {
  localStorage.setItem(TokenKey, token)
}

/**
 * 移除Token
 */
export function removeToken(): void {
  localStorage.removeItem(TokenKey)
}
