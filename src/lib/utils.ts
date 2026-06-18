import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}

export function calculateMatchScore(pro: { specialty: string; specialties: string[]; distanceMiles: number; commutingRadius: number; rating: number; id: string }, posting: { specialty: string; subcategory: string; commutingRadius: number }): number {
  let score = 0;
  
  // Base match for exact role
  if (pro.specialty === posting.specialty) score += 40;
  
  // Subspecialty match
  if (pro.specialties.includes(posting.subcategory)) score += 20;
  
  // Distance match (is the pro willing to travel here?)
  if (pro.commutingRadius >= pro.distanceMiles) score += 20;
  else if (pro.commutingRadius + 10 >= pro.distanceMiles) score += 10;
  
  // Rating match
  score += Math.min(15, (pro.rating / 5) * 15);
  
  // Add a small deterministic jitter based on ID so not everyone has the exact same score
  const jitter = pro.id.charCodeAt(pro.id.length - 1) % 5;
  score += jitter;

  return Math.min(100, Math.floor(score));
}
