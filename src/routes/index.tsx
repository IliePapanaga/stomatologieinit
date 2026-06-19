import { createFileRoute, Navigate, Link } from "@tanstack/react-router";
import { ArrowRight, Building2, ShieldCheck, Stethoscope } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useAppStore, dashboardForRole } from "@/lib/store/app-store";

export const Route = createFileRoute("/")({
  component: RouteRedirect,
});

function RouteRedirect() {
  const currentUser = useAppStore((s) => s.currentUser);

  if (currentUser) {
    return (
      <Navigate
        to={dashboardForRole(currentUser.role) as "/practice" | "/professional" | "/admin"}
        replace
      />
    );
  }

  return (
    <div className="flex min-h-svh items-center justify-center bg-background px-4 py-10 text-foreground">
      <main className="w-full max-w-3xl text-center">
        <div className="mx-auto flex h-14 w-14 items-center justify-center rounded-2xl bg-primary text-primary-foreground shadow-lg shadow-primary/25">
          <Stethoscope className="h-6 w-6" />
        </div>
        <h1 className="mt-6 text-3xl font-semibold tracking-tight sm:text-5xl">
          MDD Dental Staffing
        </h1>
        <p className="mx-auto mt-3 max-w-xl text-sm leading-6 text-muted-foreground sm:text-base">
          Sign in to manage practice staffing, professional jobs, or admin operations.
        </p>
        <div className="mt-8 flex flex-wrap justify-center gap-3">
          <Button asChild size="lg">
            <Link to="/login">
              Continue to login <ArrowRight className="h-4 w-4" />
            </Link>
          </Button>
          <Button asChild variant="outline" size="lg">
            <Link to="/practice">
              <Building2 className="h-4 w-4" /> Practice
            </Link>
          </Button>
          <Button asChild variant="outline" size="lg">
            <Link to="/professional">
              <Stethoscope className="h-4 w-4" /> Professional
            </Link>
          </Button>
          <Button asChild variant="outline" size="lg">
            <Link to="/admin">
              <ShieldCheck className="h-4 w-4" /> Admin
            </Link>
          </Button>
        </div>
      </main>
    </div>
  );
}
