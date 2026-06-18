import { useState } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  Building2,
  MapPin,
  Bell,
  Plus,
  Pencil,
  Trash2,
  Mail,
  Smartphone,
  MessageSquare,
} from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Tabs, TabsList, TabsTrigger, TabsContent } from "@/components/ui/tabs";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";
import { Switch } from "@/components/ui/switch";
import { Separator } from "@/components/ui/separator";
import { Badge } from "@/components/ui/badge";
import { mockPractice, mockLocations } from "@/lib/mock";
import { toast } from "sonner";

export const Route = createFileRoute("/practice/settings")({
  component: SettingsPage,
});

function SettingsPage() {
  return (
    <div className="space-y-6 p-6">
      <header>
        <h1 className="text-2xl font-semibold tracking-tight">Settings</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Manage your practice profile, locations, and notification preferences.
        </p>
      </header>

      <Tabs defaultValue="practice" className="space-y-6">
        <TabsList>
          <TabsTrigger value="practice" className="gap-2">
            <Building2 className="h-3.5 w-3.5" /> Practice
          </TabsTrigger>
          <TabsTrigger value="locations" className="gap-2">
            <MapPin className="h-3.5 w-3.5" /> Locations
          </TabsTrigger>
          <TabsTrigger value="notifications" className="gap-2">
            <Bell className="h-3.5 w-3.5" /> Notifications
          </TabsTrigger>
        </TabsList>

        <TabsContent value="practice"><PracticeTab /></TabsContent>
        <TabsContent value="locations"><LocationsTab /></TabsContent>
        <TabsContent value="notifications"><NotificationsTab /></TabsContent>
      </Tabs>
    </div>
  );
}

function PracticeTab() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      className="grid gap-6 lg:grid-cols-3"
    >
      <Card className="lg:col-span-2 border-border/70 shadow-sm">
        <CardHeader>
          <CardTitle className="text-base">Practice details</CardTitle>
          <p className="text-xs text-muted-foreground">Visible to professionals when they browse opportunities.</p>
        </CardHeader>
        <CardContent className="space-y-5">
          <div className="grid gap-4 sm:grid-cols-2">
            <Field label="Company name" defaultValue={mockPractice.companyName} />
            <Field label="Status" defaultValue={mockPractice.status} disabled />
            <Field label="Owner first name" defaultValue={mockPractice.ownerFirstName} />
            <Field label="Owner last name" defaultValue={mockPractice.ownerLastName} />
            <Field label="Email" type="email" defaultValue={mockPractice.email} />
            <Field label="Phone" defaultValue={mockPractice.phone} />
          </div>
          <Separator />
          <div className="flex justify-end gap-2">
            <Button variant="outline">Cancel</Button>
            <Button
              className="bg-gradient-brand text-primary-foreground"
              onClick={() => toast.success("Practice details saved")}
            >
              Save changes
            </Button>
          </div>
        </CardContent>
      </Card>

      <Card className="border-border/70 shadow-sm">
        <CardHeader>
          <CardTitle className="text-base">Account</CardTitle>
          <p className="text-xs text-muted-foreground">Plan and billing summary.</p>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="rounded-xl border border-primary/30 bg-primary/5 p-4">
            <div className="flex items-center justify-between">
              <p className="text-sm font-semibold">Growth · Annual</p>
              <Badge className="bg-gradient-brand text-primary-foreground">Active</Badge>
            </div>
            <p className="mt-1 text-xs text-muted-foreground">Renews March 14, 2027</p>
          </div>
          <Stat label="Locations" value={String(mockLocations.length)} />
          <Stat label="Active postings" value="6" />
          <Stat label="Team seats" value="4 / 10" />
        </CardContent>
      </Card>
    </motion.div>
  );
}

function Stat({ label, value }: { label: string; value: string }) {
  return (
    <div className="flex items-center justify-between text-sm">
      <span className="text-muted-foreground">{label}</span>
      <span className="font-medium tabular-nums">{value}</span>
    </div>
  );
}

function Field({
  label,
  defaultValue,
  type = "text",
  disabled,
}: {
  label: string;
  defaultValue?: string;
  type?: string;
  disabled?: boolean;
}) {
  return (
    <div className="space-y-1.5">
      <Label className="text-xs">{label}</Label>
      <Input defaultValue={defaultValue} type={type} disabled={disabled} />
    </div>
  );
}

