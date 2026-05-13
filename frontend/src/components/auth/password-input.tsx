import { useState } from "react";

import { Eye, EyeOff } from "lucide-react";

import { Input } from "@/components/ui/input";

interface PasswordInputProps {
  id: string;
  value: string;
  placeholder?: string;
  onChange: (value: string) => void;
}

export function PasswordInput({ id, value, placeholder, onChange }: PasswordInputProps) {
  const [showPassword, setShowPassword] = useState<boolean>(false);

  return (
    <div className="relative">
      <Input
        id={id}
        type={showPassword ? "text" : "password"}
        value={value}
        placeholder={placeholder}
        onChange={(event) => onChange(event.target.value)}
        className="h-12 border-border/60 bg-background pr-10 focus:border-primary"
        required
      />
      <button
        type="button"
        className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground transition-colors hover:text-foreground focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring"
        onClick={() => setShowPassword((previous) => !previous)}
        aria-label={showPassword ? "Hide password" : "Show password"}
      >
        {showPassword ? <EyeOff className="size-5" /> : <Eye className="size-5" />}
      </button>
    </div>
  );
}
