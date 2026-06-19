import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { useAppStore } from "@/lib/store/app-store";
import { Briefcase, Plus, Trash2, Phone, UserCheck } from "lucide-react";
import { motion, AnimatePresence } from "motion/react";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";

export const Route = createFileRoute("/professional/skills")({
  component: SkillsPage,
});

function SkillsPage() {
  const skills = useAppStore((s) => s.professionalProfile.skills);
  const refs = useAppStore((s) => s.professionalProfile.references);
  const addSkill = useAppStore((s) => s.addSkill);
  const removeSkill = useAppStore((s) => s.removeSkill);
  const addReference = useAppStore((s) => s.addReference);
  const removeReference = useAppStore((s) => s.removeReference);

  const [exp, setExp] = useState({
    title: "",
    organization: "",
    startDate: "",
    endDate: "",
    description: "",
  });
  const [ref, setRef] = useState({ name: "", phone: "", relationship: "" });
  const { t, i18n } = useTranslation();

  const submitExp = () => {
    if (!exp.title || !exp.organization || !exp.startDate) {
      toast.error(t("title_company_start_req"));
      return;
    }
    const start = new Date(exp.startDate);
    const end = exp.endDate ? new Date(exp.endDate) : new Date();
    const years = Math.max(
      1,
      Math.round((end.getTime() - start.getTime()) / (365.25 * 24 * 3600 * 1000)),
    );
    addSkill({
      title: exp.title,
      organization: exp.organization,
      years,
      startDate: exp.startDate,
      endDate: exp.endDate || undefined,
      description: exp.description || undefined,
    });
    setExp({ title: "", organization: "", startDate: "", endDate: "", description: "" });
    toast.success(t("work_exp_added"));
  };

  const submitRef = () => {
    if (!ref.name || !ref.phone) {
      toast.error(t("name_phone_req"));
      return;
    }
    addReference(ref);
    setRef({ name: "", phone: "", relationship: "" });
    toast.success(t("ref_added"));
  };

  const totalYears = skills.reduce((s, e) => s + e.years, 0);

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">
          {t("my_account")}
        </p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">{t("skills_exp")}</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          {t("skills_exp_desc", { totalYears, roles: skills.length, refs: refs.length })}
        </p>
      </header>

      <section className="space-y-3">
        <h2 className="flex items-center gap-2 text-sm font-semibold">
          <Briefcase className="h-4 w-4 text-primary" /> {t("work_exp")}
        </h2>
        <div className="grid gap-5 lg:grid-cols-3">
          <Card className="space-y-3 p-5 lg:col-span-1">
            <p className="text-sm font-semibold">{t("add_exp")}</p>
            <Field label={t("company_name")}>
              <Input
                value={exp.organization}
                onChange={(e) => setExp((f) => ({ ...f, organization: e.target.value }))}
                placeholder="Pacific Smiles"
              />
            </Field>
            <Field label={t("role")}>
              <Input
                value={exp.title}
                onChange={(e) => setExp((f) => ({ ...f, title: e.target.value }))}
                placeholder="Sr. Hygienist"
              />
            </Field>
            <div className="grid grid-cols-2 gap-3">
              <Field label={t("start_date")}>
                <Input
                  type="date"
                  value={exp.startDate}
                  onChange={(e) => setExp((f) => ({ ...f, startDate: e.target.value }))}
                />
              </Field>
              <Field label={t("end_date")}>
                <Input
                  type="date"
                  value={exp.endDate}
                  onChange={(e) => setExp((f) => ({ ...f, endDate: e.target.value }))}
                />
              </Field>
            </div>
            <Field label={t("description")}>
              <Textarea
                rows={3}
                value={exp.description}
                onChange={(e) => setExp((f) => ({ ...f, description: e.target.value }))}
                placeholder={t("key_resp_placeholder")}
              />
            </Field>
            <Button onClick={submitExp} className="w-full">
              <Plus className="h-4 w-4" /> {t("add_to_timeline")}
            </Button>
          </Card>

          <Card className="p-5 lg:col-span-2">
            <p className="mb-4 text-sm font-semibold">{t("career_timeline")}</p>
            {skills.length === 0 ? (
              <p className="rounded-lg border border-dashed border-border/70 p-6 text-center text-sm text-muted-foreground">
                {t("no_exp_added")}
              </p>
            ) : (
              <ol className="relative space-y-4 border-l border-border/70 pl-5">
                <AnimatePresence>
                  {skills.map((s) => (
                    <motion.li
                      key={s.id}
                      layout
                      initial={{ opacity: 0, x: -8 }}
                      animate={{ opacity: 1, x: 0 }}
                      exit={{ opacity: 0, x: 8 }}
                      className="relative"
                    >
                      <span className="absolute -left-[27px] top-1.5 flex h-4 w-4 items-center justify-center rounded-full border-2 border-primary bg-background">
                        <span className="h-1.5 w-1.5 rounded-full bg-primary" />
                      </span>
                      <div className="flex items-start gap-3 rounded-xl border border-border/60 bg-muted/30 p-3.5">
                        <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                          <Briefcase className="h-4 w-4" />
                        </div>
                        <div className="min-w-0 flex-1">
                          <div className="flex items-baseline justify-between gap-2">
                            <p className="truncate text-sm font-semibold">{s.title}</p>
                            <span className="shrink-0 text-[11px] text-muted-foreground">
                              {s.years} {s.years === 1 ? t("yr") : t("yrs")}
                            </span>
                          </div>
                          <p className="text-xs text-muted-foreground">
                            {s.organization}
                            {s.startDate &&
                              ` · ${new Date(s.startDate).toLocaleDateString(i18n.language, { month: "short", year: "numeric" })}`}
                            {s.endDate &&
                              ` – ${new Date(s.endDate).toLocaleDateString(i18n.language, { month: "short", year: "numeric" })}`}
                            {s.startDate && !s.endDate && ` – ${t("present")}`}
                          </p>
                          {s.description && (
                            <p className="mt-1.5 text-xs leading-relaxed">{s.description}</p>
                          )}
                        </div>
                        <Button
                          variant="ghost"
                          size="icon"
                          className="h-7 w-7 text-muted-foreground hover:text-destructive"
                          onClick={() => removeSkill(s.id)}
                        >
                          <Trash2 className="h-3.5 w-3.5" />
                        </Button>
                      </div>
                    </motion.li>
                  ))}
                </AnimatePresence>
              </ol>
            )}
          </Card>
        </div>
      </section>

      <section className="space-y-3">
        <h2 className="flex items-center gap-2 text-sm font-semibold">
          <UserCheck className="h-4 w-4 text-primary" /> {t("pro_references")}
        </h2>
        <div className="grid gap-5 lg:grid-cols-3">
          <Card className="space-y-3 p-5 lg:col-span-1">
            <p className="text-sm font-semibold">{t("add_reference")}</p>
            <Field label={t("ref_name")}>
              <Input
                value={ref.name}
                onChange={(e) => setRef((f) => ({ ...f, name: e.target.value }))}
                placeholder="Dr. Karen Wu"
              />
            </Field>
            <Field label={t("ref_phone")}>
              <Input
                value={ref.phone}
                onChange={(e) => setRef((f) => ({ ...f, phone: e.target.value }))}
                placeholder="+1 555 000 0000"
              />
            </Field>
            <Field label={t("relationship")}>
              <Input
                value={ref.relationship}
                onChange={(e) => setRef((f) => ({ ...f, relationship: e.target.value }))}
                placeholder={t("former_supervisor")}
              />
            </Field>
            <Button onClick={submitRef} className="w-full">
              <Plus className="h-4 w-4" /> {t("add_reference")}
            </Button>
          </Card>

          <Card className="p-5 lg:col-span-2">
            <p className="mb-4 text-sm font-semibold">{t("ref_list")}</p>
            {refs.length === 0 ? (
              <p className="rounded-lg border border-dashed border-border/70 p-6 text-center text-sm text-muted-foreground">
                {t("no_refs_yet")}
              </p>
            ) : (
              <div className="grid gap-3 md:grid-cols-2">
                <AnimatePresence>
                  {refs.map((r) => (
                    <motion.div
                      key={r.id}
                      layout
                      initial={{ opacity: 0, y: 6 }}
                      animate={{ opacity: 1, y: 0 }}
                      exit={{ opacity: 0, y: -6 }}
                      className="flex items-start gap-3 rounded-xl border border-border/60 bg-muted/30 p-3.5"
                    >
                      <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary/10 text-primary">
                        <UserCheck className="h-4 w-4" />
                      </div>
                      <div className="min-w-0 flex-1">
                        <p className="truncate text-sm font-semibold">{r.name}</p>
                        <p className="flex items-center gap-1 truncate text-xs text-muted-foreground">
                          <Phone className="h-3 w-3" /> {r.phone}
                        </p>
                        {r.relationship && (
                          <p className="mt-0.5 truncate text-[11px] text-muted-foreground">
                            {r.relationship}
                          </p>
                        )}
                      </div>
                      <Button
                        variant="ghost"
                        size="icon"
                        className="h-7 w-7 text-muted-foreground hover:text-destructive"
                        onClick={() => removeReference(r.id)}
                      >
                        <Trash2 className="h-3.5 w-3.5" />
                      </Button>
                    </motion.div>
                  ))}
                </AnimatePresence>
              </div>
            )}
          </Card>
        </div>
      </section>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="space-y-1.5">
      <Label className="text-xs uppercase tracking-wider text-muted-foreground">{label}</Label>
      {children}
    </div>
  );
}
