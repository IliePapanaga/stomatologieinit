import { ClientOnly, Navigate } from "@tanstack/react-router";
import type { ReactNode } from "react";
import { useAppStore, dashboardForRole, readStoredCurrentUser, type AppRole } from "@/lib/store/app-store";

interface Props {
  role: AppRole;
  children: ReactNode;
}

export function RequireRole({ role, children }: Props) {
  return (
    <ClientOnly fallback={null}>
      <RoleGate role={role}>{children}</RoleGate>
    </ClientOnly>
  );
}

function RoleGate({ role, children }: Props) {
  const currentUser = useAppStore((s) => s.currentUser);
  const effectiveUser = currentUser ?? readStoredCurrentUser();

  if (!effectiveUser) {
    return <Navigate to="/login" replace />;
  }
  if (effectiveUser.role !== role) {
    return <Navigate to={dashboardForRole(effectiveUser.role) as "/practice" | "/professional" | "/admin"} replace />;
  }
  return <>{children}</>;
}

