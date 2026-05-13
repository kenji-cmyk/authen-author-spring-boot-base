import { type FormEvent, useState } from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { StatusMessage } from "@/components/common/status-message";
import { verify2FA } from "@/lib/api";
import {
  clearMfaQrUri,
  clearTempToken,
  setAccessToken,
  setRefreshToken,
  setTempToken,
  setUserInfo,
  getTempToken,
} from "@/lib/auth-storage";

interface Verify2FAFormProps {
  onSuccess: () => void;
}

export function Verify2FAForm({ onSuccess }: Verify2FAFormProps) {
  const [otp, setOtp] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setIsLoading(true);

    const tempToken = getTempToken();
    if (!tempToken) {
      setError("Temp token not found. Please login again.");
      setIsLoading(false);
      return;
    }

    try {
      const response = await verify2FA(tempToken, otp);

      if (!response.accessToken || !response.refreshToken || !response.userInfo) {
        setError("Invalid verification response.");
        return;
      }

      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);
      setUserInfo(response.userInfo);
      if (response.tempToken) {
        setTempToken(response.tempToken);
      } else {
        clearTempToken();
      }
      clearMfaQrUri();
      onSuccess();
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
        <Label htmlFor="verify-otp">One-time password</Label>
        <Input
          id="verify-otp"
          type="text"
          inputMode="numeric"
          maxLength={6}
          placeholder="123456"
          value={otp}
          onChange={(event) => setOtp(event.target.value.replace(/\D/g, ""))}
          className="h-12 border-border/60 bg-background tracking-widest focus:border-primary"
          required
        />
      </div>

      {error ? <StatusMessage message={error} variant="error" /> : null}

      <Button type="submit" size="lg" className="h-12 w-full text-base" disabled={isLoading}>
        {isLoading ? "Verifying..." : "Verify 2FA"}
      </Button>
    </form>
  );
}
