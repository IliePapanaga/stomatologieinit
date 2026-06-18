import { useEffect, useRef, useState } from "react";
import { motion, AnimatePresence } from "motion/react";
import { Siren, CheckCircle2 } from "lucide-react";
import {
  Sheet,
  SheetContent,
  SheetDescription,
  SheetFooter,
  SheetHeader,
  SheetTitle,
} from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Slider } from "@/components/ui/slider";
import { Textarea } from "@/components/ui/textarea";
import { useCreateSosRequest } from "@/lib/hooks/practice";
import { mockLocations } from "@/lib/mock";
import type { ProfessionalSpecialty } from "@/lib/types/mdd";
import { toast } from "sonner";

const HOLD_MS = 3000;

export function SosButton() {
  const [open, setOpen] = useState(false);
  const [confirm, setConfirm] = useState(false);
  const [progress, setProgress] = useState(0);
  const [specialty, setSpecialty] = useState<ProfessionalSpecialty>("Hygienist");
  const [locationId, setLocationId] = useState(mockLocations[0].id);
  const [radius, setRadius] = useState(12);
  const [message, setMessage] = useState("");

  const startRef = useRef<number | null>(null);
  const rafRef = useRef<number | null>(null);

  const sos = useCreateSosRequest();

  const stop = (fired: boolean) => {
    if (rafRef.current) cancelAnimationFrame(rafRef.current);
    rafRef.current = null;
    startRef.current = null;
    if (!fired) setProgress(0);
  };

  const tick = () => {
    if (startRef.current == null) return;
    const elapsed = performance.now() - startRef.current;
    const pct = Math.min(1, elapsed / HOLD_MS);
    setProgress(pct);
    if (pct >= 1) {
      stop(true);
      setOpen(true);
      return;
    }
    rafRef.current = requestAnimationFrame(tick);
  };

  const handleDown = (e: React.PointerEvent) => {
    e.preventDefault();
    startRef.current = performance.now();
    rafRef.current = requestAnimationFrame(tick);
  };
  const handleUp = () => {
    if (progress < 1) stop(false);
  };

  useEffect(() => () => stop(false), []);

  const handleConfirm = async () => {
    const res = await sos.mutateAsync({ locationId, specialty, radius, message });
    setConfirm(true);
    setTimeout(() => {
      setOpen(false);
      setConfirm(false);
      setProgress(0);
      setMessage("");
      toast.success("SOS broadcast sent", {
        description: `Notifying ${specialty}s within ${radius} mi · ${res.id}`,
      });
    }, 1100);
  };

  const RADIUS = 54;
  const CIRC = 2 * Math.PI * RADIUS;

  return (
    <>
      <div className="relative flex flex-col items-center justify-center rounded-2xl border border-destructive/25 bg-gradient-to-br from-destructive/5 via-warning/5 to-transparent p-5 shadow-soft sm:p-6">
        <div className="mb-3 flex items-center gap-1.5 text-[11px] font-medium uppercase tracking-wider text-destructive">
          <Siren className="h-3.5 w-3.5" /> Emergency staffing
        </div>

        <button
          type="button"
          onPointerDown={handleDown}
          onPointerUp={handleUp}
          onPointerLeave={handleUp}
          onPointerCancel={handleUp}
          aria-label="Hold to send SOS"
          className="relative flex h-36 w-36 select-none items-center justify-center rounded-full text-primary-foreground transition-transform active:scale-95"
        >
          {/* gradient body */}
          <span className="absolute inset-0 rounded-full bg-gradient-sos shadow-elevated" />
          <span className="absolute inset-0 rounded-full opacity-0 transition-opacity duration-300 group-hover:opacity-100 dark:opacity-40 dark:shadow-glow" />
          {/* progress ring */}
          <svg className="absolute inset-0 -rotate-90" viewBox="0 0 120 120">
            <circle
              cx="60"
              cy="60"
              r={RADIUS}
              fill="none"
              stroke="white"
              strokeOpacity="0.25"
              strokeWidth="4"
            />
            <circle
              cx="60"
              cy="60"
              r={RADIUS}
              fill="none"
              stroke="white"
              strokeWidth="4"
              strokeLinecap="round"
              strokeDasharray={CIRC}
              strokeDashoffset={CIRC * (1 - progress)}
              style={{ transition: progress === 0 ? "stroke-dashoffset 0.25s ease" : "none" }}
            />
          </svg>
          <div className="relative flex flex-col items-center gap-1">
            <Siren className="h-7 w-7" />
            <span className="text-sm font-semibold tracking-tight">I need staff</span>
            <span className="text-[10px] font-medium uppercase tracking-widest opacity-80">
              NOW
            </span>
          </div>
        </button>

        <p className="mt-4 text-center text-xs text-muted-foreground">
          Hold for 3 seconds to broadcast a <span className="font-medium text-foreground">SosRequest</span> to nearby pros.
        </p>
      </div>

      <Sheet open={open} onOpenChange={(o) => { setOpen(o); if (!o) setProgress(0); }}>
        <SheetContent side="right" className="w-full sm:max-w-md flex flex-col">
          <SheetHeader>
            <SheetTitle className="flex items-center gap-2">
              <span className="flex h-9 w-9 items-center justify-center rounded-full bg-destructive/10 text-destructive">
                <Siren className="h-4 w-4" />
              </span>
              Confirm SOS broadcast
            </SheetTitle>
            <SheetDescription>
              Verified pros nearby will get an instant push & SMS.
            </SheetDescription>
          </SheetHeader>

          <div className="mt-6 flex-1 space-y-5 px-4">
            <div className="space-y-1.5">
              <Label>Role needed</Label>
              <Select value={specialty} onValueChange={(v) => setSpecialty(v as ProfessionalSpecialty)}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  {(["Hygienist", "Dentist", "Assistant", "FrontOffice", "Orthodontist"] as ProfessionalSpecialty[]).map((s) => (
                    <SelectItem key={s} value={s}>{s}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-1.5">
              <Label>Location</Label>
              <Select value={locationId} onValueChange={setLocationId}>
                <SelectTrigger><SelectValue /></SelectTrigger>
                <SelectContent>
                  {mockLocations.map((l) => (
                    <SelectItem key={l.id} value={l.id}>{l.name}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
            </div>

            <div className="space-y-2">
              <div className="flex items-center justify-between">
                <Label>Search radius</Label>
                <span className="text-sm font-semibold tabular-nums">{radius} mi</span>
              </div>
              <Slider value={[radius]} onValueChange={([v]) => setRadius(v)} min={2} max={30} step={1} />
            </div>

            <div className="space-y-1.5">
              <Label>Message (optional)</Label>
              <Textarea
                placeholder="Need coverage for afternoon prophy block…"
                value={message}
                onChange={(e) => setMessage(e.target.value)}
                rows={3}
              />
            </div>
          </div>

          <SheetFooter className="px-4 pb-4">
            <Button variant="outline" onClick={() => setOpen(false)} disabled={sos.isPending || confirm}>
              Cancel
            </Button>
            <Button
              onClick={handleConfirm}
              disabled={sos.isPending || confirm}
              className="bg-gradient-sos text-primary-foreground"
            >
              <AnimatePresence mode="wait" initial={false}>
                {confirm ? (
                  <motion.span key="ok" initial={{ scale: 0.5, opacity: 0 }} animate={{ scale: 1, opacity: 1 }} className="flex items-center gap-2">
                    <CheckCircle2 className="h-4 w-4" /> Sent
                  </motion.span>
                ) : (
                  <motion.span key="send" initial={{ opacity: 0 }} animate={{ opacity: 1 }} className="flex items-center gap-2">
                    <Siren className="h-4 w-4" /> Broadcast SOS
                  </motion.span>
                )}
              </AnimatePresence>
            </Button>
          </SheetFooter>
        </SheetContent>
      </Sheet>
    </>
  );
}
