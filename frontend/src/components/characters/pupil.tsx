import { useEffect, useRef, useState } from "react";

interface PupilProps {
  size?: number;
  maxDistance?: number;
  forceLookX?: number;
  forceLookY?: number;
}

export function Pupil({ size = 12, maxDistance = 5, forceLookX, forceLookY }: PupilProps) {
  const [mouseX, setMouseX] = useState<number>(0);
  const [mouseY, setMouseY] = useState<number>(0);
  const pupilRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleMouseMove = (event: MouseEvent) => {
      setMouseX(event.clientX);
      setMouseY(event.clientY);
    };

    window.addEventListener("mousemove", handleMouseMove);
    return () => window.removeEventListener("mousemove", handleMouseMove);
  }, []);

  const calculatePupilPosition = () => {
    if (!pupilRef.current) {
      return { x: 0, y: 0 };
    }
    if (forceLookX !== undefined && forceLookY !== undefined) {
      return { x: forceLookX, y: forceLookY };
    }

    const pupilRect = pupilRef.current.getBoundingClientRect();
    const pupilCenterX = pupilRect.left + pupilRect.width / 2;
    const pupilCenterY = pupilRect.top + pupilRect.height / 2;

    const deltaX = mouseX - pupilCenterX;
    const deltaY = mouseY - pupilCenterY;

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
      ref={pupilRef}
      className="rounded-full transition-transform duration-150 ease-in-out"
      style={{
        width: `${size}px`,
        height: `${size}px`,
        backgroundColor: "hsl(var(--character-black))",
        transform: `translate(${pupilPosition.x}px, ${pupilPosition.y}px)`,
      }}
    />
  );
}
