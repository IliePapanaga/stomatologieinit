import { createFileRoute } from "@tanstack/react-router";
import { Card } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Checkbox } from "@/components/ui/checkbox";
import { Slider } from "@/components/ui/slider";
import { useAppStore } from "@/lib/store/app-store";
import {
  subcategoriesBySpecialty,
  type ProfessionalSpecialty,
  type ProfessionalSubcategory,
} from "@/lib/types/mdd";
import { Sparkles, Stethoscope, HelpCircle } from "lucide-react";
import { motion } from "motion/react";

export const Route = createFileRoute("/professional/specialties")({
  component: SpecialtiesPage,
});

const specialties = Object.keys(subcategoriesBySpecialty) as ProfessionalSpecialty[];

const subLabels: Record<ProfessionalSubcategory, string> = {
  RDH: "Registered Dental Hygienist",
  EFDA: "Expanded Function DA",
  DentalAssistant: "Dental Assistant",
  SterilizationTech: "Sterilization Tech",
  TreatmentCoordinator: "Treatment Coordinator",
  Receptionist: "Receptionist",
  OfficeManager: "Office Manager",
  GeneralDentist: "General Dentist",
  Endodontist: "Endodontist",
  Periodontist: "Periodontist",
  OralSurgeon: "Oral Surgeon",
  Pediatric: "Pediatric",
};

const HYG_ASS_SUBS: ProfessionalSubcategory[] = ["RDH", "EFDA", "DentalAssistant", "SterilizationTech"];

const QUESTIONS = [
  { key: "dentrix", label: "Are you familiar with Dentrix software?" },
  { key: "eaglesoft", label: "Are you familiar with Eaglesoft software?" },
  { key: "opendental", label: "Are you familiar with Open Dental software?" },
  { key: "scaling", label: "Comfortable performing scaling & root planing (SRP)?" },
  { key: "pediatric", label: "Experience working with pediatric patients?" },
];

function SpecialtiesPage() {
  const profile = useAppStore((s) => s.professionalProfile);
  const toggle = useAppStore((s) => s.toggleSpecialty);
  const setComfort = useAppStore((s) => s.setComfortLevel);
  const setQ = useAppStore((s) => s.setQuestionnaire);

  const showQuestionnaire = profile.specialties.some((s) => HYG_ASS_SUBS.includes(s));

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">My Account</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Specialties</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Pick everything you're qualified for. Rate your comfort to improve match quality.
        </p>
      </header>

      <div className="flex flex-wrap items-center gap-2">
        <span className="text-xs text-muted-foreground">Selected:</span>
        {profile.specialties.length === 0 ? (
          <span className="text-xs italic text-muted-foreground">None yet</span>
        ) : (
          profile.specialties.map((s) => (
            <Badge key={s} variant="outline" className="border-primary/40 bg-primary/10 text-primary">
              {subLabels[s]}
            </Badge>
          ))
        )}
      </div>

      <div className="grid gap-4 lg:grid-cols-2">
        {specialties.map((sp, i) => (
          <motion.div
            key={sp}
            initial={{ opacity: 0, y: 8 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.04 }}
          >
            <Card className="p-5">
              <div className="mb-3 flex items-center gap-2">
                <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary/10 text-primary">
                  <Stethoscope className="h-4 w-4" />
                </div>
                <div>
                  <p className="text-sm font-semibold">{sp}</p>
                  <p className="text-[11px] text-muted-foreground">
                    {subcategoriesBySpecialty[sp].length} subcategories
                  </p>
                </div>
              </div>
              <div className="space-y-3">
                {subcategoriesBySpecialty[sp].map((sub) => {
                  const checked = profile.specialties.includes(sub);
                  const comfort = profile.comfortLevels[sub] ?? 5;
                  return (
                    <div
                      key={sub}
                      className="rounded-lg border border-border/60 bg-muted/30 p-3"
                    >
                      <label className="flex cursor-pointer items-center gap-2.5 text-sm">
                        <Checkbox checked={checked} onCheckedChange={() => toggle(sub)} />
                        <span>{subLabels[sub]}</span>
                      </label>
                      {checked && (
                        <div className="mt-3 space-y-1.5">
                          <div className="flex items-center justify-between text-[11px]">
                            <span className="text-muted-foreground">Comfort level</span>
                            <span className="font-semibold text-primary">{comfort} / 10</span>
                          </div>
                          <Slider
                            value={[comfort]}
                            onValueChange={(v) => setComfort(sub, v[0])}
                            min={1}
                            max={10}
                            step={1}
                          />
                        </div>
                      )}
                    </div>
                  );
                })}
              </div>
            </Card>
          </motion.div>
        ))}
      </div>

      {showQuestionnaire && (
        <Card className="space-y-3 border-primary/30 bg-primary/5 p-5">
          <div className="flex items-center gap-2">
            <HelpCircle className="h-4 w-4 text-primary" />
            <p className="text-sm font-semibold">Hygienist & Assistant questionnaire</p>
          </div>
          <p className="text-xs text-muted-foreground">
            Help us match you with practices that fit your toolkit and experience.
          </p>
          <div className="grid gap-2 sm:grid-cols-2">
            {QUESTIONS.map((q) => {
              const v = profile.questionnaire[q.key] ?? false;
              return (
                <label
                  key={q.key}
                  className="flex cursor-pointer items-center gap-2.5 rounded-lg border border-border/60 bg-background/60 p-3 text-xs"
                >
                  <Checkbox checked={v} onCheckedChange={(c) => setQ(q.key, !!c)} />
                  <span>{q.label}</span>
                </label>
              );
            })}
          </div>
        </Card>
      )}

      <Card className="flex items-center gap-3 border-primary/30 bg-primary/5 p-4">
        <Sparkles className="h-5 w-5 text-primary" />
        <p className="text-xs text-muted-foreground">
          Comfort levels are surveyed periodically. Practices see them as a confidence indicator,
          not a hard filter.
        </p>
      </Card>
    </div>
  );
}
