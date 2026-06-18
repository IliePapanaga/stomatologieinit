import { useState } from "react";
import { motion } from "motion/react";
import { Plus, Briefcase, Clock4, Loader2, MapPin } from "lucide-react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
  SheetTrigger,
} from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Slider } from "@/components/ui/slider";
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";
import { mockLocations } from "@/lib/mock";
import {
  subcategoriesBySpecialty,
  type ProfessionalSpecialty,
  type ProfessionalSubcategory,
  type TemporaryKind,
} from "@/lib/types/mdd";
import { useCreatePosting } from "@/lib/hooks/postings";
import { toast } from "sonner";

const specialties: ProfessionalSpecialty[] = [
  "Hygienist", "Dentist", "Assistant", "FrontOffice", "Orthodontist",
];

const formatSub = (s: ProfessionalSubcategory) =>
  s.replace(/([A-Z])/g, " $1").trim();

interface Props {
  trigger?: React.ReactNode;
  open?: boolean;
  onOpenChange?: (v: boolean) => void;
}

export function NewPostingSheet({ trigger, open: openProp, onOpenChange }: Props) {
  const [internalOpen, setInternalOpen] = useState(false);
  const open = openProp ?? internalOpen;
  const setOpen = onOpenChange ?? setInternalOpen;

  const [kind, setKind] = useState<"Permanent" | "Temporary">("Temporary");
  const [tempKind, setTempKind] = useState<TemporaryKind>("Simple");
  const [specialty, setSpecialty] = useState<ProfessionalSpecialty>("Hygienist");
  const [subcategory, setSubcategory] = useState<ProfessionalSubcategory>("RDH");
  const [locationId, setLocationId] = useState(mockLocations[0].id);
  const [radius, setRadius] = useState(12);
  const [title, setTitle] = useState("");
  const [startDate, setStartDate] = useState("2026-06-22");
  const [endDate, setEndDate] = useState("");
  const [startTime, setStartTime] = useState("08:00");
  const [endTime, setEndTime] = useState("16:00");
  const [hourlyRate, setHourlyRate] = useState(38);
  const [fullTime, setFullTime] = useState(true);
  const [salaryMin, setSalaryMin] = useState(78000);
  const [salaryMax, setSalaryMax] = useState(96000);
  const [benefits, setBenefits] = useState("Health, Dental, 401k, PTO");
  const [notes, setNotes] = useState("");

  const create = useCreatePosting();

  const onSpecialtyChange = (v: string) => {
    const s = v as ProfessionalSpecialty;
    setSpecialty(s);
    setSubcategory(subcategoriesBySpecialty[s][0]);
  };

  const submit = () => {
    create.mutate(
      {
        kind,
        title: title || `${formatSub(subcategory)} · ${kind}`,
        specialty,
        subcategory,
        commutingRadius: radius,
        locationId,
        startDate,
        endDate: kind === "Permanent" && endDate ? endDate : undefined,
        temporaryKind: tempKind,
        hourlyRate,
        startTime,
        endTime,
        fullTime,
        salaryMin,
        salaryMax,
        benefits: benefits.split(",").map((b) => b.trim()).filter(Boolean),
        notes,
      },
      {
        onSuccess: () => {
          toast.success(`${kind} posting published`, {
            description: `${formatSub(subcategory)} · ${radius} mi radius`,
          });
          setOpen(false);
        },
      }
    );
  };

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      {trigger ? (
        <SheetTrigger asChild>{trigger}</SheetTrigger>
      ) : (
        <SheetTrigger asChild>
          <Button className="bg-gradient-brand text-primary-foreground hover:opacity-95 dark:shadow-glow">
            <Plus className="h-4 w-4" /> New posting
          </Button>
        </SheetTrigger>
      )}
      <SheetContent side="right" className="w-full sm:max-w-xl flex flex-col p-0">
        <SheetHeader className="px-6 pt-6">
          <SheetTitle className="text-xl">Create job posting</SheetTitle>
          <SheetDescription>
            Configure role, schedule, and commuting radius. Matched against the local talent pool in real time.
          </SheetDescription>
        </SheetHeader>

        <div className="flex-1 overflow-y-auto px-6 pb-4">
          <Tabs value={kind} onValueChange={(v) => setKind(v as "Permanent" | "Temporary")} className="mt-5">
            <TabsList className="grid grid-cols-2 w-full">
              <TabsTrigger value="Temporary" className="gap-2">
                <Clock4 className="h-3.5 w-3.5" /> Temporary
              </TabsTrigger>
              <TabsTrigger value="Permanent" className="gap-2">
                <Briefcase className="h-3.5 w-3.5" /> Permanent
              </TabsTrigger>
            </TabsList>

            <div className="mt-6 space-y-5">
              <div className="space-y-1.5">
                <Label>Posting title</Label>
                <Input
                  placeholder="e.g. Lead Hygienist · Mission Bay"
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />
              </div>

              <div className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-1.5">
                  <Label>Specialty</Label>
                  <Select value={specialty} onValueChange={onSpecialtyChange}>
                    <SelectTrigger><SelectValue /></SelectTrigger>
                    <SelectContent>
                      {specialties.map((s) => <SelectItem key={s} value={s}>{s}</SelectItem>)}
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-1.5">
                  <Label>Subcategory</Label>
                  <Select value={subcategory} onValueChange={(v) => setSubcategory(v as ProfessionalSubcategory)}>
                    <SelectTrigger><SelectValue /></SelectTrigger>
                    <SelectContent>
                      {subcategoriesBySpecialty[specialty].map((s) => (
                        <SelectItem key={s} value={s}>{formatSub(s)}</SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-1.5">
                <Label>Location</Label>
                <Select value={locationId} onValueChange={setLocationId}>
                  <SelectTrigger><SelectValue /></SelectTrigger>
                  <SelectContent>
                    {mockLocations.map((l) => (
                      <SelectItem key={l.id} value={l.id}>{l.name} · {l.address.city}</SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="rounded-xl border border-border/60 bg-muted/30 p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <Label className="flex items-center gap-2 text-sm">
                    <MapPin className="h-4 w-4 text-primary" />
                    Commuting radius
                  </Label>
                  <span className="text-sm font-semibold text-foreground tabular-nums">
                    {radius} mi
                  </span>
                </div>
                <Slider
                  value={[radius]}
                  onValueChange={([v]) => setRadius(v)}
                  min={1}
                  max={50}
                  step={1}
                />
                <p className="text-xs text-muted-foreground">
                  Only professionals within {radius} miles of the practice will see this posting.
                </p>
              </div>
            </div>

            <TabsContent value="Temporary" className="mt-6 space-y-4">
              <div className="space-y-1.5">
                <Label>Schedule type</Label>
                <div className="grid grid-cols-3 gap-2">
                  {(["Simple", "Complex", "Weekly"] as TemporaryKind[]).map((k) => (
                    <button
                      key={k}
                      type="button"
                      onClick={() => setTempKind(k)}
                      className={`rounded-lg border px-3 py-2 text-xs font-medium transition ${
                        tempKind === k
                          ? "border-primary bg-primary/10 text-primary"
                          : "border-border/60 text-muted-foreground hover:bg-muted/50"
                      }`}
                    >
                      {k}
                    </button>
                  ))}
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>Date</Label>
                  <Input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>Hourly rate ($)</Label>
                  <Input type="number" value={hourlyRate} onChange={(e) => setHourlyRate(+e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>Start time</Label>
                  <Input type="time" value={startTime} onChange={(e) => setStartTime(e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>End time</Label>
                  <Input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
                </div>
              </div>
            </TabsContent>

            <TabsContent value="Permanent" className="mt-6 space-y-4">
              <div className="flex items-center justify-between rounded-lg border border-border/60 p-3">
                <div>
                  <Label className="text-sm">Full-time</Label>
                  <p className="text-xs text-muted-foreground">40 hrs / week with benefits</p>
                </div>
                <Switch checked={fullTime} onCheckedChange={setFullTime} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>Start date</Label>
                  <Input type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>End date (optional)</Label>
                  <Input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>Salary min ($)</Label>
                  <Input type="number" value={salaryMin} onChange={(e) => setSalaryMin(+e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>Salary max ($)</Label>
                  <Input type="number" value={salaryMax} onChange={(e) => setSalaryMax(+e.target.value)} />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label>Benefits</Label>
                <Input value={benefits} onChange={(e) => setBenefits(e.target.value)} />
              </div>
            </TabsContent>

            <div className="mt-6 space-y-1.5">
              <Label>Notes</Label>
              <Textarea
                rows={3}
                placeholder="Anything the candidate should know…"
                value={notes}
                onChange={(e) => setNotes(e.target.value)}
              />
            </div>

            <motion.div
              key={`${specialty}-${subcategory}-${radius}`}
              initial={{ opacity: 0, y: 6 }}
              animate={{ opacity: 1, y: 0 }}
              className="mt-5 rounded-xl border border-primary/30 bg-primary/5 p-3 text-xs text-foreground/80"
            >
              <span className="font-medium text-primary">Live match preview · </span>
              {Math.floor(8 + (radius / 50) * 22)} professionals match {formatSub(subcategory)} within {radius} mi.
            </motion.div>
          </Tabs>
        </div>

        <SheetFooter className="border-t border-border/60 bg-background/60 px-6 py-4 backdrop-blur">
          <Button variant="outline" onClick={() => setOpen(false)} disabled={create.isPending}>
            Cancel
          </Button>
          <Button
            onClick={submit}
            disabled={create.isPending}
            className="bg-gradient-brand text-primary-foreground"
          >
            {create.isPending && <Loader2 className="h-4 w-4 animate-spin" />}
            Publish posting
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
