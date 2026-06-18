import { Siren, Clock, XCircle, MapPin, Users } from "lucide-react";
import { useAppStore } from "@/lib/store/app-store";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { mockLocations } from "@/lib/mock";
import { motion, AnimatePresence } from "motion/react";

export function ActiveSosTracker() {
  const { activeSosRequests, removeSosRequest } = useAppStore();

  if (!activeSosRequests || activeSosRequests.length === 0) return null;

  return (
    <div className="flex flex-col gap-3">
      <h3 className="text-sm font-semibold tracking-tight">Active SOS Broadcasts</h3>
      <div className="grid gap-3 sm:grid-cols-2">
        <AnimatePresence>
          {activeSosRequests.map((sos) => {
            const loc = mockLocations.find(l => l.id === sos.locationId);
            
            return (
              <motion.div
                key={sos.id}
                initial={{ opacity: 0, scale: 0.95 }}
                animate={{ opacity: 1, scale: 1 }}
                exit={{ opacity: 0, scale: 0.95 }}
                className="relative overflow-hidden rounded-xl border border-destructive/30 bg-destructive/5 p-4"
              >
                <div className="flex items-start justify-between">
                  <div className="flex items-center gap-2">
                    <span className="flex h-6 w-6 items-center justify-center rounded-full bg-destructive/10 text-destructive animate-pulse">
                      <Siren className="h-3 w-3" />
                    </span>
                    <span className="font-semibold">{sos.specialty} needed</span>
                  </div>
                  <Badge variant="outline" className="border-destructive/30 text-destructive bg-destructive/10 text-[10px]">
                    {sos.status}
                  </Badge>
                </div>
                
                <div className="mt-3 space-y-1.5 text-xs text-muted-foreground">
                  <div className="flex items-center gap-2">
                    <MapPin className="h-3.5 w-3.5" />
                    <span>{loc?.name || "Unknown"} ({sos.radius} mi radius)</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Clock className="h-3.5 w-3.5" />
                    <span>Broadcasted {new Date(sos.createdAt).toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}</span>
                  </div>
                  <div className="flex items-center gap-2">
                    <Users className="h-3.5 w-3.5" />
                    <span>Searching for available professionals...</span>
                  </div>
                </div>

                <div className="mt-4 flex items-center justify-end">
                  <Button 
                    size="sm" 
                    variant="ghost" 
                    className="h-7 text-xs text-muted-foreground hover:bg-destructive/10 hover:text-destructive"
                    onClick={() => removeSosRequest(sos.id)}
                  >
                    <XCircle className="mr-1.5 h-3.5 w-3.5" /> Cancel Broadcast
                  </Button>
                </div>
              </motion.div>
            );
          })}
        </AnimatePresence>
      </div>
    </div>
  );
}
