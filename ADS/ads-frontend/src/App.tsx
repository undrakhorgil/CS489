import { FormEvent, useCallback, useEffect, useMemo, useState } from "react";

const TOKEN_KEY = "ads_token";
const USER_KEY = "ads_username";
const ROLE_KEY = "ads_role";

type Session = {
  token: string;
  username: string;
  role: string;
} | null;

type DentistRow = {
  dentistId: number;
  firstName: string;
  lastName: string;
  specialization: string;
  email: string;
};

type SurgeryRow = {
  surgeryId: number;
  name: string;
};

type OfficePatientRow = {
  patientId: number;
  firstName: string;
  lastName: string;
  email: string;
  contactPhoneNumber: string;
  mailingAddress: string;
  dateOfBirth: string;
};

type SlotRow = { startAt: string };

type CalendarAppointmentEntry = {
  startAt: string;
  status: string;
  surgeryId: number;
  surgeryName: string;
  /** Set for dentist self-schedule API; omitted for patient booking view. */
  patientName?: string | null;
};

type DayScheduleRow = { date: string; appointments: CalendarAppointmentEntry[] };

type AppointmentDetailRow = {
  appointmentId: number;
  status: string;
  startAt: string;
  proposedStartAt: string | null;
  dentist: { firstName: string; lastName: string };
  patient: { firstName: string; lastName: string };
  surgery: {
    name: string;
    locationAddress?: string | null;
    telephoneNumber?: string | null;
  };
};

function readSession(): Session {
  const token = sessionStorage.getItem(TOKEN_KEY);
  const username = sessionStorage.getItem(USER_KEY);
  const role = sessionStorage.getItem(ROLE_KEY);
  if (!token || !username || !role) return null;
  return { token, username, role };
}

function writeSession(token: string, username: string, role: string) {
  sessionStorage.setItem(TOKEN_KEY, token);
  sessionStorage.setItem(USER_KEY, username);
  sessionStorage.setItem(ROLE_KEY, role);
}

function clearSession() {
  sessionStorage.removeItem(TOKEN_KEY);
  sessionStorage.removeItem(USER_KEY);
  sessionStorage.removeItem(ROLE_KEY);
}

function apiUrl(path: string): string {
  const base = (import.meta.env.VITE_API_BASE ?? "").replace(/\/$/, "");
  const suffix = path.startsWith("/") ? path : `/${path}`;
  if (!base) return `/api/v1${suffix}`;
  return `${base}/api/v1${suffix}`;
}

function pad2(n: number) {
  return String(n).padStart(2, "0");
}

function toYmd(d: Date): string {
  return `${d.getFullYear()}-${pad2(d.getMonth() + 1)}-${pad2(d.getDate())}`;
}

/** Tomorrow (local) for a sensible first selection. */
function defaultSelectedYmd(): string {
  const t = new Date();
  t.setDate(t.getDate() + 1);
  return toYmd(t);
}

function monthMatrix(year: number, month: number): ({ day: number; ymd: string } | null)[][] {
  const first = new Date(year, month, 1);
  const startPad = first.getDay();
  const dim = new Date(year, month + 1, 0).getDate();
  const cells: ({ day: number; ymd: string } | null)[] = [];
  for (let i = 0; i < startPad; i++) cells.push(null);
  for (let d = 1; d <= dim; d++) {
    cells.push({ day: d, ymd: toYmd(new Date(year, month, d)) });
  }
  while (cells.length % 7 !== 0) cells.push(null);
  const rows: ({ day: number; ymd: string } | null)[][] = [];
  for (let i = 0; i < cells.length; i += 7) rows.push(cells.slice(i, i + 7));
  return rows;
}

function formatSlotLabel(iso: string): string {
  const d = new Date(iso.replace(" ", "T"));
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleTimeString(undefined, { hour: "numeric", minute: "2-digit" });
}

function formatApptDate(iso: string): string {
  const d = new Date(iso.replace(" ", "T"));
  if (Number.isNaN(d.getTime())) return iso;
  return d.toLocaleDateString(undefined, {
    weekday: "short",
    month: "short",
    day: "numeric",
    year: "numeric",
  });
}

function abbrevStatus(status: string): string {
  const map: Record<string, string> = {
    BOOKED: "Booked",
    REQUESTED: "Requested",
    RESCHEDULE_REQUESTED: "Resched…",
    CANCEL_REQUESTED: "Cancel?",
    CANCELLED: "Cancelled",
  };
  return map[status] ?? status;
}

type PatientCalEntry = {
  appointmentId: number;
  startAt: string;
  status: string;
  dentistName: string;
  surgeryName: string;
  surgeryAddress?: string | null;
  surgeryPhone?: string | null;
  proposedStartAt?: string | null;
};

function formatCalDayHeader(ymd: string): string {
  const d = new Date(`${ymd}T12:00:00`);
  if (Number.isNaN(d.getTime())) return ymd;
  return d.toLocaleDateString(undefined, {
    weekday: "long",
    month: "long",
    day: "numeric",
    year: "numeric",
  });
}

/** Native tooltip (hover): one visit line in the patient month grid. */
function patientCalEntryHoverTitle(e: PatientCalEntry): string {
  const lines = [
    `${formatSlotLabel(e.startAt)} — ${abbrevStatus(e.status)}`,
    `Dentist: ${e.dentistName}`,
    `Surgery: ${e.surgeryName}`,
  ];
  if (e.surgeryAddress) lines.push(`Address: ${e.surgeryAddress}`);
  if (e.surgeryPhone) lines.push(`Phone: ${e.surgeryPhone}`);
  if (e.proposedStartAt) {
    const pd = new Date(e.proposedStartAt.replace(" ", "T"));
    if (!Number.isNaN(pd.getTime())) {
      lines.push(`Proposed time: ${pd.toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" })}`);
    }
  }
  return lines.join("\n");
}

/** Native tooltip (hover): full day + every visit on that day. */
function patientCalDayHoverTitle(ymd: string, entries: PatientCalEntry[]): string {
  const head = formatCalDayHeader(ymd);
  if (entries.length === 0) {
    return `${head}\n\nNo visits on this day.\nClick to choose this date for booking below.`;
  }
  const blocks = entries.map((e) => patientCalEntryHoverTitle(e));
  return `${head}\n\n${blocks.join("\n\n────────\n\n")}`;
}

function dentistCalEntryHoverTitle(e: CalendarAppointmentEntry): string {
  const who = e.patientName?.trim() || "Patient";
  return [
    `${formatSlotLabel(e.startAt)} — ${abbrevStatus(e.status)}`,
    `Patient: ${who}`,
    `Surgery: ${e.surgeryName}`,
  ].join("\n");
}

function dentistCalDayHoverTitle(ymd: string, entries: CalendarAppointmentEntry[]): string {
  const head = formatCalDayHeader(ymd);
  if (entries.length === 0) {
    return `${head}\n\nNo visits on this day.`;
  }
  const blocks = entries.map((e) => dentistCalEntryHoverTitle(e));
  return `${head}\n\n${blocks.join("\n\n────────\n\n")}`;
}

function officeSingleAppointmentHoverTitle(a: AppointmentDetailRow): string {
  const lines = [
    `${formatSlotLabel(a.startAt)} — ${abbrevStatus(a.status)}`,
    `Patient: ${a.patient.firstName} ${a.patient.lastName}`,
    `Dentist: ${a.dentist.firstName} ${a.dentist.lastName}`,
    `Surgery: ${a.surgery.name}`,
  ];
  if (a.surgery.locationAddress) lines.push(`Address: ${a.surgery.locationAddress}`);
  if (a.proposedStartAt) {
    const pd = new Date(a.proposedStartAt.replace(" ", "T"));
    if (!Number.isNaN(pd.getTime())) {
      lines.push(`Proposed time: ${pd.toLocaleString(undefined, { dateStyle: "medium", timeStyle: "short" })}`);
    }
  }
  return lines.join("\n");
}

/** Office manager: every appointment in the directory for one day (full detail tooltip). */
function officeAllApptsCalDayHoverTitle(ymd: string, entries: AppointmentDetailRow[]): string {
  const head = formatCalDayHeader(ymd);
  if (entries.length === 0) {
    return `${head}\n\nNo appointments this day.\nClick to choose this date for booking below.`;
  }
  const blocks = entries.map((a) => officeSingleAppointmentHoverTitle(a));
  return `${head}\n\n${blocks.join("\n\n────────\n\n")}`;
}

function isAppointmentStartPast(startAt: string): boolean {
  const t = new Date(startAt.replace(" ", "T")).getTime();
  return !Number.isNaN(t) && t < Date.now();
}

/** Dentist / office table: no actions on past visits, except pending reschedule to a future slot. */
function appointmentAllowsStaffActions(a: AppointmentDetailRow): boolean {
  if (a.status === "CANCELLED") return false;
  if (a.status === "CANCEL_REQUESTED") return true;
  if (a.status === "RESCHEDULE_REQUESTED" && a.proposedStartAt) {
    const p = new Date(a.proposedStartAt.replace(" ", "T")).getTime();
    if (!Number.isNaN(p) && p >= Date.now()) return true;
  }
  return !isAppointmentStartPast(a.startAt);
}

