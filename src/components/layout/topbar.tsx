import { Bell, ChevronDown, Search, LogOut, KeyRound } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "@tanstack/react-router";
import { SidebarTrigger } from "@/components/ui/sidebar";
import { ThemeToggle } from "@/components/theme/theme-toggle";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
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
import { mockLocations, mockPractice } from "@/lib/mock";
import { useAppStore } from "@/lib/store/app-store";
import { UserProfileSheet } from "@/components/user-profile-sheet";

export function Topbar() {
  const [activeLoc, setActiveLoc] = useState(mockLocations[0]);
  const currentUser = useAppStore((s) => s.currentUser);
  const impersonator = useAppStore((s) => s.impersonator);
  const logout = useAppStore((s) => s.logout);
  const stopImpersonation = useAppStore((s) => s.stopImpersonation);
  const navigate = useNavigate();
  const [profileOpen, setProfileOpen] = useState(false);

  const onLogout = () => {
    logout();
    navigate({ to: "/login", replace: true });
  };
  const onStopImpersonation = () => {
    stopImpersonation();
    navigate({ to: "/admin", replace: true });
  };

  return (
    <>
      <header className="sticky top-0 z-30 flex h-14 items-center gap-2 border-b border-border/60 bg-background/80 px-3 backdrop-blur-xl supports-[backdrop-filter]:bg-background/60 md:px-5">
        <SidebarTrigger className="h-9 w-9" />

        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-9 gap-2 px-2 text-sm font-medium">
              <span className="hidden text-muted-foreground sm:inline">{mockPractice.companyName}</span>
              <span className="hidden text-muted-foreground/40 sm:inline">/</span>
              <span>{activeLoc.name}</span>
              <ChevronDown className="h-3.5 w-3.5 text-muted-foreground" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="start" className="w-56">
            <DropdownMenuLabel>Switch location</DropdownMenuLabel>
            <DropdownMenuSeparator />
            {mockLocations.map((loc) => (
              <DropdownMenuItem key={loc.id} onSelect={() => setActiveLoc(loc)}>
                <div className="flex flex-col">
                  <span className="text-sm font-medium">{loc.name}</span>
                  <span className="text-xs text-muted-foreground">
                    {loc.address.city}, {loc.address.state}
                  </span>
                </div>
              </DropdownMenuItem>
            ))}
          </DropdownMenuContent>
        </DropdownMenu>

        {impersonator && (
          <Badge variant="outline" className="ml-1 gap-1.5 border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400">
            <KeyRound className="h-3 w-3" /> Impersonating
          </Badge>
        )}

        <div className="relative ml-2 hidden flex-1 max-w-md md:block">
          <Search className="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <Input
            placeholder="Search professionals, postings, certificates…"
            className="h-9 rounded-full border-border/60 bg-muted/40 pl-9 text-sm focus-visible:bg-background"
          />
        </div>

        <div className="ml-auto flex items-center gap-1">
          {impersonator && (
            <Button size="sm" variant="outline" onClick={onStopImpersonation} className="h-8 gap-1.5">
              <KeyRound className="h-3.5 w-3.5" /> Exit impersonation
            </Button>
          )}
          <Button variant="ghost" size="icon" className="relative rounded-full" aria-label="Notifications">
            <Bell className="h-4 w-4" />
            <Badge className="absolute -right-0.5 -top-0.5 flex h-4 min-w-4 items-center justify-center rounded-full bg-destructive p-0 text-[10px] font-semibold text-destructive-foreground">
              3
            </Badge>
          </Button>
          <ThemeToggle />
          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" size="icon" className="ml-1 rounded-full">
                <Avatar className="h-8 w-8 ring-2 ring-primary/20">
                  <AvatarFallback className="bg-gradient-brand text-primary-foreground text-xs font-semibold">
                    {currentUser?.avatarInitials ?? "MC"}
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
                  <span className="text-xs text-muted-foreground">{currentUser?.email ?? mockPractice.email}</span>
                </div>
              </DropdownMenuLabel>
              <DropdownMenuSeparator />
              <DropdownMenuItem onSelect={() => setProfileOpen(true)}>Profile</DropdownMenuItem>
              <DropdownMenuItem>Practice settings</DropdownMenuItem>
              <DropdownMenuItem>Billing</DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem onSelect={onLogout} className="text-destructive focus:text-destructive">
                <LogOut className="mr-2 h-3.5 w-3.5" /> Sign out
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </header>

      <UserProfileSheet open={profileOpen} onOpenChange={setProfileOpen} />
    </>
  );
}
