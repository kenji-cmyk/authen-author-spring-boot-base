import type { RefObject } from "react";

import { EyeBall } from "@/components/characters/eye-ball";
import type { CharacterMotion } from "@/components/characters/scene-types";

interface BlackCharacterProps {
  characterRef: RefObject<HTMLDivElement | null>;
  motion: CharacterMotion;
  isBlinking: boolean;
  maskMode?: boolean;
}

export function BlackCharacter({ characterRef, motion, isBlinking, maskMode = false }: BlackCharacterProps) {
  const faceLeft = maskMode ? 12 : 26 + motion.faceX;
  const faceTop = maskMode ? 24 : 32 + motion.faceY;

  return (
    <div
      ref={characterRef}
      className="absolute bottom-0 transition-all duration-700 ease-in-out"
      style={{
        left: "240px",
        width: "120px",
        height: "310px",
        backgroundColor: "hsl(var(--character-black))",
        borderRadius: "8px 8px 0 0",
        zIndex: 2,
        transform: maskMode ? `skewX(${motion.bodySkew + 8}deg) translateX(16px)` : `skewX(${motion.bodySkew}deg)`,
        transformOrigin: "bottom center",
      }}
    >
      <div
        className="absolute flex gap-6 transition-all duration-300 ease-in-out"
        style={{
          left: `${faceLeft}px`,
          top: `${faceTop}px`,
        }}
      >
        <EyeBall
          size={16}
          pupilSize={6}
          maxDistance={4}
          isBlinking={isBlinking}
          forceLookX={maskMode ? 4 : undefined}
          forceLookY={maskMode ? -4 : undefined}
        />
        <EyeBall
          size={16}
          pupilSize={6}
          maxDistance={4}
          isBlinking={isBlinking}
          forceLookX={maskMode ? 4 : undefined}
          forceLookY={maskMode ? -4 : undefined}
        />
      </div>
    </div>
  );
}
