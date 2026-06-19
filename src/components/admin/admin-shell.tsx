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
  KeyRound,
  Menu,
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
                <SidebarMenuButton asChild tooltip="Owner view">
                  <Link to="/practice" className="flex items-center gap-2.5">
                    <ArrowLeftRight className="h-4 w-4" />
                    {!collapsed && <span className="text-sm">Owner view</span>}
                  </Link>
                </SidebarMenuButton>
              </SidebarMenuItem>
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter />
    </Sidebar>
  );
}

function AdminTopbar() {
  const currentUser = useAppStore((s) => s.currentUser);
  const impersonator = useAppStore((s) => s.impersonator);
  const logout = useAppStore((s) => s.logout);
  const stopImpersonation = useAppStore((s) => s.stopImpersonation);
  const navigate = useNavigate();

  const onLogout = () => {
    logout();
    navigate({ to: "/login", replace: true });
  };

  const onStopImpersonation = () => {
    stopImpersonation();
    navigate({ to: "/admin", replace: true });
  };

  return (
    <header className="sticky top-0 z-30 flex h-14 items-center gap-2 border-b border-border/60 bg-background/80 px-3 backdrop-blur-xl md:px-5">
      {/* SidebarTrigger hidden on mobile — handled by the FAB below */}
      <SidebarTrigger className="hidden md:flex h-9 w-9" />

      <Badge
        variant="outline"
        className="gap-1.5 border-primary/40 bg-primary/10 text-primary"
      >
        <ShieldCheck className="h-3 w-3" /> SuperAdmin
      </Badge>

      {impersonator && (
        <Badge
          variant="outline"
          className="hidden md:flex gap-1.5 border-amber-500/40 bg-amber-500/10 text-amber-700 dark:text-amber-300"
        >
          <KeyRound className="h-3 w-3" /> Impersonating
        </Badge>
      )}

      <div className="relative ml-2 hidden flex-1 max-w-md md:block">
        <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
        <Input
          placeholder="Search users, owners, audit logs…"
          className="h-9 rounded-full border-border/60 bg-muted/40 pl-9 text-sm focus-visible:bg-background"
        />
      </div>

      <div className="ml-auto flex items-center gap-1">
        {impersonator && (
          <Button size="sm" variant="outline" onClick={onStopImpersonation} className="h-8 gap-1.5 text-xs">
            <KeyRound className="h-3.5 w-3.5" />
            <span className="hidden sm:inline">Exit impersonation</span>
            <span className="sm:hidden">Exit</span>
          </Button>
        )}
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

function AdminFab() {
  const { toggleSidebar } = useSidebar();
  return (
    <div className="fixed bottom-4 left-3 z-[100] md:left-0 md:w-[3rem] flex justify-center transition-all duration-200 !pointer-events-auto">
      <button
        onClick={toggleSidebar}
        className="h-12 w-12 md:h-9 md:w-9 rounded-full md:rounded-md border border-border/60 md:border-transparent bg-background/90 md:bg-muted/50 backdrop-blur shadow-xl md:shadow-none hover:bg-muted text-foreground flex items-center justify-center !pointer-events-auto transition-colors"
        aria-label="Toggle sidebar"
      >
        <Menu className="h-5 w-5 md:h-4 md:w-4" />
      </button>
    </div>
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
        <AdminFab />
      </SidebarProvider>
    </div>
  );
}
