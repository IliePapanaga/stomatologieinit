import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function calculateMatchScore(
  pro: {
    specialty: string;
    distanceMiles: number;
    travelRadius: number;
    rating: number;
    id: string;
  },
  posting: { specialty: string; subcategory: string; commutingRadius: number },
): number {
  let score = 0;

  // Base match for exact role
  if (pro.specialty === posting.specialty) score += 40;

  // Distance match (is the pro willing to travel here?)
  if (pro.travelRadius >= pro.distanceMiles) score += 30;
  else if (pro.travelRadius + 10 >= pro.distanceMiles) score += 15;

  // Rating match
  score += Math.min(25, (pro.rating / 5) * 25);

  // Add a small deterministic jitter based on ID so not everyone has the exact same score
  const jitter = pro.id.charCodeAt(pro.id.length - 1) % 5;
  score += jitter;

  return Math.min(100, Math.floor(score));
}
