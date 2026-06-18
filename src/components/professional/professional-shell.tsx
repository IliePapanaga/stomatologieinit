import { useState, type ReactNode } from "react";
import { Link, useNavigate, useRouterState } from "@tanstack/react-router";
import {
  LayoutDashboard,
  Briefcase,
  ChevronDown,
  Calendar,
  Building2,
  Ban,
  History,
  UserCog,
  User,
  Stethoscope,
  FileCheck2,
  Sparkles,
  LogOut,
  KeyRound,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  SidebarMenuSub,
  SidebarMenuSubButton,
  SidebarMenuSubItem,
  SidebarProvider,
  SidebarTrigger,
  useSidebar,
} from "@/components/ui/sidebar";
import { Collapsible, CollapsibleContent, CollapsibleTrigger } from "@/components/ui/collapsible";
import { ThemeToggle } from "@/components/theme/theme-toggle";
import { Button } from "@/components/ui/button";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { useAppStore } from "@/lib/store/app-store";

type NavLeaf = { title: string; url: string; icon: typeof Briefcase; exact?: boolean };
type NavGroup = { title: string; icon: typeof Briefcase; items: NavLeaf[] };
type NavItem = NavLeaf | NavGroup;

const isGroup = (n: NavItem): n is NavGroup => "items" in n;

const nav: NavItem[] = [
  { title: "Overview", url: "/professional", icon: LayoutDashboard, exact: true },
  {
    title: "Assignments",
    icon: Briefcase,
    items: [
      { title: "Temporary Jobs", url: "/professional/temporary-jobs", icon: Calendar },
      { title: "Permanent Jobs", url: "/professional/permanent-jobs", icon: Building2 },
      { title: "Banned Offices", url: "/professional/banned-offices", icon: Ban },
    ],
  },
  { title: "Job History", url: "/professional/job-history", icon: History },
  {
    title: "My Account",
    icon: UserCog,
    items: [
      { title: "My Profile", url: "/professional/profile", icon: User },
      { title: "Specialties", url: "/professional/specialties", icon: Sparkles },
      { title: "Certificates", url: "/professional/certificates", icon: FileCheck2 },
      { title: "Skills & Experience", url: "/professional/skills", icon: Stethoscope },
    ],
  },
];

function NavGroupItem({ group, pathname }: { group: NavGroup; pathname: string }) {
  const anyActive = group.items.some((i) => pathname === i.url || pathname.startsWith(i.url + "/"));
  const [open, setOpen] = useState(anyActive);
  const { state } = useSidebar();
  const collapsed = state === "collapsed";

  if (collapsed) {
    return (
      <>
        {group.items.map((sub) => (
          <SidebarMenuItem key={sub.url}>
            <SidebarMenuButton
              asChild
              isActive={pathname === sub.url}
              tooltip={sub.title}
            >
              <Link to={sub.url} className="flex items-center gap-2.5">
                <sub.icon className="h-4 w-4" />
              </Link>
            </SidebarMenuButton>
          </SidebarMenuItem>
        ))}
      </>
    );
  }

  return (
    <Collapsible open={open || anyActive} onOpenChange={setOpen} asChild>
      <SidebarMenuItem>
        <CollapsibleTrigger asChild>
          <SidebarMenuButton tooltip={group.title} isActive={anyActive}>
            <group.icon className="h-4 w-4" />
            <span className="text-sm">{group.title}</span>
            <ChevronDown
              className={`ml-auto h-4 w-4 transition-transform ${open || anyActive ? "rotate-180" : ""}`}
            />
          </SidebarMenuButton>
        </CollapsibleTrigger>
        <CollapsibleContent>
          <SidebarMenuSub>
            {group.items.map((sub) => (
              <SidebarMenuSubItem key={sub.url}>
                <SidebarMenuSubButton asChild isActive={pathname === sub.url}>
                  <Link to={sub.url} className="flex items-center gap-2">
                    <sub.icon className="h-3.5 w-3.5" />
                    <span>{sub.title}</span>
                  </Link>
                </SidebarMenuSubButton>
              </SidebarMenuSubItem>
            ))}
          </SidebarMenuSub>
        </CollapsibleContent>
      </SidebarMenuItem>
    </Collapsible>
  );
}

