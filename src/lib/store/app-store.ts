import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";
import { mockPostings, mockProfessionals } from "@/lib/mock";
import type {
  Certificate,
  CertificateType,
  JobPosting,
  ProfessionalSpecialty,
  ProfessionalSubcategory,
  PermanentJobPosting,
  TemporaryKind,
  UserRole,
  SosRequest,
} from "@/lib/types/mdd";

export type AppRole = Extract<UserRole, "PracticeOwner" | "Professional" | "SuperAdmin">;

export interface AppUser {
  id: string;
  firstName: string;
  lastName: string;
  email: string;
  password?: string;
  role: AppRole;
  tenant: string;
  avatarInitials: string;
  // Practice-owner extras
  practiceAddress?: string;
  employeesCount?: "1-10" | "11-50" | "50+";
}

export const dashboardForRole = (role: AppRole): string => {
  switch (role) {
    case "PracticeOwner":
      return "/practice";
    case "Professional":
      return "/professional";
    case "SuperAdmin":
      return "/admin";
  }
};

const seedUsers: AppUser[] = [
  {
    id: "u_practice_demo",
    firstName: "Maya",
    lastName: "Chen",
    email: "maya@brightsidedental.com",
    password: "demo",
    role: "PracticeOwner",
    tenant: "Brightside Dental Group",
    avatarInitials: "MC",
    practiceAddress: "240 Market St, San Francisco, CA",
    employeesCount: "11-50",
  },
  {
    id: "u_pro_demo",
    firstName: "Amelia",
    lastName: "Brooks",
    email: "amelia.brooks@mdd.health",
    password: "demo",
    role: "Professional",
    tenant: "Independent",
    avatarInitials: "AB",
  },
  {
    id: "u_admin_demo",
    firstName: "Sam",
    lastName: "Johnson",
    email: "sam@mdd.health",
    password: "demo",
    role: "SuperAdmin",
    tenant: "MDD HQ",
    avatarInitials: "SJ",
  },
];

const appRoles: AppRole[] = ["PracticeOwner", "Professional", "SuperAdmin"];
const isAppRole = (role: unknown): role is AppRole =>
  typeof role === "string" && appRoles.includes(role as AppRole);

export const normalizeUser = (user: unknown): AppUser | null => {
  if (!user || typeof user !== "object") return null;
  const value = user as Partial<AppUser>;
  if (!value.email || !isAppRole(value.role)) return null;
  const firstName = value.firstName || value.email.split("@")[0] || "MDD";
  const lastName = value.lastName || "User";
  return {
    id: value.id || `u_${value.email}`,
    firstName,
    lastName,
    email: value.email,
    password: value.password,
    role: value.role,
    tenant: value.tenant || (value.role === "PracticeOwner" ? "Practice" : "Independent"),
    avatarInitials: value.avatarInitials || `${firstName[0] ?? "M"}${lastName[0] ?? "D"}`.toUpperCase(),
    practiceAddress: value.practiceAddress,
    employeesCount: value.employeesCount,
  };
};

const mergeUsersWithSeeds = (users: unknown): AppUser[] => {
  const keyed = new Map(seedUsers.map((user) => [user.email.toLowerCase(), user]));
  if (Array.isArray(users)) {
    for (const raw of users) {
      const user = normalizeUser(raw);
      if (user) keyed.set(user.email.toLowerCase(), user);
    }
  }
  return Array.from(keyed.values());
};

export const readStoredCurrentUser = (): AppUser | null => {
  if (typeof window === "undefined") return null;
  try {
    const raw = window.localStorage.getItem("mdd-app-store");
    if (!raw) return null;
    const parsed = JSON.parse(raw) as { state?: { currentUser?: unknown } };
    return normalizeUser(parsed.state?.currentUser);
  } catch {
    return null;
  }
};

export interface PracticeRef {
  id: string;
  name: string;
  city: string;
}

export const knownPractices: PracticeRef[] = [
  { id: "prc_001", name: "Brightside Dental Group", city: "San Francisco, CA" },
  { id: "prc_002", name: "Northpoint Dental", city: "Oakland, CA" },
  { id: "prc_003", name: "Summit Dental Care", city: "Berkeley, CA" },
  { id: "prc_004", name: "Coastal Smiles", city: "San Mateo, CA" },
];

