import { useRef, useState, type DragEvent } from "react";
import { createFileRoute } from "@tanstack/react-router";
import { motion } from "motion/react";
import {
  FileCheck2,
  FileWarning,
  FileClock,
  Upload,
  ShieldCheck,
  IdCard,
  Calendar as CalendarIcon,
  Plus,
} from "lucide-react";
import { Card } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { useAppStore } from "@/lib/store/app-store";
import type { CertificateStatus, CertificateType } from "@/lib/types/mdd";
import { toast } from "sonner";

export const Route = createFileRoute("/professional/certificates")({
  component: CertificatesPage,
});

const labelOf: Record<CertificateType, string> = {
  CPR: "CPR Certification",
  DAC: "Dental Assistant Cert.",
  XRAY: "Radiology (X-Ray) License",
  DDS_DMD: "State License (DDS / DMD)",
  DEA: "DEA Registration",
  LIABILITY: "Liability Insurance",
  NPI: "NPI Number",
};

const uploadOptions: CertificateType[] = ["CPR", "NPI", "LIABILITY", "DDS_DMD", "XRAY", "DAC", "DEA"];

const statusStyles: Record<CertificateStatus, string> = {
  Valid: "border-emerald-500/40 bg-emerald-500/10 text-emerald-600 dark:text-emerald-400",
  Expired: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
  Pending: "border-amber-500/40 bg-amber-500/10 text-amber-600 dark:text-amber-400",
  Rejected: "border-rose-500/40 bg-rose-500/10 text-rose-600 dark:text-rose-400",
  Missing: "border-border bg-muted text-muted-foreground",
};

const statusIcon = (s: CertificateStatus) =>
  s === "Valid" ? FileCheck2 : s === "Pending" ? FileClock : FileWarning;

