import type {
  ActivityEvent,
  JobPosting,
  Payment,
  PaymentMethodCard,
  Practice,
  PracticeDashboardData,
  PracticeLocation,
  Professional,
  ProfessionalSpecialty,
  WorkSchedule,
} from "@/lib/types/mdd";

export const mockPractice: Practice = {
  id: "prc_001",
  companyName: "Brightside Dental Group",
  ownerFirstName: "Dr. Maya",
  ownerLastName: "Chen",
  email: "maya@brightsidedental.com",
  phone: "+1 415 555 0142",
  status: "Active",
};

export const mockLocations: PracticeLocation[] = [
  {
    id: "loc_001",
    practiceId: mockPractice.id,
    name: "Mission Bay Studio",
    address: { street: "488 Mission Bay Blvd", city: "San Francisco", state: "CA", zip: "94158" },
    lat: 37.7706,
    lng: -122.3893,
    radius: 12,
    contactFirstName: "Priya",
    contactLastName: "Nair",
    phone: "+1 415 555 0188",
  },
  {
    id: "loc_002",
    practiceId: mockPractice.id,
    name: "Noe Valley Clinic",
    address: { street: "1290 Castro St", city: "San Francisco", state: "CA", zip: "94114" },
    lat: 37.7506,
    lng: -122.4339,
    radius: 8,
    contactFirstName: "Jordan",
    contactLastName: "Reyes",
    phone: "+1 415 555 0199",
  },
];

const specialties: ProfessionalSpecialty[] = [
  "Hygienist",
  "Dentist",
  "Assistant",
  "FrontOffice",
  "Orthodontist",
];

const firstNames = [
  "Amelia", "Noah", "Sofia", "Liam", "Zara", "Ethan", "Maya", "Kai", "Leah", "Marcus",
  "Priya", "Jordan", "Ava", "Diego", "Hana", "Theo", "Isla", "Rohan", "Nora", "Sam",
  "Imani", "Lucas", "Yara", "Mateo", "Eden",
];
const lastNames = [
  "Brooks", "Patel", "Nguyen", "Okafor", "Reyes", "Kim", "Diaz", "Chen", "Park", "Singh",
  "Cohen", "Rivera", "Murphy", "Hassan", "Suzuki", "Lopez", "Adler", "Iqbal", "Walsh", "Tran",
  "Bauer", "Costa", "Ito", "Khan", "Russo",
];

function seeded(n: number) {
  const x = Math.sin(n) * 10000;
  return x - Math.floor(x);
}

function makeProfessional(i: number): Professional {
  const specialty = specialties[i % specialties.length];
  const distance = +(seeded(i + 1) * 13.5 + 0.6).toFixed(1);
  // Deterministic polar coordinate within 14 mile radius for radar
  const angle = (i / 25) * Math.PI * 2 + seeded(i + 7) * 0.6;
  const r = distance / 14;
  return {
    id: `pro_${String(i).padStart(3, "0")}`,
    firstName: firstNames[i % firstNames.length],
    lastName: lastNames[i % lastNames.length],
    email: `pro${i}@example.com`,
    phone: "+1 415 555 0" + (100 + i),
    specialty,
    rating: +(3.8 + seeded(i + 13) * 1.2).toFixed(1),
    status: "Active",
    documentStatus: i % 7 === 0 ? "Incomplete" : "Complete",
    desiredHourly: 28 + (i % 6) * 4,
    availabilityMask: [true, true, true, true, true, i % 3 === 0, false],
    travelRadius: 15 + (i % 4) * 5,
    distanceMiles: distance,
    lat: 37.77 + Math.cos(angle) * r * 0.1,
    lng: -122.42 + Math.sin(angle) * r * 0.1,
    certificates: [
      { id: `cert_${i}_cpr`, professionalId: `pro_${String(i).padStart(3, "0")}`, type: "CPR", status: i % 11 === 0 ? "Expired" : "Valid" },
      { id: `cert_${i}_xray`, professionalId: `pro_${String(i).padStart(3, "0")}`, type: "XRAY", status: "Valid" },
    ],
    online: i % 3 !== 0,
  };
}

export const mockProfessionals: Professional[] = Array.from({ length: 25 }, (_, i) => makeProfessional(i));

