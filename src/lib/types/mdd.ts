// MDD domain types — names mirror entities in the architecture spec.

export type UUID = string;
export type ISODateTime = string;

export type UserRole = "SuperAdmin" | "SystemAdmin" | "PracticeOwner" | "Professional";
export type EntityStatus = "Active" | "Inactive";

export interface AddressStruct {
  street: string;
  city: string;
  state: string;
  zip: string;
  lat?: number;
  lng?: number;
}

export interface Practice {
  id: UUID;
  companyName: string;
  ownerFirstName: string;
  ownerLastName: string;
  email: string;
  phone: string;
  status: EntityStatus;
}

export interface PracticeLocation {
  id: UUID;
  practiceId: UUID;
  name: string;
  address: AddressStruct;
  lat: number;
  lng: number;
  radius: number; // miles
  contactFirstName: string;
  contactLastName: string;
  phone: string;
}

export type ProfessionalSpecialty =
  | "Hygienist"
  | "Dentist"
  | "Assistant"
  | "FrontOffice"
  | "Orthodontist";

export type ProfessionalSubcategory =
  | "RDH"
  | "EFDA"
  | "DentalAssistant"
  | "SterilizationTech"
  | "TreatmentCoordinator"
  | "Receptionist"
  | "OfficeManager"
  | "GeneralDentist"
  | "Endodontist"
  | "Periodontist"
  | "OralSurgeon"
  | "Pediatric";

export const subcategoriesBySpecialty: Record<ProfessionalSpecialty, ProfessionalSubcategory[]> = {
  Hygienist: ["RDH", "EFDA"],
  Assistant: ["DentalAssistant", "SterilizationTech", "EFDA"],
  FrontOffice: ["Receptionist", "TreatmentCoordinator", "OfficeManager"],
  Dentist: ["GeneralDentist", "Endodontist", "Periodontist", "OralSurgeon", "Pediatric"],
  Orthodontist: ["GeneralDentist", "Pediatric"],
};

export type CertificateType =
  | "CPR"
  | "DAC"
  | "XRAY"
  | "DDS_DMD"
  | "DEA"
  | "LIABILITY"
  | "NPI";

export type CertificateStatus = "Valid" | "Expired" | "Pending" | "Rejected" | "Missing";

export interface Certificate {
  id: UUID;
  professionalId: UUID;
  type: CertificateType;
  status: CertificateStatus;
  expirationDate?: string;
  licenseNumber?: string;
  fileUrl?: string;
  rejectionComments?: string;
}

export interface Professional {
  id: UUID;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  specialty: ProfessionalSpecialty;
  rating: number;
  status: EntityStatus;
  documentStatus: "Complete" | "Incomplete" | "Pending";
  desiredHourly: number;
  availabilityMask: boolean[]; // length 7, Mon–Sun
  travelRadius: number;
  distanceMiles: number; // distance from current location
  lat: number;
  lng: number;
  avatarUrl?: string;
  certificates: Certificate[];
  online?: boolean;
}

export type PostingStatus = "Draft" | "Open" | "Filled" | "Cancelled" | "Expired";

export interface BasePosting {
  id: UUID;
  practiceId: UUID;
  locationId: UUID;
  specialty: ProfessionalSpecialty;
  subcategory: ProfessionalSubcategory;
  commutingRadius: number; // miles, max distance pros can be from location
  status: PostingStatus;
  startDate: ISODateTime;
  endDate?: ISODateTime;
  applicantsCount: number;
  workingSpaces: number;
  matchPercentage: number; // 0-100, AI match score
  title?: string;
  hiredCandidateIds?: string[];
}

export interface PermanentJobPosting extends BasePosting {
  kind: "Permanent";
  fullTime: boolean;
  salaryRange: { min: number; max: number };
  benefits: string[];
}

export type TemporaryKind = "Simple" | "Complex" | "Weekly";

export interface JobDay {
  date: string;
  startTime: string;
  endTime: string;
  breakMinutes: number;
}

export interface WorkSchedule {
  id: UUID;
  postingId: UUID;
  days: JobDay[];
}

export interface TemporaryJobPosting extends BasePosting {
  kind: "Temporary";
  temporaryKind: TemporaryKind;
  hourlyRate: number;
  days: JobDay[];
}

export type JobPosting = PermanentJobPosting | TemporaryJobPosting;

export interface JobInterview {
  id: UUID;
  postingId: UUID;
  candidateId: UUID;
  scheduledDate: ISODateTime;
  status: "Scheduled" | "Completed" | "Cancelled" | "NoShow";
  attemptCount: number;
}

export interface CheckIn {
  id: UUID;
  postingId: UUID;
  professionalId: UUID;
  at: ISODateTime;
}

export interface NoShow {
  id: UUID;
  postingId: UUID;
  professionalId: UUID;
  at: ISODateTime;
  reason?: string;
}

export interface AttendanceAlert {
  id: UUID;
  postingId: UUID;
  professionalId: UUID;
  level: "Info" | "Warning" | "Critical";
  message: string;
  at: ISODateTime;
}

export interface SosRequest {
  id: UUID;
  practiceId: UUID;
  locationId: UUID;
  specialty: ProfessionalSpecialty;
  radius: number;
  message?: string;
  createdAt: ISODateTime;
  status: "Pending" | "Matched" | "Cancelled";
}

export type ActivityEvent =
  | ({ kind: "CheckIn" } & CheckIn)
  | ({ kind: "NoShow" } & NoShow)
  | ({ kind: "AttendanceAlert" } & AttendanceAlert)
  | ({ kind: "JobInterview" } & JobInterview)
  | ({ kind: "SosRequest" } & SosRequest);

export interface PaymentMethodCard {
  id: UUID;
  brand: "Visa" | "Mastercard" | "Amex" | "ACH";
  last4: string;
  expMonth?: number;
  expYear?: number;
  holderName: string;
  isDefault: boolean;
  type: "CC" | "ACH";
}

export interface Payment {
  id: UUID;
  practiceId: UUID;
  amount: number;
  status: "Pending" | "Succeeded" | "Failed" | "Refunded";
  method: "CC" | "ACH";
  gatewayId: string;
  date: ISODateTime;
  description: string;
}

export interface PracticeDashboardData {
  practice: Practice;
  location: PracticeLocation;
  kpis: {
    activePostings: number;
    filledToday: number;
    pendingInterviews: number;
    sosSent: number;
  };
}
