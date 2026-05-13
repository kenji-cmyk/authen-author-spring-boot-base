import type { RefObject } from "react";

import { EyeBall } from "@/components/characters/eye-ball";
import type { CharacterMotion } from "@/components/characters/scene-types";

interface PurpleCharacterProps {
  characterRef: RefObject<HTMLDivElement | null>;
  motion: CharacterMotion;
  isBlinking: boolean;
  maskMode?: boolean;
}

export function PurpleCharacter({ characterRef, motion, isBlinking, maskMode = false }: PurpleCharacterProps) {
  const faceLeft = maskMode ? 24 : 45 + motion.faceX;
  const faceTop = maskMode ? 28 : 40 + motion.faceY;

  return (
    <div
      ref={characterRef}
      className="absolute bottom-0 transition-all duration-700 ease-in-out"
      style={{
        left: "70px",
        width: "180px",
        height: maskMode ? "430px" : "400px",
        backgroundColor: "hsl(var(--character-purple))",
        borderRadius: "10px 10px 0 0",
        zIndex: 1,
        transform: maskMode ? `skewX(${motion.bodySkew - 12}deg) translateX(40px)` : `skewX(${motion.bodySkew}deg)`,
        transformOrigin: "bottom center",
      }}
    >
      <div
        className="absolute flex gap-8 transition-all duration-300 ease-in-out"
        style={{
          left: `${faceLeft}px`,
          top: `${faceTop}px`,
        }}
      >
        <EyeBall isBlinking={isBlinking} forceLookX={maskMode ? -5 : undefined} forceLookY={maskMode ? -4 : undefined} />
        <EyeBall isBlinking={isBlinking} forceLookX={maskMode ? -5 : undefined} forceLookY={maskMode ? -4 : undefined} />
      </div>
    </div>
  );
}
