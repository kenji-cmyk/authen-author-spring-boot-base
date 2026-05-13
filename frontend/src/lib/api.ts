import type {
  Disable2FAResponse,
  Enable2FAResponse,
  LoginResponse,
  RefreshTokenResponse,
  RegisterResponse,
  UserResponse,
  UsersPageResponse,
} from "@/types/auth";

type Primitive = string | number | boolean | null;
type JsonRecord = Record<string, Primitive>;

function extractErrorMessage(data: unknown): string {
  if (!data || typeof data !== "object") {
    return "Request failed";
  }

  const objectData = data as Record<string, unknown>;
  if (typeof objectData.message === "string" && objectData.message.length > 0) {
    return objectData.message;
  }

  const validationMessages = Object.values(objectData).filter(
    (value): value is string => typeof value === "string" && value.length > 0,
  );

  if (validationMessages.length > 0) {
    return validationMessages[0];
  }

  return "Request failed";
}

async function request<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(path, init);
  const textData = await response.text();
  let data: unknown = null;

  if (textData) {
    try {
      data = JSON.parse(textData) as unknown;
    } catch {
      if (!response.ok) {
        throw new Error("Request failed");
      }
    }
  }

  if (!response.ok) {
    throw new Error(extractErrorMessage(data));
  }

  return data as T;
}

function jsonHeaders(accessToken?: string): HeadersInit {
  return {
    "Content-Type": "application/json",
    ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {}),
  };
}

function jsonBody(data: JsonRecord): string {
  return JSON.stringify(data);
}

export async function login(username: string, password: string): Promise<LoginResponse> {
  return request<LoginResponse>("/api/auth/login", {
    method: "POST",
    headers: jsonHeaders(),
    body: jsonBody({ username, password }),
  });
}

export async function register(
  username: string,
  password: string,
  mfaEnabled: boolean,
): Promise<RegisterResponse> {
  return request<RegisterResponse>("/api/auth/register", {
    method: "POST",
    headers: jsonHeaders(),
    body: jsonBody({ username, password, mfaEnabled }),
  });
}

export async function verify2FA(tempToken: string, otp: string): Promise<LoginResponse> {
  return request<LoginResponse>("/api/auth/verify-2fa", {
    method: "POST",
    headers: jsonHeaders(),
    body: jsonBody({ tempToken, otp }),
  });
}

export async function refreshToken(refreshTokenValue: string): Promise<RefreshTokenResponse> {
  return request<RefreshTokenResponse>("/api/auth/refresh-token", {
    method: "POST",
    headers: jsonHeaders(),
    body: jsonBody({ refreshToken: refreshTokenValue }),
  });
}

export async function enable2FA(
  accessToken: string,
  tempToken: string,
): Promise<Enable2FAResponse> {
  return request<Enable2FAResponse>("/api/auth/enable-2fa", {
    method: "POST",
    headers: jsonHeaders(accessToken),
    body: jsonBody({ tempToken }),
  });
}

export async function disable2FA(
  accessToken: string,
  tempToken: string,
  otp: string,
): Promise<Disable2FAResponse> {
  return request<Disable2FAResponse>("/api/auth/disable-2fa", {
    method: "POST",
    headers: jsonHeaders(accessToken),
    body: jsonBody({ tempToken, otp }),
  });
}

export async function getCurrentUser(accessToken: string): Promise<UserResponse> {
  return request<UserResponse>("/api/users/me", {
    method: "GET",
    headers: jsonHeaders(accessToken),
  });
}

export async function getUsers(
  accessToken: string,
  page: number,
  limit: number,
): Promise<UsersPageResponse> {
  return request<UsersPageResponse>(`/api/users?page=${page}&limit=${limit}`, {
    method: "GET",
    headers: jsonHeaders(accessToken),
  });
}