export const mockPostings: JobPosting[] = [
  {
    id: "post_001",
    practiceId: mockPractice.id,
    locationId: "loc_001",
    specialty: "Hygienist",
    subcategory: "RDH",
    commutingRadius: 12,
    status: "Open",
    startDate: new Date(Date.now() + 86400000).toISOString(),
    endDate: new Date(Date.now() + 86400000 * 30).toISOString(),
    applicantsCount: 7,
    workingSpaces: 3,
    hiredCandidateIds: ["pro_002"],
    matchPercentage: 92,
    title: "Lead Registered Dental Hygienist",
    kind: "Permanent",
    fullTime: true,
    salaryRange: { min: 78000, max: 96000 },
    benefits: ["Health", "Dental", "401k", "PTO"],
  },
  {
    id: "post_002",
    practiceId: mockPractice.id,
    locationId: "loc_001",
    specialty: "Assistant",
    subcategory: "DentalAssistant",
    commutingRadius: 8,
    status: "Open",
    startDate: new Date(Date.now() + 86400000 * 2).toISOString(),
    applicantsCount: 12,
    workingSpaces: 2,
    hiredCandidateIds: ["pro_004", "pro_005"],
    matchPercentage: 78,
    title: "Chairside Dental Assistant",
    kind: "Temporary",
    temporaryKind: "Simple",
    hourlyRate: 38,
    days: [{ date: new Date(Date.now() + 86400000 * 2).toISOString().split('T')[0], startTime: "08:00", endTime: "16:00", breakMinutes: 30 }],
  },
  {
    id: "post_003",
    practiceId: mockPractice.id,
    locationId: "loc_002",
    specialty: "Dentist",
    subcategory: "GeneralDentist",
    commutingRadius: 20,
    status: "Open",
    startDate: new Date(Date.now() - 86400000 * 7).toISOString(),
    applicantsCount: 3,
    workingSpaces: 1,
    hiredCandidateIds: [],
    matchPercentage: 64,
    title: "Locum General Dentist · 3-day week",
    kind: "Temporary",
    temporaryKind: "Weekly",
    hourlyRate: 95,
    days: [
      { date: new Date(Date.now() - 86400000 * 3).toISOString().split('T')[0], startTime: "09:00", endTime: "17:00", breakMinutes: 45 },
      { date: new Date(Date.now() - 86400000 * 2).toISOString().split('T')[0], startTime: "09:00", endTime: "17:00", breakMinutes: 45 },
      { date: new Date(Date.now() - 86400000 * 1).toISOString().split('T')[0], startTime: "09:00", endTime: "17:00", breakMinutes: 45 },
    ],
  },
  {
    id: "post_004",
    practiceId: mockPractice.id,
    locationId: "loc_001",
    specialty: "FrontOffice",
    subcategory: "TreatmentCoordinator",
    commutingRadius: 10,
    status: "Open",
    startDate: new Date(Date.now() + 86400000 * 14).toISOString(),
    endDate: new Date(Date.now() + 86400000 * 60).toISOString(),
    applicantsCount: 5,
    workingSpaces: 2,
    hiredCandidateIds: ["pro_007", "pro_008"],
    matchPercentage: 81,
    title: "Treatment Coordinator",
    kind: "Permanent",
    fullTime: true,
    salaryRange: { min: 56000, max: 68000 },
    benefits: ["Health", "Dental", "PTO"],
  },
  {
    id: "post_005",
    practiceId: mockPractice.id,
    locationId: "loc_002",
    specialty: "Hygienist",
    subcategory: "EFDA",
    commutingRadius: 15,
    status: "Open",
    startDate: new Date(Date.now() + 86400000 * 4).toISOString(),
    applicantsCount: 9,
    workingSpaces: 5,
    hiredCandidateIds: [],
    matchPercentage: 87,
    title: "EFDA Hygienist · Complex schedule",
    kind: "Temporary",
    temporaryKind: "Complex",
    hourlyRate: 52,
    days: [
      { date: new Date(Date.now() + 86400000 * 5).toISOString().split('T')[0], startTime: "07:30", endTime: "13:00", breakMinutes: 15 },
      { date: new Date(Date.now() + 86400000 * 7).toISOString().split('T')[0], startTime: "12:00", endTime: "19:00", breakMinutes: 30 },
    ],
  },
  {
    id: "post_006",
    practiceId: mockPractice.id,
    locationId: "loc_001",
    specialty: "Dentist",
    subcategory: "Endodontist",
    commutingRadius: 25,
    status: "Filled",
    startDate: new Date(Date.now() - 86400000 * 2).toISOString(),
    endDate: new Date(Date.now() - 86400000 * 2).toISOString(),
    applicantsCount: 4,
    workingSpaces: 1,
    hiredCandidateIds: ["pro_001"],
    matchPercentage: 96,
    title: "Endodontist · Part-time associate",
    kind: "Permanent",
    fullTime: false,
    salaryRange: { min: 140000, max: 180000 },
    benefits: ["Malpractice", "CE budget"],
  },
];

