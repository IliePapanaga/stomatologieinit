import { useState } from "react";
import { Plus, Briefcase, Clock4 } from "lucide-react";
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
import { Switch } from "@/components/ui/switch";
import { Textarea } from "@/components/ui/textarea";
import { mockLocations } from "@/lib/mock";
import type { ProfessionalSpecialty, TemporaryKind } from "@/lib/types/mdd";
import { toast } from "sonner";

const specialties: ProfessionalSpecialty[] = [
  "Hygienist",
  "Dentist",
  "Assistant",
  "FrontOffice",
  "Orthodontist",
];

export function JobCreatorSheet() {
  const [open, setOpen] = useState(false);
  const [kind, setKind] = useState<"Permanent" | "Temporary">("Temporary");
  const [tempKind, setTempKind] = useState<TemporaryKind>("Simple");

  const submit = () => {
    toast.success(`${kind} posting drafted`, {
      description:
        kind === "Temporary" ? `${tempKind} schedule queued for review` : "Saved to drafts",
    });
    setOpen(false);
  };

  return (
    <Sheet open={open} onOpenChange={setOpen}>
      <SheetTrigger asChild>
        <Button className="bg-gradient-brand text-primary-foreground hover:opacity-95 dark:shadow-glow">
          <Plus className="h-4 w-4" /> New posting
        </Button>
      </SheetTrigger>
      <SheetContent side="right" className="w-full sm:max-w-xl flex flex-col">
        <SheetHeader>
          <SheetTitle>Create job posting</SheetTitle>
          <SheetDescription>
            Choose permanent or temporary. Field shape mirrors the MDD entity model.
          </SheetDescription>
        </SheetHeader>

        <div className="flex-1 overflow-y-auto px-4 pb-4">
          <Tabs
            value={kind}
            onValueChange={(v) => setKind(v as "Permanent" | "Temporary")}
            className="mt-4"
          >
            <TabsList className="grid grid-cols-2">
              <TabsTrigger value="Temporary" className="gap-2">
                <Clock4 className="h-3.5 w-3.5" /> Temporary
              </TabsTrigger>
              <TabsTrigger value="Permanent" className="gap-2">
                <Briefcase className="h-3.5 w-3.5" /> Permanent
              </TabsTrigger>
            </TabsList>

            <div className="mt-5 grid gap-4 sm:grid-cols-2">
              <div className="space-y-1.5">
                <Label>Specialty</Label>
                <Select defaultValue="Hygienist">
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {specialties.map((s) => (
                      <SelectItem key={s} value={s}>
                        {s}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="space-y-1.5">
                <Label>Location</Label>
                <Select defaultValue={mockLocations[0].id}>
                  <SelectTrigger>
                    <SelectValue />
                  </SelectTrigger>
                  <SelectContent>
                    {mockLocations.map((l) => (
                      <SelectItem key={l.id} value={l.id}>
                        {l.name}
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
            </div>

            <TabsContent value="Temporary" className="mt-5 space-y-4">
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
                  <Input type="date" defaultValue="2026-06-22" />
                </div>
                <div className="space-y-1.5">
                  <Label>Hourly rate ($)</Label>
                  <Input type="number" defaultValue={38} />
                </div>
                <div className="space-y-1.5">
                  <Label>Start time</Label>
                  <Input type="time" defaultValue="08:00" />
                </div>
                <div className="space-y-1.5">
                  <Label>End time</Label>
                  <Input type="time" defaultValue="16:00" />
                </div>
              </div>
            </TabsContent>

            <TabsContent value="Permanent" className="mt-5 space-y-4">
              <div className="flex items-center justify-between rounded-lg border border-border/60 p-3">
                <div>
                  <Label className="text-sm">Full-time</Label>
                  <p className="text-xs text-muted-foreground">40 hrs / week with benefits</p>
                </div>
                <Switch defaultChecked />
              </div>
              <div className="grid grid-cols-2 gap-3">
                <div className="space-y-1.5">
                  <Label>Salary min ($)</Label>
                  <Input type="number" defaultValue={78000} />
                </div>
                <div className="space-y-1.5">
                  <Label>Salary max ($)</Label>
                  <Input type="number" defaultValue={96000} />
                </div>
              </div>
              <div className="space-y-1.5">
                <Label>Benefits</Label>
                <Input defaultValue="Health, Dental, 401k, PTO" />
              </div>
            </TabsContent>

            <div className="mt-5 space-y-1.5">
              <Label>Notes</Label>
              <Textarea rows={3} placeholder="Anything the candidate should know…" />
            </div>
          </Tabs>
        </div>

        <SheetFooter className="px-4 pb-4">
          <Button variant="outline" onClick={() => setOpen(false)}>
            Cancel
          </Button>
          <Button onClick={submit} className="bg-gradient-brand text-primary-foreground">
            Publish posting
          </Button>
        </SheetFooter>
      </SheetContent>
    </Sheet>
  );
}
