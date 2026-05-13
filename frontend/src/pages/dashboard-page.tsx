import { useState } from "react";

import { ApiPlayground } from "@/components/dashboard/api-playground";
import { DashboardAnimatedPanel } from "@/components/dashboard/dashboard-animated-panel";
import { DashboardHeader } from "@/components/dashboard/dashboard-header";
import { clearSession, getUserInfo } from "@/lib/auth-storage";
import type { UserResponse } from "@/types/auth";

interface DashboardPageProps {
  onGoLogin: () => void;
}

export function DashboardPage({ onGoLogin }: DashboardPageProps) {
  const [user, setUser] = useState<UserResponse | null>(getUserInfo());

  const handleLogout = () => {
    clearSession();
    onGoLogin();
  };

  return (
    <main className="min-h-screen bg-background p-4 md:p-8">
      <div className="mx-auto flex w-full max-w-6xl flex-col gap-6">
        <DashboardHeader user={user} onLogout={handleLogout} />
        <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
          <div className="lg:col-span-1">
            <DashboardAnimatedPanel />
          </div>
          <div className="lg:col-span-2">
            <ApiPlayground onUserUpdated={setUser} />
          </div>
        </div>
      </div>
    </main>
  );
}