function ProSidebar() {
  const { state } = useSidebar();
  const collapsed = state === "collapsed";
  const pathname = useRouterState({ select: (s) => s.location.pathname });

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader>
        <div className="flex items-center gap-2.5 px-2 py-2">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-primary text-primary-foreground shadow-lg shadow-primary/30">
            <Stethoscope className="h-4 w-4" strokeWidth={2.25} />
          </div>
          {!collapsed && (
            <div className="flex flex-col leading-tight">
              <span className="text-sm font-semibold tracking-tight">MDD · Pro</span>
              <span className="text-[10px] uppercase tracking-wider text-muted-foreground">
                Talent Console
              </span>
            </div>
          )}
        </div>
      </SidebarHeader>
      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Workspace</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {nav.map((item) =>
                isGroup(item) ? (
                  <NavGroupItem key={item.title} group={item} pathname={pathname} />
                ) : (
                  <SidebarMenuItem key={item.url}>
                    <SidebarMenuButton
                      asChild
                      isActive={item.exact ? pathname === item.url : pathname.startsWith(item.url)}
                      tooltip={item.title}
                    >
                      <Link to={item.url} className="flex items-center gap-2.5">
                        <item.icon className="h-4 w-4" />
                        {!collapsed && <span className="text-sm">{item.title}</span>}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                )
              )}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>
    </Sidebar>
  );
}

function ProTopbar() {
  const currentUser = useAppStore((s) => s.currentUser);
  const impersonator = useAppStore((s) => s.impersonator);
  const logout = useAppStore((s) => s.logout);
  const stopImpersonation = useAppStore((s) => s.stopImpersonation);
  const navigate = useNavigate();

  const onLogout = () => {
    logout();
    navigate({ to: "/login", replace: true });
  };
  const onStop = () => {
    stopImpersonation();
    navigate({ to: "/admin", replace: true });
  };

  return (
    <header className="sticky top-0 z-30 flex h-14 items-center gap-2 border-b border-border/60 bg-background/80 px-3 backdrop-blur-xl md:px-5">
      <SidebarTrigger className="h-9 w-9" />
      <Badge variant="outline" className="gap-1.5 border-primary/40 bg-primary/10 text-primary">
        <Stethoscope className="h-3 w-3" /> Professional
      </Badge>
      {impersonator && (
        <Badge
          variant="outline"
          className="gap-1.5 border-amber-500/40 bg-amber-500/10 text-amber-700 dark:text-amber-300"
        >
          <KeyRound className="h-3 w-3" /> Impersonating · {impersonator.firstName}
        </Badge>
      )}
      <div className="ml-auto flex items-center gap-2">
        {impersonator && (
          <Button variant="ghost" size="sm" onClick={onStop} className="h-8">
            Exit impersonation
          </Button>
        )}
        <ThemeToggle />
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-9 gap-2 px-2">
              <Avatar className="h-7 w-7">
                <AvatarFallback className="bg-primary text-primary-foreground text-xs">
                  {currentUser?.avatarInitials ?? "AB"}
                </AvatarFallback>
              </Avatar>
              <div className="hidden text-left leading-tight md:block">
                <p className="text-xs font-semibold">
                  {currentUser?.firstName} {currentUser?.lastName}
                </p>
                <p className="text-[10px] text-muted-foreground">{currentUser?.email}</p>
              </div>
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end" className="w-52">
            <DropdownMenuLabel>Account</DropdownMenuLabel>
            <DropdownMenuItem asChild>
              <Link to="/professional/profile">Edit profile</Link>
            </DropdownMenuItem>
            <DropdownMenuSeparator />
            <DropdownMenuItem onClick={onLogout} className="text-destructive focus:text-destructive">
              <LogOut className="h-4 w-4" /> Sign out
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </div>
    </header>
  );
}

export function ProfessionalShell({ children }: { children: ReactNode }) {
  return (
    <SidebarProvider>
      <ProSidebar />
      <div className="flex min-h-svh flex-1 flex-col">
        <ProTopbar />
        <main className="flex-1 bg-muted/30">{children}</main>
      </div>
    </SidebarProvider>
  );
}
