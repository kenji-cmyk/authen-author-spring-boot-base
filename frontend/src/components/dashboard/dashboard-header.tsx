import { LogOut, Shield, User } from "lucide-react";

import { Button } from "@/components/ui/button";
import type { UserResponse } from "@/types/auth";

interface DashboardHeaderProps {
  user: UserResponse | null;
  onLogout: () => void;
}

export function DashboardHeader({ user, onLogout }: DashboardHeaderProps) {
  return (
    <header className="flex flex-col gap-4 rounded-xl border border-border bg-card p-6 md:flex-row md:items-center md:justify-between">
      <div className="space-y-1">
        <h1 className="text-3xl font-bold leading-tight">Authentication Dashboard</h1>
        <p className="text-sm text-muted-foreground">Manage login, 2FA and protected API calls.</p>
      </div>

      <div className="flex flex-wrap items-center gap-3">
        <div className="flex items-center gap-2 rounded-lg border border-border bg-background px-3 py-2 text-sm">
          <User className="size-4 text-muted-foreground" />
          <span>{user?.username ?? "Unknown user"}</span>
        </div>
        <div className="flex items-center gap-2 rounded-lg border border-border bg-background px-3 py-2 text-sm">
          <Shield className="size-4 text-muted-foreground" />
          <span>{user?.mfaEnabled ? "2FA enabled" : "2FA disabled"}</span>
        </div>
        <Button type="button" variant="outline" className="h-10" onClick={onLogout}>
          <LogOut className="mr-2 size-4" />
          Logout
        </Button>
      </div>
    </header>
  );
}