/** Patient: withdraw a request or ask to cancel a booked upcoming visit. */
function patientCanSelfServiceCancel(a: AppointmentDetailRow): boolean {
  if (a.status === "CANCELLED" || a.status === "CANCEL_REQUESTED") return false;
  if (a.status !== "BOOKED" && a.status !== "REQUESTED") return false;
  return !isAppointmentStartPast(a.startAt);
}

/** Past = grey; then status (booked green, requested yellow, cancelled red, pending orange). */
function appointmentStatusCalendarClass(entry: { startAt: string; status: string }): string {
  const t = new Date(entry.startAt.replace(" ", "T")).getTime();
  if (!Number.isNaN(t) && t < Date.now()) return "cal-appt-past";
  switch (entry.status) {
    case "CANCELLED":
      return "cal-appt-cancelled";
    case "BOOKED":
      return "cal-appt-booked";
    case "REQUESTED":
      return "cal-appt-requested";
    case "CANCEL_REQUESTED":
    case "RESCHEDULE_REQUESTED":
      return "cal-appt-pending";
    default:
      return "cal-appt-neutral";
  }
}

function parseMonthScheduleJson(json: string): Record<string, CalendarAppointmentEntry[]> {
  const rows = JSON.parse(json) as DayScheduleRow[];
  const map: Record<string, CalendarAppointmentEntry[]> = {};
  for (const r of rows) {
    map[r.date] = r.appointments ?? [];
  }
  return map;
}

