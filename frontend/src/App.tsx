import { useEffect, useMemo, useState } from "react";

import { DashboardPage } from "@/pages/dashboard-page";
import { LoginPage } from "@/pages/login-page";
import { Verify2FAPage } from "@/pages/verify-2fa-page";
import { getAccessToken, getTempToken } from "@/lib/auth-storage";

type RoutePath = "/" | "/login" | "/verify-2fa" | "/dashboard";

function normalizePath(path: string): RoutePath {
  if (path === "/login" || path === "/verify-2fa" || path === "/dashboard") {
    return path;
  }
  return "/";
}

function navigate(path: Exclude<RoutePath, "/">, replace = false): void {
  if (replace) {
    window.history.replaceState({}, "", path);
  } else {
    window.history.pushState({}, "", path);
  }
  window.dispatchEvent(new PopStateEvent("popstate"));
}

function resolveDefaultPath(): Exclude<RoutePath, "/"> {
  if (getAccessToken()) {
    return "/dashboard";
  }
  return "/login";
}

function resolveProtectedPath(path: RoutePath): Exclude<RoutePath, "/"> {
  const accessToken = getAccessToken();
  const tempToken = getTempToken();

  if (path === "/dashboard" && !accessToken) {
    return "/login";
  }
  if (path === "/verify-2fa" && !tempToken) {
    return "/login";
  }
  if (path === "/login" && accessToken) {
    return "/dashboard";
  }
  if (path === "/") {
    return resolveDefaultPath();
  }
  return path;
}

// Handle OAuth2 redirect synchronously before App mounts
const params = new URLSearchParams(window.location.search);
const token = params.get("token");
if (token) {
  import("@/lib/auth-storage").then(({ setAccessToken }) => {
    setAccessToken(token);
    // Reload to clear the token from the URL and apply the auth state cleanly
    window.location.replace(window.location.pathname);
  });
}

function useCurrentPath() {
  const [path, setPath] = useState<RoutePath>(normalizePath(window.location.pathname));

  useEffect(() => {
    const onPathChange = () => {
      setPath(normalizePath(window.location.pathname));
    };

    window.addEventListener("popstate", onPathChange);
    return () => window.removeEventListener("popstate", onPathChange);
  }, []);

  return path;
}

function App() {
  const path = useCurrentPath();
  const finalPath = useMemo(() => resolveProtectedPath(path), [path]);

  useEffect(() => {
    if (normalizePath(window.location.pathname) !== finalPath) {
      navigate(finalPath, true);
    }
  }, [finalPath]);

  if (finalPath === "/verify-2fa") {
    return <Verify2FAPage onGoLogin={() => navigate("/login")} onGoDashboard={() => navigate("/dashboard")} />;
  }

  if (finalPath === "/dashboard") {
    return <DashboardPage onGoLogin={() => navigate("/login")} />;
  }

  return <LoginPage onGoVerify2FA={() => navigate("/verify-2fa")} onGoDashboard={() => navigate("/dashboard")} />;
}

export default App;
