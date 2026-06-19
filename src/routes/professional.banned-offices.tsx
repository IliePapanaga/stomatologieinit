import { createFileRoute } from "@tanstack/react-router";
import { Ban, Undo2, Building2, Briefcase, MapPin, DollarSign, Eye } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { useAppStore, knownPractices } from "@/lib/store/app-store";
import { toast } from "sonner";
import { motion } from "motion/react";
import { mockLocations } from "@/lib/mock";
import { useTranslation } from "react-i18next";

export const Route = createFileRoute("/professional/banned-offices")({
  component: BannedOfficesPage,
});

function BannedOfficesPage() {
  const banned = useAppStore((s) => s.bannedPracticeIds);
  const hidden = useAppStore((s) => s.hiddenPostingIds);
  const unban = useAppStore((s) => s.unbanPractice);
  const unhidePosting = useAppStore((s) => s.unhidePosting);
  const jobPostings = useAppStore((s) => s.jobPostings);
  const { t } = useTranslation();

  const bannedPractices = banned.map(
    (id) =>
      knownPractices.find((p) => p.id === id) ?? {
        id,
        name: t("owner_fallback"),
        city: "—",
      },
  );

  const hiddenPostings = jobPostings.filter((p) => hidden.includes(p.id));

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">
          {t("assignments")}
        </p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">{t("hidden_jobs")}</h1>
        <p className="mt-1 text-sm text-muted-foreground">{t("hidden_jobs_desc")}</p>
      </header>

      <Tabs defaultValue="jobs">
        <TabsList>
          <TabsTrigger value="jobs" className="gap-2">
            <Briefcase className="h-3.5 w-3.5" />
            {t("hidden_jobs")}
            {hiddenPostings.length > 0 && (
              <Badge variant="secondary" className="ml-1 h-5 px-1.5 text-[10px]">
                {hiddenPostings.length}
              </Badge>
            )}
          </TabsTrigger>
          <TabsTrigger value="owners" className="gap-2">
            <Building2 className="h-3.5 w-3.5" />
            {t("blocked_owners")}
            {bannedPractices.length > 0 && (
              <Badge variant="secondary" className="ml-1 h-5 px-1.5 text-[10px]">
                {bannedPractices.length}
              </Badge>
            )}
          </TabsTrigger>
        </TabsList>

        {/* Hidden individual postings */}
        <TabsContent value="jobs" className="mt-4">
          {hiddenPostings.length === 0 ? (
            <EmptyState
              icon={<Eye className="h-6 w-6 text-muted-foreground" />}
              text={t("no_jobs_hidden")}
              sub={t("no_jobs_hidden_desc")}
            />
          ) : (
            <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
              {hiddenPostings.map((p, i) => {
                const loc = mockLocations.find((l) => l.id === p.locationId);
                const practice = knownPractices.find((kp) => kp.id === p.practiceId);
                return (
                  <motion.div
                    key={p.id}
                    initial={{ opacity: 0, y: 8 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: i * 0.04 }}
                  >
                    <Card className="group relative flex h-full flex-col overflow-hidden p-0 opacity-75 hover:opacity-100 transition-opacity">
                      <div className="flex items-start justify-between gap-3 border-b border-border/60 bg-muted/30 p-4">
                        <div className="min-w-0">
                          <Badge
                            variant="outline"
                            className="border-rose-500/30 bg-rose-500/10 text-rose-600 dark:text-rose-400"
                          >
                            {t("hidden")}
                          </Badge>
                          <h3 className="mt-2 truncate text-sm font-semibold">
                            {p.title ?? p.subcategory}
                          </h3>
                          <p className="mt-0.5 text-xs text-muted-foreground">
                            {practice?.name ?? t("owner_fallback")}
                          </p>
                        </div>
                        <Badge
                          variant="outline"
                          className="shrink-0 border-primary/30 bg-primary/10 text-primary text-xs"
                        >
                          {p.kind === "Temporary"
                            ? `$${(p as { hourlyRate: number }).hourlyRate}/hr`
                            : `$${((p as { salaryRange: { min: number } }).salaryRange.min / 1000).toFixed(0)}k+`}
                        </Badge>
                      </div>
                      <div className="flex flex-1 flex-col gap-1.5 p-4 text-xs text-muted-foreground">
                        {loc && (
                          <span className="flex items-center gap-1.5">
                            <MapPin className="h-3.5 w-3.5 shrink-0" />
                            {loc.name} · {loc.address.city}
                          </span>
                        )}
                        <span className="flex items-center gap-1.5">
                          <Briefcase className="h-3.5 w-3.5 shrink-0" />
                          {p.kind} · {p.subcategory}
                        </span>
                      </div>
                      <div className="flex border-t border-border/60 bg-muted/30 p-3">
                        <Button
                          size="sm"
                          variant="outline"
                          className="flex-1 gap-1.5"
                          onClick={() => {
                            unhidePosting(p.id);
                            toast.success(t("job_restored"), {
                              description: p.title ?? p.subcategory,
                            });
                          }}
                        >
                          <Undo2 className="h-3.5 w-3.5" />
                          {t("restore_to_feed")}
                        </Button>
                      </div>
                    </Card>
                  </motion.div>
                );
              })}
            </div>
          )}
        </TabsContent>

        {/* Blocked whole practices */}
        <TabsContent value="owners" className="mt-4">
          {bannedPractices.length === 0 ? (
            <EmptyState
              icon={<Ban className="h-6 w-6 text-muted-foreground" />}
              text={t("no_owners_blocked")}
              sub={t("no_owners_blocked_desc")}
            />
          ) : (
            <div className="grid gap-3 sm:grid-cols-2 xl:grid-cols-3">
              {bannedPractices.map((p, i) => (
                <motion.div
                  key={p.id}
                  initial={{ opacity: 0, y: 8 }}
                  animate={{ opacity: 1, y: 0 }}
                  transition={{ delay: i * 0.04 }}
                >
                  <Card className="flex items-center gap-3 p-4">
                    <div className="flex h-11 w-11 items-center justify-center rounded-xl bg-rose-500/10 text-rose-600 dark:text-rose-400">
                      <Building2 className="h-5 w-5" />
                    </div>
                    <div className="min-w-0 flex-1">
                      <p className="text-sm font-semibold">{p.name}</p>
                      <p className="text-xs text-muted-foreground">{p.city}</p>
                    </div>
                    <Button
                      size="sm"
                      variant="outline"
                      className="gap-1.5"
                      onClick={() => {
                        unban(p.id);
                        toast.success(t("owner_unblocked"), { description: p.name });
                      }}
                    >
                      <Undo2 className="h-3.5 w-3.5" /> {t("unblock")}
                    </Button>
                  </Card>
                </motion.div>
              ))}
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}

function EmptyState({ icon, text, sub }: { icon: React.ReactNode; text: string; sub: string }) {
  return (
    <Card className="flex flex-col items-center gap-2 p-10 text-center">
      {icon}
      <p className="text-sm font-medium text-foreground">{text}</p>
      <p className="text-xs text-muted-foreground">{sub}</p>
    </Card>
  );
}
