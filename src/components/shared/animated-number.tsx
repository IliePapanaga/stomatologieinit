import { motion, useMotionValue, useTransform, animate } from "motion/react";
import { useEffect } from "react";

interface Props {
  value: number;
  duration?: number;
  format?: (n: number) => string;
}

export function AnimatedNumber({ value, duration = 0.9, format }: Props) {
  const mv = useMotionValue(0);
  const rounded = useTransform(mv, (latest) => (format ? format(latest) : Math.round(latest).toString()));

  useEffect(() => {
    const controls = animate(mv, value, { duration, ease: "easeOut" });
    return () => controls.stop();
  }, [value, duration, mv]);

  return <motion.span>{rounded}</motion.span>;
}
