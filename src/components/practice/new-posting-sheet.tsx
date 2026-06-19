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
import { useCreatePosting, useUpdatePosting } from "@/lib/hooks/postings";
import { toast } from "sonner";
import type { JobPosting, PermanentJobPosting, TemporaryJobPosting } from "@/lib/types/mdd";
import { useTranslation } from "react-i18next";

const specialties: ProfessionalSpecialty[] = [
  "Hygienist",
  "Dentist",
  "Assistant",
  "FrontOffice",
  "Orthodontist",
];

const formatSub = (s: ProfessionalSubcategory) => s.replace(/([A-Z])/g, " $1").trim();

interface Props {
  trigger?: React.ReactNode;
  open?: boolean;
  onOpenChange?: (v: boolean) => void;
  initialData?: JobPosting;
  hideTrigger?: boolean;
}

export function NewPostingSheet({
  trigger,
  open: openProp,
  onOpenChange,
  initialData,
  hideTrigger,
}: Props) {
  const [internalOpen, setInternalOpen] = useState(false);
  const open = openProp ?? internalOpen;
  const setOpen = onOpenChange ?? setInternalOpen;
  const { t } = useTranslation();

  const isTemp = initialData?.kind === "Temporary";
  const isPerm = initialData?.kind === "Permanent";

  const tempInitial = isTemp ? (initialData as TemporaryJobPosting) : null;
  const permInitial = isPerm ? (initialData as PermanentJobPosting) : null;

  const [kind, setKind] = useState<"Permanent" | "Temporary">(initialData?.kind || "Temporary");
  const [tempKind, setTempKind] = useState<TemporaryKind>(tempInitial?.temporaryKind || "Simple");
  const [specialty, setSpecialty] = useState<ProfessionalSpecialty>(
    initialData?.specialty || "Hygienist",
  );
  const [subcategory, setSubcategory] = useState<ProfessionalSubcategory>(
    initialData?.subcategory || "RDH",
  );
  const [locationId, setLocationId] = useState(initialData?.locationId || mockLocations[0].id);
  const [radius, setRadius] = useState(initialData?.commutingRadius || 12);
  const [title, setTitle] = useState(initialData?.title || "");
  const [startDate, setStartDate] = useState(initialData?.startDate?.split("T")[0] || "2026-06-22");
  const [endDate, setEndDate] = useState(initialData?.endDate?.split("T")[0] || "");
  const [startTime, setStartTime] = useState(tempInitial?.days[0]?.startTime || "08:00");
  const [endTime, setEndTime] = useState(tempInitial?.days[0]?.endTime || "16:00");
  const [hourlyRate, setHourlyRate] = useState(tempInitial?.hourlyRate || 38);
  const [fullTime, setFullTime] = useState(permInitial?.fullTime ?? true);
  const [salaryMin, setSalaryMin] = useState(permInitial?.salaryRange?.min || 78000);
  const [salaryMax, setSalaryMax] = useState(permInitial?.salaryRange?.max || 96000);
  const [benefits, setBenefits] = useState(
    permInitial?.benefits?.join(", ") || "Health, Dental, 401k, PTO",
  );
  const [workingSpaces, setWorkingSpaces] = useState(initialData?.workingSpaces || 1);
  const [notes, setNotes] = useState("");

  const create = useCreatePosting();
  const update = useUpdatePosting();

  const isPending = create.isPending || update.isPending;

  const onSpecialtyChange = (v: string) => {
    const s = v as ProfessionalSpecialty;
    setSpecialty(s);
    setSubcategory(subcategoriesBySpecialty[s][0]);
  };

  const submit = () => {
    const payload = {
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
      benefits: benefits
        .split(",")
        .map((b) => b.trim())
        .filter(Boolean),
      workingSpaces,
      notes,
    };

    if (initialData) {
      update.mutate(
        { id: initialData.id, updates: payload },
        {
          onSuccess: () => {
            toast.success(t("role_updated"));
            setOpen(false);
          },
        },
      );
    } else {
      create.mutate(payload as any, {
        onSuccess: () => {
          toast.success(t("role_published", { kind: t(kind.toLowerCase()) }), {
            description: `${formatSub(subcategory)} · ${radius} ${t("mi_radius")}`,
          });
          setOpen(false);
        },
      });
    }
  };

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      {!hideTrigger &&
        (trigger ? (
          <SheetTrigger asChild>{trigger}</SheetTrigger>
        ) : (
          <SheetTrigger asChild>
            <Button className="bg-gradient-brand text-primary-foreground hover:opacity-95 dark:shadow-glow">
              <Plus className="h-4 w-4" /> {t("new_role")}
            </Button>
          </SheetTrigger>
        ))}
      <SheetContent side="right" className="w-full sm:max-w-xl flex flex-col p-0">
        <SheetHeader className="px-6 pt-6">
          <SheetTitle className="text-xl">{initialData ? t("edit_role") : t("create_role")}</SheetTitle>
          <SheetDescription>
            {t("configure_role_desc")}
          </SheetDescription>
        </SheetHeader>

        <div className="flex-1 overflow-y-auto px-6 pb-4">
          <Tabs
            value={kind}
            onValueChange={(v) => setKind(v as "Permanent" | "Temporary")}
            className="mt-5"
          >
            <TabsList className="grid grid-cols-2 w-full">
              <TabsTrigger value="Temporary" className="gap-2">
                <Clock4 className="h-3.5 w-3.5" /> {t("temporary")}
              </TabsTrigger>
              <TabsTrigger value="Permanent" className="gap-2">
                <Briefcase className="h-3.5 w-3.5" /> {t("permanent")}
              </TabsTrigger>
            </TabsList>

            <div className="mt-6 space-y-5">
              <div className="space-y-1.5">
                <Label>{t("role_title")}</Label>
                <Input
                  placeholder={t("role_title_placeholder")}
                  value={title}
                  onChange={(e) => setTitle(e.target.value)}
                />
              </div>

              <div className="grid gap-4 sm:grid-cols-2">
                <div className="space-y-1.5">
                  <Label>{t("specialty")}</Label>
                  <Select value={specialty} onValueChange={onSpecialtyChange}>
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {specialties.map((s) => (
                        <SelectItem key={s} value={s}>
                          {t(`${s.toLowerCase()}_label`)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
                <div className="space-y-1.5">
                  <Label>{t("subcategory")}</Label>
                  <Select
                    value={subcategory}
                    onValueChange={(v) => setSubcategory(v as ProfessionalSubcategory)}
                  >
                    <SelectTrigger>
                      <SelectValue />
                    </SelectTrigger>
                    <SelectContent>
                      {subcategoriesBySpecialty[specialty].map((s) => (
                        <SelectItem key={s} value={s}>
                          {formatSub(s)}
                        </SelectItem>
                      ))}
                    </SelectContent>
                  </Select>
                </div>
              </div>

              <div className="space-y-1.5">
                <Label>{t("location")}</Label>
                <Select value={locationId} onValueChange={setLocationId}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {mockLocations.map((l) => (
                      <SelectItem key={l.id} value={l.id}>
                        {l.name} · {l.address.city}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>

              <div className="rounded-xl border border-border/60 bg-muted/30 p-4 space-y-3">
                <div className="flex items-center justify-between">
                  <Label className="flex items-center gap-2 text-sm">
                    <MapPin className="h-4 w-4 text-primary" />
                    {t("commuting_radius")}
                  </Label>
                  <span className="text-sm font-semibold text-foreground tabular-nums">
                    {radius} {t("mi")}
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
                  {t("radius_desc", { radius })}
                </p>
              </div>

              <div className="space-y-1.5">
                <Label>{t("working_spaces")}</Label>
                <Input
                  type="number"
                  min={1}
                  max={99}
                  value={workingSpaces}
                  onChange={(e) => setWorkingSpaces(+e.target.value)}
                />
              </div>
            </div>

            <TabsContent value="Temporary" className="mt-6 space-y-4">
              <div className="space-y-1.5">
                <Label>{t("schedule_type")}</Label>
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
                      {t(`schedule_${k.toLowerCase()}`)}
                    </button>
                  ))}
                </div>
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>{t("date")}</Label>
                  <Input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>{t("hourly_rate_input")}</Label>
                  <Input
                    type="number"
                    value={hourlyRate}
                    onChange={(e) => setHourlyRate(+e.target.value)}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>{t("start_time")}</Label>
                  <Input
                    type="time"
                    value={startTime}
                    onChange={(e) => setStartTime(e.target.value)}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>{t("end_time")}</Label>
                  <Input type="time" value={endTime} onChange={(e) => setEndTime(e.target.value)} />
                </div>
              </div>
            </TabsContent>

            <TabsContent value="Permanent" className="mt-6 space-y-4">
              <div className="flex items-center justify-between rounded-lg border border-border/60 p-3">
                <div>
                  <Label className="text-sm">{t("full_time")}</Label>
                  <p className="text-xs text-muted-foreground">{t("full_time_desc")}</p>
                </div>
                <Switch checked={fullTime} onCheckedChange={setFullTime} />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>{t("start_date")}</Label>
                  <Input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>{t("end_date_optional")}</Label>
                  <Input type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} />
                </div>
                <div className="space-y-1.5">
                  <Label>{t("salary_min")}</Label>
                  <Input
                    type="number"
                    value={salaryMin}
                    onChange={(e) => setSalaryMin(+e.target.value)}
                  />
                </div>
                <div className="space-y-1.5">
                  <Label>{t("salary_max")}</Label>
                  <Input
                    type="number"
                    value={salaryMax}
                    onChange={(e) => setSalaryMax(+e.target.value)}
                  />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label>{t("benefits")}</Label>
                <Input value={benefits} onChange={(e) => setBenefits(e.target.value)} />
              </div>
            </TabsContent>

            <div className="mt-6 space-y-1.5">
              <Label>{t("notes")}</Label>
              <Textarea
                rows={3}
                placeholder={t("notes_placeholder")}
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
              <span className="font-medium text-primary">{t("live_match_preview")} · </span>
              {t("professionals_match", {
                count: Math.floor(8 + (radius / 50) * 22),
                subcategory: formatSub(subcategory),
                radius,
              })}
            </motion.div>
          </Tabs>
        </div>

        <SheetFooter className="border-t border-border/60 bg-background/60 px-6 py-4 backdrop-blur">
          <Button variant="outline" onClick={() => setOpen(false)} disabled={isPending}>
            {t("cancel")}
          </Button>
          <Button
            onClick={submit}
            disabled={isPending}
            className="bg-gradient-brand text-primary-foreground"
          >
            {isPending && <Loader2 className="h-4 w-4 animate-spin" />}
            {initialData ? t("save_changes") : t("publish_role")}
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