export const mockActivity: ActivityEvent[] = [
  {
    kind: "CheckIn",
    id: "act_001",
    postingId: "post_002",
    professionalId: "pro_003",
    at: new Date(Date.now() - 1000 * 60 * 12).toISOString(),
  },
  {
    kind: "JobInterview",
    id: "act_002",
    postingId: "post_001",
    candidateId: "pro_007",
    scheduledDate: new Date(Date.now() + 1000 * 60 * 60 * 3).toISOString(),
    status: "Scheduled",
    attemptCount: 1,
  },
  {
    kind: "AttendanceAlert",
    id: "act_003",
    postingId: "post_003",
    professionalId: "pro_014",
    level: "Warning",
    message: "Running 10 min late",
    at: new Date(Date.now() - 1000 * 60 * 38).toISOString(),
  },
  {
    kind: "NoShow",
    id: "act_004",
    postingId: "post_002",
    professionalId: "pro_021",
    at: new Date(Date.now() - 1000 * 60 * 60 * 2).toISOString(),
    reason: "Family emergency",
  },
  {
    kind: "CheckIn",
    id: "act_005",
    postingId: "post_001",
    professionalId: "pro_011",
    at: new Date(Date.now() - 1000 * 60 * 60 * 4).toISOString(),
  },
];

export const mockUpcomingShifts: (WorkSchedule & { professional: Professional; specialty: ProfessionalSpecialty })[] = [
  {
    id: "ws_001",
    postingId: "post_002",
    specialty: "Assistant",
    professional: mockProfessionals[2],
    days: [{ date: "2026-06-18", startTime: "08:00", endTime: "16:00", breakMinutes: 30 }],
  },
  {
    id: "ws_002",
    postingId: "post_003",
    specialty: "Dentist",
    professional: mockProfessionals[6],
    days: [{ date: "2026-06-19", startTime: "09:00", endTime: "17:00", breakMinutes: 45 }],
  },
  {
    id: "ws_003",
    postingId: "post_001",
    specialty: "Hygienist",
    professional: mockProfessionals[10],
    days: [{ date: "2026-06-20", startTime: "10:00", endTime: "18:00", breakMinutes: 30 }],
  },
];

export const mockPaymentMethods: PaymentMethodCard[] = [
  { id: "pm_001", brand: "Visa", last4: "4242", expMonth: 11, expYear: 2028, holderName: "Maya Chen", isDefault: true, type: "CC" },
  { id: "pm_002", brand: "Mastercard", last4: "8821", expMonth: 7, expYear: 2027, holderName: "Brightside Dental", isDefault: false, type: "CC" },
  { id: "pm_003", brand: "ACH", last4: "0033", holderName: "Brightside Dental LLC", isDefault: false, type: "ACH" },
];

export const mockPayments: Payment[] = [
  { id: "pay_001", practiceId: mockPractice.id, amount: 1240, status: "Succeeded", method: "CC", gatewayId: "prg_aa12", date: new Date(Date.now() - 86400000 * 2).toISOString(), description: "Posting fees · June batch" },
  { id: "pay_002", practiceId: mockPractice.id, amount: 380, status: "Succeeded", method: "ACH", gatewayId: "prg_ab32", date: new Date(Date.now() - 86400000 * 9).toISOString(), description: "Temporary placement · pro_011" },
];

export const mockDashboard: PracticeDashboardData = {
  practice: mockPractice,
  location: mockLocations[0],
  kpis: {
    activePostings: 6,
    filledToday: 3,
    pendingInterviews: 4,
    sosSent: 1,
  },
};
