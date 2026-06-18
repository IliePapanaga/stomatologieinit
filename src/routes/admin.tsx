import { createFileRoute, Outlet } from "@tanstack/react-router";
import { AdminShell } from "@/components/admin/admin-shell";
import { RequireRole } from "@/components/auth/require-role";

export const Route = createFileRoute("/admin")({
  component: AdminLayout,
});

function AdminLayout() {
  return (
    <RequireRole role="SuperAdmin">
      <AdminShell>
        <Outlet />
      </AdminShell>
    </RequireRole>
  );
}