function CertificatesPage() {
  const profile = useAppStore((s) => s.professionalProfile);
  const upload = useAppStore((s) => s.uploadCertificate);
  const [drag, setDrag] = useState(false);
  const fileRef = useRef<HTMLInputElement>(null);

  const [type, setType] = useState<CertificateType>("CPR");
  const [issueDate, setIssueDate] = useState("");
  const [expirationDate, setExpirationDate] = useState("");
  const [licenseNumber, setLicenseNumber] = useState("");
  const [fileName, setFileName] = useState<string>("");

  const certs = profile.certificates;
  const valid = certs.filter((c) => c.status === "Valid").length;
  const expired = certs.filter((c) => c.status === "Expired").length;
  const pending = certs.filter((c) => c.status === "Pending").length;

  const submit = () => {
    if (!fileName) {
      toast.error("Attach a file to upload");
      return;
    }
    upload({ type, fileName, issueDate, expirationDate, licenseNumber: licenseNumber || undefined });
    toast.success("Certificate uploaded", { description: `${labelOf[type]} marked Valid.` });
    setIssueDate("");
    setExpirationDate("");
    setLicenseNumber("");
    setFileName("");
  };

  const handleFiles = (files: FileList | null) => {
    if (!files || files.length === 0) return;
    setFileName(files[0].name);
  };

  const onDrop = (e: DragEvent<HTMLDivElement>) => {
    e.preventDefault();
    setDrag(false);
    handleFiles(e.dataTransfer.files);
  };

  return (
    <div className="space-y-6 p-6">
      <header>
        <p className="text-xs font-medium uppercase tracking-wider text-primary">My Account</p>
        <h1 className="mt-1 text-2xl font-semibold tracking-tight">Certificates</h1>
        <p className="mt-1 text-sm text-muted-foreground">
          Upload certifications & licenses. Practices see verified status.
        </p>
      </header>

      <div className="grid gap-3 sm:grid-cols-3">
        <SummaryCard label="Valid" value={valid} tint="emerald" icon={ShieldCheck} />
        <SummaryCard label="Expired" value={expired} tint="rose" icon={FileWarning} />
        <SummaryCard label="Pending" value={pending} tint="amber" icon={FileClock} />
      </div>

      <Card className="space-y-4 p-5">
        <p className="text-sm font-semibold">Upload a certificate</p>

        <div
          onDragOver={(e) => {
            e.preventDefault();
            setDrag(true);
          }}
          onDragLeave={() => setDrag(false)}
          onDrop={onDrop}
          className={`flex flex-col items-center gap-1.5 rounded-2xl border-2 border-dashed p-6 text-center transition ${
            drag ? "border-primary bg-primary/5" : "border-border/70 bg-muted/30"
          }`}
        >
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/10 text-primary">
            <Upload className="h-4 w-4" />
          </div>
          <p className="text-sm font-medium">
            {fileName ? `Selected: ${fileName}` : "Drop file here or browse"}
          </p>
          <p className="text-[11px] text-muted-foreground">PDF, JPG, PNG · up to 10 MB</p>
          <input
            ref={fileRef}
            type="file"
            accept=".pdf,.jpg,.jpeg,.png"
            className="hidden"
            onChange={(e) => handleFiles(e.target.files)}
          />
          <Button size="sm" variant="outline" className="mt-2" onClick={() => fileRef.current?.click()}>
            <Upload className="h-3.5 w-3.5" /> Browse files
          </Button>
        </div>

        <div className="grid gap-4 md:grid-cols-2">
          <Field label="Certificate type">
            <Select value={type} onValueChange={(v) => setType(v as CertificateType)}>
              <SelectTrigger>
                <SelectValue />
              </SelectTrigger>
              <SelectContent>
                {uploadOptions.map((t) => (
                  <SelectItem key={t} value={t}>
                    {labelOf[t]}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </Field>
          <Field label="License / NPI #">
            <Input
              value={licenseNumber}
              onChange={(e) => setLicenseNumber(e.target.value)}
              placeholder="optional"
            />
          </Field>
          <Field label="Issue date">
            <div className="relative">
              <CalendarIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="date"
                className="pl-9"
                value={issueDate}
                onChange={(e) => setIssueDate(e.target.value)}
              />
            </div>
          </Field>
          <Field label="Expiration date">
            <div className="relative">
              <CalendarIcon className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
              <Input
                type="date"
                className="pl-9"
                value={expirationDate}
                onChange={(e) => setExpirationDate(e.target.value)}
              />
            </div>
          </Field>
        </div>

        <div className="flex justify-end">
          <Button onClick={submit}>
            <Plus className="h-4 w-4" /> Save certificate
          </Button>
        </div>
      </Card>

      <div>
        <p className="mb-3 text-sm font-semibold">Saved certificates</p>
        <div className="grid gap-3 md:grid-cols-2 xl:grid-cols-3">
          {certs.map((c, i) => {
            const Icon = statusIcon(c.status);
            return (
              <motion.div
                key={c.id}
                initial={{ opacity: 0, y: 8 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: i * 0.04 }}
              >
                <Card className="relative flex h-full flex-col gap-3 overflow-hidden p-0">
                  <div className="flex items-start justify-between gap-2 bg-gradient-to-br from-primary/10 to-transparent p-4">
                    <div className="flex items-center gap-2.5">
                      <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-primary/15 text-primary">
                        <IdCard className="h-5 w-5" />
                      </div>
                      <div>
                        <p className="text-sm font-semibold">{labelOf[c.type]}</p>
                        <p className="text-[11px] text-muted-foreground">
                          {c.licenseNumber ? `# ${c.licenseNumber}` : c.fileUrl ?? "Awaiting upload"}
                        </p>
                      </div>
                    </div>
                    <Badge variant="outline" className={`gap-1 ${statusStyles[c.status]}`}>
                      <Icon className="h-3 w-3" /> {c.status}
                    </Badge>
                  </div>
                  <div className="flex items-center justify-between gap-2 px-4 pb-3 text-[11px] text-muted-foreground">
                    <span>
                      {c.expirationDate
                        ? `Expires ${new Date(c.expirationDate).toLocaleDateString()}`
                        : "No expiration"}
                    </span>
                    <Button
                      size="sm"
                      variant="ghost"
                      className="h-7 text-xs"
                      onClick={() => {
                        setType(c.type);
                        fileRef.current?.click();
                      }}
                    >
                      <Upload className="h-3 w-3" /> {c.status === "Valid" ? "Replace" : "Upload"}
                    </Button>
                  </div>
                </Card>
              </motion.div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div className="space-y-1.5">
      <Label className="text-xs uppercase tracking-wider text-muted-foreground">{label}</Label>
      {children}
    </div>
  );
}

function SummaryCard({
  label,
  value,
  tint,
  icon: Icon,
}: {
  label: string;
  value: number;
  tint: "emerald" | "rose" | "amber";
  icon: typeof FileCheck2;
}) {
  const tints: Record<string, string> = {
    emerald: "bg-emerald-500/10 text-emerald-600 dark:text-emerald-400 border-emerald-500/30",
    rose: "bg-rose-500/10 text-rose-600 dark:text-rose-400 border-rose-500/30",
    amber: "bg-amber-500/10 text-amber-600 dark:text-amber-400 border-amber-500/30",
  };
  return (
    <Card className="flex items-center gap-4 p-4">
      <div className={`flex h-11 w-11 items-center justify-center rounded-xl border ${tints[tint]}`}>
        <Icon className="h-5 w-5" />
      </div>
      <div>
        <p className="text-xs uppercase tracking-wider text-muted-foreground">{label}</p>
        <p className="text-2xl font-semibold">{value}</p>
      </div>
    </Card>
  );
}
