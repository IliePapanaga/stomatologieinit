import { useMemo, useState } from "react";
import { createFileRoute, useNavigate, useRouter, Link } from "@tanstack/react-router";
import { useTranslation } from "react-i18next";
import i18n from "@/lib/i18n";
import { motion, AnimatePresence } from "motion/react";
import {
  Stethoscope,
  Building2,
  Sparkles,
  ArrowRight,
  ArrowLeft,
  Mail,
  Lock,
  Loader2,
  Chrome,
  MapPin,
  Users,
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useAppStore, dashboardForRole, type AppRole, type AppUser } from "@/lib/store/app-store";
import { toast } from "sonner";

export const Route = createFileRoute("/login")({
  component: LoginPage,
});

type Step = "email" | "password" | "role" | "signup-practice" | "signup-pro";

function LoginPage() {
  const [step, setStep] = useState<Step>("email");
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [loading, setLoading] = useState(false);
  const [role, setRole] = useState<AppRole>("PracticeOwner");
  const { t } = useTranslation();

  const findUserByEmail = useAppStore((s) => s.findUserByEmail);
  const loginWithPassword = useAppStore((s) => s.loginWithPassword);
  const addUser = useAppStore((s) => s.addUser);
  const navigate = useNavigate();
  const router = useRouter();

  const goDashboard = async (user: AppUser) => {
    await router.invalidate();
    navigate({ to: dashboardForRole(user.role) });
  };

  const onContinueEmail = async () => {
    if (!email.includes("@")) {
      toast.error("Enter a valid email");
      return;
    }
    setLoading(true);
    await new Promise((r) => setTimeout(r, 220));
    setLoading(false);
    const found = findUserByEmail(email);
    setStep(found ? "password" : "role");
  };

  const onSignIn = async () => {
    setLoading(true);
    await new Promise((r) => setTimeout(r, 260));
    const u = loginWithPassword(email, password);
    setLoading(false);
    if (!u) {
      toast.error("Incorrect password");
      return;
    }
    toast.success(`Welcome back, ${u.firstName}`);
    goDashboard(u);
  };

  const onPickRole = (r: AppRole) => {
    setRole(r);
    setStep(r === "PracticeOwner" ? "signup-practice" : "signup-pro");
  };

  return (
    <div className="relative flex min-h-svh items-center justify-center overflow-hidden bg-background px-4 py-10">
      <div className="pointer-events-none absolute inset-0">
        <div className="absolute -left-32 top-10 h-80 w-80 rounded-full bg-primary/20 blur-3xl" />
        <div className="absolute -right-24 bottom-0 h-96 w-96 rounded-full bg-indigo-500/20 blur-3xl" />
      </div>

      <motion.div
        initial={{ opacity: 0, y: 16 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.4 }}
        className="relative w-full max-w-md"
      >
        <div className="mb-6 text-center">
          <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-lg shadow-primary/30">
            <Sparkles className="h-5 w-5" />
          </div>
          <h1 className="mt-4 text-2xl font-semibold tracking-tight">{t("welcome_to_mdd")}</h1>
          <p className="mt-1.5 text-sm text-muted-foreground">
            {step === "email" && t("enter_email")}
            {step === "password" && t("welcome_back_enter_password")}
            {step === "role" && t("pick_role")}
            {step === "signup-practice" && t("tell_us_about")}
            {step === "signup-pro" && t("create_pro_account")}
          </p>
        </div>

        <Card className="overflow-hidden border-border/70 p-6 shadow-xl backdrop-blur">
          <AnimatePresence mode="wait">
            {step === "email" && (
              <motion.div
                key="email"
                initial={{ opacity: 0, x: 8 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -8 }}
                className="space-y-4"
              >
                <Field label="Email">
                  <div className="relative">
                    <Mail className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                      type="email"
                      autoFocus
                      value={email}
                      placeholder="you@clinic.com"
                      className="pl-9"
                      onChange={(e) => setEmail(e.target.value)}
                      onKeyDown={(e) => e.key === "Enter" && onContinueEmail()}
                    />
                  </div>
                </Field>
                <Button onClick={onContinueEmail} disabled={loading} className="w-full">
                  {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : t("continue")}
                  {!loading && <ArrowRight className="h-4 w-4" />}
                </Button>
                <DemoHints
                  onPick={(e) => {
                    setEmail(e);
                  }}
                />
              </motion.div>
            )}

            {step === "password" && (
              <motion.div
                key="password"
                initial={{ opacity: 0, x: 8 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -8 }}
                className="space-y-4"
              >
                <div className="rounded-lg border border-border/60 bg-muted/40 px-3 py-2 text-xs">
                  Signing in as <span className="font-semibold">{email}</span>
                </div>
                <Field label="Password">
                  <div className="relative">
                    <Lock className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
                    <Input
                      type="password"
                      autoFocus
                      value={password}
                      placeholder="••••••••"
                      className="pl-9"
                      onChange={(e) => setPassword(e.target.value)}
                      onKeyDown={(e) => e.key === "Enter" && onSignIn()}
                    />
                  </div>
                  <p className="text-[11px] text-muted-foreground">
                    Demo password for seeded users: <span className="font-mono">demo</span>
                  </p>
                </Field>
                <div className="flex gap-2">
                  <Button variant="outline" onClick={() => setStep("email")} className="flex-1">
                    <ArrowLeft className="h-4 w-4" /> Back
                  </Button>
                  <Button onClick={onSignIn} disabled={loading || !password} className="flex-1">
                    {loading ? <Loader2 className="h-4 w-4 animate-spin" /> : "Sign in"}
                  </Button>
                </div>
              </motion.div>
            )}

            {step === "role" && (
              <motion.div
                key="role"
                initial={{ opacity: 0, x: 8 }}
                animate={{ opacity: 1, x: 0 }}
                exit={{ opacity: 0, x: -8 }}
                className="space-y-3"
              >
                <RoleCard
                  icon={Building2}
                  title="Owner"
                  tagline="Post jobs, manage staff & schedule shifts."
                  onClick={() => onPickRole("PracticeOwner")}
                />
                <RoleCard
                  icon={Stethoscope}
                  title="Professional"
                  tagline="Find shifts, manage certificates, get hired."
                  onClick={() => onPickRole("Professional")}
                />
                <Button variant="ghost" onClick={() => setStep("email")} className="w-full">
                  <ArrowLeft className="h-4 w-4" /> Use a different email
                </Button>
              </motion.div>
            )}

            {step === "signup-practice" && (
              <PracticeSignupForm
                email={email}
                onBack={() => setStep("role")}
                onDone={(u) => {
                  toast.success(`Owner "${u.tenant}" created`);
                  goDashboard(u);
                }}
                add={addUser}
              />
            )}

            {step === "signup-pro" && (
              <ProSignupForm
                email={email}
                onBack={() => setStep("role")}
                onDone={(u) => {
                  toast.success(`Welcome aboard, ${u.firstName}!`);
                  goDashboard(u);
                }}
                add={addUser}
              />
            )}
          </AnimatePresence>
        </Card>

        <p className="mt-4 text-center text-[11px] text-muted-foreground">
          {t("demo_env_notice")}{" "}
          <Link to="/" className="underline">
            {t("back_to_home")}
          </Link>
        </p>
      </motion.div>

      <Button
        variant="ghost"
        size="sm"
        onClick={() => i18n.changeLanguage(i18n.language === "en" ? "es" : "en")}
        className="absolute bottom-4 left-4 z-50 flex items-center gap-2 rounded-lg text-muted-foreground hover:bg-muted/50 hover:text-foreground"
      >
        <span className="text-lg">{i18n.language === "en" ? "🇪🇸" : "🇺🇸"}</span>
        <span>{i18n.language === "en" ? "Español" : "English"}</span>
      </Button>
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

function RoleCard({
  icon: Icon,
  title,
  tagline,
  onClick,
}: {
  icon: typeof Building2;
  title: string;
  tagline: string;
  onClick: () => void;
}) {
  return (
    <button
      onClick={onClick}
      className="group flex w-full items-center gap-4 rounded-xl border border-border/60 bg-muted/30 p-4 text-left transition hover:border-primary/50 hover:bg-primary/5"
    >
      <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-lg bg-primary/10 text-primary">
        <Icon className="h-5 w-5" />
      </div>
      <div className="min-w-0 flex-1">
        <p className="text-sm font-semibold">{title}</p>
        <p className="truncate text-xs text-muted-foreground">{tagline}</p>
      </div>
      <ArrowRight className="h-4 w-4 text-muted-foreground transition group-hover:text-primary" />
    </button>
  );
}

const ADDRESS_SUGGESTIONS = [
  "240 Market St, San Francisco, CA",
  "1500 Broadway, Oakland, CA",
  "2200 Shattuck Ave, Berkeley, CA",
  "55 El Camino Real, San Mateo, CA",
];

function PracticeSignupForm({
  email,
  onBack,
  onDone,
  add,
}: {
  email: string;
  onBack: () => void;
  onDone: (u: AppUser) => void;
  add: (u: AppUser) => AppUser;
}) {
  const [org, setOrg] = useState("");
  const [addr, setAddr] = useState("");
  const [employees, setEmployees] = useState<"1-10" | "11-50" | "50+">("1-10");
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [password, setPassword] = useState("");
  const [showAddrList, setShowAddrList] = useState(false);

  const filtered = useMemo(
    () =>
      addr.length > 1
        ? ADDRESS_SUGGESTIONS.filter((a) => a.toLowerCase().includes(addr.toLowerCase()))
        : ADDRESS_SUGGESTIONS,
    [addr],
  );

  const submit = () => {
    if (!org || !addr || !firstName || !lastName || !password) {
      toast.error("Please fill every field");
      return;
    }
    const u: AppUser = {
      id: `u_${Date.now()}`,
      firstName,
      lastName,
      email,
      password,
      role: "PracticeOwner",
      tenant: org,
      avatarInitials: (firstName[0] + lastName[0]).toUpperCase(),
      practiceAddress: addr,
      employeesCount: employees,
    };
    onDone(add(u));
  };

  return (
    <motion.div
      key="signup-practice"
      initial={{ opacity: 0, x: 8 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -8 }}
      className="space-y-3"
    >
      <Field label="Organization / Owner name">
        <Input
          value={org}
          onChange={(e) => setOrg(e.target.value)}
          placeholder="Brightside Dental"
        />
      </Field>
      <Field label="Owner address">
        <div className="relative">
          <MapPin className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={addr}
            onFocus={() => setShowAddrList(true)}
            onBlur={() => setTimeout(() => setShowAddrList(false), 150)}
            onChange={(e) => {
              setAddr(e.target.value);
              setShowAddrList(true);
            }}
            placeholder="Start typing…"
            className="pl-9"
          />
          {showAddrList && filtered.length > 0 && (
            <div className="absolute z-10 mt-1 w-full overflow-hidden rounded-lg border border-border/70 bg-popover shadow-lg">
              {filtered.map((s) => (
                <button
                  key={s}
                  type="button"
                  onMouseDown={() => {
                    setAddr(s);
                    setShowAddrList(false);
                  }}
                  className="flex w-full items-center gap-2 px-3 py-2 text-left text-xs hover:bg-muted"
                >
                  <MapPin className="h-3 w-3 text-primary" /> {s}
                </button>
              ))}
            </div>
          )}
        </div>
      </Field>
      <Field label="Number of employees">
        <Select value={employees} onValueChange={(v) => setEmployees(v as typeof employees)}>
          <SelectTrigger>
            <Users className="h-4 w-4 text-muted-foreground" />
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="1-10">1 – 10</SelectItem>
            <SelectItem value="11-50">11 – 50</SelectItem>
            <SelectItem value="50+">50+</SelectItem>
          </SelectContent>
        </Select>
      </Field>
      <div className="grid grid-cols-2 gap-3">
        <Field label="Contact first name">
          <Input value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        </Field>
        <Field label="Contact last name">
          <Input value={lastName} onChange={(e) => setLastName(e.target.value)} />
        </Field>
      </div>
      <Field label="Password">
        <Input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="••••••••"
        />
      </Field>
      <div className="flex gap-2 pt-1">
        <Button variant="outline" onClick={onBack} className="flex-1">
          <ArrowLeft className="h-4 w-4" /> Back
        </Button>
        <Button onClick={submit} className="flex-1">
          Create account <ArrowRight className="h-4 w-4" />
        </Button>
      </div>
    </motion.div>
  );
}

function ProSignupForm({
  email,
  onBack,
  onDone,
  add,
}: {
  email: string;
  onBack: () => void;
  onDone: (u: AppUser) => void;
  add: (u: AppUser) => AppUser;
}) {
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [password, setPassword] = useState("");

  const submit = () => {
    if (!firstName || !lastName || !password) {
      toast.error("Please fill every field");
      return;
    }
    const u: AppUser = {
      id: `u_${Date.now()}`,
      firstName,
      lastName,
      email,
      password,
      role: "Professional",
      tenant: "Independent",
      avatarInitials: (firstName[0] + lastName[0]).toUpperCase(),
    };
    onDone(add(u));
  };

  const google = () => {
    const u: AppUser = {
      id: `u_g_${Date.now()}`,
      firstName: "Alex",
      lastName: "Google",
      email,
      password: "google-oauth",
      role: "Professional",
      tenant: "Independent",
      avatarInitials: "AG",
    };
    onDone(add(u));
  };

  return (
    <motion.div
      key="signup-pro"
      initial={{ opacity: 0, x: 8 }}
      animate={{ opacity: 1, x: 0 }}
      exit={{ opacity: 0, x: -8 }}
      className="space-y-3"
    >
      <Button
        variant="outline"
        onClick={google}
        className="w-full border-border/70 bg-background/60"
      >
        <Chrome className="h-4 w-4" /> Sign up with Google
      </Button>
      <div className="flex items-center gap-3">
        <div className="h-px flex-1 bg-border" />
        <span className="text-[10px] uppercase tracking-wider text-muted-foreground">or</span>
        <div className="h-px flex-1 bg-border" />
      </div>
      <div className="grid grid-cols-2 gap-3">
        <Field label="First name">
          <Input value={firstName} onChange={(e) => setFirstName(e.target.value)} />
        </Field>
        <Field label="Last name">
          <Input value={lastName} onChange={(e) => setLastName(e.target.value)} />
        </Field>
      </div>
      <Field label="Password">
        <Input
          type="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="••••••••"
        />
      </Field>
      <div className="flex gap-2 pt-1">
        <Button variant="outline" onClick={onBack} className="flex-1">
          <ArrowLeft className="h-4 w-4" /> Back
        </Button>
        <Button onClick={submit} className="flex-1">
          Create account <ArrowRight className="h-4 w-4" />
        </Button>
      </div>
    </motion.div>
  );
}

function DemoHints({ onPick }: { onPick: (email: string) => void }) {
  const demos = [
    { label: "Owner", email: "maya@brightsidedental.com" },
    { label: "Professional", email: "amelia.brooks@mdd.health" },
    { label: "Admin", email: "sam@mdd.health" },
  ];
  return (
    <div className="rounded-lg border border-dashed border-border/60 bg-muted/30 p-3">
      <p className="mb-2 text-[10px] uppercase tracking-wider text-muted-foreground">
        Demo accounts (password: demo)
      </p>
      <div className="flex flex-wrap gap-1.5">
        {demos.map((d) => (
          <button
            key={d.email}
            type="button"
            onClick={() => onPick(d.email)}
            className="rounded-md border border-border/70 bg-background px-2 py-1 text-[11px] hover:border-primary/50 hover:text-primary"
          >
            {d.label}
          </button>
        ))}
      </div>
    </div>
  );
}
