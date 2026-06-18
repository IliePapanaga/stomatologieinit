import { createFileRoute, Outlet } from "@tanstack/react-router";
import { AppShell } from "@/components/layout/app-shell";
import { RequireRole } from "@/components/auth/require-role";

export const Route = createFileRoute("/practice")({
  component: PracticeLayout,
});

function PracticeLayout() {
  return (
    <RequireRole role="PracticeOwner">
      <AppShell>
        <Outlet />
      </AppShell>
    </RequireRole>
  );
}
