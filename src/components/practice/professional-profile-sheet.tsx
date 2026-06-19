import { useState, useMemo } from "react";
import {
  Sheet,
  SheetContent,
  SheetHeader,
  SheetTitle,
  SheetDescription,
} from "@/components/ui/sheet";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import {
  Star,
  ShieldCheck,
  Mail,
  Phone,
  MapPin,
  Award,
  CheckCircle2,
  MessageSquare,
  Plus,
} from "lucide-react";
import { Professional } from "@/lib/types/mdd";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";
import { useAppStore } from "@/lib/store/app-store";
import { mockActivity } from "@/lib/mock";

interface ProfessionalProfileSheetProps {
  pro: Professional | null;
  onOpenChange: (open: boolean) => void;
  defaultTab?: "overview" | "reviews";
}

// Generate deterministic mock reviews
const generateMockReviews = (id: string) => {
  const seed = id.split("").reduce((acc, char) => acc + char.charCodeAt(0), 0);
  const count = (seed % 5) + 3; // 3 to 7 reviews
  const authors = ["Dr. Smith", "Brightside Dental", "Smile Clinic", "Dr. Lee", "Family Dental"];
  const comments = [
    "Great work, very professional and punctual.",
    "Patients loved them. Highly recommended.",
    "Good experience overall.",
    "Very knowledgeable and efficient.",
    "Showed up on time and integrated well with our team.",
  ];

  return Array.from({ length: count }).map((_, i) => ({
    id: `rev_${id}_${i}`,
    author: authors[(seed + i) % authors.length],
    rating: 4 + ((seed + i) % 2), // 4 or 5 stars
    text: comments[(seed + i) % comments.length],
    date: new Date(Date.now() - i * 86400000 * 5).toLocaleDateString(),
  }));
};

