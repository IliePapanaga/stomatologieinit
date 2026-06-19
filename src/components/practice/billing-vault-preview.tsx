import { motion } from "motion/react";
import { CreditCard, Landmark, Lock, Plus } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { usePaymentMethods } from "@/lib/hooks/practice";

export function BillingVaultPreview() {
  const { data: methods = [] } = usePaymentMethods();

  return (
    <Card className="shadow-soft border-border/60">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <div>
          <CardTitle className="text-base flex items-center gap-2">
            <Lock className="h-4 w-4 text-primary" /> Vault
          </CardTitle>
          <p className="mt-1 text-xs text-muted-foreground">
            Tokenized via PrimeRate · iframe-ready container
          </p>
        </div>
        <Button variant="ghost" size="sm" className="h-8 gap-1.5 text-xs">
          <Plus className="h-3.5 w-3.5" /> Add
        </Button>
      </CardHeader>
      <CardContent>
        {/* iframe-ready shell */}
        <div
          data-primerate-mount
          className="relative space-y-2 rounded-xl border border-dashed border-border bg-muted/20 p-2"
        >
          {methods.map((pm, i) => (
            <motion.div
              key={pm.id}
              initial={{ opacity: 0, y: 6 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: i * 0.07 }}
              className="group relative flex items-center gap-3 overflow-hidden rounded-lg border border-border/60 bg-card p-3"
            >
              <div
                className={`flex h-9 w-12 items-center justify-center rounded-md text-[10px] font-bold tracking-wide ${
                  pm.type === "ACH"
                    ? "bg-secondary text-secondary-foreground"
                    : "bg-gradient-brand text-primary-foreground"
                }`}
              >
                {pm.type === "ACH" ? (
                  <Landmark className="h-4 w-4" />
                ) : (
                  <CreditCard className="h-4 w-4" />
                )}
              </div>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-medium">
                  {pm.brand} •••• {pm.last4}
                </p>
                <p className="truncate text-[11px] text-muted-foreground">
                  {pm.holderName}
                  {pm.expMonth
                    ? ` · ${pm.expMonth.toString().padStart(2, "0")}/${pm.expYear?.toString().slice(-2)}`
                    : ""}
                </p>
              </div>
              {pm.isDefault && (
                <Badge
                  variant="outline"
                  className="border-success/40 bg-success/10 text-success text-[10px]"
                >
                  Default
                </Badge>
              )}
            </motion.div>
          ))}
          <p className="px-1 pb-1 pt-2 text-center text-[10px] uppercase tracking-wider text-muted-foreground/70">
            ◌ secured iframe slot
          </p>
        </div>
      </CardContent>
    </Card>
  );
}
