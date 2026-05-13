import { useEffect, useRef, useState } from "react";

interface EyeBallProps {
  size?: number;
  pupilSize?: number;
  maxDistance?: number;
  isBlinking?: boolean;
  forceLookX?: number;
  forceLookY?: number;
}

export function EyeBall({
  size = 18,
  pupilSize = 7,
  maxDistance = 5,
  isBlinking = false,
  forceLookX,
  forceLookY,
}: EyeBallProps) {
  const [mouseX, setMouseX] = useState<number>(0);
  const [mouseY, setMouseY] = useState<number>(0);
  const eyeRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleMouseMove = (event: MouseEvent) => {
      setMouseX(event.clientX);
      setMouseY(event.clientY);
    };

    window.addEventListener("mousemove", handleMouseMove);
    return () => window.removeEventListener("mousemove", handleMouseMove);
  }, []);

  const calculatePupilPosition = () => {
    if (!eyeRef.current) {
      return { x: 0, y: 0 };
    }
    if (forceLookX !== undefined && forceLookY !== undefined) {
      return { x: forceLookX, y: forceLookY };
    }

    const eyeRect = eyeRef.current.getBoundingClientRect();
    const eyeCenterX = eyeRect.left + eyeRect.width / 2;
    const eyeCenterY = eyeRect.top + eyeRect.height / 2;

    const deltaX = mouseX - eyeCenterX;
    const deltaY = mouseY - eyeCenterY;

    const distance = Math.min(Math.sqrt(deltaX ** 2 + deltaY ** 2), maxDistance);
    const angle = Math.atan2(deltaY, deltaX);

    return {
      x: Math.cos(angle) * distance,
      y: Math.sin(angle) * distance,
    };
  };

  const pupilPosition = calculatePupilPosition();

  return (
    <div
      ref={eyeRef}
      className="flex items-center justify-center overflow-hidden rounded-full transition-all duration-150 ease-in-out"
      style={{
        width: `${size}px`,
        height: isBlinking ? "2px" : `${size}px`,
        backgroundColor: "hsl(var(--primary-foreground))",
      }}
    >
      {!isBlinking ? (
        <div
          className="rounded-full transition-transform duration-150 ease-in-out"
          style={{
            width: `${pupilSize}px`,
            height: `${pupilSize}px`,
            backgroundColor: "hsl(var(--character-black))",
            transform: `translate(${pupilPosition.x}px, ${pupilPosition.y}px)`,
          }}
        />
      ) : null}
    </div>
  );
}
