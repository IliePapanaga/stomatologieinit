import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";
import { Slider } from "@/components/ui/slider";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { useAppStore } from "@/lib/store/app-store";
import { toast } from "sonner";
import { Save, MapPin } from "lucide-react";

export const Route = createFileRoute("/professional/profile")({
  component: ProfilePage,
});

function ProfilePage() {
  const profile = useAppStore((s) => s.professionalProfile);
  const update = useAppStore((s) => s.updateProfile);
  const [form, setForm] = useState(profile);

  const set = <K extends keyof typeof form>(k: K, v: (typeof form)[K]) =>
    setForm((f) => ({ ...f, [k]: v }));

  const initials = `${form.firstName[0] ?? ""}${form.lastName[0] ?? ""}`.toUpperCase();

  const onSave = () => {
    update({ ...form, avatarInitials: initials });
    toast.success("Profile saved");
  };

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">My Account</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Edit profile</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Update your contact info and how far you'll travel for shifts.
        </p>
      </header>

      <div className="grid gap-5 lg:grid-cols-3">
        <Card className="flex flex-col items-center gap-3 p-6 lg:col-span-1">
          <Avatar className="h-24 w-24 ring-4 ring-primary/20">
            <AvatarFallback className="bg-primary text-2xl text-primary-foreground">
              {initials || "AB"}
            </AvatarFallback>
          </Avatar>
          <div className="text-center">
            <p className="text-base font-semibold">
              {form.firstName} {form.lastName}
            </p>
            <p className="text-xs text-muted-foreground">{form.email}</p>
            {form.avatarFileName && (
              <p className="mt-1 text-[10px] text-muted-foreground">📎 {form.avatarFileName}</p>
            )}
          </div>
          <label className="cursor-pointer">
            <input
              type="file"
              accept="image/*"
              className="hidden"
              onChange={(e) => {
                const f = e.target.files?.[0];
                if (!f) return;
                set("avatarFileName", f.name);
                toast.success("Avatar selected", { description: f.name });
              }}
            />
            <span className="inline-flex h-9 items-center justify-center rounded-md border border-input bg-background px-3 text-xs font-medium hover:bg-accent">
              Upload avatar
            </span>
          </label>
        </Card>

        <Card className="space-y-5 p-6 lg:col-span-2">
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="First name">
              <Input value={form.firstName} onChange={(e) => set("firstName", e.target.value)} />
            </Field>
            <Field label="Last name">
              <Input value={form.lastName} onChange={(e) => set("lastName", e.target.value)} />
            </Field>
            <Field label="Phone number">
              <Input value={form.phone} onChange={(e) => set("phone", e.target.value)} />
            </Field>
            <Field label="Email">
              <Input value={form.email} onChange={(e) => set("email", e.target.value)} />
            </Field>
          </div>
          <Field label="Home address">
            <Input
              value={form.address}
              onChange={(e) => set("address", e.target.value)}
              placeholder="123 Main St, City, ST"
            />
          </Field>
          <Field label="Short bio">
            <Textarea
              rows={3}
              value={form.bio}
              onChange={(e) => set("bio", e.target.value)}
              placeholder="A few sentences about your background…"
            />
          </Field>

          <div className="space-y-3 rounded-xl border border-border/70 bg-muted/30 p-4">
            <div className="flex items-center justify-between">
              <Label className="flex items-center gap-2 text-sm">
                <MapPin className="h-4 w-4 text-primary" /> Commuting radius
              </Label>
              <span className="text-sm font-semibold text-primary">
                {form.commutingRadius} mi
              </span>
            </div>
            <Slider
              value={[form.commutingRadius]}
              onValueChange={(v) => set("commutingRadius", v[0])}
              min={0}
              max={50}
              step={1}
            />
            <p className="text-[11px] text-muted-foreground">
              You'll only see jobs within this distance of your home base.
            </p>
          </div>

          <div className="flex justify-end">
            <Button onClick={onSave}>
              <Save className="h-4 w-4" /> Save changes
            </Button>
          </div>
        </Card>
      </div>
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
