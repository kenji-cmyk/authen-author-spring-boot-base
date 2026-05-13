import { type RefObject, useEffect, useMemo, useRef, useState } from "react";

import { cn } from "@/lib/utils";
import { BlackCharacter } from "@/components/characters/black-character";
import { OrangeCharacter } from "@/components/characters/orange-character";
import { PurpleCharacter } from "@/components/characters/purple-character";
import type { CharacterMotion } from "@/components/characters/scene-types";
import { YellowCharacter } from "@/components/characters/yellow-character";

interface AnimatedCharactersSceneProps {
  className?: string;
  maskMode?: boolean;
}

function useBlink(intervalMs: number) {
  const [isBlinking, setIsBlinking] = useState<boolean>(false);

  useEffect(() => {
    const intervalId = window.setInterval(() => {
      setIsBlinking(true);
      window.setTimeout(() => setIsBlinking(false), 150);
    }, intervalMs);

    return () => window.clearInterval(intervalId);
  }, [intervalMs]);

  return isBlinking;
}

function calculateMotion(
  ref: RefObject<HTMLDivElement | null>,
  mouseX: number,
  mouseY: number,
): CharacterMotion {
  if (!ref.current) {
    return { faceX: 0, faceY: 0, bodySkew: 0 };
  }

  const rect = ref.current.getBoundingClientRect();
  const centerX = rect.left + rect.width / 2;
  const centerY = rect.top + rect.height / 3;

  const deltaX = mouseX - centerX;
  const deltaY = mouseY - centerY;

  return {
    faceX: Math.max(-15, Math.min(15, deltaX / 20)),
    faceY: Math.max(-10, Math.min(10, deltaY / 30)),
    bodySkew: Math.max(-6, Math.min(6, -deltaX / 120)),
  };
}

export function AnimatedCharactersScene({ className, maskMode = false }: AnimatedCharactersSceneProps) {
  const [mouseX, setMouseX] = useState<number>(0);
  const [mouseY, setMouseY] = useState<number>(0);

  const purpleRef = useRef<HTMLDivElement>(null);
  const blackRef = useRef<HTMLDivElement>(null);
  const yellowRef = useRef<HTMLDivElement>(null);
  const orangeRef = useRef<HTMLDivElement>(null);

  const purpleBlink = useBlink(4200);
  const blackBlink = useBlink(3900);

  useEffect(() => {
    const handleMouseMove = (event: MouseEvent) => {
      setMouseX(event.clientX);
      setMouseY(event.clientY);
    };

    window.addEventListener("mousemove", handleMouseMove);
    return () => window.removeEventListener("mousemove", handleMouseMove);
  }, []);

  const purpleMotion = useMemo(() => calculateMotion(purpleRef, mouseX, mouseY), [mouseX, mouseY]);
  const blackMotion = useMemo(() => calculateMotion(blackRef, mouseX, mouseY), [mouseX, mouseY]);
  const yellowMotion = useMemo(() => calculateMotion(yellowRef, mouseX, mouseY), [mouseX, mouseY]);
  const orangeMotion = useMemo(() => calculateMotion(orangeRef, mouseX, mouseY), [mouseX, mouseY]);

  return (
    <div className={cn("relative max-w-full overflow-hidden", className)} style={{ width: "550px", height: "400px" }}>
      <PurpleCharacter characterRef={purpleRef} motion={purpleMotion} isBlinking={purpleBlink} maskMode={maskMode} />
      <BlackCharacter characterRef={blackRef} motion={blackMotion} isBlinking={blackBlink} maskMode={maskMode} />
      <OrangeCharacter characterRef={orangeRef} motion={orangeMotion} maskMode={maskMode} />
      <YellowCharacter characterRef={yellowRef} motion={yellowMotion} maskMode={maskMode} />
    </div>
  );
}
