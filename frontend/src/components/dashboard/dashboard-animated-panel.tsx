import { useEffect, useMemo, useRef, useState } from "react";

import { OrangeCharacter } from "@/components/characters/orange-character";

export function DashboardAnimatedPanel() {
  const [mouseX, setMouseX] = useState<number>(0);
  const [mouseY, setMouseY] = useState<number>(0);
  const orangeRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleMouseMove = (event: MouseEvent) => {
      setMouseX(event.clientX);
      setMouseY(event.clientY);
    };

    window.addEventListener("mousemove", handleMouseMove);
    return () => window.removeEventListener("mousemove", handleMouseMove);
  }, []);

  const orangeMotion = useMemo(() => {
    if (!orangeRef.current) {
      return { faceX: 0, faceY: 0, bodySkew: 0 };
    }

    const rect = orangeRef.current.getBoundingClientRect();
    const centerX = rect.left + rect.width / 2;
    const centerY = rect.top + rect.height / 2;
    const deltaX = mouseX - centerX;
    const deltaY = mouseY - centerY;

    return {
      faceX: Math.max(-12, Math.min(12, deltaX / 18)),
      faceY: Math.max(-8, Math.min(8, deltaY / 24)),
      bodySkew: Math.max(-6, Math.min(6, -deltaX / 100)),
    };
  }, [mouseX, mouseY]);

  return (
    <section className="h-full overflow-hidden rounded-xl border border-border bg-card p-6">
      <div className="space-y-2">
        <h2 className="text-2xl font-semibold leading-tight">Orange assistant</h2>
        <p className="text-sm text-muted-foreground">
          Character animation remains on dashboard with a cleaner single-character layout.
        </p>
      </div>
      <div className="mt-6 flex min-h-64 items-end justify-center rounded-lg bg-muted/40 p-4">
        <div className="relative h-56 w-60 overflow-hidden">
          <OrangeCharacter characterRef={orangeRef} motion={orangeMotion} />
        </div>
      </div>
    </section>
  );
}