export interface JobHistoryEntry {
  id: string;
  postingId: string;
  practiceId: string;
  practiceName: string;
  date: string;
  hours: number;
  earnings: number;
  status: "Checked-In" | "No-Show" | "Completed";
  role: string;
}

export interface SkillEntry {
  id: string;
  title: string;
  organization: string;
  years: number;
  startDate?: string;
  endDate?: string;
  description?: string;
}

export interface WorkReference {
  id: string;
  name: string;
  phone: string;
  relationship: string;
}

export interface PracticeReview {
  id: string;
  professionalId: string;
  practiceId: string;
  author: string;
  rating: number;
  text: string;
  date: string;
}

export interface CertificateExtra extends Certificate {
  issueDate?: string;
}

export interface ProfessionalProfile {
  firstName: string;
  lastName: string;
  phone: string;
  email: string;
  address: string;
  avatarInitials: string;
  avatarFileName?: string;
  commutingRadius: number;
  bio: string;
  specialties: ProfessionalSubcategory[];
  comfortLevels: Partial<Record<ProfessionalSubcategory, number>>;
  questionnaire: Partial<Record<string, boolean>>;
  certificates: CertificateExtra[];
  skills: SkillEntry[];
  references: WorkReference[];
}

const initialProfile: ProfessionalProfile = {
  firstName: "Amelia",
  lastName: "Brooks",
  phone: "+1 415 555 0102",
  email: "amelia.brooks@mdd.health",
  address: "1422 Fillmore St, San Francisco, CA 94115",
  avatarInitials: "AB",
  commutingRadius: 18,
  bio: "RDH with 6 years in pediatric & general practice. Fluent in Spanish.",
  specialties: ["RDH", "EFDA"],
  comfortLevels: { RDH: 9, EFDA: 7 },
  questionnaire: { dentrix: true, eaglesoft: false, opendental: true },
  certificates: [
    { id: "c1", professionalId: "u_pro_demo", type: "CPR", status: "Valid", expirationDate: "2027-04-12", licenseNumber: "CPR-99127" },
    { id: "c2", professionalId: "u_pro_demo", type: "XRAY", status: "Valid", expirationDate: "2026-09-30", licenseNumber: "XR-44231" },
    { id: "c5", professionalId: "u_pro_demo", type: "NPI", status: "Valid", licenseNumber: "1497823014" },
    { id: "c4", professionalId: "u_pro_demo", type: "LIABILITY", status: "Expired", expirationDate: "2025-12-01" },
    { id: "c6", professionalId: "u_pro_demo", type: "DEA", status: "Missing" },
    { id: "c7", professionalId: "u_pro_demo", type: "DDS_DMD", status: "Missing" },
    { id: "c8", professionalId: "u_pro_demo", type: "DAC", status: "Valid", expirationDate: "2028-01-22", licenseNumber: "DAC-77019" },
  ],
  skills: [
    { id: "s1", title: "Sr. Hygienist", organization: "Pacific Smiles", years: 3, startDate: "2022-03-01", endDate: "2025-04-30", description: "Adult perio & SRP focus." },
    { id: "s2", title: "Hygienist", organization: "Bay Pediatric Dental", years: 2, startDate: "2020-01-01", endDate: "2022-02-28", description: "Pediatric prophy, fluoride, sealants." },
    { id: "s3", title: "Dental Assistant", organization: "Mission Family Dental", years: 1 },
  ],
  references: [
    { id: "r1", name: "Dr. Karen Wu", phone: "+1 415 555 0177", relationship: "Former supervisor at Pacific Smiles" },
  ],
};

const initialHistory: JobHistoryEntry[] = [
  { id: "h1", postingId: "post_h1", practiceId: "prc_001", practiceName: "Brightside Dental Group", date: "2026-06-10", hours: 8, earnings: 304, status: "Checked-In", role: "Hygienist · Temp" },
  { id: "h2", postingId: "post_h2", practiceId: "prc_002", practiceName: "Northpoint Dental", date: "2026-06-05", hours: 6, earnings: 228, status: "Completed", role: "Hygienist · Temp" },
  { id: "h3", postingId: "post_h3", practiceId: "prc_003", practiceName: "Summit Dental Care", date: "2026-05-28", hours: 0, earnings: 0, status: "No-Show", role: "EFDA · Temp" },
  { id: "h4", postingId: "post_h4", practiceId: "prc_001", practiceName: "Brightside Dental Group", date: "2026-05-22", hours: 8, earnings: 312, status: "Completed", role: "Hygienist · Temp" },
  { id: "h5", postingId: "post_h5", practiceId: "prc_004", practiceName: "Coastal Smiles", date: "2026-05-15", hours: 7, earnings: 266, status: "Completed", role: "Hygienist · Temp" },
];