export default function App() {
  const [session, setSession] = useState<Session>(() => readSession());
  const [username, setUsername] = useState("patient1");
  const [password, setPassword] = useState("password");

  const [surgeryName, setSurgeryName] = useState("ADS - Web UI Test");
  const [surgeryAddress, setSurgeryAddress] = useState("1 Main St, Fairfield, IA");
  const [surgeryPhone, setSurgeryPhone] = useState("515-555-9999");

  const [dentists, setDentists] = useState<DentistRow[]>([]);
  const [surgeries, setSurgeries] = useState<SurgeryRow[]>([]);
  const [selectedDentistId, setSelectedDentistId] = useState("");
  const [selectedSurgeryId, setSelectedSurgeryId] = useState("");
  const [viewMonth, setViewMonth] = useState(() => new Date());
  const [selectedYmd, setSelectedYmd] = useState(defaultSelectedYmd);
  const [slots, setSlots] = useState<SlotRow[]>([]);
  const [slotsLoading, setSlotsLoading] = useState(false);
  const [selectedSlotStart, setSelectedSlotStart] = useState<string | null>(null);
  const [isBooking, setIsBooking] = useState(false);
  const [scheduleByDay, setScheduleByDay] = useState<Record<string, CalendarAppointmentEntry[]>>({});
  const [myAppointments, setMyAppointments] = useState<AppointmentDetailRow[]>([]);
  const [appointmentsLoading, setAppointmentsLoading] = useState(false);
  const [dentistActionBusy, setDentistActionBusy] = useState(false);
  const [patientActionBusy, setPatientActionBusy] = useState(false);
  const [officePatients, setOfficePatients] = useState<OfficePatientRow[]>([]);
  const [officeDentists, setOfficeDentists] = useState<DentistRow[]>([]);
  const [officeSurgeries, setOfficeSurgeries] = useState<SurgeryRow[]>([]);
  const [officeAppointments, setOfficeAppointments] = useState<AppointmentDetailRow[]>([]);
  const [officeListsLoading, setOfficeListsLoading] = useState(false);
  const [officeActionBusy, setOfficeActionBusy] = useState(false);
  type OfficeTab = "surgery" | "patient" | "dentist" | "appointment";
  const [officeTab, setOfficeTab] = useState<OfficeTab>("surgery");
  const [officeMgrViewMonth, setOfficeMgrViewMonth] = useState(() => new Date());
  const [officeMgrSelectedYmd, setOfficeMgrSelectedYmd] = useState(defaultSelectedYmd);
  const [officeMgrPatientId, setOfficeMgrPatientId] = useState("");
  const [officeMgrDentistId, setOfficeMgrDentistId] = useState("");
  const [officeMgrSurgeryId, setOfficeMgrSurgeryId] = useState("");
  const [officeMgrSlots, setOfficeMgrSlots] = useState<SlotRow[]>([]);
  const [officeMgrSlotsLoading, setOfficeMgrSlotsLoading] = useState(false);
  const [officeMgrSelectedSlot, setOfficeMgrSelectedSlot] = useState<string | null>(null);
  const [officeMgrBooking, setOfficeMgrBooking] = useState(false);

  const [regPatientFirst, setRegPatientFirst] = useState("");
  const [regPatientLast, setRegPatientLast] = useState("");
  const [regPatientPhone, setRegPatientPhone] = useState("");
  const [regPatientEmail, setRegPatientEmail] = useState("");
  const [regPatientAddress, setRegPatientAddress] = useState("");
  const [regPatientDob, setRegPatientDob] = useState("");
  const [regPatientSubmitting, setRegPatientSubmitting] = useState(false);

  const [regDentistFirst, setRegDentistFirst] = useState("");
  const [regDentistLast, setRegDentistLast] = useState("");
  const [regDentistPhone, setRegDentistPhone] = useState("");
  const [regDentistEmail, setRegDentistEmail] = useState("");
  const [regDentistSpec, setRegDentistSpec] = useState("");
  const [regDentistSubmitting, setRegDentistSubmitting] = useState(false);

  const roleUpper = useMemo(() => (session?.role ?? "").toUpperCase(), [session]);

  const calYear = viewMonth.getFullYear();
  const calMonth = viewMonth.getMonth();
  const calRows = useMemo(() => monthMatrix(calYear, calMonth), [calYear, calMonth]);
  const officeMgrCalYear = officeMgrViewMonth.getFullYear();
  const officeMgrCalMonth = officeMgrViewMonth.getMonth();
  const officeMgrCalRows = useMemo(
    () => monthMatrix(officeMgrCalYear, officeMgrCalMonth),
    [officeMgrCalYear, officeMgrCalMonth]
  );
  const todayYmd = useMemo(() => toYmd(new Date()), []);

  /** Every appointment in the office list for the manager's viewed month (all dentists / surgeries). */
  const officeAllApptsByDay = useMemo(() => {
    if (roleUpper !== "OFFICE_MANAGER") return {};
    const map: Record<string, AppointmentDetailRow[]> = {};
    for (const a of officeAppointments) {
      const d = new Date(a.startAt.replace(" ", "T"));
      if (Number.isNaN(d.getTime())) continue;
      if (d.getFullYear() !== officeMgrCalYear || d.getMonth() !== officeMgrCalMonth) continue;
      const ymd = toYmd(d);
      const list = map[ymd] ?? [];
      list.push(a);
      map[ymd] = list;
    }
    for (const list of Object.values(map)) {
      list.sort(
        (x, y) =>
          new Date(x.startAt.replace(" ", "T")).getTime() -
          new Date(y.startAt.replace(" ", "T")).getTime()
      );
    }
    return map;
  }, [roleUpper, officeAppointments, officeMgrCalYear, officeMgrCalMonth]);

  const patientScheduleByDay = useMemo(() => {
    if (roleUpper !== "PATIENT") return {};
    const map: Record<string, PatientCalEntry[]> = {};
    for (const a of myAppointments) {
      const d = new Date(a.startAt.replace(" ", "T"));
      if (Number.isNaN(d.getTime())) continue;
      if (d.getFullYear() !== calYear || d.getMonth() !== calMonth) continue;
      const ymd = toYmd(d);
      const dentistName = `${a.dentist.firstName} ${a.dentist.lastName}`;
      const list = map[ymd] ?? [];
      list.push({
        appointmentId: a.appointmentId,
        startAt: a.startAt,
        status: a.status,
        dentistName,
        surgeryName: a.surgery.name,
        surgeryAddress: a.surgery.locationAddress,
        surgeryPhone: a.surgery.telephoneNumber,
        proposedStartAt: a.proposedStartAt,
      });
      map[ymd] = list;
    }
    for (const list of Object.values(map)) {
      list.sort(
        (x, y) =>
          new Date(x.startAt.replace(" ", "T")).getTime() -
          new Date(y.startAt.replace(" ", "T")).getTime()
      );
    }
    return map;
  }, [roleUpper, myAppointments, calYear, calMonth]);

  function logAppError(context: string, err: unknown) {
    const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
    console.error(`[ADS] ${context}`, detail);
  }

  async function apiFetch(
    path: string,
    opts: { method?: string; body?: unknown; auth?: boolean } = {}
  ): Promise<string> {
    const { method = "GET", body, auth = false } = opts;
    const headers: Record<string, string> = { Accept: "application/json" };
    if (body !== undefined) headers["Content-Type"] = "application/json";
    if (auth) {
      const t = readSession()?.token;
      if (!t) throw new Error("Not signed in.");
      headers.Authorization = `Bearer ${t}`;
    }
    const res = await fetch(apiUrl(path), {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
    const text = await res.text();
    let pretty = text;
    try {
      pretty = text ? JSON.stringify(JSON.parse(text), null, 2) : "";
    } catch {
      // keep raw
    }
    if (!res.ok) {
      const err = new Error(`HTTP ${res.status}`);
      (err as Error & { detail?: string }).detail = pretty || text;
      throw err;
    }
    return pretty;
  }

  useEffect(() => {
    if (!session || roleUpper !== "PATIENT") return;
    let cancelled = false;
    (async () => {
      try {
        const [dj, sj] = await Promise.all([
          apiFetch("/patient/booking/dentists", { auth: true }),
          apiFetch("/patient/booking/surgeries", { auth: true }),
        ]);
        if (cancelled) return;
        const dList = JSON.parse(dj) as DentistRow[];
        const sList = JSON.parse(sj) as SurgeryRow[];
        setDentists(dList);
        setSurgeries(sList);
        setSelectedDentistId((prev) => prev || (dList[0] ? String(dList[0].dentistId) : ""));
        setSelectedSurgeryId((prev) => prev || (sList[0] ? String(sList[0].surgeryId) : ""));
      } catch (err) {
        if (!cancelled) logAppError("Could not load dentists/surgeries", err);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [session, roleUpper]);

  useEffect(() => {
    if (!session || roleUpper !== "DENTIST") {
      setScheduleByDay({});
      return;
    }
    let cancelled = false;
    const month = `${calYear}-${pad2(calMonth + 1)}`;
    (async () => {
      try {
        const json = await apiFetch(`/dentist/booking/month-schedule?month=${encodeURIComponent(month)}`, {
          auth: true,
        });
        if (cancelled) return;
        setScheduleByDay(parseMonthScheduleJson(json));
      } catch {
        if (!cancelled) setScheduleByDay({});
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [session, roleUpper, calYear, calMonth]);

  useEffect(() => {
    if (!session || roleUpper !== "PATIENT" || !selectedDentistId || !selectedYmd) return;
    let cancelled = false;
    setSlotsLoading(true);
    (async () => {
      try {
        const surgeryQ =
          selectedSurgeryId !== ""
            ? `&surgeryId=${encodeURIComponent(selectedSurgeryId)}`
            : "";
        const q = `/patient/booking/availability?dentistId=${encodeURIComponent(selectedDentistId)}&date=${encodeURIComponent(selectedYmd)}${surgeryQ}`;
        const json = await apiFetch(q, { auth: true });
        if (cancelled) return;
        setSlots(JSON.parse(json) as SlotRow[]);
      } catch (err) {
        if (!cancelled) {
          setSlots([]);
          logAppError("Availability failed", err);
        }
      } finally {
        if (!cancelled) setSlotsLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [session, roleUpper, selectedDentistId, selectedYmd, selectedSurgeryId]);

  useEffect(() => {
    if (!session || (roleUpper !== "PATIENT" && roleUpper !== "DENTIST")) {
      setMyAppointments([]);
      return;
    }
    let cancelled = false;
    setAppointmentsLoading(true);
    (async () => {
      try {
        const path = roleUpper === "PATIENT" ? "/patient/appointments" : "/dentist/appointments";
        const json = await apiFetch(path, { auth: true });
        if (cancelled) return;
        setMyAppointments(JSON.parse(json) as AppointmentDetailRow[]);
      } catch (err) {
        if (!cancelled) {
          setMyAppointments([]);
          logAppError("Could not load appointments", err);
        }
      } finally {
        if (!cancelled) setAppointmentsLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [session, roleUpper]);

  useEffect(() => {
    if (!selectedSlotStart) return;
    if (!slots.some((s) => s.startAt === selectedSlotStart)) {
      setSelectedSlotStart(null);
    }
  }, [selectedYmd, slots, selectedSlotStart]);

  async function onLogin(e: FormEvent) {
    e.preventDefault();
    try {
      const json = await apiFetch("/auth/login", {
        method: "POST",
        body: { username: username.trim(), password },
      });
      const data = JSON.parse(json) as {
        accessToken: string;
        username: string;
        role: string;
      };
      writeSession(data.accessToken, data.username, data.role);
      setSession(readSession());
    } catch (err) {
      clearSession();
      setSession(null);
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(`Login failed\n\n${detail}`);
    }
  }

  function onLogout() {
    clearSession();
    setSession(null);
    setDentists([]);
    setSurgeries([]);
    setSelectedDentistId("");
    setSelectedSurgeryId("");
    setSlots([]);
    setSelectedSlotStart(null);
    setScheduleByDay({});
    setMyAppointments([]);
    setOfficePatients([]);
    setOfficeDentists([]);
    setOfficeSurgeries([]);
    setOfficeAppointments([]);
    setOfficeTab("surgery");
    setOfficeMgrPatientId("");
    setOfficeMgrDentistId("");
    setOfficeMgrSurgeryId("");
    setOfficeMgrSlots([]);
    setOfficeMgrSelectedSlot(null);
    setOfficeMgrViewMonth(new Date());
    setOfficeMgrSelectedYmd(defaultSelectedYmd());
  }

  async function refreshMonthSchedule() {
    if (roleUpper !== "DENTIST") return;
    const month = `${calYear}-${pad2(calMonth + 1)}`;
    const json = await apiFetch(`/dentist/booking/month-schedule?month=${encodeURIComponent(month)}`, {
      auth: true,
    });
    setScheduleByDay(parseMonthScheduleJson(json));
  }

  async function dentistAppointmentPost(suffix: string) {
    setDentistActionBusy(true);
    try {
      await apiFetch(`/dentist/appointments/${suffix}`, { method: "POST", auth: true });
      await reloadMyAppointments();
      await refreshMonthSchedule();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(detail);
    } finally {
      setDentistActionBusy(false);
    }
  }

  async function patientRequestCancel(appointmentId: number) {
    setPatientActionBusy(true);
    try {
      await apiFetch(`/patient/appointments/${appointmentId}/cancel-request`, { method: "POST", auth: true });
      await reloadMyAppointments();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(detail);
    } finally {
      setPatientActionBusy(false);
    }
  }

  async function reloadMyAppointments() {
    if (!session || (roleUpper !== "PATIENT" && roleUpper !== "DENTIST")) return;
    const path = roleUpper === "PATIENT" ? "/patient/appointments" : "/dentist/appointments";
    const json = await apiFetch(path, { auth: true });
    setMyAppointments(JSON.parse(json) as AppointmentDetailRow[]);
  }

  const loadOfficeLists = useCallback(async () => {
    if (!session || roleUpper !== "OFFICE_MANAGER") return;
    setOfficeListsLoading(true);
    try {
      const [pj, dj, sj, aj] = await Promise.all([
        apiFetch("/office/patients", { auth: true }),
        apiFetch("/office/dentists", { auth: true }),
        apiFetch("/office/surgeries", { auth: true }),
        apiFetch("/office/appointments", { auth: true }),
      ]);
      const pList = JSON.parse(pj) as OfficePatientRow[];
      const dList = JSON.parse(dj) as DentistRow[];
      const sList = JSON.parse(sj) as SurgeryRow[];
      setOfficePatients(pList);
      setOfficeDentists(dList);
      setOfficeSurgeries(sList);
      setOfficeAppointments(JSON.parse(aj) as AppointmentDetailRow[]);
      setOfficeMgrPatientId((prev) => prev || (pList[0] ? String(pList[0].patientId) : ""));
      setOfficeMgrDentistId((prev) => prev || (dList[0] ? String(dList[0].dentistId) : ""));
      setOfficeMgrSurgeryId((prev) => prev || (sList[0] ? String(sList[0].surgeryId) : ""));
    } catch (err) {
      logAppError("Could not load office lists", err);
    } finally {
      setOfficeListsLoading(false);
    }
  }, [session, roleUpper]);

  useEffect(() => {
    if (!session || roleUpper !== "OFFICE_MANAGER") {
      setOfficePatients([]);
      setOfficeDentists([]);
      setOfficeSurgeries([]);
      setOfficeAppointments([]);
      setOfficeTab("surgery");
      setOfficeMgrPatientId("");
      setOfficeMgrDentistId("");
      setOfficeMgrSurgeryId("");
      setOfficeMgrSlots([]);
      setOfficeMgrSelectedSlot(null);
      setOfficeMgrViewMonth(new Date());
      setOfficeMgrSelectedYmd(defaultSelectedYmd());
      return;
    }
    void loadOfficeLists();
  }, [session, roleUpper, loadOfficeLists]);

  useEffect(() => {
    if (!session || roleUpper !== "OFFICE_MANAGER" || officeTab !== "appointment" || !officeMgrDentistId || !officeMgrSelectedYmd) {
      return;
    }
    let cancelled = false;
    setOfficeMgrSlotsLoading(true);
    (async () => {
      try {
        const surgeryQ =
          officeMgrSurgeryId !== "" ? `&surgeryId=${encodeURIComponent(officeMgrSurgeryId)}` : "";
        const q = `/office/booking/availability?dentistId=${encodeURIComponent(officeMgrDentistId)}&date=${encodeURIComponent(officeMgrSelectedYmd)}${surgeryQ}`;
        const json = await apiFetch(q, { auth: true });
        if (!cancelled) setOfficeMgrSlots(JSON.parse(json) as SlotRow[]);
      } catch (err) {
        if (!cancelled) {
          setOfficeMgrSlots([]);
          logAppError("Office availability failed", err);
        }
      } finally {
        if (!cancelled) setOfficeMgrSlotsLoading(false);
      }
    })();
    return () => {
      cancelled = true;
    };
  }, [session, roleUpper, officeTab, officeMgrDentistId, officeMgrSelectedYmd, officeMgrSurgeryId]);

  useEffect(() => {
    if (!officeMgrSelectedSlot) return;
    if (!officeMgrSlots.some((s) => s.startAt === officeMgrSelectedSlot)) {
      setOfficeMgrSelectedSlot(null);
    }
  }, [officeMgrSelectedYmd, officeMgrSlots, officeMgrSelectedSlot]);

  async function officeAppointmentPost(suffix: string) {
    setOfficeActionBusy(true);
    try {
      await apiFetch(`/office/appointments/${suffix}`, { method: "POST", auth: true });
      await loadOfficeLists();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(detail);
    } finally {
      setOfficeActionBusy(false);
    }
  }

  async function confirmOfficeDirectBook() {
    const startAt = officeMgrSelectedSlot;
    if (!startAt) {
      alert("Select a time slot first.");
      return;
    }
    if (!officeMgrPatientId || !officeMgrDentistId || !officeMgrSurgeryId) {
      alert("Choose patient, dentist, and surgery.");
      return;
    }
    setOfficeMgrBooking(true);
    try {
      await apiFetch("/office/appointments/direct-book", {
        method: "POST",
        auth: true,
        body: {
          patientId: Number(officeMgrPatientId),
          dentistId: Number(officeMgrDentistId),
          surgeryId: Number(officeMgrSurgeryId),
          startAt,
          channel: "PHONE",
        },
      });
      setOfficeMgrSelectedSlot(null);
      const surgeryQ =
        officeMgrSurgeryId !== "" ? `&surgeryId=${encodeURIComponent(officeMgrSurgeryId)}` : "";
      const refresh = await apiFetch(
        `/office/booking/availability?dentistId=${encodeURIComponent(officeMgrDentistId)}&date=${encodeURIComponent(officeMgrSelectedYmd)}${surgeryQ}`,
        { auth: true }
      );
      setOfficeMgrSlots(JSON.parse(refresh) as SlotRow[]);
      await loadOfficeLists();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(`Booking failed\n\n${detail}`);
    } finally {
      setOfficeMgrBooking(false);
    }
  }

  async function onRegisterOfficePatient(e: FormEvent) {
    e.preventDefault();
    if (!regPatientFirst.trim() || !regPatientLast.trim() || !regPatientEmail.trim() || !regPatientAddress.trim()) {
      alert("First name, last name, email, and mailing address are required.");
      return;
    }
    if (!regPatientDob.trim()) {
      alert("Date of birth is required.");
      return;
    }
    setRegPatientSubmitting(true);
    try {
      await apiFetch("/office/patients", {
        method: "POST",
        auth: true,
        body: {
          firstName: regPatientFirst.trim(),
          lastName: regPatientLast.trim(),
          contactPhoneNumber: regPatientPhone.trim() || null,
          email: regPatientEmail.trim(),
          mailingAddress: regPatientAddress.trim(),
          dateOfBirth: regPatientDob.trim(),
        },
      });
      setRegPatientFirst("");
      setRegPatientLast("");
      setRegPatientPhone("");
      setRegPatientEmail("");
      setRegPatientAddress("");
      setRegPatientDob("");
      await loadOfficeLists();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(`Request failed\n\n${detail}`);
    } finally {
      setRegPatientSubmitting(false);
    }
  }

  async function onRegisterOfficeDentist(e: FormEvent) {
    e.preventDefault();
    if (!regDentistFirst.trim() || !regDentistLast.trim() || !regDentistEmail.trim() || !regDentistSpec.trim()) {
      alert("First name, last name, email, and specialization are required.");
      return;
    }
    setRegDentistSubmitting(true);
    try {
      await apiFetch("/office/dentists", {
        method: "POST",
        auth: true,
        body: {
          firstName: regDentistFirst.trim(),
          lastName: regDentistLast.trim(),
          contactPhoneNumber: regDentistPhone.trim() || null,
          email: regDentistEmail.trim(),
          specialization: regDentistSpec.trim(),
        },
      });
      setRegDentistFirst("");
      setRegDentistLast("");
      setRegDentistPhone("");
      setRegDentistEmail("");
      setRegDentistSpec("");
      await loadOfficeLists();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(`Request failed\n\n${detail}`);
    } finally {
      setRegDentistSubmitting(false);
    }
  }

  function shiftOfficeMgrMonth(delta: number) {
    setOfficeMgrViewMonth((prev) => {
      const n = new Date(prev.getFullYear(), prev.getMonth() + delta, 1);
      const y = n.getFullYear();
      const mo = n.getMonth();
      setOfficeMgrSelectedYmd((sel) => {
        const dSel = new Date(`${sel}T12:00:00`);
        if (!Number.isNaN(dSel.getTime()) && dSel.getFullYear() === y && dSel.getMonth() === mo) {
          return sel;
        }
        return toYmd(n);
      });
      return n;
    });
  }

  async function confirmBookAppointment() {
    const startAt = selectedSlotStart;
    if (!startAt) {
      alert("Select a time slot first.");
      return;
    }
    if (!selectedDentistId || !selectedSurgeryId) {
      alert("Choose a dentist and surgery location first.");
      return;
    }
    setIsBooking(true);
    try {
      await apiFetch("/patient/appointment-requests", {
        method: "POST",
        auth: true,
        body: {
          dentistId: Number(selectedDentistId),
          surgeryId: Number(selectedSurgeryId),
          startAt,
          channel: "ONLINE",
        },
      });
      setSelectedSlotStart(null);
      const surgeryQ =
        selectedSurgeryId !== "" ? `&surgeryId=${encodeURIComponent(selectedSurgeryId)}` : "";
      const refresh = await apiFetch(
        `/patient/booking/availability?dentistId=${encodeURIComponent(selectedDentistId)}&date=${encodeURIComponent(selectedYmd)}${surgeryQ}`,
        { auth: true }
      );
      setSlots(JSON.parse(refresh) as SlotRow[]);
      await reloadMyAppointments();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(`Booking failed\n\n${detail}`);
    } finally {
      setIsBooking(false);
    }
  }

  async function onRegisterSurgery(e: FormEvent) {
    e.preventDefault();
    try {
      await apiFetch("/office/surgeries", {
        method: "POST",
        auth: true,
        body: {
          name: surgeryName.trim(),
          locationAddress: surgeryAddress.trim(),
          telephoneNumber: surgeryPhone.trim(),
        },
      });
      await loadOfficeLists();
    } catch (err) {
      const detail = (err as Error & { detail?: string }).detail ?? (err as Error).message;
      alert(`Request failed\n\n${detail}`);
    }
  }

  function shiftMonth(delta: number) {
    setViewMonth((prev) => {
      const n = new Date(prev.getFullYear(), prev.getMonth() + delta, 1);
      const y = n.getFullYear();
      const mo = n.getMonth();
      setSelectedYmd((sel) => {
        const dSel = new Date(`${sel}T12:00:00`);
        if (!Number.isNaN(dSel.getTime()) && dSel.getFullYear() === y && dSel.getMonth() === mo) {
          return sel;
        }
        return toYmd(n);
      });
      return n;
    });
  }

  return (
    <div className="page">
      <header>
        <div>
          <h1>ADS web client</h1>
        </div>
        {session && (
          <div className="badge">
            <span>
              Signed in as <strong>{session.username}</strong>
            </span>
            <span>·</span>
            <span>
              Role <strong>{session.role}</strong>
            </span>
            <span>·</span>
            <button type="button" className="danger" onClick={onLogout}>
              Sign out
            </button>
          </div>
        )}
      </header>

      <div className="grid">
        {!session && (
          <section className="card" aria-labelledby="loginTitle">
            <h2 id="loginTitle">Sign in</h2>
            <form onSubmit={onLogin}>
              <label htmlFor="username">Username</label>
              <input
                id="username"
                name="username"
                autoComplete="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                required
              />

              <label htmlFor="password">Password</label>
              <input
                id="password"
                name="password"
                type="password"
                autoComplete="current-password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />

              <div className="actions">
                <button type="submit" className="primary">
                  Sign in
                </button>
              </div>
              <p className="hint">
                Demo logins (password <code>password</code> for each):{" "}
                <strong>Patient</strong> <code>patient1</code> — calendar booking below after sign-in;{" "}
                <strong>Office manager</strong> <code>manager</code>; <strong>Dentist</strong> <code>dentist1</code>.
              </p>
            </form>
          </section>
        )}

        {session && (
          <section className="card" aria-labelledby="portalTitle">
            <h2 id="portalTitle">Portal</h2>
            <p className="hint">Uses your session token for authenticated requests.</p>

            {(roleUpper === "PATIENT" || roleUpper === "DENTIST") && (
              <div style={{ marginTop: "0.75rem" }}>
                <h3 style={{ marginTop: 0 }}>Your appointments</h3>
                {appointmentsLoading ? (
                  <p className="hint">Loading…</p>
                ) : myAppointments.length === 0 ? (
                  <p className="hint">No appointments yet.</p>
                ) : (
                  <div className="appt-table-wrap">
                    <table className="appt-table">
                      <thead>
                        <tr>
                          <th>Date</th>
                          <th>Time</th>
                          {roleUpper === "PATIENT" ? <th>Dentist</th> : <th>Patient</th>}
                          <th>Location</th>
                          <th>Status</th>
                          {roleUpper === "DENTIST" || roleUpper === "PATIENT" ? <th>Actions</th> : null}
                        </tr>
                      </thead>
                      <tbody>
                        {myAppointments.map((a) => (
                          <tr key={a.appointmentId}>
                            <td>{formatApptDate(a.startAt)}</td>
                            <td>{formatSlotLabel(a.startAt)}</td>
                            <td className="appt-name-cell">
                              {roleUpper === "PATIENT"
                                ? `${a.dentist.firstName} ${a.dentist.lastName}`
                                : `${a.patient.firstName} ${a.patient.lastName}`}
                            </td>
                            <td className="appt-loc-cell">
                              <div className="appt-loc-name">{a.surgery.name}</div>
                              {a.surgery.locationAddress ? (
                                <div className="appt-loc-addr">{a.surgery.locationAddress}</div>
                              ) : null}
                              {a.surgery.telephoneNumber ? (
                                <div className="appt-loc-phone">{a.surgery.telephoneNumber}</div>
                              ) : null}
                            </td>
                            <td>{abbrevStatus(a.status)}</td>
                            {roleUpper === "DENTIST" ? (
                              <td className="appt-actions-cell">
                                <div className="appt-actions">
                                  {!appointmentAllowsStaffActions(a) ? (
                                    <span className="hint appt-actions-past">
                                      {a.status === "CANCELLED" ? "—" : "Past"}
                                    </span>
                                  ) : null}
                                  {appointmentAllowsStaffActions(a) && a.status === "REQUESTED" ? (
                                    <>
                                      <button
                                        type="button"
                                        className="compact primary"
                                        disabled={dentistActionBusy}
                                        onClick={() =>
                                          void dentistAppointmentPost(`${a.appointmentId}/confirm-booking`)
                                        }
                                      >
                                        Confirm
                                      </button>
                                      <button
                                        type="button"
                                        className="compact danger"
                                        disabled={dentistActionBusy}
                                        onClick={() =>
                                          void dentistAppointmentPost(`${a.appointmentId}/cancel-visit`)
                                        }
                                      >
                                        Reject
                                      </button>
                                    </>
                                  ) : null}
                                  {appointmentAllowsStaffActions(a) && a.status === "BOOKED" ? (
                                    <button
                                      type="button"
                                      className="compact danger"
                                      disabled={dentistActionBusy}
                                      onClick={() =>
                                        void dentistAppointmentPost(`${a.appointmentId}/cancel-visit`)
                                      }
                                    >
                                      Cancel visit
                                    </button>
                                  ) : null}
                                  {appointmentAllowsStaffActions(a) && a.status === "CANCEL_REQUESTED" ? (
                                    <button
                                      type="button"
                                      className="compact danger"
                                      disabled={dentistActionBusy}
                                      onClick={() =>
                                        void dentistAppointmentPost(`${a.appointmentId}/approve-patient-cancel`)
                                      }
                                    >
                                      Approve cancel
                                    </button>
                                  ) : null}
                                  {appointmentAllowsStaffActions(a) && a.status === "RESCHEDULE_REQUESTED" ? (
                                    <>
                                      <button
                                        type="button"
                                        className="compact primary"
                                        disabled={dentistActionBusy}
                                        onClick={() =>
                                          void dentistAppointmentPost(`${a.appointmentId}/approve-reschedule`)
                                        }
                                      >
                                        Approve time
                                      </button>
                                      <button
                                        type="button"
                                        className="compact"
                                        disabled={dentistActionBusy}
                                        onClick={() =>
                                          void dentistAppointmentPost(`${a.appointmentId}/reject-reschedule`)
                                        }
                                      >
                                        Keep old time
                                      </button>
                                    </>
                                  ) : null}
                                </div>
                              </td>
                            ) : roleUpper === "PATIENT" ? (
                              <td className="appt-actions-cell">
                                <div className="appt-actions">
                                  {!patientCanSelfServiceCancel(a) && a.status !== "CANCEL_REQUESTED" ? (
                                    <span className="hint appt-actions-past">
                                      {a.status === "CANCELLED" ? "—" : "No self-service action"}
                                    </span>
                                  ) : null}
                                  {a.status === "CANCEL_REQUESTED" ? (
                                    <span className="hint">Awaiting dentist or office to approve cancel</span>
                                  ) : null}
                                  {patientCanSelfServiceCancel(a) && a.status === "REQUESTED" ? (
                                    <button
                                      type="button"
                                      className="compact danger"
                                      disabled={patientActionBusy}
                                      onClick={() => {
                                        if (window.confirm("Withdraw this appointment request?")) {
                                          void patientRequestCancel(a.appointmentId);
                                        }
                                      }}
                                    >
                                      Withdraw request
                                    </button>
                                  ) : null}
                                  {patientCanSelfServiceCancel(a) && a.status === "BOOKED" ? (
                                    <button
                                      type="button"
                                      className="compact danger"
                                      disabled={patientActionBusy}
                                      onClick={() => {
                                        if (
                                          window.confirm(
                                            "Send a cancel request? Your dentist or the office must approve before the visit is removed."
                                          )
                                        ) {
                                          void patientRequestCancel(a.appointmentId);
                                        }
                                      }}
                                    >
                                      Request cancel
                                    </button>
                                  ) : null}
                                </div>
                              </td>
                            ) : null}
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            )}

            {roleUpper === "PATIENT" && (
              <>
                <div className="booking-panel patient-month-panel" style={{ marginTop: "1.25rem" }}>
                  <h3 style={{ marginTop: 0 }}>Your month</h3>
                  <p className="hint">
                    Your visits (every dentist and surgery).{" "}
                    <span className="cal-legend cal-appt-booked">Booked</span>{" "}
                    <span className="cal-legend cal-appt-requested">Requested</span>{" "}
                    <span className="cal-legend cal-appt-pending">Pending change</span>{" "}
                    <span className="cal-legend cal-appt-cancelled">Cancelled</span>{" "}
                    <span className="cal-legend cal-appt-past">Past</span>. Hover a day or a visit line for full
                    details. Click any day below to choose a booking date. Use <strong>Your appointments</strong> to
                    withdraw a request or request cancellation of a booked visit.
                  </p>
                  <div className="cal-nav">
                    <button type="button" onClick={() => shiftMonth(-1)}>
                      ← Month
                    </button>
                    <strong>
                      {viewMonth.toLocaleString(undefined, { month: "long", year: "numeric" })}
                    </strong>
                    <button type="button" onClick={() => shiftMonth(1)}>
                      Month →
                    </button>
                  </div>
                  <div className="cal-grid cal-head" aria-hidden>
                    {["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].map((d) => (
                      <div key={d}>{d}</div>
                    ))}
                  </div>
                  {calRows.map((week, wi) => (
                    <div className="cal-grid" key={wi}>
                      {week.map((cell, ci) => {
                        if (!cell) return <div key={ci} className="cal-cell cal-empty" />;
                        const dayAppts = patientScheduleByDay[cell.ymd] ?? [];
                        return (
                          <button
                            key={ci}
                            type="button"
                            className={
                              "cal-cell" +
                              (cell.ymd === selectedYmd ? " cal-selected" : "") +
                              (cell.ymd === todayYmd ? " cal-today" : "") +
                              (dayAppts.length > 0 ? " cal-has-busy" : "")
                            }
                            title={patientCalDayHoverTitle(cell.ymd, dayAppts)}
                            onClick={() => {
                              setSelectedYmd(cell.ymd);
                              setSelectedSlotStart(null);
                            }}
                          >
                            <span className="cal-daynum">{cell.day}</span>
                            {dayAppts.length > 0 ? (
                              <div className="cal-entries">
                                {dayAppts.slice(0, 3).map((e) => (
                                  <span
                                    key={e.appointmentId}
                                    className={`cal-appt-line ${appointmentStatusCalendarClass(e)}`}
                                    title={patientCalEntryHoverTitle(e)}
                                  >
                                    {formatSlotLabel(e.startAt)} · {e.dentistName} @ {e.surgeryName}
                                  </span>
                                ))}
                                {dayAppts.length > 3 ? (
                                  <span className="cal-appt-more">+{dayAppts.length - 3} more</span>
                                ) : null}
                              </div>
                            ) : null}
                          </button>
                        );
                      })}
                    </div>
                  ))}
                </div>

                <div className="booking-panel" style={{ marginTop: "1.25rem" }}>
                  <h3 style={{ marginTop: 0 }}>Book an appointment</h3>
                  <p className="hint">
                    Use the calendar above to pick the day. Then choose <strong>dentist</strong> and{" "}
                    <strong>surgery location</strong> for this booking only, select a free time, and book. Changing
                    surgery clears the selected slot and reloads availability.
                  </p>

                  <div className="booking-selects">
                    <div>
                      <label htmlFor="pickDentist">Dentist</label>
                      <select
                        id="pickDentist"
                        value={selectedDentistId}
                        onChange={(e) => {
                          setSelectedDentistId(e.target.value);
                          setSelectedSlotStart(null);
                        }}
                      >
                        {dentists.map((d) => (
                          <option key={d.dentistId} value={d.dentistId}>
                            {d.firstName} {d.lastName} — {d.specialization}
                          </option>
                        ))}
                      </select>
                    </div>
                    <div>
                      <label htmlFor="pickSurgery">Surgery location</label>
                      <select
                        id="pickSurgery"
                        value={selectedSurgeryId}
                        onChange={(e) => {
                          setSelectedSurgeryId(e.target.value);
                          setSelectedSlotStart(null);
                        }}
                      >
                        {surgeries.map((s) => (
                          <option key={s.surgeryId} value={s.surgeryId}>
                            {s.name}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div style={{ marginTop: "0.85rem" }}>
                    <strong>Selected day:</strong> <code>{selectedYmd}</code>
                    {slotsLoading ? <span className="hint"> — loading slots…</span> : null}
                  </div>
                  <div className="slot-row" role="list">
                    {slots.length === 0 && !slotsLoading ? (
                      <p className="hint" style={{ margin: "0.35rem 0 0" }}>
                        No open slots this day (past times, overlaps, or dentist already has 5 booked visits that week).
                      </p>
                    ) : (
                      slots.map((s) => (
                        <button
                          key={s.startAt}
                          type="button"
                          className={"slot-btn" + (selectedSlotStart === s.startAt ? " slot-picked" : "")}
                          disabled={isBooking}
                          onClick={() => setSelectedSlotStart(s.startAt)}
                        >
                          {formatSlotLabel(s.startAt)}
                        </button>
                      ))
                    )}
                  </div>
                  <div className="book-row">
                    {selectedSlotStart ? (
                      <p className="hint" style={{ margin: "0.5rem 0 0.25rem" }}>
                        Selected: <strong>{formatSlotLabel(selectedSlotStart)}</strong> on {selectedYmd}
                        {selectedSurgeryId ? (
                          <>
                            {" "}
                            at{" "}
                            <strong>
                              {surgeries.find((s) => String(s.surgeryId) === selectedSurgeryId)?.name ?? "—"}
                            </strong>
                          </>
                        ) : null}
                      </p>
                    ) : (
                      <p className="hint" style={{ margin: "0.5rem 0 0.25rem" }}>
                        Select a time above, then book.
                      </p>
                    )}
                    <div className="actions" style={{ marginTop: "0.35rem" }}>
                      <button
                        type="button"
                        className="primary"
                        disabled={!selectedSlotStart || isBooking}
                        onClick={() => void confirmBookAppointment()}
                      >
                        {isBooking ? "Booking…" : "Book appointment"}
                      </button>
                    </div>
                  </div>
                </div>
              </>
            )}

            {roleUpper === "DENTIST" && (
              <div className="booking-panel" style={{ marginTop: "1.25rem" }}>
                <h3 style={{ marginTop: 0 }}>Your month schedule</h3>
                <p className="hint">
                  Each line is time and patient name. <span className="cal-legend cal-appt-booked">Booked</span>{" "}
                  <span className="cal-legend cal-appt-requested">Requested</span>{" "}
                  <span className="cal-legend cal-appt-pending">Pending change</span>{" "}
                  <span className="cal-legend cal-appt-cancelled">Cancelled</span>{" "}
                  <span className="cal-legend cal-appt-past">Past</span>. Hover a day or a line for details. Full
                  address is in the table above.
                </p>
                <div className="cal-nav">
                  <button type="button" onClick={() => shiftMonth(-1)}>
                    ← Month
                  </button>
                  <strong>
                    {viewMonth.toLocaleString(undefined, { month: "long", year: "numeric" })}
                  </strong>
                  <button type="button" onClick={() => shiftMonth(1)}>
                    Month →
                  </button>
                </div>
                <div className="cal-grid cal-head" aria-hidden>
                  {["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].map((d) => (
                    <div key={d}>{d}</div>
                  ))}
                </div>
                {calRows.map((week, wi) => (
                  <div className="cal-grid" key={wi}>
                    {week.map((cell, ci) => {
                      if (!cell) return <div key={ci} className="cal-cell cal-empty" />;
                      const dayAppts = scheduleByDay[cell.ymd] ?? [];
                      return (
                        <div
                          key={ci}
                          className={
                            "cal-cell cal-dentist-day" +
                            (cell.ymd === todayYmd ? " cal-today" : "") +
                            (dayAppts.length > 0 ? " cal-has-busy" : "")
                          }
                          title={dentistCalDayHoverTitle(cell.ymd, dayAppts)}
                        >
                          <span className="cal-daynum">{cell.day}</span>
                          {dayAppts.length > 0 ? (
                            <div className="cal-entries">
                              {dayAppts.slice(0, 3).map((e, idx) => (
                                <span
                                  key={`${e.startAt}-${idx}`}
                                  className={`cal-appt-line ${appointmentStatusCalendarClass(e)}`}
                                  title={dentistCalEntryHoverTitle(e)}
                                >
                                  {formatSlotLabel(e.startAt)} · {e.patientName?.trim() || "—"}
                                </span>
                              ))}
                              {dayAppts.length > 3 ? (
                                <span className="cal-appt-more">+{dayAppts.length - 3} more</span>
                              ) : null}
                            </div>
                          ) : null}
                        </div>
                      );
                    })}
                  </div>
                ))}
              </div>
            )}

            {roleUpper === "OFFICE_MANAGER" && (
              <div className="office-portal" style={{ marginTop: "1rem" }}>
                <h3 style={{ marginTop: 0 }}>Office manager</h3>
                <p className="hint">
                  Each tab lists one part of the directory and lets you register that entity. On{" "}
                  <strong>Appointment</strong>, use the table for requests and changes, and the calendar to place a{" "}
                  <strong>booked</strong> visit immediately (not an online-style request).
                </p>

                <div className="office-tab-shell">
                  <div className="office-tablist" role="tablist" aria-label="Office manager sections">
                    {(
                      [
                        ["surgery", "Surgery"],
                        ["patient", "Patient"],
                        ["dentist", "Dentist"],
                        ["appointment", "Appointment"],
                      ] as const
                    ).map(([id, label]) => (
                      <button
                        key={id}
                        type="button"
                        role="tab"
                        id={`office-tab-${id}`}
                        aria-selected={officeTab === id}
                        aria-controls="office-tabpanel"
                        className={"office-tab" + (officeTab === id ? " office-tab-active" : "")}
                        onClick={() => setOfficeTab(id)}
                      >
                        {label}
                      </button>
                    ))}
                  </div>

                  <div className="office-tab-panel" id="office-tabpanel" role="tabpanel" aria-labelledby={`office-tab-${officeTab}`}>
                {officeListsLoading ? (
                  <p className="hint" style={{ marginTop: "0.35rem" }}>
                    Loading directory…
                  </p>
                ) : (
                  <>
                    {officeTab === "surgery" && (
                      <>
                        <h4 className="office-subhead" style={{ marginTop: "1rem" }}>
                          Register surgery
                        </h4>
                        <form onSubmit={onRegisterSurgery}>
                          <label htmlFor="surgeryName">Name</label>
                          <input
                            id="surgeryName"
                            value={surgeryName}
                            onChange={(e) => setSurgeryName(e.target.value)}
                            required
                          />

                          <label htmlFor="surgeryAddress">Location address</label>
                          <input
                            id="surgeryAddress"
                            value={surgeryAddress}
                            onChange={(e) => setSurgeryAddress(e.target.value)}
                            required
                          />

                          <label htmlFor="surgeryPhone">Telephone</label>
                          <input
                            id="surgeryPhone"
                            value={surgeryPhone}
                            onChange={(e) => setSurgeryPhone(e.target.value)}
                            required
                          />

                          <div className="actions">
                            <button type="submit" className="primary">
                              Register surgery
                            </button>
                          </div>
                        </form>

                        <h4 className="office-subhead" style={{ marginTop: "1.25rem" }}>
                          Surgeries
                        </h4>
                        <div className="appt-table-wrap">
                          <table className="appt-table">
                            <thead>
                              <tr>
                                <th>ID</th>
                                <th>Name</th>
                              </tr>
                            </thead>
                            <tbody>
                              {officeSurgeries.length === 0 ? (
                                <tr>
                                  <td colSpan={2} className="hint">
                                    No surgeries yet.
                                  </td>
                                </tr>
                              ) : (
                                officeSurgeries.map((s) => (
                                  <tr key={s.surgeryId}>
                                    <td>{s.surgeryId}</td>
                                    <td className="appt-name-cell">{s.name}</td>
                                  </tr>
                                ))
                              )}
                            </tbody>
                          </table>
                        </div>
                      </>
                    )}

                    {officeTab === "patient" && (
                      <>
                        <h4 className="office-subhead" style={{ marginTop: "1rem" }}>
                          Register patient
                        </h4>
                        <form onSubmit={(e) => void onRegisterOfficePatient(e)}>
                          <label htmlFor="officeRegPf">First name</label>
                          <input
                            id="officeRegPf"
                            value={regPatientFirst}
                            onChange={(e) => setRegPatientFirst(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegPl">Last name</label>
                          <input
                            id="officeRegPl"
                            value={regPatientLast}
                            onChange={(e) => setRegPatientLast(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegPphone">Contact phone</label>
                          <input
                            id="officeRegPphone"
                            value={regPatientPhone}
                            onChange={(e) => setRegPatientPhone(e.target.value)}
                          />
                          <label htmlFor="officeRegPemail">Email</label>
                          <input
                            id="officeRegPemail"
                            type="email"
                            value={regPatientEmail}
                            onChange={(e) => setRegPatientEmail(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegPaddr">Mailing address</label>
                          <input
                            id="officeRegPaddr"
                            value={regPatientAddress}
                            onChange={(e) => setRegPatientAddress(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegPdob">Date of birth</label>
                          <input
                            id="officeRegPdob"
                            type="date"
                            value={regPatientDob}
                            onChange={(e) => setRegPatientDob(e.target.value)}
                            required
                          />
                          <div className="actions">
                            <button type="submit" className="primary" disabled={regPatientSubmitting}>
                              {regPatientSubmitting ? "Saving…" : "Register patient"}
                            </button>
                          </div>
                        </form>

                        <h4 className="office-subhead" style={{ marginTop: "1.25rem" }}>
                          Patients
                        </h4>
                        <div className="appt-table-wrap">
                          <table className="appt-table">
                            <thead>
                              <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Email</th>
                                <th>Phone</th>
                                <th>DOB</th>
                              </tr>
                            </thead>
                            <tbody>
                              {officePatients.length === 0 ? (
                                <tr>
                                  <td colSpan={5} className="hint">
                                    No patients yet.
                                  </td>
                                </tr>
                              ) : (
                                officePatients.map((p) => (
                                  <tr key={p.patientId}>
                                    <td>{p.patientId}</td>
                                    <td className="appt-name-cell">
                                      {p.firstName} {p.lastName}
                                    </td>
                                    <td>{p.email}</td>
                                    <td>{p.contactPhoneNumber}</td>
                                    <td>{p.dateOfBirth}</td>
                                  </tr>
                                ))
                              )}
                            </tbody>
                          </table>
                        </div>
                      </>
                    )}

                    {officeTab === "dentist" && (
                      <>
                        <h4 className="office-subhead" style={{ marginTop: "1rem" }}>
                          Register dentist
                        </h4>
                        <form onSubmit={(e) => void onRegisterOfficeDentist(e)}>
                          <label htmlFor="officeRegDf">First name</label>
                          <input
                            id="officeRegDf"
                            value={regDentistFirst}
                            onChange={(e) => setRegDentistFirst(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegDl">Last name</label>
                          <input
                            id="officeRegDl"
                            value={regDentistLast}
                            onChange={(e) => setRegDentistLast(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegDphone">Contact phone</label>
                          <input
                            id="officeRegDphone"
                            value={regDentistPhone}
                            onChange={(e) => setRegDentistPhone(e.target.value)}
                          />
                          <label htmlFor="officeRegDemail">Email</label>
                          <input
                            id="officeRegDemail"
                            type="email"
                            value={regDentistEmail}
                            onChange={(e) => setRegDentistEmail(e.target.value)}
                            required
                          />
                          <label htmlFor="officeRegDspec">Specialization</label>
                          <input
                            id="officeRegDspec"
                            value={regDentistSpec}
                            onChange={(e) => setRegDentistSpec(e.target.value)}
                            required
                          />
                          <div className="actions">
                            <button type="submit" className="primary" disabled={regDentistSubmitting}>
                              {regDentistSubmitting ? "Saving…" : "Register dentist"}
                            </button>
                          </div>
                        </form>

                        <h4 className="office-subhead" style={{ marginTop: "1.25rem" }}>
                          Dentists
                        </h4>
                        <div className="appt-table-wrap">
                          <table className="appt-table">
                            <thead>
                              <tr>
                                <th>ID</th>
                                <th>Name</th>
                                <th>Specialization</th>
                                <th>Email</th>
                              </tr>
                            </thead>
                            <tbody>
                              {officeDentists.length === 0 ? (
                                <tr>
                                  <td colSpan={4} className="hint">
                                    No dentists yet.
                                  </td>
                                </tr>
                              ) : (
                                officeDentists.map((d) => (
                                  <tr key={d.dentistId}>
                                    <td>{d.dentistId}</td>
                                    <td className="appt-name-cell">
                                      {d.firstName} {d.lastName}
                                    </td>
                                    <td>{d.specialization}</td>
                                    <td>{d.email}</td>
                                  </tr>
                                ))
                              )}
                            </tbody>
                          </table>
                        </div>
                      </>
                    )}

                    {officeTab === "appointment" && (
                      <>
                        <h4 className="office-subhead" style={{ marginTop: "1rem" }}>
                          All appointments
                        </h4>
                        <p className="hint" style={{ marginTop: "0.35rem" }}>
                          This calendar lists <strong>every appointment</strong> in the directory for the month (all
                          patients, dentists, and locations). A clear day means <strong>nothing is scheduled</strong> in
                          the system that day; busy lines show who is already booked so you can pick a sensible day, then
                          use <strong>Open slots</strong> below for free half-hour openings for that dentist.{" "}
                          <span className="cal-legend cal-appt-booked">Booked</span>{" "}
                          <span className="cal-legend cal-appt-requested">Requested</span>{" "}
                          <span className="cal-legend cal-appt-pending">Pending change</span>{" "}
                          <span className="cal-legend cal-appt-cancelled">Cancelled</span>{" "}
                          <span className="cal-legend cal-appt-past">Past</span>. Hover a day or a line for details.
                        </p>
                        <div className="cal-nav" style={{ marginTop: "0.65rem" }}>
                          <button type="button" onClick={() => shiftOfficeMgrMonth(-1)}>
                            ← Month
                          </button>
                          <strong>
                            {officeMgrViewMonth.toLocaleString(undefined, { month: "long", year: "numeric" })}
                          </strong>
                          <button type="button" onClick={() => shiftOfficeMgrMonth(1)}>
                            Month →
                          </button>
                        </div>
                        <div className="booking-panel patient-month-panel" style={{ marginTop: "0.65rem" }}>
                          <h4 className="office-subhead" style={{ marginTop: 0 }}>
                            Schedule — all appointments this month
                          </h4>
                          <div className="cal-grid cal-head" aria-hidden>
                            {["Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"].map((d) => (
                              <div key={d}>{d}</div>
                            ))}
                          </div>
                          {officeMgrCalRows.map((week, wi) => (
                            <div className="cal-grid" key={wi}>
                              {week.map((cell, ci) => {
                                if (!cell) return <div key={ci} className="cal-cell cal-empty" />;
                                const dayAppts = officeAllApptsByDay[cell.ymd] ?? [];
                                return (
                                  <button
                                    key={ci}
                                    type="button"
                                    className={
                                      "cal-cell" +
                                      (cell.ymd === officeMgrSelectedYmd ? " cal-selected" : "") +
                                      (cell.ymd === todayYmd ? " cal-today" : "") +
                                      (dayAppts.length > 0 ? " cal-has-busy" : "")
                                    }
                                    title={officeAllApptsCalDayHoverTitle(cell.ymd, dayAppts)}
                                    onClick={() => {
                                      setOfficeMgrSelectedYmd(cell.ymd);
                                      setOfficeMgrSelectedSlot(null);
                                    }}
                                  >
                                    <span className="cal-daynum">{cell.day}</span>
                                    {dayAppts.length > 0 ? (
                                      <div className="cal-entries">
                                        {dayAppts.slice(0, 3).map((a) => (
                                          <span
                                            key={a.appointmentId}
                                            className={`cal-appt-line ${appointmentStatusCalendarClass(a)}`}
                                            title={officeSingleAppointmentHoverTitle(a)}
                                          >
                                            {formatSlotLabel(a.startAt)} · {a.patient.lastName} /{" "}
                                            {a.dentist.lastName}
                                          </span>
                                        ))}
                                        {dayAppts.length > 3 ? (
                                          <span className="cal-appt-more">+{dayAppts.length - 3} more</span>
                                        ) : null}
                                      </div>
                                    ) : null}
                                  </button>
                                );
                              })}
                            </div>
                          ))}
                        </div>

                        <div className="booking-panel" style={{ marginTop: "1.25rem" }}>
                          <h4 className="office-subhead" style={{ marginTop: 0 }}>
                            Book confirmed visit
                          </h4>
                          <p className="hint">
                            <strong>Booking only:</strong> patient, dentist, and surgery here only choose{" "}
                            <em>who</em> is booked and <em>where</em>; they do not filter the schedule calendar or the
                            appointment list below. Pick the <strong>day</strong> from the month controls or by clicking
                            the schedule calendar, then choose an <strong>open slot</strong> for the dentist you selected
                            — those
                            times are the real available openings. Confirming creates a <strong>BOOKED</strong> visit
                            (weekly limits and overlaps still apply).
                          </p>
                          <div className="booking-selects">
                            <div>
                              <label htmlFor="officeMgrPickPatient">Patient</label>
                              <select
                                id="officeMgrPickPatient"
                                value={officeMgrPatientId}
                                onChange={(e) => {
                                  setOfficeMgrPatientId(e.target.value);
                                  setOfficeMgrSelectedSlot(null);
                                }}
                              >
                                {officePatients.length === 0 ? (
                                  <option value="">No patients — use Patient tab</option>
                                ) : (
                                  officePatients.map((p) => (
                                    <option key={p.patientId} value={p.patientId}>
                                      {p.firstName} {p.lastName} ({p.email})
                                    </option>
                                  ))
                                )}
                              </select>
                            </div>
                            <div>
                              <label htmlFor="officeMgrPickDentist">Dentist</label>
                              <select
                                id="officeMgrPickDentist"
                                value={officeMgrDentistId}
                                onChange={(e) => {
                                  setOfficeMgrDentistId(e.target.value);
                                  setOfficeMgrSelectedSlot(null);
                                }}
                              >
                                {officeDentists.length === 0 ? (
                                  <option value="">No dentists — use Dentist tab</option>
                                ) : (
                                  officeDentists.map((d) => (
                                    <option key={d.dentistId} value={d.dentistId}>
                                      {d.firstName} {d.lastName}
                                    </option>
                                  ))
                                )}
                              </select>
                            </div>
                            <div>
                              <label htmlFor="officeMgrPickSurgery">Surgery location</label>
                              <select
                                id="officeMgrPickSurgery"
                                value={officeMgrSurgeryId}
                                onChange={(e) => {
                                  setOfficeMgrSurgeryId(e.target.value);
                                  setOfficeMgrSelectedSlot(null);
                                }}
                              >
                                {officeSurgeries.length === 0 ? (
                                  <option value="">No surgeries — use Surgery tab</option>
                                ) : (
                                  officeSurgeries.map((s) => (
                                    <option key={s.surgeryId} value={s.surgeryId}>
                                      {s.name}
                                    </option>
                                  ))
                                )}
                              </select>
                            </div>
                          </div>

                          <div style={{ marginTop: "0.85rem" }}>
                            <strong>Selected day:</strong> <code>{officeMgrSelectedYmd}</code>
                            {officeMgrSlotsLoading ? <span className="hint"> — loading slots…</span> : null}
                          </div>
                          <h5 className="office-subhead" style={{ marginTop: "0.75rem", marginBottom: "0.35rem" }}>
                            Open slots
                          </h5>
                          <p className="hint" style={{ marginTop: 0 }}>
                            Times the selected dentist can still take on this day (not shown on the schedule calendar
                            above).
                          </p>
                          <div className="slot-row" role="list">
                            {officeMgrSlots.length === 0 && !officeMgrSlotsLoading ? (
                              <p className="hint" style={{ margin: "0.35rem 0 0" }}>
                                No open slots this day (choose another day, dentist, or surgery; or weekly limit
                                reached).
                              </p>
                            ) : (
                              officeMgrSlots.map((s) => (
                                <button
                                  key={s.startAt}
                                  type="button"
                                  className={
                                    "slot-btn" + (officeMgrSelectedSlot === s.startAt ? " slot-picked" : "")
                                  }
                                  disabled={officeMgrBooking}
                                  onClick={() => setOfficeMgrSelectedSlot(s.startAt)}
                                >
                                  {formatSlotLabel(s.startAt)}
                                </button>
                              ))
                            )}
                          </div>
                          <div className="book-row">
                            {officeMgrSelectedSlot ? (
                              <p className="hint" style={{ margin: "0.5rem 0 0.25rem" }}>
                                Selected: <strong>{formatSlotLabel(officeMgrSelectedSlot)}</strong> on{" "}
                                {officeMgrSelectedYmd}
                                {officeMgrSurgeryId ? (
                                  <>
                                    {" "}
                                    at{" "}
                                    <strong>
                                      {officeSurgeries.find((s) => String(s.surgeryId) === officeMgrSurgeryId)?.name ??
                                        "—"}
                                    </strong>
                                  </>
                                ) : null}
                              </p>
                            ) : (
                              <p className="hint" style={{ margin: "0.5rem 0 0.25rem" }}>
                                Select a time above, then book as confirmed.
                              </p>
                            )}
                            <div className="actions" style={{ marginTop: "0.35rem" }}>
                              <button
                                type="button"
                                className="primary"
                                disabled={
                                  !officeMgrSelectedSlot ||
                                  officeMgrBooking ||
                                  !officeMgrPatientId ||
                                  !officeMgrDentistId ||
                                  !officeMgrSurgeryId
                                }
                                onClick={() => void confirmOfficeDirectBook()}
                              >
                                {officeMgrBooking ? "Booking…" : "Book confirmed appointment"}
                              </button>
                            </div>
                          </div>
                        </div>

                        <h4 className="office-subhead" style={{ marginTop: "1.25rem" }}>
                          Appointment list
                        </h4>
                        <p className="hint" style={{ marginTop: "0.25rem" }}>
                          Same information as the schedule calendar, with staff actions. Shown last so you can use the
                          calendar and open slots first, then review or act on rows here.
                        </p>
                        <div className="appt-table-wrap">
                          <table className="appt-table">
                            <thead>
                              <tr>
                                <th>When</th>
                                <th>Patient</th>
                                <th>Dentist</th>
                                <th>Location</th>
                                <th>Status</th>
                                <th>Actions</th>
                              </tr>
                            </thead>
                            <tbody>
                              {officeAppointments.length === 0 ? (
                                <tr>
                                  <td colSpan={6} className="hint">
                                    No appointments yet.
                                  </td>
                                </tr>
                              ) : (
                                officeAppointments.map((a) => (
                                  <tr key={a.appointmentId}>
                                    <td>
                                      {new Date(a.startAt.replace(" ", "T")).toLocaleString(undefined, {
                                        dateStyle: "medium",
                                        timeStyle: "short",
                                      })}
                                    </td>
                                    <td className="appt-name-cell">
                                      {a.patient.firstName} {a.patient.lastName}
                                    </td>
                                    <td className="appt-name-cell">
                                      {a.dentist.firstName} {a.dentist.lastName}
                                    </td>
                                    <td className="appt-loc-cell">
                                      <div className="appt-loc-name">{a.surgery.name}</div>
                                      {a.surgery.locationAddress ? (
                                        <div className="appt-loc-addr">{a.surgery.locationAddress}</div>
                                      ) : null}
                                    </td>
                                    <td>{abbrevStatus(a.status)}</td>
                                    <td className="appt-actions-cell">
                                      <div className="appt-actions">
                                        {!appointmentAllowsStaffActions(a) ? (
                                          <span className="hint appt-actions-past">
                                            {a.status === "CANCELLED" ? "—" : "Past"}
                                          </span>
                                        ) : null}
                                        {appointmentAllowsStaffActions(a) && a.status === "REQUESTED" ? (
                                          <>
                                            <button
                                              type="button"
                                              className="compact primary"
                                              disabled={officeActionBusy}
                                              onClick={() => void officeAppointmentPost(`${a.appointmentId}/book`)}
                                            >
                                              Confirm
                                            </button>
                                            <button
                                              type="button"
                                              className="compact danger"
                                              disabled={officeActionBusy}
                                              onClick={() =>
                                                void officeAppointmentPost(`${a.appointmentId}/cancel-visit`)
                                              }
                                            >
                                              Reject
                                            </button>
                                          </>
                                        ) : null}
                                        {appointmentAllowsStaffActions(a) && a.status === "BOOKED" ? (
                                          <button
                                            type="button"
                                            className="compact danger"
                                            disabled={officeActionBusy}
                                            onClick={() =>
                                              void officeAppointmentPost(`${a.appointmentId}/cancel-visit`)
                                            }
                                          >
                                            Cancel visit
                                          </button>
                                        ) : null}
                                        {appointmentAllowsStaffActions(a) && a.status === "CANCEL_REQUESTED" ? (
                                          <button
                                            type="button"
                                            className="compact danger"
                                            disabled={officeActionBusy}
                                            onClick={() =>
                                              void officeAppointmentPost(`${a.appointmentId}/confirm-cancel`)
                                            }
                                          >
                                            Approve cancel
                                          </button>
                                        ) : null}
                                        {appointmentAllowsStaffActions(a) && a.status === "RESCHEDULE_REQUESTED" ? (
                                          <>
                                            <button
                                              type="button"
                                              className="compact primary"
                                              disabled={officeActionBusy}
                                              onClick={() =>
                                                void officeAppointmentPost(`${a.appointmentId}/confirm-reschedule`)
                                              }
                                            >
                                              Approve time
                                            </button>
                                            <button
                                              type="button"
                                              className="compact"
                                              disabled={officeActionBusy}
                                              onClick={() =>
                                                void officeAppointmentPost(`${a.appointmentId}/reject-reschedule`)
                                              }
                                            >
                                              Keep old time
                                            </button>
                                          </>
                                        ) : null}
                                      </div>
                                    </td>
                                  </tr>
                                ))
                              )}
                            </tbody>
                          </table>
                        </div>
                      </>
                    )}
                  </>
                )}
                  </div>
                </div>
              </div>
            )}
          </section>
        )}
      </div>
    </div>
  );
}