function LocationsTab() {
  return (
    <motion.div
      initial={{ opacity: 0, y: 6 }}
      animate={{ opacity: 1, y: 0 }}
      className="space-y-4"
    >
      <div className="flex items-center justify-between">
        <p className="text-sm text-muted-foreground">
          {mockLocations.length} location{mockLocations.length === 1 ? "" : "s"} configured
        </p>
        <Button
          className="bg-gradient-brand text-primary-foreground"
          onClick={() => toast("Open the location creator")}
        >
          <Plus className="h-4 w-4" /> Add location
        </Button>
      </div>

      <div className="grid gap-4 md:grid-cols-2">
        {mockLocations.map((loc, i) => (
          <motion.div
            key={loc.id}
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ delay: i * 0.08 }}
          >
            <Card className="overflow-hidden border-border/70 shadow-sm">
              <MapPlaceholder lat={loc.lat} lng={loc.lng} radius={loc.radius} />
              <CardContent className="space-y-3 pt-4">
                <div className="flex items-start justify-between">
                  <div>
                    <h3 className="font-semibold">{loc.name}</h3>
                    <p className="text-xs text-muted-foreground">
                      {loc.address.street}, {loc.address.city}, {loc.address.state} {loc.address.zip}
                    </p>
                  </div>
                  <div className="flex gap-1">
                    <Button variant="ghost" size="icon" className="h-7 w-7">
                      <Pencil className="h-3.5 w-3.5" />
                    </Button>
                    <Button variant="ghost" size="icon" className="h-7 w-7 text-muted-foreground hover:text-destructive">
                      <Trash2 className="h-3.5 w-3.5" />
                    </Button>
                  </div>
                </div>
                <Separator />
                <div className="grid grid-cols-3 gap-2 text-center text-xs">
                  <Meta label="Radius" value={`${loc.radius} mi`} />
                  <Meta label="Contact" value={`${loc.contactFirstName} ${loc.contactLastName[0]}.`} />
                  <Meta label="Phone" value={loc.phone.slice(-4)} prefix="•••• " />
                </div>
              </CardContent>
            </Card>
          </motion.div>
        ))}
      </div>
    </motion.div>
  );
}

function Meta({ label, value, prefix }: { label: string; value: string; prefix?: string }) {
  return (
    <div className="rounded-lg bg-muted/40 px-2 py-2">
      <p className="text-[10px] uppercase tracking-wide text-muted-foreground">{label}</p>
      <p className="mt-0.5 text-xs font-medium">{prefix}{value}</p>
    </div>
  );
}

function MapPlaceholder({ lat, lng, radius }: { lat: number; lng: number; radius: number }) {
  return (
    <div className="relative h-36 w-full overflow-hidden bg-gradient-to-br from-primary/15 via-muted to-secondary/20">
      {/* Decorative grid */}
      <svg className="absolute inset-0 h-full w-full opacity-40" xmlns="http://www.w3.org/2000/svg">
        <defs>
          <pattern id={`grid-${lat}`} width="24" height="24" patternUnits="userSpaceOnUse">
            <path d="M 24 0 L 0 0 0 24" fill="none" stroke="currentColor" strokeWidth="0.5" className="text-foreground/20" />
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill={`url(#grid-${lat})`} />
      </svg>

      {/* Radius ring */}
      <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2">
        <motion.div
          initial={{ scale: 0.6, opacity: 0 }}
          animate={{ scale: 1, opacity: 1 }}
          className="relative h-28 w-28 rounded-full border-2 border-primary/40 bg-primary/10"
        >
          <div className="absolute left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2">
            <div className="flex h-7 w-7 items-center justify-center rounded-full bg-primary text-primary-foreground shadow-lg shadow-primary/30">
              <MapPin className="h-4 w-4" />
            </div>
          </div>
        </motion.div>
      </div>

      <div className="absolute bottom-2 left-2 flex items-center gap-1.5 rounded-md bg-background/80 px-2 py-1 text-[10px] font-medium backdrop-blur">
        <span className="h-1.5 w-1.5 rounded-full bg-emerald-500" />
        Google Maps · {radius} mi radius
      </div>
      <div className="absolute bottom-2 right-2 rounded-md bg-background/80 px-2 py-1 font-mono text-[10px] text-muted-foreground backdrop-blur">
        {lat.toFixed(4)}, {lng.toFixed(4)}
      </div>
    </div>
  );
}

