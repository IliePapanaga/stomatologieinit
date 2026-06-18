import { motion } from "motion/react";
import { CalendarDays, Clock } from "lucide-react";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import { Badge } from "@/components/ui/badge";
import { useUpcomingShifts } from "@/lib/hooks/practice";

export function UpcomingShifts() {
  const { data: shifts = [] } = useUpcomingShifts();
  return (
    <Card className="shadow-soft border-border/60">
      <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
        <CardTitle className="text-base flex items-center gap-2">
          <CalendarDays className="h-4 w-4 text-primary" /> Upcoming shifts
        </CardTitle>
        <Badge variant="secondary" className="text-[10px]">{shifts.length} scheduled</Badge>
      </CardHeader>
      <CardContent className="space-y-2">
        {shifts.map((s, i) => {
          const day = s.days[0];
          const initials = s.professional.firstName[0] + s.professional.lastName[0];
          return (
            <motion.div
              key={s.id}
              initial={{ opacity: 0, y: 8 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ duration: 0.3, delay: i * 0.07 }}
              className="flex items-center gap-3 rounded-xl border border-border/50 bg-card/50 p-3 transition-colors hover:border-primary/30"
            >
              <Avatar className="h-10 w-10 ring-1 ring-border">
                <AvatarFallback className="bg-primary/10 text-primary text-xs font-semibold">
                  {initials}
                </AvatarFallback>
              </Avatar>
              <div className="min-w-0 flex-1">
                <p className="truncate text-sm font-medium">
                  {s.professional.firstName} {s.professional.lastName}
                </p>
                <p className="text-xs text-muted-foreground">{s.specialty}</p>
              </div>
              <div className="text-right">
                <p className="text-xs font-medium tabular-nums">{day.date.slice(5)}</p>
                <p className="flex items-center justify-end gap-1 text-[11px] text-muted-foreground">
                  <Clock className="h-3 w-3" /> {day.startTime}–{day.endTime}
                </p>
              </div>
            </motion.div>
          );
        })}
      </CardContent>
    </Card>
  );
}
