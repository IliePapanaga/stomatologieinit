
## Scope (Phase 1)

Build the foundational app shell and the **Practice Dashboard** as the first fully realized surface. Professional Dashboard, Auth/Onboarding, Document Vault, and Swipe Deck get route stubs + entity-aligned interfaces for future phases.

## Design System

- **Stack**: TanStack Start (current template) + Tailwind v4 + shadcn/ui + Framer Motion (`motion` package) + lucide-react. React Query already wired. Apollo deferred — expose typed mock hooks (`usePracticeDashboard`, `useNearbyProfessionals`, `useCreateSosRequest`) so swap-to-GraphQL is mechanical.
- **Tokens** (`src/styles.css`, oklch, light + dark via `.dark` class on `<html>`):
  - `--primary`: teal-600 (light) / teal-400 (dark) — hygiene/health
  - `--secondary`: slate-900 — trust
  - `--background`: white (light) / zinc-950 (dark); `--card`: slate-50 / slate-800
  - `--foreground`: slate-900 / slate-200 (no pure white, no pure black)
  - `--destructive` rose-500, `--warning` amber-500 for expired Certificates
  - `--gradient-brand`, `--shadow-soft`, `--shadow-glow` (teal glow in dark)
- **Typography**: Plus Jakarta Sans loaded via `<link>` in `__root.tsx` head; registered as `--font-sans` in `@theme`.
- **Theme toggle**: persisted in `localStorage`, respects `prefers-color-scheme` on first load, no FOUC (inline script in root head).
- **Motion primitives**: `fadeSlideUp` (opacity 0→1, y 10→0, 300ms), `staggerChildren` 60ms, button `whileHover={{ scale: 1.02 }}`, shimmer Skeleton (gradient sweep, no spinners).

## Routes

```
src/routes/
  __root.tsx              ThemeProvider, fonts, QueryClientProvider
  index.tsx               redirect → /practice
  practice.tsx            AppShell (Sidebar + Topbar) + <Outlet />
  practice.index.tsx      Practice Dashboard (built this phase)
  practice.postings.tsx   placeholder
  practice.staff.tsx      placeholder
  practice.billing.tsx    placeholder
  professional.tsx        placeholder shell (future)
  auth.tsx                placeholder (future)
```

## Components

**Layout** (`src/components/layout/`)
- `AppShell` — sidebar + topbar + main; responsive (icon-rail on tablet, Sheet on mobile)
- `Sidebar` — shadcn sidebar nav: Dashboard, Postings, Staff, Schedule, Billing, Settings; active-route highlight; collapsible
- `Topbar` — practice/location switcher, search, notifications bell w/ badge, `ThemeToggle`, user avatar menu
- `ThemeToggle` — sun/moon with rotate animation

**Practice Dashboard** (`src/components/practice/`)
- `KpiStrip` — 4 stat cards (Active Postings, Filled Today, Pending Interviews, SOS Sent) with animated counters
- `LiveRadar` — centerpiece: concentric SVG circles with pulsing rings (Framer Motion), dots = nearby Professionals color-coded by specialty, legend, "14 Hygienists active within 12mi". Pure SVG, no map lib.
- `SosButton` — gradient (rose→amber) **hold-3s-to-fire** button: pointer-down starts circular progress sweep, release < 3s cancels, completion triggers mock `SosRequest` and opens confirmation Sheet (role / location / radius). Visually distinct from primary CTAs + glow in dark mode.
- `JobCreatorSheet` — shadcn `Sheet` (right side) with tabs **Permanent** / **Temporary** (Simple / Complex / Weekly sub-modes). Fields aligned to `PermanentJobPosting`, `TemporaryJobPosting`, `JobDay`, `WorkSchedule`. Mock submit.
- `RecentActivityFeed` — list of `CheckIn`, `NoShow`, `AttendanceAlert`, `JobInterview` events, staggered in, time-ago
- `UpcomingShifts` — next `WorkSchedule` entries with assigned Professional avatars
- `BillingVaultPreview` — stack of mock `PaymentMethodCard`s (last-4, brand icon, ACH/CC badge); container explicitly marked as iframe-ready shell for PrimeRate Drop-in

**Shared** (`src/components/shared/`)
- `ShimmerSkeleton`, `AnimatedNumber`, `EmptyState`, `StatusBadge` (maps entity status enums → token colors)

## Mock Data (`src/lib/mock/`)
- `practice.ts` — current `Practice` + `PracticeLocation`s
- `professionals.ts` — ~25 `Professional`s with rating, specialty, `availabilityMask`, distance, `Certificate` status
- `postings.ts` — mixed `PermanentJobPosting` + `TemporaryJobPosting` variants
- `activity.ts` — `CheckIn` / `NoShow` / `SosRequest` events
- `payments.ts` — `PaymentMethodCard`s + recent `Payment`s
- TypeScript interfaces match spec entity names exactly, exported from `src/lib/types/mdd.ts`.

## Data Hooks (`src/lib/hooks/`)
React Query hooks returning mock data via simulated latency so swap-to-GraphQL is trivial:
- `usePracticeDashboard()`, `useNearbyProfessionals(radius)`, `useRecentActivity()`, `useUpcomingShifts()`, `usePaymentMethods()`, `useCreateSosRequest()` (mutation)

## Out of Scope (Phase 1)
Professional swipe deck, Document Vault, Shift Calendar, Auth + gamified onboarding, real map, real PrimeRate integration, GraphQL/Apollo wiring. Route stubs + interfaces ready for future phases.

## Technical Notes
- All colors in oklch; shadcn tokens mapped through `@theme inline`.
- Framer Motion via `import { motion } from "motion/react"`.
- No Lovable Cloud needed yet — fully frontend with mock data.
- Default route read shape: loader `ensureQueryData` + component `useSuspenseQuery` per TanStack Query integration.
