import { useMemo, useState } from "react";
import { createFileRoute, useNavigate } from "@tanstack/react-router";
import { useAppStore, dashboardForRole, type AppRole, type AppUser } from "@/lib/store/app-store";
import { motion } from "motion/react";
import {
  Search,
  UserCog,
  ShieldCheck,
  Building2,
  Stethoscope,
  KeyRound,
  MoreHorizontal,
  Ban,
  CheckCircle2,
  Mail,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import {
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from "@/components/ui/alert-dialog";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { toast } from "sonner";
import type { UserRole } from "@/lib/types/mdd";
import { useTranslation } from "react-i18next";
import i18n from "@/lib/i18n";

export const Route = createFileRoute("/admin/users")({
  component: UsersPage,
});

interface AdminUser {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  role: UserRole;
  tenant: string;
  status: "Active" | "Suspended" | "Pending";
  lastSeen: string;
}

const baseUsers: AdminUser[] = [
  {
    id: "u_001",
    firstName: "Maya",
    lastName: "Chen",
    email: "maya@brightsidedental.com",
    role: "PracticeOwner",
    tenant: "Brightside Dental Group",
    status: "Active",
    lastSeen: "2m ago",
  },
  {
    id: "u_002",
    firstName: "Amelia",
    lastName: "Brooks",
    email: "amelia@example.com",
    role: "Professional",
    tenant: "—",
    status: "Active",
    lastSeen: "14m ago",
  },
  {
    id: "u_003",
    firstName: "Noah",
    lastName: "Patel",
    email: "noah@example.com",
    role: "Professional",
    tenant: "—",
    status: "Pending",
    lastSeen: "3h ago",
  },
  {
    id: "u_004",
    firstName: "Alex",
    lastName: "Chen",
    email: "achen@mdd.health",
    role: "SystemAdmin",
    tenant: "MDD HQ",
    status: "Active",
    lastSeen: "1m ago",
  },
  {
    id: "u_005",
    firstName: "Sofia",
    lastName: "Nguyen",
    email: "sofia@northpoint.dental",
    role: "PracticeOwner",
    tenant: "Northpoint Dental",
    status: "Active",
    lastSeen: "1d ago",
  },
  {
    id: "u_006",
    firstName: "Liam",
    lastName: "Okafor",
    email: "liam@example.com",
    role: "Professional",
    tenant: "—",
    status: "Suspended",
    lastSeen: "9d ago",
  },
  {
    id: "u_007",
    firstName: "Zara",
    lastName: "Reyes",
    email: "zara@example.com",
    role: "Professional",
    tenant: "—",
    status: "Active",
    lastSeen: "27m ago",
  },
  {
    id: "u_008",
    firstName: "Ethan",
    lastName: "Kim",
    email: "ethan@summit.dental",
    role: "PracticeOwner",
    tenant: "Summit Dental Care",
    status: "Active",
    lastSeen: "4h ago",
  },
  {
    id: "u_009",
    firstName: "Priya",
    lastName: "Nair",
    email: "priya@brightsidedental.com",
    role: "PracticeOwner",
    tenant: "Brightside Dental Group",
    status: "Active",
    lastSeen: "12m ago",
  },
  {
    id: "u_010",
    firstName: "Sam",
    lastName: "Johnson",
    email: "sjohnson@mdd.health",
    role: "SuperAdmin",
    tenant: "MDD HQ",
    status: "Active",
    lastSeen: "just now",
  },
  {
    id: "u_011",
    firstName: "Hana",
    lastName: "Suzuki",
    email: "hana@example.com",
    role: "Professional",
    tenant: "—",
    status: "Active",
    lastSeen: "2h ago",
  },
  {
    id: "u_012",
    firstName: "Marcus",
    lastName: "Diaz",
    email: "marcus@coastal.dental",
    role: "PracticeOwner",
    tenant: "Coastal Smiles",
    status: "Pending",
    lastSeen: "—",
  },
];

const roleStyles: Record<UserRole, string> = {
  SuperAdmin: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
  SystemAdmin: "border-primary/40 bg-primary/10 text-primary",
  PracticeOwner: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Professional: "border-border bg-muted text-foreground",
};

const roleIcons: Record<UserRole, typeof ShieldCheck> = {
  SuperAdmin: ShieldCheck,
  SystemAdmin: UserCog,
  PracticeOwner: Building2,
  Professional: Stethoscope,
};

const statusStyles: Record<AdminUser["status"], string> = {
  Active: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Suspended: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
  Pending: "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400",
};

// Role label shown in UI (PracticeOwner → Owner)
const roleLabel: Record<UserRole, string> = {
  get SuperAdmin() {
    return i18n.t("super_admin");
  },
  get SystemAdmin() {
    return i18n.t("system_admin");
  },
  get PracticeOwner() {
    return i18n.t("owner_role");
  },
  get Professional() {
    return i18n.t("professional_role");
  },
};

function UsersPage() {
  const [q, setQ] = useState("");
  const [roleFilter, setRoleFilter] = useState<"All" | UserRole>("All");
  const [statusFilter, setStatusFilter] = useState<"All" | AdminUser["status"]>("All");
  const [impersonateTarget, setImpersonateTarget] = useState<AdminUser | null>(null);
  const [suspendTarget, setSuspendTarget] = useState<AdminUser | null>(null);

  const suspendedUserIds = useAppStore((s) => s.suspendedUserIds);
  const suspendUser = useAppStore((s) => s.suspendUser);
  const unsuspendUser = useAppStore((s) => s.unsuspendUser);
  const impersonateAction = useAppStore((s) => s.impersonate);
  const navigate = useNavigate();
  const { t } = useTranslation();

  // Merge store-suspended status with base users
  const mockUsers = useMemo<AdminUser[]>(
    () =>
      baseUsers.map((u) => ({
        ...u,
        status:
          suspendedUserIds.includes(u.id) && u.status !== "Pending"
            ? "Suspended"
            : u.status === "Suspended" && !suspendedUserIds.includes(u.id)
              ? u.status // keep original "Suspended" if not overridden
              : u.status,
      })),
    [suspendedUserIds],
  );

  const filtered = useMemo(() => {
    const term = q.trim().toLowerCase();
    return mockUsers.filter((u) => {
      if (roleFilter !== "All" && u.role !== roleFilter) return false;
      if (statusFilter !== "All" && u.status !== statusFilter) return false;
      if (!term) return true;
      return (
        `${u.firstName} ${u.lastName}`.toLowerCase().includes(term) ||
        u.email.toLowerCase().includes(term) ||
        u.tenant.toLowerCase().includes(term)
      );
    });
  }, [q, roleFilter, statusFilter, mockUsers]);

  const confirmImpersonate = () => {
    if (!impersonateTarget) return;
    const target = impersonateTarget;
    const targetRole = target.role as AppRole;
    if (
      targetRole !== "PracticeOwner" &&
      targetRole !== "Professional" &&
      targetRole !== "SuperAdmin"
    ) {
      toast.error(t("cannot_impersonate"));
      setImpersonateTarget(null);
      return;
    }
    const appUser: AppUser = {
      id: target.id,
      firstName: target.firstName,
      lastName: target.lastName,
      email: target.email,
      role: targetRole,
      tenant: target.tenant,
      avatarInitials: `${target.firstName[0]}${target.lastName[0]}`,
    };
    impersonateAction(appUser);
    toast.success(t("impersonation_started"), {
      description: `${t("signed_in_as", { name: target.firstName + " " + target.lastName })} ${t("all_actions_audited")}`,
    });
    setImpersonateTarget(null);
    navigate({ to: dashboardForRole(targetRole), replace: true });
  };

  const confirmSuspend = () => {
    if (!suspendTarget) return;
    const isSuspended = suspendTarget.status === "Suspended";
    if (isSuspended) {
      unsuspendUser(suspendTarget.id);
      toast.success(t("account_restored"), {
        description: `${suspendTarget.firstName} ${suspendTarget.lastName} ${t("is_active_again")}`,
      });
    } else {
      suspendUser(suspendTarget.id);
      toast(t("account_suspended"), {
        description: `${suspendTarget.firstName} ${suspendTarget.lastName} ${t("can_no_longer_login")}`,
      });
    }
    setSuspendTarget(null);
  };

  return (
    <div className="space-y-5 p-4 md:p-6">
      <header className="flex flex-wrap items-end justify-between gap-4">
        <div>
          <p className="text-xs font-medium uppercase tracking-wider text-primary">
            {t("user_management")}
          </p>
          <h1 className="mt-1 text-2xl font-semibold tracking-tight">{t("users")}</h1>
          <p className="mt-1 text-sm text-muted-foreground">{t("users_desc")}</p>
        </div>
        <Button className="bg-primary text-primary-foreground hover:bg-primary/90">
          <Mail className="h-4 w-4" /> {t("invite_user")}
        </Button>
      </header>

      {/* Filters */}
      <div className="flex flex-wrap items-center gap-3">
        <div className="relative w-full sm:max-w-xs">
          <Search className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            value={q}
            onChange={(e) => setQ(e.target.value)}
            placeholder={t("search_users")}
            className="pl-9"
          />
        </div>
        <Select value={roleFilter} onValueChange={(v) => setRoleFilter(v as typeof roleFilter)}>
          <SelectTrigger className="w-[140px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="All">{t("all_roles")}</SelectItem>
            <SelectItem value="SuperAdmin">{t("super_admin")}</SelectItem>
            <SelectItem value="SystemAdmin">{t("system_admin")}</SelectItem>
            <SelectItem value="PracticeOwner">{t("owner_role")}</SelectItem>
            <SelectItem value="Professional">{t("professional_role")}</SelectItem>
          </SelectContent>
        </Select>
        <Select
          value={statusFilter}
          onValueChange={(v) => setStatusFilter(v as typeof statusFilter)}
        >
          <SelectTrigger className="w-[130px]">
            <SelectValue />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="All">{t("all_status")}</SelectItem>
            <SelectItem value="Active">{t("active")}</SelectItem>
            <SelectItem value="Pending">{t("pending")}</SelectItem>
            <SelectItem value="Suspended">{t("suspended")}</SelectItem>
          </SelectContent>
        </Select>
        <span className="ml-auto text-xs text-muted-foreground">
          {filtered.length} {t("of")} {mockUsers.length} {t("users").toLowerCase()}
        </span>
      </div>

      {/* Mobile cards */}
      <div className="md:hidden space-y-3">
        {filtered.map((u, i) => {
          const RoleIcon = roleIcons[u.role];
          const canImpersonate = u.role !== "SuperAdmin" && u.status !== "Suspended";
          const isSuspended = u.status === "Suspended";
          return (
            <motion.div
              key={u.id}
              initial={{ opacity: 0, y: 6 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: Math.min(i * 0.04, 0.2) }}
            >
              <Card className="p-4 space-y-3">
                <div className="flex items-center gap-3">
                  <Avatar className="h-10 w-10">
                    <AvatarFallback
                      className={`text-xs font-semibold ${isSuspended ? "bg-rose-500/20 text-rose-600 dark:text-rose-400" : "bg-primary text-primary-foreground"}`}
                    >
                      {u.firstName[0]}
                      {u.lastName[0]}
                    </AvatarFallback>
                  </Avatar>
                  <div className="min-w-0 flex-1">
                    <p
                      className={`text-sm font-medium ${isSuspended ? "line-through text-muted-foreground" : ""}`}
                    >
                      {u.firstName} {u.lastName}
                    </p>
                    <p className="text-xs text-muted-foreground truncate">{u.email}</p>
                  </div>
                  <Badge variant="outline" className={statusStyles[u.status]}>
                    {t(u.status.toLowerCase() as any)}
                  </Badge>
                </div>
                <div className="flex flex-wrap items-center gap-2">
                  <Badge variant="outline" className={`gap-1 ${roleStyles[u.role]}`}>
                    <RoleIcon className="h-3 w-3" /> {roleLabel[u.role]}
                  </Badge>
                  <span className="text-xs text-muted-foreground">{u.tenant}</span>
                  <span className="ml-auto text-xs text-muted-foreground">{u.lastSeen}</span>
                </div>
                <div className="flex gap-2 pt-1 border-t border-border/60">
                  <Button
                    size="sm"
                    variant="outline"
                    disabled={!canImpersonate}
                    onClick={() => setImpersonateTarget(u)}
                    className="flex-1 gap-1.5 border-primary/40 text-primary hover:bg-primary/10"
                  >
                    <KeyRound className="h-3.5 w-3.5" />
                    {t("impersonate")}
                  </Button>
                  <Button
                    size="sm"
                    variant="outline"
                    disabled={u.role === "SuperAdmin" || u.status === "Pending"}
                    onClick={() => setSuspendTarget(u)}
                    className={`flex-1 gap-1.5 ${isSuspended ? "border-emerald-500/40 text-emerald-600 dark:text-emerald-400 hover:bg-emerald-500/10" : "border-rose-500/40 text-rose-600 dark:text-rose-400 hover:bg-rose-500/10"}`}
                  >
                    {isSuspended ? (
                      <>
                        <CheckCircle2 className="h-3.5 w-3.5" /> {t("restore")}
                      </>
                    ) : (
                      <>
                        <Ban className="h-3.5 w-3.5" /> {t("suspend")}
                      </>
                    )}
                  </Button>
                </div>
              </Card>
            </motion.div>
          );
        })}
        {filtered.length === 0 && (
          <div className="rounded-xl border border-dashed border-border/60 p-8 text-center text-sm text-muted-foreground">
            {t("no_users_match")}
          </div>
        )}
      </div>

      {/* Desktop table */}
      <Card className="hidden md:block overflow-hidden border-border/70 shadow-sm">
        <div className="overflow-x-auto">
          <table className="w-full text-sm">
            <thead>
              <tr className="border-b border-border/60 bg-muted/40">
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">
                  {t("user")}
                </th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">
                  {t("role")}
                </th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">
                  {t("tenant")}
                </th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">
                  {t("status")}
                </th>
                <th className="px-5 py-3 text-left text-xs font-medium uppercase tracking-wider text-muted-foreground">
                  {t("last_seen")}
                </th>
                <th className="px-5 py-3 text-right text-xs font-medium uppercase tracking-wider text-muted-foreground">
                  {t("actions")}
                </th>
              </tr>
            </thead>
            <tbody className="divide-y divide-border/60">
              {filtered.map((u, i) => {
                const RoleIcon = roleIcons[u.role];
                const canImpersonate = u.role !== "SuperAdmin" && u.status !== "Suspended";
                const isSuspended = u.status === "Suspended";
                return (
                  <motion.tr
                    key={u.id}
                    initial={{ opacity: 0, y: 4 }}
                    animate={{ opacity: 1, y: 0 }}
                    transition={{ delay: Math.min(i * 0.02, 0.25), duration: 0.25 }}
                    className="border-b border-border/60 transition hover:bg-muted/40"
                  >
                    <td className="px-5 py-3">
                      <div className="flex items-center gap-3">
                        <Avatar className="h-9 w-9">
                          <AvatarFallback
                            className={`text-xs font-semibold ${isSuspended ? "bg-rose-500/20 text-rose-600 dark:text-rose-400" : "bg-primary text-primary-foreground"}`}
                          >
                            {u.firstName[0]}
                            {u.lastName[0]}
                          </AvatarFallback>
                        </Avatar>
                        <div className="min-w-0">
                          <p
                            className={`truncate text-sm font-medium ${isSuspended ? "line-through text-muted-foreground" : ""}`}
                          >
                            {u.firstName} {u.lastName}
                          </p>
                          <p className="truncate text-[11px] text-muted-foreground">{u.email}</p>
                        </div>
                      </div>
                    </td>
                    <td className="px-5 py-3">
                      <Badge variant="outline" className={`gap-1 ${roleStyles[u.role]}`}>
                        <RoleIcon className="h-3 w-3" /> {roleLabel[u.role]}
                      </Badge>
                    </td>
                    <td className="px-5 py-3 text-sm text-muted-foreground">{u.tenant}</td>
                    <td className="px-5 py-3">
                      <Badge variant="outline" className={statusStyles[u.status]}>
                        {t(u.status.toLowerCase() as any)}
                      </Badge>
                    </td>
                    <td className="px-5 py-3 text-sm text-muted-foreground">{u.lastSeen}</td>
                    <td className="px-5 py-3 text-right">
                      <div className="flex items-center justify-end gap-2">
                        <Button
                          size="sm"
                          variant="outline"
                          disabled={!canImpersonate}
                          onClick={() => setImpersonateTarget(u)}
                          className="h-8 gap-1.5 border-primary/40 text-primary hover:bg-primary/10 hover:text-primary"
                        >
                          <KeyRound className="h-3.5 w-3.5" /> {t("impersonate")}
                        </Button>
                        <DropdownMenu>
                          <DropdownMenuTrigger asChild>
                            <Button variant="ghost" size="icon" className="h-8 w-8">
                              <MoreHorizontal className="h-4 w-4" />
                            </Button>
                          </DropdownMenuTrigger>
                          <DropdownMenuContent align="end" className="w-48">
                            <DropdownMenuItem>
                              <Mail className="mr-2 h-3.5 w-3.5" /> {t("send_email")}
                            </DropdownMenuItem>
                            <DropdownMenuItem>
                              <UserCog className="mr-2 h-3.5 w-3.5" /> {t("edit_role")}
                            </DropdownMenuItem>
                            <DropdownMenuSeparator />
                            <DropdownMenuItem
                              disabled={u.role === "SuperAdmin" || u.status === "Pending"}
                              onClick={() => setSuspendTarget(u)}
                              className={
                                isSuspended
                                  ? "text-emerald-600 dark:text-emerald-400 focus:text-emerald-600"
                                  : "text-destructive focus:text-destructive"
                              }
                            >
                              {isSuspended ? (
                                <>
                                  <CheckCircle2 className="mr-2 h-3.5 w-3.5" />{" "}
                                  {t("restore_account")}
                                </>
                              ) : (
                                <>
                                  <Ban className="mr-2 h-3.5 w-3.5" /> {t("suspend_account")}
                                </>
                              )}
                            </DropdownMenuItem>
                          </DropdownMenuContent>
                        </DropdownMenu>
                      </div>
                    </td>
                  </motion.tr>
                );
              })}
              {filtered.length === 0 && (
                <tr>
                  <td colSpan={6} className="py-12 text-center text-sm text-muted-foreground">
                    {t("no_users_match")}
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </Card>

      {/* Impersonate dialog */}
      <AlertDialog
        open={!!impersonateTarget}
        onOpenChange={(o) => !o && setImpersonateTarget(null)}
      >
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle className="flex items-center gap-2">
              <KeyRound className="h-4 w-4 text-primary" /> {t("start_impersonation")}
            </AlertDialogTitle>
            <AlertDialogDescription asChild>
              <div className="space-y-3">
                <p>
                  {t("acting_as")}{" "}
                  <span className="font-medium text-foreground">
                    {impersonateTarget?.firstName} {impersonateTarget?.lastName}
                  </span>{" "}
                  ({roleLabel[impersonateTarget?.role as UserRole] ?? impersonateTarget?.role}).{" "}
                  {t("impersonation_notice")}
                </p>
                <div className="rounded-lg border border-amber-500/30 bg-amber-500/10 p-3 text-xs text-amber-700 dark:text-amber-300">
                  Calls <code className="font-mono">ImpersonateController.start(userId)</code> ·
                  session auto-expires in 30 min.
                </div>
              </div>
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{t("cancel")}</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmImpersonate}
              className="bg-primary text-primary-foreground hover:bg-primary/90"
            >
              {t("start_session")}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>

      {/* Suspend/restore dialog */}
      <AlertDialog open={!!suspendTarget} onOpenChange={(o) => !o && setSuspendTarget(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle
              className={`flex items-center gap-2 ${suspendTarget?.status === "Suspended" ? "text-emerald-600 dark:text-emerald-400" : "text-destructive"}`}
            >
              {suspendTarget?.status === "Suspended" ? (
                <>
                  <CheckCircle2 className="h-4 w-4" /> {t("restore_account")}
                </>
              ) : (
                <>
                  <Ban className="h-4 w-4" /> {t("suspend_account")}
                </>
              )}
            </AlertDialogTitle>
            <AlertDialogDescription>
              {suspendTarget?.status === "Suspended"
                ? t("restore_user_confirm", {
                    name: `${suspendTarget?.firstName} ${suspendTarget?.lastName}`,
                  })
                : t("suspend_user_confirm", {
                    name: `${suspendTarget?.firstName} ${suspendTarget?.lastName}`,
                  })}
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>{t("cancel")}</AlertDialogCancel>
            <AlertDialogAction
              onClick={confirmSuspend}
              className={
                suspendTarget?.status === "Suspended"
                  ? "bg-emerald-600 text-white hover:bg-emerald-700"
                  : "bg-destructive text-destructive-foreground hover:bg-destructive/90"
              }
            >
              {suspendTarget?.status === "Suspended" ? t("restore_access") : t("suspend_account")}
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  );
}
