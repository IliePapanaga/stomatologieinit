import { useState, useMemo } from "react";
import { Sheet, SheetContent, SheetHeader, SheetTitle, SheetDescription } from "@/components/ui/sheet";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
  MapPin,
  Phone,
  Clock,
  Star,
  Briefcase,
  Building2,
  ExternalLink,
  CalendarDays,
  DollarSign,
  Camera,
  Plus,
  User,
  CheckCircle2,
} from "lucide-react";
import { useAppStore, knownPractices, type PracticeOwnerReview } from "@/lib/store/app-store";
import { mockLocations, mockPostings } from "@/lib/mock";
import type { JobPosting } from "@/lib/types/mdd";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";

// ---------- helpers ---------------------------------------------------------

const PRO_LAT = 37.7845;
const PRO_LNG = -122.4323;

function haversine(lat1: number, lng1: number, lat2: number, lng2: number) {
  const R = 3959;
  const dLat = ((lat2 - lat1) * Math.PI) / 180;
  const dLng = ((lng2 - lng1) * Math.PI) / 180;
  const a =
    Math.sin(dLat / 2) ** 2 +
    Math.cos((lat1 * Math.PI) / 180) *
      Math.cos((lat2 * Math.PI) / 180) *
      Math.sin(dLng / 2) ** 2;
  return R * 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
}

// Deterministic extended data for any practice
function getPracticeInfo(practiceId: string) {
  const known = knownPractices.find((p) => p.id === practiceId);
  const loc = mockLocations.find((l) => l.practiceId === practiceId);

  const seed = practiceId.split("").reduce((a, c) => a + c.charCodeAt(0), 0);
  const fallbackAddresses = [
    { street: "1200 Market St", city: "Oakland", state: "CA", zip: "94607", lat: 37.804, lng: -122.271 },
    { street: "340 College Ave", city: "Berkeley", state: "CA", zip: "94704", lat: 37.872, lng: -122.259 },
    { street: "700 B St", city: "San Mateo", state: "CA", zip: "94401", lat: 37.562, lng: -122.326 },
    { street: "280 Grand Ave", city: "South SF", state: "CA", zip: "94080", lat: 37.655, lng: -122.406 },
  ];
  const fallback = fallbackAddresses[seed % fallbackAddresses.length];

  const address = loc?.address ?? fallback;
  const lat = loc?.lat ?? fallback.lat;
  const lng = loc?.lng ?? fallback.lng;

  return {
    name: known?.name ?? "Owner",
    city: known?.city ?? "San Francisco, CA",
    address,
    lat,
    lng,
    phone: loc?.phone ?? `+1 415 555 0${100 + (seed % 100)}`,
    contact: loc ? `${loc.contactFirstName} ${loc.contactLastName}` : "Owner Manager",
    distanceMiles: haversine(PRO_LAT, PRO_LNG, lat, lng).toFixed(1),
    hours: ["Mon – Fri  8 am – 5 pm", "Sat  9 am – 2 pm", "Sun  Closed"][seed % 3],
    googleMapsUrl: `https://www.google.com/maps/search/?api=1&query=${lat},${lng}`,
    osmEmbedUrl: `https://www.openstreetmap.org/export/embed.html?bbox=${lng - 0.012},${lat - 0.008},${lng + 0.012},${lat + 0.008}&layer=mapnik&marker=${lat},${lng}`,
  };
}

const roomLabels = ["Reception", "Treatment Room", "X-Ray Suite", "Lab", "Sterilization", "Consultation"];
const roomColors = [
  "from-primary/20 to-primary/5",
  "from-emerald-500/20 to-emerald-500/5",
  "from-indigo-500/20 to-indigo-500/5",
  "from-amber-500/20 to-amber-500/5",
  "from-rose-500/20 to-rose-500/5",
  "from-purple-500/20 to-purple-500/5",
];

// ---------- types -----------------------------------------------------------

type Tab = "overview" | "postings" | "review";

interface Props {
  practiceId: string | null;
  highlightPostingId?: string;
  onOpenChange: (open: boolean) => void;
}

// ---------- component -------------------------------------------------------

