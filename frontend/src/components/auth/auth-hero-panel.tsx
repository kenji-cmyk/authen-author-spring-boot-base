import { ShieldCheck, Sparkles } from "lucide-react";

export function AuthHeroPanel() {
  return (
    <aside className="relative hidden lg:flex flex-col justify-between overflow-hidden bg-gradient-to-br from-primary/90 via-primary to-primary/80 p-12 text-primary-foreground">
      <div className="relative z-10 flex items-center gap-3">
        <div className="flex size-9 items-center justify-center rounded-lg bg-primary-foreground/10">
          <Sparkles className="size-5" />
        </div>
        <p className="text-lg font-semibold leading-tight">Authen Author</p>
      </div>

      <div className="relative z-10 space-y-4">
        <h1 className="text-3xl font-bold leading-tight">Secure access with modern authentication</h1>
        <p className="max-w-md text-base text-primary-foreground/80">
          Login, verify 2FA and manage API flows in one clear interface designed for fast testing.
        </p>
      </div>

      <div className="relative z-10 flex items-center gap-3 rounded-xl border border-primary-foreground/20 bg-primary-foreground/10 p-4">
        <ShieldCheck className="size-5" />
        <p className="text-sm text-primary-foreground/90">Use one-time passwords for stronger protection.</p>
      </div>

      <div className="absolute inset-0 bg-primary-foreground/5" />
      <div className="absolute -right-8 top-24 size-48 rounded-full bg-primary-foreground/15 blur-3xl" />
      <div className="absolute -left-8 bottom-20 size-56 rounded-full bg-primary-foreground/10 blur-3xl" />
    </aside>
  );
}
