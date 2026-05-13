import { AuthLayout } from "@/components/auth/auth-layout";
import { Verify2FAForm } from "@/components/auth/verify-2fa-form";
import { Button } from "@/components/ui/button";
import { getMfaQrUri } from "@/lib/auth-storage";

interface Verify2FAPageProps {
  onGoLogin: () => void;
  onGoDashboard: () => void;
}

export function Verify2FAPage({ onGoLogin, onGoDashboard }: Verify2FAPageProps) {
  const secretImageUri = getMfaQrUri();

  return (
    <AuthLayout title="2FA verification" description="Enter 6-digit OTP from your authenticator app">
      <div className="space-y-5 rounded-xl border border-border bg-card p-6">
        {secretImageUri ? (
          <div className="rounded-lg border border-border bg-muted/50 p-4 text-center">
            <p className="mb-3 text-sm text-muted-foreground">Scan this QR code before confirming OTP</p>
            <img src={secretImageUri} alt="2FA QR code" className="mx-auto size-36 rounded-md bg-white p-2" />
          </div>
        ) : null}
        <Verify2FAForm onSuccess={onGoDashboard} />
        <Button type="button" variant="link" className="w-full" onClick={onGoLogin}>
          Back to login
        </Button>
      </div>
    </AuthLayout>
  );
}
