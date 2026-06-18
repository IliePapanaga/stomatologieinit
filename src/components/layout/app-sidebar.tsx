import { Link, useRouterState } from "@tanstack/react-router";
import {
  LayoutDashboard,
  Briefcase,
  Users,
  CalendarDays,
  CreditCard,
  Settings,
  Stethoscope,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarHeader,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";

const navItems = [
  { title: "Dashboard", url: "/practice", icon: LayoutDashboard, exact: true },
  { title: "Postings", url: "/practice/postings", icon: Briefcase },
  { title: "Staff", url: "/practice/staff", icon: Users },
  { title: "Schedule", url: "/practice/schedule", icon: CalendarDays },
  { title: "Billing", url: "/practice/billing", icon: CreditCard },
  { title: "Settings", url: "/practice/settings", icon: Settings },
];

export function AppSidebar() {
  const { state } = useSidebar();
  const collapsed = state === "collapsed";
  const pathname = useRouterState({ select: (s) => s.location.pathname });

  const isActive = (url: string, exact?: boolean) =>
    exact ? pathname === url : pathname === url || pathname.startsWith(url + "/");

  return (
    <Sidebar collapsible="icon">
      <SidebarHeader>
        <div className="flex items-center gap-2.5 px-2 py-2">
          <div className="flex h-9 w-9 shrink-0 items-center justify-center rounded-xl bg-gradient-brand text-primary-foreground shadow-glow">
            <Stethoscope className="h-4.5 w-4.5" strokeWidth={2.25} />
          </div>
          {!collapsed && (
            <div className="flex flex-col leading-tight">
              <span className="text-sm font-semibold tracking-tight">MDD</span>
              <span className="text-[10px] uppercase tracking-wider text-muted-foreground">
                Dental Staffing
              </span>
            </div>
          )}
        </div>
      </SidebarHeader>

      <SidebarContent>
        <SidebarGroup>
          <SidebarGroupLabel>Practice</SidebarGroupLabel>
          <SidebarGroupContent>
            <SidebarMenu>
              {navItems.map((item) => {
                const active = isActive(item.url, item.exact);
                return (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton
                      asChild
                      isActive={active}
                      tooltip={item.title}
                      className="data-[active=true]:bg-sidebar-accent data-[active=true]:text-sidebar-accent-foreground"
                    >
                      <Link to={item.url} className="flex items-center gap-2.5">
                        <item.icon className="h-4 w-4" />
                        {!collapsed && <span className="text-sm">{item.title}</span>}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                );
              })}
            </SidebarMenu>
          </SidebarGroupContent>
        </SidebarGroup>
      </SidebarContent>

      <SidebarFooter>
        {!collapsed && (
          <div className="m-2 rounded-xl border border-sidebar-border bg-sidebar-accent/40 p-3">
            <p className="text-[11px] font-medium text-sidebar-accent-foreground">
              Need backup staff fast?
            </p>
            <p className="mt-0.5 text-[11px] text-muted-foreground">
              Trigger an SOS from the dashboard.
            </p>
          </div>
        )}
      </SidebarFooter>
    </Sidebar>
  );
}
