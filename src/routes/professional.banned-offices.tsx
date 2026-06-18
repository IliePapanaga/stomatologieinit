import { createFileRoute } from "@tanstack/react-router";
import { Ban, Undo2, Building2 } from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { useAppStore, knownPractices } from "@/lib/store/app-store";
import { toast } from "sonner";
import { motion } from "motion/react";

export const Route = createFileRoute("/professional/banned-offices")({
  component: BannedOfficesPage,
});

function BannedOfficesPage() {
  const banned = useAppStore((s) => s.bannedPracticeIds);
  const unban = useAppStore((s) => s.unbanPractice);

  const list = banned.map(
    (id) =>
      knownPractices.find((p) => p.id === id) ?? {
        id,
        name: "Unknown practice",
        city: "—",
      }
  );

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">Assignments</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Banned offices</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Practices you've hidden won't appear in your job feed.
        </p>
      </header>

      {list.length === 0 ? (
        <Card className="flex flex-col items-center gap-2 p-10 text-center text-sm text-muted-foreground">
          <Ban className="h-6 w-6 text-muted-foreground" />
          You haven't banned any offices yet.
        </Card>
      ) : (
        <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          {list.map((p, i) => (
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
                  onClick={() => {
                    unban(p.id);
                    toast.success("Practice restored", { description: p.name });
                  }}
                >
                  <Undo2 className="h-3.5 w-3.5" /> Restore
                </Button>
              </Card>
            </motion.div>
          ))}
        </div>
      )}
    </div>
  );
}
