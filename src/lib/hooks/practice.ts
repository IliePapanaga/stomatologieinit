import { queryOptions, useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import {
  mockActivity,
  mockDashboard,
  mockPaymentMethods,
  mockProfessionals,
  mockUpcomingShifts,
} from "@/lib/mock";
import type { SosRequest } from "@/lib/types/mdd";
import { useAppStore } from "@/lib/store/app-store";

const delay = <T>(value: T, ms = 220) =>
  new Promise<T>((resolve) => setTimeout(() => resolve(value), ms));

export const dashboardQuery = () =>
  queryOptions({
    queryKey: ["practice", "dashboard"],
    queryFn: () => delay(mockDashboard),
  });

export const nearbyProfessionalsQuery = (radius = 14) =>
  queryOptions({
    queryKey: ["practice", "nearby", radius],
    queryFn: () => delay(mockProfessionals.filter((p) => p.distanceMiles <= radius)),
  });

export const recentActivityQuery = () =>
  queryOptions({
    queryKey: ["practice", "activity"],
    queryFn: () => delay(mockActivity),
  });

export const upcomingShiftsQuery = () =>
  queryOptions({
    queryKey: ["practice", "shifts"],
    queryFn: () => delay(mockUpcomingShifts),
  });

export const paymentMethodsQuery = () =>
  queryOptions({
    queryKey: ["practice", "payment-methods"],
    queryFn: () => delay(mockPaymentMethods),
  });

export const usePracticeDashboard = () => useQuery(dashboardQuery());
export const useNearbyProfessionals = (radius?: number) => useQuery(nearbyProfessionalsQuery(radius));
export const useRecentActivity = () => useQuery(recentActivityQuery());
export const useUpcomingShifts = () => useQuery(upcomingShiftsQuery());
export const usePaymentMethods = () => useQuery(paymentMethodsQuery());

export interface CreateSosInput {
  locationId: string;
  specialty: SosRequest["specialty"];
  radius: number;
  message?: string;
}

export const useCreateSosRequest = () => {
  const qc = useQueryClient();
  return useMutation({
    mutationFn: async (input: CreateSosInput): Promise<SosRequest> => {
      await new Promise((r) => setTimeout(r, 600));
      return {
        id: `sos_${Date.now()}`,
        practiceId: "prc_001",
        locationId: input.locationId,
        specialty: input.specialty,
        radius: input.radius,
        message: input.message,
        createdAt: new Date().toISOString(),
        status: "Pending",
      };
    },
    onSuccess: (data) => {
      useAppStore.getState().addSosRequest(data);
      qc.invalidateQueries({ queryKey: ["practice", "dashboard"] });
      qc.invalidateQueries({ queryKey: ["practice", "activity"] });
    },
  });
};
