import { useState, useMemo } from "react";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetDescription } from "@/components/ui/sheet";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Star, MapPin, Briefcase } from "lucide-react";
import { JobPosting, Professional } from "@/lib/types/mdd";
import { mockProfessionals } from "@/lib/mock";
import { ProfessionalProfileSheet } from "@/components/practice/professional-profile-sheet";
import { useUpdatePosting } from "@/lib/hooks/postings";
import { toast } from "sonner";

interface CandidatesSheetProps {
  posting: JobPosting | null;
  mode?: "candidates" | "hired";
  onOpenChange: (open: boolean) => void;
}

export function CandidatesSheet({ posting, mode = "candidates", onOpenChange }: CandidatesSheetProps) {
  const [selectedPro, setSelectedPro] = useState<Professional | null>(null);
  const update = useUpdatePosting();

  // Deterministically pick a few professionals as candidates based on posting ID
  const candidates = useMemo(() => {
    if (!posting) return [];
    const seed = posting.id.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
    // Filter pros that match specialty if possible, else take all
    let pool = mockProfessionals.filter(p => p.specialty === posting.specialty);
    if (pool.length === 0) pool = mockProfessionals;
    
    // Pick exactly 3 to 6 candidates
    const count = (seed % 4) + 3;
    const shuffled = [...pool].sort((a, b) => {
      const aSeed = a.id.charCodeAt(a.id.length - 1);
      const bSeed = b.id.charCodeAt(b.id.length - 1);
      return (aSeed + seed) % 2 - (bSeed + seed) % 2;
    });
    
    const allGeneratedCandidates = shuffled.slice(0, count);
    const hiredSet = new Set(posting.hiredCandidateIds || []);
    
    if (mode === "hired") {
      // Return ALL hired pros (even if they weren't in the auto-generated list, though they usually are)
      return mockProfessionals.filter(p => hiredSet.has(p.id));
    } else {
      // Exclude hired pros from candidates
      return allGeneratedCandidates.filter(p => !hiredSet.has(p.id));
    }
  }, [posting, mode]);

  return (
    <>
      <Sheet open={!!posting} onOpenChange={onOpenChange}>
        <SheetContent className="flex w-full flex-col border-l-border/60 bg-background/95 p-0 backdrop-blur-xl sm:max-w-md">
          <SheetHeader className="border-b border-border/60 bg-muted/30 p-6">
            <div className="flex items-center justify-between">
              <SheetTitle>{mode === "candidates" ? "Candidates" : "Hired Staff"}</SheetTitle>
              {posting && mode === "candidates" && (
                <Badge variant={(posting.workingSpaces - (posting.hiredCandidateIds?.length || 0)) > 0 ? "outline" : "secondary"} className="bg-background">
                  {posting.workingSpaces - (posting.hiredCandidateIds?.length || 0)} spots left
                </Badge>
              )}
            </div>
            <SheetDescription>
              Professionals who have {mode === "candidates" ? "applied to" : "been hired for"}: <span className="font-semibold text-foreground">{posting?.title || "this role"}</span>
            </SheetDescription>
          </SheetHeader>

          <div className="flex-1 overflow-y-auto p-4 space-y-3">
            {candidates.map((pro) => {
              const initials = `${pro.firstName[0]}${pro.lastName[0]}`;
              const matchScore = Math.floor(75 + (pro.rating * 5) + (pro.id.charCodeAt(0) % 10)); // random-ish 75-99%
              
              return (
                <div 
                  key={pro.id} 
                  className="group cursor-pointer rounded-xl border border-border/60 bg-card p-4 transition-all hover:border-primary/40 hover:shadow-sm"
                  onClick={() => setSelectedPro(pro)}
                >
                  <div className="flex items-start gap-4">
                    <Avatar className="h-12 w-12 ring-2 ring-primary/10">
                      <AvatarFallback className="bg-gradient-brand text-primary-foreground font-semibold">
                        {initials}
                      </AvatarFallback>
                    </Avatar>
                    <div className="flex-1 min-w-0">
                      <div className="flex items-center justify-between">
                        <h4 className="font-semibold truncate">
                          {pro.firstName} {pro.lastName}
                        </h4>
                        <span className="text-sm font-bold text-emerald-600">{Math.min(99, matchScore)}% Match</span>
                      </div>
                      <p className="text-xs text-muted-foreground mt-0.5 truncate">{pro.specialty}</p>
                      
                      <div className="mt-2 flex flex-wrap items-center gap-2 text-[11px]">
                        <span className="flex items-center gap-1 bg-amber-500/10 text-amber-600 px-1.5 py-0.5 rounded-sm">
                          <Star className="h-3 w-3 fill-amber-500" />
                          {pro.rating.toFixed(1)}
                        </span>
                        <span className="flex items-center gap-1 text-muted-foreground">
                          <Briefcase className="h-3 w-3" />
                          {pro.distanceMiles + 2} yrs exp
                        </span>
                        <span className="flex items-center gap-1 text-muted-foreground">
                          <MapPin className="h-3 w-3" />
                          {pro.distanceMiles} mi
                        </span>
                      </div>
                    </div>
                  </div>
                  {mode === "candidates" && (
                    <div className="mt-4 flex items-center gap-2">
                      <Button 
                        size="sm" 
                        className="w-full bg-gradient-brand text-primary-foreground hover:opacity-95"
                        disabled={!posting || (posting.workingSpaces - (posting.hiredCandidateIds?.length || 0)) <= 0}
                        onClick={(e) => {
                          e.stopPropagation();
                          const spotsLeft = (posting?.workingSpaces ?? 0) - (posting?.hiredCandidateIds?.length || 0);
                          if (posting && spotsLeft > 0) {
                            const hired = [...(posting.hiredCandidateIds || []), pro.id];
                            update.mutate({
                              id: posting.id,
                              updates: { 
                                hiredCandidateIds: hired,
                                ...(hired.length === posting.workingSpaces ? { status: "Filled" } : {})
                              }
                            });
                            toast.success(`${pro.firstName} hired!`, { description: `Spots remaining: ${spotsLeft - 1}` });
                          }
                        }}
                      >
                        Hire candidate
                      </Button>
                    </div>
                  )}
                </div>
              );
            })}
            
            {candidates.length === 0 && (
              <div className="p-8 text-center text-muted-foreground">
                <p>{mode === "candidates" ? "No candidates available." : "No one has been hired yet."}</p>
              </div>
            )}
          </div>
        </SheetContent>
      </Sheet>

      <ProfessionalProfileSheet 
        pro={selectedPro} 
        onOpenChange={(open) => !open && setSelectedPro(null)} 
      />
    </>
  );
}
