import { type FormEvent, useState } from "react";

import { Button } from "@/components/ui/button";
import { Checkbox } from "@/components/ui/checkbox";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { StatusMessage } from "@/components/common/status-message";
import { register } from "@/lib/api";
import { clearMfaQrUri, setMfaQrUri, setTempToken } from "@/lib/auth-storage";
import { PasswordInput } from "@/components/auth/password-input";

interface RegisterFormProps {
  onNeed2FA: () => void;
}

export function RegisterForm({ onNeed2FA }: RegisterFormProps) {
  const [username, setUsername] = useState<string>("");
  const [password, setPassword] = useState<string>("");
  const [enableMfa, setEnableMfa] = useState<boolean>(true);
  const [isLoading, setIsLoading] = useState<boolean>(false);
  const [error, setError] = useState<string>("");
  const [success, setSuccess] = useState<string>("");

  const handleSubmit = async (event: FormEvent) => {
    event.preventDefault();
    setError("");
    setSuccess("");
    clearMfaQrUri();
    setIsLoading(true);

    try {
      const response = await register(username, password, enableMfa);
      if (response.tempToken) {
        setTempToken(response.tempToken);
        setMfaQrUri(response.secretImageUri ?? "");
      }
      setSuccess(response.message ?? "Register success");

      if (response.tempToken) {
        onNeed2FA();
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
        <Label htmlFor="register-username">Username</Label>
        <Input
          id="register-username"
          type="text"
          placeholder="new_user"
          autoComplete="off"
          value={username}
          onChange={(event) => setUsername(event.target.value)}
          className="h-12 border-border/60 bg-background focus:border-primary"
          required
        />
      </div>

      <div className="space-y-2">
        <Label htmlFor="register-password">Password</Label>
        <PasswordInput
          id="register-password"
          value={password}
          placeholder="At least 8 chars, 1 uppercase, 1 number"
          onChange={setPassword}
        />
      </div>

      <div className="flex items-center space-x-2">
        <Checkbox
          id="register-enable-2fa"
          checked={enableMfa}
          onCheckedChange={(value) => setEnableMfa(value === true)}
        />
        <Label htmlFor="register-enable-2fa" className="cursor-pointer font-normal">
          Enable 2FA for this account
        </Label>
      </div>

      {error ? <StatusMessage message={error} variant="error" /> : null}
      {success ? <StatusMessage message={success} variant="success" /> : null}

      <Button type="submit" size="lg" className="h-12 w-full text-base" disabled={isLoading}>
        {isLoading ? "Creating account..." : "Register"}
      </Button>
    </form>
  );
}
