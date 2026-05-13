import type { ReactNode } from "react";

import { Sparkles } from "lucide-react";

import { AuthHeroPanel } from "@/components/auth/auth-hero-panel";

interface AuthLayoutProps {
  title: string;
  description: string;
  children: ReactNode;
  maskCharacters?: boolean;
}

export function AuthLayout({ title, description, children, maskCharacters = false }: AuthLayoutProps) {
  return (
    <main className="grid min-h-screen lg:grid-cols-2">
      <AuthHeroPanel maskCharacters={maskCharacters} />

      <section className="flex items-center justify-center bg-background p-8">
        <div className="w-full max-w-[420px] space-y-8">
          <div className="flex items-center justify-center gap-2 text-lg font-semibold lg:hidden">
            <div className="flex size-8 items-center justify-center rounded-lg bg-primary/10">
              <Sparkles className="size-4 text-primary" />
            </div>
            <span>Authen Author</span>
          </div>

          <header className="space-y-2 text-center">
            <h1 className="text-3xl font-bold leading-tight">{title}</h1>
            <p className="text-sm text-muted-foreground">{description}</p>
          </header>

          {children}
        </div>
      </section>
    </main>
  );
}