export function PracticeOwnerSheet({ practiceId, highlightPostingId, onOpenChange }: Props) {
  const [tab, setTab] = useState<Tab>("overview");
  const [rating, setRating] = useState(5);
  const [text, setText] = useState("");
  const [editing, setEditing] = useState(false);

  const jobPostings = useAppStore((s) => s.jobPostings);
  const practiceOwnerReviews = useAppStore((s) => s.practiceOwnerReviews);
  const addPracticeOwnerReview = useAppStore((s) => s.addPracticeOwnerReview);
  const jobHistory = useAppStore((s) => s.jobHistory);
  const currentUser = useAppStore((s) => s.currentUser);
  const bannedPracticeIds = useAppStore((s) => s.bannedPracticeIds);
  const banPractice = useAppStore((s) => s.banPractice);
  const unbanPractice = useAppStore((s) => s.unbanPractice);
  const { t } = useTranslation();

  // Memoised data
  const info = useMemo(
    () => (practiceId ? getPracticeInfo(practiceId) : null),
    [practiceId]
  );

  const otherPostings = useMemo<JobPosting[]>(
    () =>
      [...jobPostings, ...mockPostings].filter(
        (p, i, arr) =>
          p.practiceId === practiceId &&
          p.status === "Open" &&
          arr.findIndex((q) => q.id === p.id) === i // dedup
      ),
    [jobPostings, practiceId]
  );

  const existingReview = useMemo<PracticeOwnerReview | undefined>(
    () =>
      practiceOwnerReviews.find(
        (r) => r.practiceId === practiceId && r.professionalId === currentUser?.id
      ),
    [practiceOwnerReviews, practiceId, currentUser]
  );

  // Professional can review if they have worked there
  const canReview = useMemo(() => {
    if (!practiceId) return false;
    return jobHistory.some((h) => h.practiceId === practiceId);
  }, [jobHistory, practiceId]);

  const openEdit = () => {
    setRating(existingReview?.rating ?? 5);
    setText(existingReview?.text ?? "");
    setEditing(true);
  };

  const submitReview = () => {
    if (!text.trim() || !practiceId) return;
    addPracticeOwnerReview({ practiceId, rating, text });
    toast.success(existingReview ? t("review_updated") : t("review_submitted"));
    setEditing(false);
  };

  const isBanned = practiceId ? bannedPracticeIds.includes(practiceId) : false;

  const handleToggleBan = () => {
    if (!practiceId) return;
    if (isBanned) {
      unbanPractice(practiceId);
      toast.success(t("unban_owner"));
    } else {
      banPractice(practiceId);
      toast.success(t("ban_owner"));
    }
  };

  if (!practiceId || !info) return null;

  const tabs: { id: Tab; label: string }[] = [
    { id: "overview", label: t("overview") },
    { id: "postings", label: `${t("postings")} (${otherPostings.length})` },
    { id: "review", label: t("review") },
  ];

  return (
    <Sheet open={!!practiceId} onOpenChange={onOpenChange}>
      <SheetContent className="flex w-full flex-col border-l-border/60 bg-background/95 p-0 backdrop-blur-xl sm:max-w-lg overflow-hidden">
        {/* ── Header ─────────────────────────────────────────────────────── */}
        <SheetHeader className="sr-only">
          <SheetTitle>{info.name}</SheetTitle>
          <SheetDescription>{t("owner_details_and_review")}</SheetDescription>
        </SheetHeader>

        <div className="bg-gradient-to-br from-primary/10 via-primary/5 to-transparent p-6 pt-12">
          <div className="flex items-start gap-4">
            <div className="flex h-16 w-16 shrink-0 items-center justify-center rounded-2xl bg-primary/15 text-primary ring-4 ring-background shadow">
              <Building2 className="h-8 w-8" />
            </div>
            <div className="flex-1 min-w-0">
              <div className="flex justify-between items-start gap-2">
                <div>
                  <h2 className="text-xl font-bold tracking-tight">{info.name}</h2>
                  <p className="text-sm text-muted-foreground mt-0.5">{info.city}</p>
                </div>
                <Button 
                  variant={isBanned ? "destructive" : "outline"}
                  size="sm"
                  className="h-7 text-xs"
                  onClick={handleToggleBan}
                >
                  {isBanned ? t("unban_owner") : t("ban_owner")}
                </Button>
              </div>
              <div className="mt-2 flex flex-wrap items-center gap-2">
                <Badge variant="outline" className="gap-1 bg-background/60">
                  <MapPin className="h-3 w-3 text-primary" />
                  {info.distanceMiles} {t("mi_away")}
                </Badge>
                {existingReview && (
                  <Badge variant="outline" className="gap-1 bg-amber-500/10 text-amber-600 border-amber-500/30">
                    <Star className="h-3 w-3 fill-amber-500" />
                    {t("your_review_rating", { rating: existingReview.rating })}
                  </Badge>
                )}
              </div>
            </div>
          </div>
        </div>

        {/* ── Tabs ───────────────────────────────────────────────────────── */}
        <div className="flex border-b border-border/60 px-4 bg-background/50">
          {tabs.map((t) => (
            <button
              key={t.id}
              className={`px-4 py-3 text-sm font-medium transition-colors whitespace-nowrap ${
                tab === t.id
                  ? "border-b-2 border-primary text-primary"
                  : "text-muted-foreground hover:text-foreground"
              }`}
              onClick={() => setTab(t.id)}
            >
              {t.label}
            </button>
          ))}
        </div>

        {/* ── Content ────────────────────────────────────────────────────── */}
        <div className="flex-1 overflow-y-auto p-6 space-y-6">

          {/* ═══ OVERVIEW ═══ */}
          {tab === "overview" && (
            <>
              {/* Address + quick info */}
              <section className="space-y-3">
                <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  {t("location")}
                </h3>
                <div className="rounded-xl border border-border/60 bg-card divide-y divide-border/60">
                  <InfoRow icon={MapPin}>
                    {info.address.street}, {info.address.city}, {info.address.state} {info.address.zip}
                  </InfoRow>
                  <InfoRow icon={Phone}>{info.phone}</InfoRow>
                  <InfoRow icon={User}>{info.contact}</InfoRow>
                  <InfoRow icon={Clock}>{info.hours}</InfoRow>
                </div>
              </section>

              {/* Map embed */}
              <section className="space-y-2">
                <div className="flex items-center justify-between">
                  <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                    {t("map")}
                  </h3>
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-7 gap-1.5 text-xs text-primary"
                    onClick={() => window.open(info.googleMapsUrl, "_blank")}
                  >
                    <ExternalLink className="h-3.5 w-3.5" />
                    {t("open_in_google_maps")}
                  </Button>
                </div>
                <div className="overflow-hidden rounded-xl border border-border/60">
                  <iframe
                    src={info.osmEmbedUrl}
                    width="100%"
                    height="180"
                    className="block"
                    style={{ border: 0 }}
                    title="Practice location map"
                    loading="lazy"
                  />
                </div>
              </section>

              {/* Photos */}
              <section className="space-y-3">
                <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground flex items-center gap-1.5">
                  <Camera className="h-3.5 w-3.5" /> {t("photos")}
                </h3>
                <div className="grid grid-cols-3 gap-2">
                  {roomLabels.map((label, i) => (
                    <div
                      key={label}
                      className={`relative flex aspect-square flex-col items-center justify-center rounded-xl bg-gradient-to-br ${roomColors[i]} border border-border/40 p-2`}
                    >
                      <Building2 className="h-6 w-6 text-muted-foreground/40" />
                      <span className="mt-1 text-[10px] text-center leading-tight text-muted-foreground">
                        {label}
                      </span>
                    </div>
                  ))}
                </div>
              </section>
            </>
          )}

          {tab === "postings" && (
            <section className="space-y-3">
              <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                {t("open_positions_at")} {info.name}
              </h3>
              {otherPostings.length === 0 ? (
                <div className="rounded-xl border border-dashed border-border/60 p-8 text-center text-sm text-muted-foreground">
                  <Briefcase className="mx-auto mb-2 h-5 w-5 text-primary" />
                  {t("no_open_positions")}
                </div>
              ) : (
                <div className="space-y-2.5">
                  {otherPostings.map((p) => {
                    const isHighlighted = p.id === highlightPostingId;
                    const loc = mockLocations.find((l) => l.id === p.locationId);
                    return (
                      <div
                        key={p.id}
                        className={`rounded-xl border p-4 transition ${
                          isHighlighted
                            ? "border-primary/50 bg-primary/5"
                            : "border-border/60 bg-card"
                        }`}
                      >
                        <div className="flex items-start justify-between gap-2">
                          <div className="min-w-0">
                            <div className="flex flex-wrap items-center gap-2">
                              <p className="text-sm font-semibold truncate">{p.title ?? p.subcategory}</p>
                              {isHighlighted && (
                                <Badge className="h-4 px-1.5 text-[10px] bg-primary/15 text-primary border-0">
                                  {t("this_shift")}
                                </Badge>
                              )}
                            </div>
                            <p className="mt-0.5 text-xs text-muted-foreground">
                              {p.subcategory} · {p.kind}
                            </p>
                          </div>
                          <Badge
                            variant="outline"
                            className="shrink-0 border-primary/40 bg-primary/10 text-primary text-xs"
                          >
                            {p.kind === "Temporary"
                              ? `$${(p as { hourlyRate: number }).hourlyRate}/hr`
                              : `$${((p as { salaryRange: { min: number } }).salaryRange.min / 1000).toFixed(0)}k+`}
                          </Badge>
                        </div>
                        <div className="mt-2 flex flex-wrap items-center gap-x-3 gap-y-1 text-[11px] text-muted-foreground">
                          {loc && (
                            <span className="flex items-center gap-1">
                              <MapPin className="h-3 w-3" />
                              {loc.name}
                            </span>
                          )}
                          <span className="flex items-center gap-1">
                            <CalendarDays className="h-3 w-3" />
                            {new Date(p.startDate).toLocaleDateString("en-US", {
                              month: "short",
                              day: "numeric",
                            })}
                          </span>
                          <span className="flex items-center gap-1">
                            <CheckCircle2 className="h-3 w-3 text-primary" />
                            {p.matchPercentage}% {t("match")}
                          </span>
                        </div>
                      </div>
                    );
                  })}
                </div>
              )}
            </section>
          )}
          {/* ═══ REVIEW ═══ */}
          {tab === "review" && (
            <section className="space-y-5">
              <div className="flex items-center justify-between">
                <h3 className="text-xs font-semibold uppercase tracking-wider text-muted-foreground">
                  {t("your_review_title")}
                </h3>
                {canReview && !editing && (
                  <Button size="sm" onClick={openEdit} className="gap-1.5">
                    {existingReview ? (
                      <>
                        <Star className="h-3.5 w-3.5" />
                        {t("edit_review")}
                      </>
                    ) : (
                      <>
                        <Plus className="h-3.5 w-3.5" />
                        {t("leave_review")}
                      </>
                    )}
                  </Button>
                )}
              </div>

              {!canReview && (
                <div className="rounded-xl border border-dashed border-border/60 bg-muted/20 p-6 text-center text-sm text-muted-foreground">
                  <DollarSign className="mx-auto mb-2 h-5 w-5" />
                  {t("can_review_desc")}
                </div>
              )}

              {canReview && editing && (
                <div className="rounded-xl border border-primary/30 bg-primary/5 p-4 space-y-4">
                  <div>
                    <Label className="text-xs font-medium">{t("rating")}</Label>
                    <div className="mt-1.5 flex items-center gap-1">
                      {[1, 2, 3, 4, 5].map((s) => (
                        <button key={s} onClick={() => setRating(s)} className="focus:outline-none">
                          <Star
                            className={`h-7 w-7 transition ${
                              s <= rating
                                ? "fill-amber-500 text-amber-500"
                                : "text-muted-foreground/30 hover:text-amber-400"
                            }`}
                          />
                        </button>
                      ))}
                      <span className="ml-2 text-sm font-medium text-muted-foreground">
                        {rating} / 5
                      </span>
                    </div>
                  </div>
                  <div>
                    <Label className="text-xs font-medium">{t("comment")}</Label>
                    <Textarea
                      value={text}
                      onChange={(e) => setText(e.target.value)}
                      placeholder={t("review_placeholder")}
                      className="mt-1.5 resize-none"
                      rows={4}
                    />
                  </div>
                  <div className="flex justify-end gap-2">
                    <Button variant="ghost" size="sm" onClick={() => setEditing(false)}>
                      {t("cancel")}
                    </Button>
                    <Button size="sm" onClick={submitReview} disabled={!text.trim()}>
                      {t("submit")}
                    </Button>
                  </div>
                </div>
              )}

              {canReview && existingReview && !editing && (
                <div className="rounded-xl border border-border/60 bg-card p-4">
                  <div className="flex items-start justify-between">
                    <div>
                      <p className="text-sm font-medium">{t("your_review_title")}</p>
                      <p className="text-xs text-muted-foreground">{existingReview.date}</p>
                    </div>
                    <div className="flex items-center gap-0.5">
                      {Array.from({ length: 5 }).map((_, i) => (
                        <Star
                          key={i}
                          className={`h-3.5 w-3.5 ${
                            i < existingReview.rating
                              ? "fill-amber-500 text-amber-500"
                              : "text-muted-foreground/20"
                          }`}
                        />
                      ))}
                    </div>
                  </div>
                  <p className="mt-3 text-sm text-foreground/80">{existingReview.text}</p>
                </div>
              )}

              {canReview && !existingReview && !editing && (
                <div className="rounded-xl border border-dashed border-border/60 bg-muted/10 p-6 text-center text-sm text-muted-foreground">
                  <Star className="mx-auto mb-2 h-5 w-5 text-amber-400" />
                  {t("no_review_yet")}
                  <br />
                  <button className="mt-2 text-primary underline-offset-2 hover:underline" onClick={openEdit}>
                    {t("write_one_now")}
                  </button>
                </div>
              )}
            </section>
          )}
        </div>
      </SheetContent>
    </Sheet>
  );
}

// ---------- small helpers ---------------------------------------------------

function InfoRow({
  icon: Icon,
  children,
}: {
  icon: typeof MapPin;
  children: React.ReactNode;
}) {
  return (
    <div className="flex items-start gap-3 px-4 py-3">
      <Icon className="mt-0.5 h-4 w-4 shrink-0 text-primary" />
      <span className="text-sm leading-snug">{children}</span>
    </div>
  );
}
