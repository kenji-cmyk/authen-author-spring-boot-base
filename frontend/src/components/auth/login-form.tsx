import { type FormEvent, useState } from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { StatusMessage } from "@/components/common/status-message";
import { login } from "@/lib/api";
import {
  clearMfaQrUri,
  setAccessToken,
  setMfaQrUri,
  setRefreshToken,
  setTempToken,
  setUserInfo,
} from "@/lib/auth-storage";
import { PasswordInput } from "@/components/auth/password-input";

interface LoginFormProps {
  onNeed2FA: () => void;
  onSuccess: () => void;
  onPasswordTypingChange?: (isTyping: boolean) => void;
}

export function LoginForm({ onNeed2FA, onSuccess, onPasswordTypingChange }: LoginFormProps) {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setIsLoading(true);

    try {
      const response = await login(username, password);

      if (response.userInfo) {
        setUserInfo(response.userInfo);
      }

      if (response.tempToken && !response.accessToken) {
        setTempToken(response.tempToken);
        setMfaQrUri(response.secretImageUri ?? "");
        onNeed2FA();
        return;
      }

      if (response.accessToken) {
        setAccessToken(response.accessToken);
        if (response.refreshToken) {
          setRefreshToken(response.refreshToken);
        }
        if (response.tempToken) {
          setTempToken(response.tempToken);
        }
        clearMfaQrUri();
        onSuccess();
      }
    } catch (requestError) {
      const message = requestError instanceof Error ? requestError.message : "Network error. Please try again.";
      setError(message);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <form onSubmit={handleSubmit} className="space-y-5">
      <div className="space-y-2">
        <Label htmlFor="login-username">Username</Label>
        <Input
          id="login-username"
          type="text"
          placeholder="anna"
          autoComplete="off"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          className="h-12 border-border/60 bg-background focus:border-primary"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="login-password">Password</Label>
        <PasswordInput
          id="login-password"
          value={password}
          placeholder="••••••••"
          onChange={setPassword}
          onFocus={() => onPasswordTypingChange?.(true)}
          onBlur={() => onPasswordTypingChange?.(false)}
        />
      </div>

      {error ? <StatusMessage message={error} variant="error" /> : null}

      <Button type="submit" size="lg" className="h-12 w-full text-base" disabled={isLoading}>
        {isLoading ? "Signing in..." : "Log in"}
      </Button>
    </form>
  );
}
