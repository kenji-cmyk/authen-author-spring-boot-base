export interface UserResponse {
  id: number;
  username: string;
  email: string;
  roles: string[];
  provider?: {
    id?: number;
    name?: string;
  };
  mfaEnabled: boolean;
  mfaVerified: boolean;
}

export interface LoginResponse {
  accessToken?: string;
  refreshToken?: string;
  message?: string;
  tempToken?: string;
  secretImageUri?: string;
  userInfo?: UserResponse;
}

export interface RegisterResponse {
  userInfo?: UserResponse;
  secretImageUri?: string;
  tempToken?: string;
  message?: string;
}

export interface RefreshTokenResponse {
  accessToken: string;
  refreshToken: string;
}

export interface Enable2FAResponse {
  secretImageUri?: string;
  tempToken?: string;
  message?: string;
}

export interface Disable2FAResponse {
  userInfo?: UserResponse;
  message?: string;
}

export interface UsersPageResponse {
  items: UserResponse[];
  total: number;
  page: number;
  limit: number;
  totalPages: number;
}
