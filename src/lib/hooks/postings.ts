import { useAppStore, type CreatePostingInput } from "@/lib/store/app-store";
import type { JobPosting } from "@/lib/types/mdd";

export type { CreatePostingInput };

export function usePostings(): { data: JobPosting[]; isLoading: false } {
  const data = useAppStore((s) => s.jobPostings);
  return { data, isLoading: false };
}

export function useCreatePosting() {
  const addPosting = useAppStore((s) => s.addPosting);
  return {
    mutate: (
      input: CreatePostingInput,
      opts?: { onSuccess?: (p: JobPosting) => void; onError?: (e: unknown) => void },
    ) => {
      try {
        const p = addPosting(input);
        opts?.onSuccess?.(p);
      } catch (e) {
        opts?.onError?.(e);
      }
    },
    isPending: false,
  };
}

export function useUpdatePosting() {
  const updatePosting = useAppStore((s) => s.updatePosting);
  return {
    mutate: (
      args: { id: string; updates: Partial<JobPosting> },
      opts?: { onSuccess?: () => void },
    ) => {
      updatePosting(args.id, args.updates);
      opts?.onSuccess?.();
    },
    isPending: false,
  };
}

export function useRemovePosting() {
  const removePosting = useAppStore((s) => s.removePosting);
  return {
    mutate: (id: string, opts?: { onSuccess?: () => void }) => {
      removePosting(id);
      opts?.onSuccess?.();
    },
    isPending: false,
  };
}
