import type { RefObject } from "react";

import { Pupil } from "@/components/characters/pupil";
import type { CharacterMotion } from "@/components/characters/scene-types";

interface OrangeCharacterProps {
  characterRef: RefObject<HTMLDivElement | null>;
  motion: CharacterMotion;
  maskMode?: boolean;
}

export function OrangeCharacter({ characterRef, motion, maskMode = false }: OrangeCharacterProps) {
  const faceLeft = maskMode ? 50 : 82 + motion.faceX;
  const faceTop = maskMode ? 85 : 90 + motion.faceY;

  return (
    <div
      ref={characterRef}
      className="absolute bottom-0 transition-all duration-700 ease-in-out"
      style={{
        left: "0px",
        width: "240px",
        height: "200px",
        zIndex: 3,
        backgroundColor: "hsl(var(--character-orange))",
        borderRadius: "120px 120px 0 0",
        transform: maskMode ? "skewX(0deg)" : `skewX(${motion.bodySkew}deg)`,
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
        <Pupil forceLookX={maskMode ? -5 : undefined} forceLookY={maskMode ? -4 : undefined} />
        <Pupil forceLookX={maskMode ? -5 : undefined} forceLookY={maskMode ? -4 : undefined} />
      </div>
    </div>
  );
}
