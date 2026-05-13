import type { RefObject } from "react";

import { Pupil } from "@/components/characters/pupil";
import type { CharacterMotion } from "@/components/characters/scene-types";

interface YellowCharacterProps {
  characterRef: RefObject<HTMLDivElement | null>;
  motion: CharacterMotion;
  maskMode?: boolean;
}

export function YellowCharacter({ characterRef, motion, maskMode = false }: YellowCharacterProps) {
  const faceLeft = maskMode ? 20 : 52 + motion.faceX;
  const faceTop = maskMode ? 35 : 40 + motion.faceY;
  const mouthLeft = maskMode ? 10 : 40 + motion.faceX;
  const mouthTop = maskMode ? 88 : 88 + motion.faceY;

  return (
    <div
      ref={characterRef}
      className="absolute bottom-0 transition-all duration-700 ease-in-out"
      style={{
        left: "310px",
        width: "140px",
        height: "230px",
        zIndex: 4,
        backgroundColor: "hsl(var(--character-yellow))",
        borderRadius: "70px 70px 0 0",
        transform: maskMode ? "skewX(0deg)" : `skewX(${motion.bodySkew}deg)`,
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
        <Pupil forceLookX={maskMode ? -5 : undefined} forceLookY={maskMode ? -4 : undefined} />
        <Pupil forceLookX={maskMode ? -5 : undefined} forceLookY={maskMode ? -4 : undefined} />
      </div>
      <div
        className="absolute h-1 w-20 rounded-full transition-all duration-300 ease-in-out"
        style={{
          left: `${mouthLeft}px`,
          top: `${mouthTop}px`,
          backgroundColor: "hsl(var(--character-black))",
        }}
      />
    </div>
  );
}