export interface CreatePostingInput {
  kind: "Permanent" | "Temporary";
  title: string;
  specialty: ProfessionalSpecialty;
  subcategory: ProfessionalSubcategory;
  commutingRadius: number;
  locationId: string;
  startDate: string;
  endDate?: string;
  temporaryKind?: TemporaryKind;
  hourlyRate?: number;
  startTime?: string;
  endTime?: string;
  fullTime?: boolean;
  salaryMin?: number;
  salaryMax?: number;
  benefits?: string[];
  notes?: string;
  workingSpaces: number;
}

export interface CertificateUploadInput {
  type: CertificateType;
  fileName: string;
  issueDate?: string;
  expirationDate?: string;
  licenseNumber?: string;
}

interface AppState {
  currentUser: AppUser | null;
  impersonator: AppUser | null;
  users: AppUser[];
  jobPostings: JobPosting[];
  professionals: typeof mockProfessionals;

  // Professional state
  professionalProfile: ProfessionalProfile;
  bannedPracticeIds: string[];
  appliedPostingIds: string[];
  practiceReviews: PracticeReview[];
  activeSosRequests: SosRequest[];

  // auth
  findUserByEmail: (email: string) => AppUser | undefined;
  loginWithPassword: (email: string, password: string) => AppUser | null;
  addUser: (user: AppUser) => AppUser;
  loginAs: (role: AppRole) => AppUser;
  loginUser: (user: AppUser) => void;
  logout: () => void;
  impersonate: (user: AppUser) => void;
  stopImpersonation: () => void;
  addPosting: (input: CreatePostingInput) => JobPosting;
  updatePosting: (id: string, updates: Partial<JobPosting>) => void;
  removePosting: (id: string) => void;

  applyToPosting: (postingId: string) => void;
  banPractice: (practiceId: string) => void;
  unbanPractice: (practiceId: string) => void;
  updateProfile: (patch: Partial<ProfessionalProfile>) => void;
  updateCurrentUser: (patch: Partial<AppUser>) => void;
  toggleSpecialty: (sub: ProfessionalSubcategory) => void;
  setComfortLevel: (sub: ProfessionalSubcategory, level: number) => void;
  setQuestionnaire: (key: string, value: boolean) => void;
  uploadCertificate: (input: CertificateUploadInput) => void;
  addSkill: (entry: Omit<SkillEntry, "id">) => void;
  removeSkill: (id: string) => void;
  addReference: (entry: Omit<WorkReference, "id">) => void;
  removeReference: (id: string) => void;
  addPracticeReview: (review: Omit<PracticeReview, "id" | "date" | "practiceId" | "author">) => void;
  addSosRequest: (req: SosRequest) => void;
  removeSosRequest: (id: string) => void;
}

