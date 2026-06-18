import type { ReactNode } from "react";
import { Link, useNavigate, useRouterState } from "@tanstack/react-router";
import {
  LayoutDashboard,
  Users,
  FileBarChart2,
  ShieldCheck,
  Activity,
  ArrowLeftRight,
  Bell,
  Search,
  LogOut,
} from "lucide-react";
import { useAppStore } from "@/lib/store/app-store";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarInset,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarProvider,
  SidebarTrigger,
  useSidebar,
} from "@/components/ui/sidebar";
import { ThemeToggle } from "@/components/theme/theme-toggle";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Badge } from "@/components/ui/badge";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

const navItems = [
  { title: "Overview", url: "/admin", icon: LayoutDashboard, exact: true },
  { title: "Users", url: "/admin/users", icon: Users },
  { title: "Reports", url: "/admin/reports", icon: FileBarChart2 },
];

function AdminSidebar() {
  const { state } = useSidebar();
  const collapsed = state === "collapsed";
  const pathname = useRouterState({ select: (s) => s.location.pathname });
  const isActive = (url: string, exact?: boolean) =>
    exact ? pathname === url : pathname === url || pathname.startsWith(url + "/");

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader>
        <div className="flex items-center gap-2.5 px-2 py-2">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary text-primary-foreground shadow-lg shadow-primary/30">
            <ShieldCheck className="h-4 w-4" strokeWidth={2.25} />
          </div>
          {!collapsed && (
            <div className="flex flex-col leading-tight">
              <span className="text-sm font-semibold tracking-tight">MDD · Admin</span>
              <span className="text-[10px] uppercase tracking-wider text-muted-foreground">
                System Console
              </span>
            </div>
          )}
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Console</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => (
                <SidebarMenuItem key={item.title}>
                  <SidebarMenuButton
                    asChild
                    isActive={isActive(item.url, item.exact)}
                    tooltip={item.title}
                    className="data-[active=true]:bg-sidebar-accent data-[active=true]:text-sidebar-accent-foreground"
                  >
                    <Link to={item.url} className="flex items-center gap-2.5">
                      <item.icon className="h-4 w-4" />
                      {!collapsed && <span className="text-sm">{item.title}</span>}
                    </Link>
                  </SidebarMenuButton>
                </SidebarMenuItem>
              ))}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>

        <SidebarGroup>
          <SidebarGroupLabel>Switch context</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              <SidebarMenuItem>
                <SidebarMenuButton asChild tooltip="Practice view">
                  <Link to="/practice" className="flex items-center gap-2.5">
                    <ArrowLeftRight className="h-4 w-4" />
                    {!collapsed && <span className="text-sm">Practice view</span>}
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        {!collapsed && (
          <div className="m-2 rounded-xl border border-sidebar-border bg-sidebar-accent/40 p-3">
            <p className="flex items-center gap-1.5 text-[11px] font-medium text-sidebar-accent-foreground">
              <Activity className="h-3 w-3" /> All systems nominal
            </p>
            <p className="mt-0.5 text-[11px] text-muted-foreground">
              Uptime 99.98% · 30d
            </p>
          </div>
        )}
      </SidebarFooter>
    </Sidebar>
  );
}

function AdminTopbar() {
  const currentUser = useAppStore((s) => s.currentUser);
  const logout = useAppStore((s) => s.logout);
  const navigate = useNavigate();
  const onLogout = () => {
    logout();
    navigate({ to: "/login", replace: true });
  };
  return (
    <header className="sticky top-0 z-30 flex h-14 items-center gap-2 border-b border-border/60 bg-background/80 px-3 backdrop-blur-xl md:px-5">
      <SidebarTrigger className="h-9 w-9" />

      <Badge
        variant="outline"
        className="gap-1.5 border-primary/40 bg-primary/10 text-primary"
      >
        <ShieldCheck className="h-3 w-3" /> SuperAdmin
      </Badge>

      <div className="relative ml-2 hidden flex-1 max-w-md md:block">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Search users, practices, audit logs…"
          className="h-9 rounded-full border-border/60 bg-muted/40 pl-9 text-sm focus-visible:bg-background"
        />
      </div>

      <div className="ml-auto flex items-center gap-1">
        <Button variant="ghost" size="icon" className="relative rounded-full" aria-label="Notifications">
          <Bell className="h-4 w-4" />
          <Badge className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive p-0 text-[10px] font-semibold text-destructive-foreground">
            7
          </Badge>
        </Button>
        <ThemeToggle />
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" size="icon" className="ml-1 rounded-full">
              <Avatar className="h-8 w-8 ring-2 ring-primary/30">
                <AvatarFallback className="bg-primary text-primary-foreground text-xs font-semibold">
                  {currentUser?.avatarInitials ?? "SA"}
                </AvatarFallback>
              </Avatar>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-56">
            <DropdownMenuLabel className="font-normal">
              <div className="flex flex-col">
                <span className="text-sm font-medium">
                  {currentUser?.firstName} {currentUser?.lastName}
                </span>
                <span className="text-xs text-muted-foreground">{currentUser?.email ?? "admin@mdd.health"}</span>
              </div>
            </DropdownMenuLabel>
            <DropdownMenuSeparator />
            <DropdownMenuItem>Audit log</DropdownMenuItem>
            <DropdownMenuItem>Security settings</DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onSelect={onLogout} className="text-destructive focus:text-destructive">
              <LogOut className="mr-2 h-3.5 w-3.5" /> Sign out
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}

export function AdminShell({ children }: { children: ReactNode }) {
  return (
    <div className="admin-theme min-h-svh w-full">
      <SidebarProvider>
        <div className="flex min-h-svh w-full bg-background text-foreground">
          <AdminSidebar />
          <SidebarInset className="flex min-w-0 flex-1 flex-col">
            <AdminTopbar />
            <main className="flex-1 overflow-x-hidden">{children}</main>
          </SidebarInset>
        </div>
      </SidebarProvider>
    </div>
  );
}
