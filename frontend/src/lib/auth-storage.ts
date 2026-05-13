import type { UserResponse } from "@/types/auth";

const ACCESS_TOKEN_KEY = "accessToken";
const REFRESH_TOKEN_KEY = "refreshToken";
const TEMP_TOKEN_KEY = "tempToken";
const USER_INFO_KEY = "userInfo";
const MFA_QR_URI_KEY = "mfaQrUri";

export function getAccessToken(): string {
  return localStorage.getItem(ACCESS_TOKEN_KEY) ?? "";
}

export function setAccessToken(token: string): void {
  localStorage.setItem(ACCESS_TOKEN_KEY, token);
}

export function getRefreshToken(): string {
  return localStorage.getItem(REFRESH_TOKEN_KEY) ?? "";
}

export function setRefreshToken(token: string): void {
  localStorage.setItem(REFRESH_TOKEN_KEY, token);
}

export function getTempToken(): string {
  return localStorage.getItem(TEMP_TOKEN_KEY) ?? "";
}

export function setTempToken(token: string): void {
  localStorage.setItem(TEMP_TOKEN_KEY, token);
}

export function clearTempToken(): void {
  localStorage.removeItem(TEMP_TOKEN_KEY);
}

export function setMfaQrUri(secretImageUri: string): void {
  if (secretImageUri) {
    localStorage.setItem(MFA_QR_URI_KEY, secretImageUri);
    return;
  }

  localStorage.removeItem(MFA_QR_URI_KEY);
}

export function getMfaQrUri(): string {
  return localStorage.getItem(MFA_QR_URI_KEY) ?? "";
}

export function clearMfaQrUri(): void {
  localStorage.removeItem(MFA_QR_URI_KEY);
}

export function setUserInfo(userInfo: UserResponse): void {
  localStorage.setItem(USER_INFO_KEY, JSON.stringify(userInfo));
}

export function getUserInfo(): UserResponse | null {
  const rawData = localStorage.getItem(USER_INFO_KEY);
  if (!rawData) {
    return null;
  }

  try {
    return JSON.parse(rawData) as UserResponse;
  } catch {
    localStorage.removeItem(USER_INFO_KEY);
    return null;
  }
}

export function clearSession(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(TEMP_TOKEN_KEY);
  localStorage.removeItem(USER_INFO_KEY);
  localStorage.removeItem(MFA_QR_URI_KEY);
}