export const useAppStore = create<AppState>()(
  persist(
    (set, get) => ({
      currentUser: null,
      impersonator: null,
      users: seedUsers,
      jobPostings: mockPostings,
      professionals: mockProfessionals,

      professionalProfile: initialProfile,
      bannedPracticeIds: [],
      appliedPostingIds: [],
      jobHistory: initialHistory,
      practiceReviews: [],
      activeSosRequests: [],

      findUserByEmail: (email) =>
        get().users.find((u) => u.email.trim().toLowerCase() === email.trim().toLowerCase()),
      loginWithPassword: (email, password) => {
        const u = get().findUserByEmail(email);
        if (!u) return null;
        // demo: accept any non-empty password if user has none stored
        if (u.password && u.password !== password) return null;
        set({ currentUser: u, impersonator: null });
        return u;
      },
      addUser: (user) => {
        const existing = get().findUserByEmail(user.email);
        if (existing) return existing;
        set({ users: [...get().users, user], currentUser: user, impersonator: null });
        return user;
      },
      loginAs: (role) => {
        const user =
          get().users.find((u) => u.role === role) ?? seedUsers.find((u) => u.role === role)!;
        set({ currentUser: user, impersonator: null });
        return user;
      },
      loginUser: (user) => set({ currentUser: user, impersonator: null }),
      logout: () => set({ currentUser: null, impersonator: null }),
      impersonate: (user) => {
        const me = get().currentUser;
        if (!me) return;
        set({ currentUser: user, impersonator: me });
      },
      stopImpersonation: () => {
        const imp = get().impersonator;
        if (!imp) return;
        set({ currentUser: imp, impersonator: null });
      },
      addPosting: (input) => {
        const base = {
          id: `post_${Date.now()}`,
          practiceId: "prc_001",
          locationId: input.locationId,
          specialty: input.specialty,
          subcategory: input.subcategory,
          commutingRadius: input.commutingRadius,
          status: "Open" as const,
          startDate: new Date(input.startDate).toISOString(),
          endDate: input.endDate ? new Date(input.endDate).toISOString() : undefined,
          applicantsCount: 0,
          matchPercentage: Math.floor(60 + Math.random() * 35),
          title: input.title,
          workingSpaces: input.workingSpaces || 1,
        };
        const posting: JobPosting =
          input.kind === "Permanent"
            ? ({
                ...base,
                kind: "Permanent",
                fullTime: input.fullTime ?? true,
                salaryRange: { min: input.salaryMin ?? 0, max: input.salaryMax ?? 0 },
                benefits: input.benefits ?? [],
              } satisfies PermanentJobPosting)
            : ({
                ...base,
                kind: "Temporary",
                temporaryKind: input.temporaryKind ?? "Simple",
                hourlyRate: input.hourlyRate ?? 0,
                days: [
                  {
                    date: input.startDate,
                    startTime: input.startTime ?? "08:00",
                    endTime: input.endTime ?? "16:00",
                    breakMinutes: 30,
                  },
                ],
              } satisfies TemporaryJobPosting);
        set({ jobPostings: [posting, ...get().jobPostings] });
        return posting;
      },
      updatePosting: (id, updates) => {
        set({
          jobPostings: get().jobPostings.map((p) => (p.id === id ? { ...p, ...updates } as JobPosting : p)),
        });
      },
      removePosting: (id) => {
        set({
          jobPostings: get().jobPostings.filter((p) => p.id !== id),
        });
      },

      applyToPosting: (postingId) => {
        const list = get().appliedPostingIds;
        if (list.includes(postingId)) return;
        set({ appliedPostingIds: [...list, postingId] });
      },
      banPractice: (practiceId) => {
        const list = get().bannedPracticeIds;
        if (list.includes(practiceId)) return;
        set({ bannedPracticeIds: [...list, practiceId] });
      },
      unbanPractice: (practiceId) =>
        set({ bannedPracticeIds: get().bannedPracticeIds.filter((p) => p !== practiceId) }),
      updateProfile: (patch) =>
        set({ professionalProfile: { ...get().professionalProfile, ...patch } }),
      updateCurrentUser: (patch) => {
        const u = get().currentUser;
        if (!u) return;
        const updated = { ...u, ...patch };
        set({
          currentUser: updated,
          users: get().users.map((user) => (user.id === u.id ? updated : user)),
        });
      },
      toggleSpecialty: (sub) => {
        const p = get().professionalProfile;
        const has = p.specialties.includes(sub);
        set({
          professionalProfile: {
            ...p,
            specialties: has ? p.specialties.filter((s) => s !== sub) : [...p.specialties, sub],
          },
        });
      },
      setComfortLevel: (sub, level) => {
        const p = get().professionalProfile;
        set({
          professionalProfile: { ...p, comfortLevels: { ...p.comfortLevels, [sub]: level } },
        });
      },
      setQuestionnaire: (key, value) => {
        const p = get().professionalProfile;
        set({
          professionalProfile: { ...p, questionnaire: { ...p.questionnaire, [key]: value } },
        });
      },
      uploadCertificate: (input) => {
        const p = get().professionalProfile;
        const existing = p.certificates.find((c) => c.type === input.type);
        const updated: CertificateExtra = existing
          ? {
              ...existing,
              status: "Valid",
              fileUrl: input.fileName,
              issueDate: input.issueDate ?? existing.issueDate,
              expirationDate: input.expirationDate ?? existing.expirationDate,
              licenseNumber: input.licenseNumber ?? existing.licenseNumber,
            }
          : {
              id: `c_${Date.now()}`,
              professionalId: get().currentUser?.id ?? "u_pro_demo",
              type: input.type,
              status: "Valid",
              fileUrl: input.fileName,
              issueDate: input.issueDate,
              expirationDate: input.expirationDate,
              licenseNumber: input.licenseNumber,
            };
        const certs = existing
          ? p.certificates.map((c) => (c.type === input.type ? updated : c))
          : [...p.certificates, updated];
        set({ professionalProfile: { ...p, certificates: certs } });
      },
      addSkill: (entry) => {
        const p = get().professionalProfile;
        set({
          professionalProfile: {
            ...p,
            skills: [{ ...entry, id: `s_${Date.now()}` }, ...p.skills],
          },
        });
      },
      removeSkill: (id) => {
        const p = get().professionalProfile;
        set({ professionalProfile: { ...p, skills: p.skills.filter((s) => s.id !== id) } });
      },
      addReference: (entry) => {
        const p = get().professionalProfile;
        set({
          professionalProfile: {
            ...p,
            references: [{ ...entry, id: `r_${Date.now()}` }, ...p.references],
          },
        });
      },
      removeReference: (id) => {
        const p = get().professionalProfile;
        set({
          professionalProfile: { ...p, references: p.references.filter((r) => r.id !== id) },
        });
      },
      addPracticeReview: (review) => {
        const u = get().currentUser;
        if (!u) return;
        const tenant = u.tenant;
        const existingIdx = get().practiceReviews.findIndex(r => r.professionalId === review.professionalId && r.practiceId === tenant);
        
        const newRev: PracticeReview = {
          ...review,
          id: existingIdx >= 0 ? get().practiceReviews[existingIdx].id : `rev_prac_${Date.now()}`,
          practiceId: tenant,
          author: `${u.firstName} ${u.lastName} (${u.tenant})`,
          date: new Date().toLocaleDateString("en-US", { month: "short", day: "numeric", year: "numeric" }),
        };

        if (existingIdx >= 0) {
          const updated = [...get().practiceReviews];
          updated[existingIdx] = newRev;
          set({ practiceReviews: updated });
        } else {
          set({ practiceReviews: [newRev, ...get().practiceReviews] });
        }
      },
      addSosRequest: (req) => set({ activeSosRequests: [req, ...get().activeSosRequests] }),
      removeSosRequest: (id) => set({ activeSosRequests: get().activeSosRequests.filter((r) => r.id !== id) }),
    }),
    {
      name: "mdd-app-store",
      version: 4,
      storage: createJSONStorage(() => {
        if (typeof window === "undefined") {
          return {
            getItem: () => null,
            setItem: () => {},
            removeItem: () => {},
          } as unknown as Storage;
        }
        return window.localStorage;
      }),
      migrate: (persisted) => {
        const state = (persisted ?? {}) as Partial<AppState>;
        return {
          ...state,
          currentUser: normalizeUser(state.currentUser),
          impersonator: normalizeUser(state.impersonator),
          users: mergeUsersWithSeeds(state.users),
          jobPostings: mockPostings,
          professionalProfile: { ...initialProfile, ...(state.professionalProfile ?? {}) },
          bannedPracticeIds: Array.isArray(state.bannedPracticeIds) ? state.bannedPracticeIds : [],
          appliedPostingIds: Array.isArray(state.appliedPostingIds) ? state.appliedPostingIds : [],
          jobHistory: Array.isArray(state.jobHistory) ? state.jobHistory : initialHistory,
          practiceReviews: Array.isArray(state.practiceReviews) ? state.practiceReviews : [],
          activeSosRequests: Array.isArray(state.activeSosRequests) ? state.activeSosRequests : [],
        } as AppState;
      },
      partialize: (s) => ({
        currentUser: s.currentUser,
        impersonator: s.impersonator,
        users: s.users,
        jobPostings: s.jobPostings,
        professionalProfile: s.professionalProfile,
        bannedPracticeIds: s.bannedPracticeIds,
        appliedPostingIds: s.appliedPostingIds,
        jobHistory: s.jobHistory,
        practiceReviews: s.practiceReviews,
        activeSosRequests: s.activeSosRequests,
      }),
    }
  )
);
