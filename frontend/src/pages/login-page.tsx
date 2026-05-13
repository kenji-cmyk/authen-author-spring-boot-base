import { useState } from "react";

import { AuthLayout } from "@/components/auth/auth-layout";
import { LoginForm } from "@/components/auth/login-form";
import { RegisterForm } from "@/components/auth/register-form";
import { Button } from "@/components/ui/button";
import { cn } from "@/lib/utils";

interface LoginPageProps {
  onGoVerify2FA: () => void;
  onGoDashboard: () => void;
}

type AuthTab = "login" | "register";

export function LoginPage({ onGoVerify2FA, onGoDashboard }: LoginPageProps) {
  const [activeTab, setActiveTab] = useState<AuthTab>("login");

  return (
    <AuthLayout title="Welcome back" description="Choose a flow to continue authentication">
      <div className="space-y-5 rounded-xl border border-border bg-card p-6">
        <div className="grid grid-cols-2 rounded-lg bg-muted p-1">
          <Button
            type="button"
            variant="ghost"
            className={cn("h-10", activeTab === "login" ? "bg-background text-foreground shadow-sm" : "text-muted-foreground")}
            onClick={() => setActiveTab("login")}
          >
            Login
          </Button>
          <Button
            type="button"
            variant="ghost"
            className={cn(
              "h-10",
              activeTab === "register" ? "bg-background text-foreground shadow-sm" : "text-muted-foreground",
            )}
            onClick={() => setActiveTab("register")}
          >
            Register
          </Button>
        </div>

        {activeTab === "login" ? (
          <LoginForm onNeed2FA={onGoVerify2FA} onSuccess={onGoDashboard} />
        ) : (
          <RegisterForm onNeed2FA={onGoVerify2FA} />
        )}
      </div>
    </AuthLayout>
  );
}
