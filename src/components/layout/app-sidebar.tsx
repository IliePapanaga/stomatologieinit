import { Link, useRouterState } from "@tanstack/react-router";
import {
  LayoutDashboard,
  Briefcase,
  Users,
  CalendarDays,
  CreditCard,
  Settings,
  BarChart2,
  ChevronDown,
  ChevronRight,
  Globe,
} from "lucide-react";
import {
  Sidebar,
  SidebarContent,
  SidebarFooter,
  SidebarGroup,
  SidebarGroupContent,
  SidebarGroupLabel,
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar";
import { useTranslation } from "react-i18next";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { useState } from "react";

// Main nav — Settings lives in the footer
const navItems = [
  { title: "Dashboard", url: "/practice",          icon: LayoutDashboard, exact: true },
  { title: "Postings",  url: "/practice/postings", icon: Briefcase },
  { title: "Staff",     url: "/practice/staff",    icon: Users },
  { title: "Schedule",  url: "/practice/schedule", icon: CalendarDays },
  { title: "Billing",   url: "/practice/billing",  icon: CreditCard },
];

const reportItems: { title: string; tab: string }[] = [
  { title: "Clients",              tab: "clients" },
  { title: "Professionals",        tab: "professionals" },
  { title: "Payments",             tab: "payments" },
  { title: "Positions",            tab: "positions" },
  { title: "Canceled Postings",    tab: "canceled" },
  { title: "Not Filled Positions", tab: "notfilled" },
];

export function AppSidebar() {
  const { state, isMobile, setOpenMobile } = useSidebar();
  const collapsed = state === "collapsed";

  // Read both pathname + search from router (reactive, no window.location lag)
  const { pathname, searchStr } = useRouterState({
    select: (s) => ({
      pathname:  s.location.pathname,
      searchStr: s.location.searchStr,
    }),
  });

  const { t, i18n } = useTranslation();
  const [reportsOpen, setReportsOpen] = useState(
    pathname.startsWith("/practice/reports"),
  );

  const isActive = (url: string, exact?: boolean) =>
    exact ? pathname === url : pathname === url || pathname.startsWith(url + "/");

  const isReportsActive  = pathname.startsWith("/practice/reports");
  const isSettingsActive = pathname === "/practice/settings";

  // Current active tab from URL — parsed reactively from router
  const activeTab = new URLSearchParams(searchStr ?? "").get("tab") ?? "";

  const close = () => { if (isMobile) setOpenMobile(false); };

  return (
    <>
      <Sidebar collapsible="icon">
        {/* ── Main navigation ── */}
        <SidebarContent className="pt-4">
          <SidebarGroup>
            <SidebarGroupLabel>{t("owner_view")}</SidebarGroupLabel>
            <SidebarGroupContent>
              <SidebarMenu>
                {/* Regular nav items */}
                {navItems.map((item) => (
                  <SidebarMenuItem key={item.title}>
                    <SidebarMenuButton
                      asChild
                      isActive={isActive(item.url, item.exact)}
                      tooltip={item.title}
                    >
                      <Link to={item.url} onClick={close} className="flex items-center gap-2.5">
                        <item.icon className="h-4 w-4 shrink-0" />
                        {!collapsed && (
                          <span className="truncate">{t(item.title.toLowerCase())}</span>
                        )}
                      </Link>
                    </SidebarMenuButton>
                  </SidebarMenuItem>
                ))}

                {/* Reports — collapsible group */}
                <SidebarMenuItem>
                  <SidebarMenuButton
                    isActive={isReportsActive}
                    tooltip={t("reports")}
                    asChild={false}
                    onClick={() => {
                      if (!collapsed) setReportsOpen((o) => !o);
                    }}
                    className="data-[active=true]:bg-sidebar-accent data-[active=true]:text-sidebar-accent-foreground"
                  >
                    <div className="flex items-center gap-2.5 w-full">
                      <BarChart2 className="h-4 w-4 shrink-0" />
                      {!collapsed && (
                        <>
                          <span className="text-sm flex-1 truncate">{t("reports")}</span>
                          {reportsOpen
                            ? <ChevronDown  className="h-3.5 w-3.5 text-muted-foreground shrink-0" />
                            : <ChevronRight className="h-3.5 w-3.5 text-muted-foreground shrink-0" />
                          }
                        </>
                      )}
                    </div>
                  </SidebarMenuButton>

                  {/* Sub-items — only when sidebar is expanded and group is open */}
                  {!collapsed && reportsOpen && (
                    <div className="mt-0.5 ml-6 space-y-0.5 border-l border-sidebar-border/60 pl-3">
                      {reportItems.map((sub) => (
                        <Link
                          key={sub.tab}
                          to="/practice/reports"
                          search={{ tab: sub.tab }}
                          className={`block rounded-md px-2 py-1.5 text-xs transition-colors ${
                            isReportsActive && activeTab === sub.tab
                              ? "bg-sidebar-accent text-sidebar-accent-foreground font-medium"
                              : "text-muted-foreground hover:bg-muted hover:text-foreground"
                          }`}
                          onClick={close}
                        >
                          {t("report_" + sub.tab)}
                        </Link>
                      ))}
                    </div>
                  )}
                </SidebarMenuItem>
              </SidebarMenu>
            </SidebarGroupContent>
          </SidebarGroup>
        </SidebarContent>

        {/* ── Footer: Settings → Language ── */}
        <SidebarFooter className="pb-4">
          <SidebarMenu>
            <SidebarMenuItem>
              <SidebarMenuButton
                asChild
                isActive={isSettingsActive}
                tooltip={t("settings")}
              >
                <Link to="/practice/settings" onClick={close} className="flex items-center gap-2.5">
                  <Settings className="h-4 w-4 shrink-0" />
                  {!collapsed && <span className="truncate">{t("settings")}</span>}
                </Link>
              </SidebarMenuButton>
            </SidebarMenuItem>

            <SidebarMenuItem>
              <SidebarMenuButton
                tooltip={i18n.language === "en" ? "Español" : "English"}
                onClick={() => i18n.changeLanguage(i18n.language === "en" ? "es" : "en")}
              >
                <span className="text-base leading-none flex items-center justify-center w-4 shrink-0">
                  {i18n.language === "en" ? "🇪🇸" : "🇺🇸"}
                </span>
                {!collapsed && (
                  <span className="truncate">
                    {i18n.language === "en" ? "Español" : "English"}
                  </span>
                )}
              </SidebarMenuButton>
            </SidebarMenuItem>
          </SidebarMenu>
          <div className="hidden md:flex justify-center w-full mt-2">
            <SidebarTrigger className="h-9 w-9 border border-border/40 hover:bg-muted" />
          </div>
        </SidebarFooter>
      </Sidebar>
    </>
  );
}
