import { useState } from "react";

import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { StatusMessage } from "@/components/common/status-message";
import { disable2FA, enable2FA, getCurrentUser, getUsers, refreshToken } from "@/lib/api";
import {
  getAccessToken,
  getRefreshToken,
  getTempToken,
  setAccessToken,
  setRefreshToken,
  setTempToken,
  setUserInfo,
} from "@/lib/auth-storage";
import type { UserResponse, UsersPageResponse } from "@/types/auth";

interface ApiPlaygroundProps {
  onUserUpdated: (user: UserResponse) => void;
}

export function ApiPlayground({ onUserUpdated }: ApiPlaygroundProps) {
  const [message, setMessage] = useState<string>("");
  const [error, setError] = useState<string>("");
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const [disableOtp, setDisableOtp] = useState<string>("");
  const [listPage, setListPage] = useState<string>("0");
  const [listLimit, setListLimit] = useState<string>("10");
  const [usersResult, setUsersResult] = useState<UsersPageResponse | null>(null);
  const [myProfile, setMyProfile] = useState<UserResponse | null>(null);
  const [qrUri, setQrUri] = useState<string>("");

  const startRequest = () => {
    setError("");
    setMessage("");
    setIsLoading(true);
  };

  const failRequest = (requestError: unknown) => {
    const requestMessage = requestError instanceof Error ? requestError.message : "Network error. Please try again.";
    setError(requestMessage);
    setIsLoading(false);
  };

  const finishRequest = () => {
    setIsLoading(false);
  };

  const handleRefreshToken = async () => {
    startRequest();
    try {
      const response = await refreshToken(getRefreshToken());
      setAccessToken(response.accessToken);
      setRefreshToken(response.refreshToken);
      setMessage("Refresh token success");
    } catch (requestError) {
      failRequest(requestError);
      return;
    }
    finishRequest();
  };

  const handleGetCurrentUser = async () => {
    startRequest();
    try {
      const response = await getCurrentUser(getAccessToken());
      setUserInfo(response);
      onUserUpdated(response);
      setMyProfile(response);
      setMessage("Loaded /api/users/me");
    } catch (requestError) {
      failRequest(requestError);
      return;
    }
    finishRequest();
  };

  const handleGetUsers = async () => {
    startRequest();
    try {
      const response = await getUsers(getAccessToken(), Number(listPage), Number(listLimit));
      setUsersResult(response);
      setMessage("Loaded /api/users");
    } catch (requestError) {
      failRequest(requestError);
      return;
    }
    finishRequest();
  };

  const handleEnable2FA = async () => {
    startRequest();
    const tempToken = getTempToken();
    if (!tempToken) {
      setError("Temp token is missing. Please login again to issue a new temp token.");
      setIsLoading(false);
      return;
    }

    try {
      const response = await enable2FA(getAccessToken(), tempToken);
      setQrUri(response.secretImageUri ?? "");
      if (response.tempToken) {
        setTempToken(response.tempToken);
      }
      setMessage(response.message ?? "2FA enabled flow started");
    } catch (requestError) {
      failRequest(requestError);
      return;
    }
    finishRequest();
  };

  const handleDisable2FA = async () => {
    startRequest();
    const tempToken = getTempToken();
    if (!tempToken) {
      setError("Temp token is missing. Please login again to issue a new temp token.");
      setIsLoading(false);
      return;
    }

    try {
      const response = await disable2FA(getAccessToken(), tempToken, disableOtp);
      if (response.userInfo) {
        setUserInfo(response.userInfo);
        onUserUpdated(response.userInfo);
      }
      setMessage(response.message ?? "2FA disabled");
      setDisableOtp("");
    } catch (requestError) {
      failRequest(requestError);
      return;
    }
    finishRequest();
  };

  return (
    <section className="grid gap-6 lg:grid-cols-2">
      <article className="space-y-4 rounded-xl border border-border bg-card p-6">
        <h2 className="text-xl font-semibold leading-tight">Auth API actions</h2>
        <div className="grid gap-3">
          <Button type="button" className="h-11" disabled={isLoading} onClick={handleRefreshToken}>
            Refresh token
          </Button>
          <Button type="button" variant="outline" className="h-11" disabled={isLoading} onClick={handleGetCurrentUser}>
            Get current user
          </Button>
          <div className="grid grid-cols-2 gap-3">
            <div className="space-y-2">
              <Label htmlFor="users-page">Page</Label>
              <Input
                id="users-page"
                value={listPage}
                onChange={(event) => setListPage(event.target.value.replace(/\D/g, ""))}
                className="h-11"
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="users-limit">Limit</Label>
              <Input
                id="users-limit"
                value={listLimit}
                onChange={(event) => setListLimit(event.target.value.replace(/\D/g, ""))}
                className="h-11"
              />
            </div>
          </div>
          <Button type="button" variant="outline" className="h-11" disabled={isLoading} onClick={handleGetUsers}>
            Get users list
          </Button>
        </div>
      </article>

      <article className="space-y-4 rounded-xl border border-border bg-card p-6">
        <h2 className="text-xl font-semibold leading-tight">2FA management</h2>
        <p className="rounded-lg border border-border bg-muted/40 p-3 text-sm text-muted-foreground">
          Temp token is taken automatically from current session.
        </p>
        <Button type="button" className="h-11 w-full" disabled={isLoading} onClick={handleEnable2FA}>
          Enable 2FA
        </Button>
        <div className="space-y-2">
          <Label htmlFor="disable-otp">OTP to disable 2FA</Label>
          <Input
            id="disable-otp"
            value={disableOtp}
            onChange={(event) => setDisableOtp(event.target.value.replace(/\D/g, ""))}
            maxLength={6}
            placeholder="123456"
            className="h-11"
          />
        </div>
        <Button type="button" variant="outline" className="h-11 w-full" disabled={isLoading} onClick={handleDisable2FA}>
          Disable 2FA
        </Button>
        {qrUri ? <img src={qrUri} alt="2FA QR code" className="mx-auto size-36 rounded-md border border-border bg-white p-2" /> : null}
      </article>

      {error ? <StatusMessage message={error} variant="error" /> : null}
      {message ? <StatusMessage message={message} variant="success" /> : null}

      {myProfile ? (
        <article className="rounded-xl border border-border bg-card p-6">
          <h3 className="mb-3 text-xl font-semibold leading-tight">/api/users/me response</h3>
          <pre className="overflow-x-auto rounded-lg bg-muted p-4 text-xs text-foreground">{JSON.stringify(myProfile, null, 2)}</pre>
        </article>
      ) : null}

      {usersResult ? (
        <article className="rounded-xl border border-border bg-card p-6">
          <h3 className="mb-3 text-xl font-semibold leading-tight">/api/users response</h3>
          <pre className="overflow-x-auto rounded-lg bg-muted p-4 text-xs text-foreground">{JSON.stringify(usersResult, null, 2)}</pre>
        </article>
      ) : null}
    </section>
  );
}