interface NotificationPref {
  id: string;
  label: string;
  description: string;
  email: boolean;
  sms: boolean;
  push: boolean;
}

const initialPrefs: NotificationPref[] = [
  { id: "applicants", label: "New applicants", description: "Someone applies to one of your postings", email: true, sms: false, push: true },
  { id: "checkin", label: "Shift check-ins", description: "Professional checks in or out", email: false, sms: true, push: true },
  { id: "noshow", label: "No-shows & late alerts", description: "Critical attendance events", email: true, sms: true, push: true },
  { id: "sos", label: "SOS matched", description: "A professional accepts your emergency SOS", email: true, sms: true, push: true },
  { id: "billing", label: "Billing & invoices", description: "Successful payments and statements", email: true, sms: false, push: false },
  { id: "weekly", label: "Weekly summary", description: "Performance digest every Monday", email: true, sms: false, push: false },
];

function NotificationsTab() {
  const [prefs, setPrefs] = useState(initialPrefs);

  const toggle = (id: string, channel: "email" | "sms" | "push") =>
    setPrefs((p) => p.map((x) => (x.id === id ? { ...x, [channel]: !x[channel] } : x)));

  return (
    <motion.div initial={{ opacity: 0, y: 6 }} animate={{ opacity: 1, y: 0 }}>
      <Card className="border-border/70 shadow-sm">
        <CardHeader>
          <CardTitle className="text-base">Channels</CardTitle>
          <p className="text-xs text-muted-foreground">Pick how you want to hear about each event type.</p>
        </CardHeader>
        <CardContent className="p-0">
          <div className="hidden grid-cols-[1fr_auto_auto_auto] gap-x-8 border-b border-border/60 px-6 py-3 text-[10px] font-medium uppercase tracking-wider text-muted-foreground sm:grid">
            <span>Event</span>
            <span className="flex items-center gap-1.5"><Mail className="h-3 w-3" /> Email</span>
            <span className="flex items-center gap-1.5"><MessageSquare className="h-3 w-3" /> SMS</span>
            <span className="flex items-center gap-1.5"><Smartphone className="h-3 w-3" /> Push</span>
          </div>
          {prefs.map((p, i) => (
            <motion.div
              key={p.id}
              initial={{ opacity: 0, x: -6 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: i * 0.04 }}
              className="grid grid-cols-1 items-center gap-3 border-b border-border/60 px-6 py-4 last:border-b-0 sm:grid-cols-[1fr_auto_auto_auto] sm:gap-x-8"
            >
              <div className="min-w-0">
                <p className="text-sm font-medium">{p.label}</p>
                <p className="text-xs text-muted-foreground">{p.description}</p>
              </div>
              <div className="flex gap-6 sm:contents">
                <ChannelToggle label="Email" checked={p.email} onChange={() => toggle(p.id, "email")} />
                <ChannelToggle label="SMS" checked={p.sms} onChange={() => toggle(p.id, "sms")} />
                <ChannelToggle label="Push" checked={p.push} onChange={() => toggle(p.id, "push")} />
              </div>
            </motion.div>
          ))}
        </CardContent>
      </Card>

      <div className="mt-4 flex justify-end">
        <Button
          className="bg-gradient-brand text-primary-foreground"
          onClick={() => toast.success("Notification preferences saved")}
        >
          Save preferences
        </Button>
      </div>
    </motion.div>
  );
}

function ChannelToggle({
  label,
  checked,
  onChange,
}: {
  label: string;
  checked: boolean;
  onChange: () => void;
}) {
  return (
    <div className="flex items-center gap-2">
      <span className="text-[10px] uppercase tracking-wide text-muted-foreground sm:hidden">{label}</span>
      <Switch checked={checked} onCheckedChange={onChange} />
    </div>
  );
}