export function ProfessionalProfileSheet({
  pro,
  onOpenChange,
  defaultTab = "overview",
}: ProfessionalProfileSheetProps) {
  const [tab, setTab] = useState<"overview" | "reviews">(defaultTab);
  const [reviewOpen, setReviewOpen] = useState(false);
  const [newReview, setNewReview] = useState("");
  const [newRating, setNewRating] = useState(5);

  const { practiceReviews, addPracticeReview, bannedProfessionalIds, banProfessional, unbanProfessional } = useAppStore();
  const { t } = useTranslation();
  
  const proReviews = useMemo(
    () => (practiceReviews || []).filter((r) => r.professionalId === pro?.id),
    [practiceReviews, pro],
  );

  const mockReviews = useMemo(() => (pro ? generateMockReviews(pro.id) : []), [pro]);
  const localReviews = useMemo(() => [...proReviews, ...mockReviews], [proReviews, mockReviews]);

  const hasWorked = useMemo(() => {
    if (!pro) return false;
    const { jobPostings } = useAppStore.getState();
    const hiredAnywhere = jobPostings.some((p) => p.hiredCandidateIds?.includes(pro.id));
    const inActivity = mockActivity.some(
      (a) => a.kind === "CheckIn" && a.professionalId === pro.id,
    );
    // For demo purposes, assume 80% of professionals have worked for you before so you can test reviews
    const isDemoWorker = pro.id.charCodeAt(pro.id.length - 1) % 5 !== 0;
    return hiredAnywhere || inActivity || isDemoWorker;
  }, [pro]);
  const existingReview = proReviews[0];
  const canReview = hasWorked;

  const handleOpenReview = () => {
    if (reviewOpen) {
      setReviewOpen(false);
    } else {
      if (existingReview) {
        setNewReview(existingReview.text);
        setNewRating(existingReview.rating);
      } else {
        setNewReview("");
        setNewRating(5);
      }
      setReviewOpen(true);
    }
  };

  const isBanned = pro ? bannedProfessionalIds.includes(pro.id) : false;

  const handleToggleBan = () => {
    if (!pro) return;
    if (isBanned) {
      unbanProfessional(pro.id);
      toast.success(t("unban_professional"));
    } else {
      banProfessional(pro.id);
      toast.success(t("ban_professional"));
    }
  };

  if (!pro) return null;

  const initials = `${pro.firstName[0] || ""}${pro.lastName[0] || ""}`;

  const submitReview = () => {
    if (!newReview.trim()) return;
    addPracticeReview({
      professionalId: pro.id,
      rating: newRating,
      text: newReview,
    });
    toast.success("Review submitted successfully");
    setReviewOpen(false);
    setNewReview("");
    setNewRating(5);
  };

  return (
    <Sheet open={!!pro} onOpenChange={onOpenChange}>
      <SheetContent className="flex w-full flex-col border-l-border/60 bg-background/95 p-0 backdrop-blur-xl sm:max-w-lg overflow-hidden">
        {/* Header Profile Section */}
        <div className="bg-muted/30 p-6 pt-12">
          <div className="flex items-start gap-4">
            <Avatar className="h-16 w-16 ring-4 ring-background shadow-sm">
              <AvatarFallback className="bg-gradient-brand text-xl font-bold text-primary-foreground">
                {initials}
              </AvatarFallback>
            </Avatar>
            <div className="flex-1">
              <div className="flex justify-between items-start gap-2">
                <div>
                  <h2 className="text-xl font-bold tracking-tight">
                    {pro.firstName} {pro.lastName}
                  </h2>
                  <p className="text-sm text-muted-foreground">{pro.specialty}</p>
                </div>
                <Button 
                  variant={isBanned ? "destructive" : "outline"}
                  size="sm"
                  className="h-7 text-xs"
                  onClick={handleToggleBan}
                >
                  {isBanned ? t("unban_professional") : t("ban_professional")}
                </Button>
              </div>
              <div className="mt-2 flex flex-wrap items-center gap-2">
                <Badge
                  variant="outline"
                  className="gap-1 bg-amber-500/10 text-amber-600 border-amber-500/30"
                >
                  <Star className="h-3 w-3 fill-amber-500" />
                  {pro.rating.toFixed(1)}
                </Badge>
                <Badge
                  variant="outline"
                  className="gap-1 bg-emerald-500/10 text-emerald-600 border-emerald-500/30"
                >
                  <ShieldCheck className="h-3 w-3" />
                  Verified
                </Badge>
              </div>
            </div>
            <div className="flex flex-col gap-2">
              <Button size="icon" variant="outline" className="h-8 w-8 rounded-full">
                <Mail className="h-4 w-4" />
              </Button>
              <Button size="icon" variant="outline" className="h-8 w-8 rounded-full">
                <Phone className="h-4 w-4" />
              </Button>
            </div>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex border-b border-border/60 px-4">
          <button
            className={`px-4 py-3 text-sm font-medium transition-colors ${tab === "overview" ? "border-b-2 border-primary text-primary" : "text-muted-foreground hover:text-foreground"}`}
            onClick={() => setTab("overview")}
          >
            Overview
          </button>
          <button
            className={`px-4 py-3 text-sm font-medium transition-colors ${tab === "reviews" ? "border-b-2 border-primary text-primary" : "text-muted-foreground hover:text-foreground"}`}
            onClick={() => setTab("reviews")}
          >
            Reviews ({localReviews.length})
          </button>
        </div>

        {/* Scrollable Content */}
        <div className="flex-1 overflow-y-auto p-6">
          {tab === "overview" ? (
            <div className="space-y-8">
              <section>
                <h3 className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">
                  About
                </h3>
                <p className="mt-2 text-sm">
                  {pro.firstName} is a highly rated {pro.specialty} with {pro.rating} stars across{" "}
                  {localReviews.length} assignments. Known for being punctual, professional, and
                  excellent with patients.
                </p>
                <div className="mt-3 flex items-center gap-2 text-sm text-muted-foreground">
                  <MapPin className="h-4 w-4" />
                  Within {pro.distanceMiles} miles
                </div>
              </section>

              <section>
                <h3 className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">
                  Stats
                </h3>
                <div className="mt-3 grid grid-cols-2 gap-3">
                  <div className="rounded-xl border border-border/60 bg-card p-3">
                    <div className="text-2xl font-bold text-foreground">
                      {localReviews.length * 3 + 12}
                    </div>
                    <div className="text-xs text-muted-foreground">Total Shifts</div>
                  </div>
                  <div className="rounded-xl border border-border/60 bg-card p-3">
                    <div className="text-2xl font-bold text-emerald-600">98%</div>
                    <div className="text-xs text-muted-foreground">Reliability Score</div>
                  </div>
                </div>
              </section>

              <section>
                <h3 className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">
                  Certificates
                </h3>
                <div className="mt-3 space-y-2">
                  {["CPR/BLS", "State License", "Liability Insurance", "OSHA Training"].map(
                    (cert, i) => (
                      <div
                        key={i}
                        className="flex items-center justify-between rounded-lg border border-border/60 bg-card p-3"
                      >
                        <div className="flex items-center gap-3">
                          <Award className="h-4 w-4 text-primary" />
                          <span className="text-sm font-medium">{cert}</span>
                        </div>
                        <CheckCircle2 className="h-4 w-4 text-emerald-500" />
                      </div>
                    ),
                  )}
                </div>
              </section>
            </div>
          ) : (
            <div className="space-y-6">
              <div className="flex items-center justify-between">
                <h3 className="text-sm font-semibold uppercase tracking-wider text-muted-foreground">
                  All Reviews
                </h3>
                {canReview && (
                  <Button size="sm" onClick={handleOpenReview} className="gap-1">
                    {reviewOpen ? (
                      "Cancel"
                    ) : (
                      <>
                        <Plus className="h-4 w-4" /> {existingReview ? "Edit Review" : "Add Review"}
                      </>
                    )}
                  </Button>
                )}
              </div>

              {reviewOpen && (
                <div className="rounded-xl border border-primary/30 bg-primary/5 p-4 space-y-4">
                  <div>
                    <Label>Rating</Label>
                    <div className="mt-1 flex items-center gap-1">
                      {[1, 2, 3, 4, 5].map((star) => (
                        <button
                          key={star}
                          onClick={() => setNewRating(star)}
                          className="focus:outline-none"
                        >
                          <Star
                            className={`h-6 w-6 ${star <= newRating ? "fill-amber-500 text-amber-500" : "text-muted-foreground/30"}`}
                          />
                        </button>
                      ))}
                    </div>
                  </div>
                  <div>
                    <Label>Comment</Label>
                    <Textarea
                      value={newReview}
                      onChange={(e) => setNewReview(e.target.value)}
                      placeholder="How was their performance?"
                      className="mt-1 resize-none"
                    />
                  </div>
                  <div className="flex justify-end">
                    <Button size="sm" onClick={submitReview}>
                      Submit
                    </Button>
                  </div>
                </div>
              )}

              <div className="space-y-4">
                {localReviews.map((rev) => (
                  <div key={rev.id} className="rounded-xl border border-border/60 bg-card p-4">
                    <div className="flex items-start justify-between">
                      <div>
                        <div className="font-medium text-sm">{rev.author}</div>
                        <div className="text-xs text-muted-foreground">{rev.date}</div>
                      </div>
                      <div className="flex items-center gap-0.5">
                        <Star className="h-3.5 w-3.5 fill-amber-500 text-amber-500" />
                        <span className="text-sm font-medium">{rev.rating}</span>
                      </div>
                    </div>
                    <p className="mt-3 text-sm text-foreground/80">{rev.text}</p>
                  </div>
                ))}
              </div>
            </div>
          )}
        </div>
      </SheetContent>
    </Sheet>
  );
}
