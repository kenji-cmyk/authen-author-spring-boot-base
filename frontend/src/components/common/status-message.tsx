import { cn } from "@/lib/utils";

interface StatusMessageProps {
  message: string;
  variant?: "error" | "success" | "info";
}

const classesByVariant: Record<NonNullable<StatusMessageProps["variant"]>, string> = {
  error: "border-destructive/30 bg-destructive/10 text-destructive",
  success: "border-primary/30 bg-primary/10 text-foreground",
  info: "border-border bg-muted/50 text-muted-foreground",
};

export function StatusMessage({ message, variant = "info" }: StatusMessageProps) {
  return (
    <div className={cn("rounded-lg border p-3 text-sm", classesByVariant[variant])} role="status" aria-live="polite">
      {message}
    </div>
  );
}
