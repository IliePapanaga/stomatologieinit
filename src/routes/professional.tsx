import { createFileRoute, Outlet } from "@tanstack/react-router";
import { ProfessionalShell } from "@/components/professional/professional-shell";
import { RequireRole } from "@/components/auth/require-role";

export const Route = createFileRoute("/professional")({
  component: ProfessionalLayout,
});

function ProfessionalLayout() {
  return (
    <RequireRole role="Professional">
      <ProfessionalShell>
        <Outlet />
      </ProfessionalShell>
    </RequireRole>
  );
}
