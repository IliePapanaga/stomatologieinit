import { useEffect } from "react";
import { createFileRoute, Link, useNavigate } from "@tanstack/react-router";
import { FileCheck2 } from "lucide-react";
import { Button } from "@/components/ui/button";

export const Route = createFileRoute("/professional/documents")({
  component: DocumentsCompatibilityRoute,
});

function DocumentsCompatibilityRoute() {
  const navigate = useNavigate();

  useEffect(() => {
    navigate({ to: "/professional/certificates", replace: true });
  }, [navigate]);

  return (
    <div className="flex min-h-[70svh] items-center justify-center p-6">
      <div className="max-w-sm text-center">
        <div className="mx-auto flex h-12 w-12 items-center justify-center rounded-xl bg-primary/10 text-primary">
          <FileCheck2 className="h-5 w-5" />
        </div>
        <h1 className="mt-4 text-xl font-semibold tracking-tight">Certificates</h1>
        <p className="mt-2 text-sm text-muted-foreground">
          Documents are managed in the certificates workspace.
        </p>
        <Button asChild className="mt-5">
          <Link to="/professional/certificates">Open certificates</Link>
        </Button>
      </div>
    </div>
  );
}