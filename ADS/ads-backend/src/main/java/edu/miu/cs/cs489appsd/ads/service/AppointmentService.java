package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;
import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.exception.BusinessRuleException;
import edu.miu.cs.cs489appsd.ads.exception.NotFoundException;
import edu.miu.cs.cs489appsd.ads.repository.AppointmentRepository;
import edu.miu.cs.cs489appsd.ads.repository.DentistRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.AppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.api.dto.AvailableSlotResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.CalendarAppointmentEntry;
import edu.miu.cs.cs489appsd.ads.api.dto.DayScheduleResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.MonthScheduleKind;
import edu.miu.cs.cs489appsd.ads.api.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.SurgeryResponse;
import edu.miu.cs.cs489appsd.ads.api.dto.mapper.ResponseDtoMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class AppointmentService {

    /** Half-hour booking grid; aligns with {@link #SLOT_MINUTES}. */
    private static final LocalTime DAY_START = LocalTime.of(8, 0);
    private static final LocalTime DAY_END = LocalTime.of(17, 0);
    private static final int SLOT_MINUTES = 30;

    private final AppointmentRepository appointmentRepository;
    private final DentistRepository dentistRepository;
    private final PatientRepository patientRepository;
    private final SurgeryRepository surgeryRepository;
    private final EmailNotificationService emailNotificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DentistRepository dentistRepository,
                              PatientRepository patientRepository,
                              SurgeryRepository surgeryRepository,
                              EmailNotificationService emailNotificationService) {
        this.appointmentRepository = appointmentRepository;
        this.dentistRepository = dentistRepository;
        this.patientRepository = patientRepository;
        this.surgeryRepository = surgeryRepository;
        this.emailNotificationService = emailNotificationService;
    }

    public AppointmentDetailResponse requestAppointment(long patientId, AppointmentRequestDto dto) {
        ensurePatientExists(patientId);
        ensureDentistExists(dto.dentistId());
        ensureSurgeryExists(dto.surgeryId());

        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setDentistId(dto.dentistId());
        a.setSurgeryId(dto.surgeryId());
        a.setStartAt(dto.startAt());
        a.setStatus(AppointmentStatus.REQUESTED);
        a.setChannel(dto.channel());
        Appointment saved = appointmentRepository.save(a);
        return toDetail(saved);
    }

    /**
     * Office manager: create a {@link AppointmentStatus#BOOKED} visit immediately (same overlap and weekly rules
     * as patient booking, plus unpaid-bill rule as for requests).
     */
    public AppointmentDetailResponse officeBookImmediately(long patientId, AppointmentRequestDto dto) {
        ensurePatientExists(patientId);
        ensureDentistExists(dto.dentistId());
        ensureSurgeryExists(dto.surgeryId());
        LocalDateTime startAt = dto.startAt();
        if (!startAt.isAfter(LocalDateTime.now())) {
            throw new BusinessRuleException("Appointment time must be in the future.");
        }
        List<Appointment> byDentist = appointmentRepository.findByDentistId(dto.dentistId());
        if (overlapsBusyAppointment(startAt, byDentist)) {
            throw new BusinessRuleException("Dentist is not available at that time.");
        }
        assertWeekCapacity(dto.dentistId(), startAt, null);
        Appointment a = new Appointment();
        a.setPatientId(patientId);
        a.setDentistId(dto.dentistId());
        a.setSurgeryId(dto.surgeryId());
        a.setStartAt(startAt);
        a.setStatus(AppointmentStatus.BOOKED);
        a.setChannel(dto.channel());
        Appointment saved = appointmentRepository.save(a);
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        emailNotificationService.sendAppointmentConfirmation(saved, patient.getEmail());
        return toDetail(saved);
    }

    public AppointmentDetailResponse bookAppointment(long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (a.getStatus() != AppointmentStatus.REQUESTED) {
            throw new BusinessRuleException("Only REQUESTED appointments can be booked.");
        }
        assertWeekCapacity(a.getDentistId(), a.getStartAt(), null);
        a.setStatus(AppointmentStatus.BOOKED);
        Appointment saved = appointmentRepository.save(a);
        Patient patient = patientRepository.findById(a.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        emailNotificationService.sendAppointmentConfirmation(saved, patient.getEmail());
        return toDetail(saved);
    }

    /**
     * Patient-initiated cancel: {@code REQUESTED} visits are withdrawn immediately ({@code CANCELLED}).
     * {@code BOOKED} upcoming visits become {@code CANCEL_REQUESTED} until dentist or office approves.
     */
    public void requestCancel(long patientId, long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (!a.getPatientId().equals(patientId)) {
            throw new BusinessRuleException("Appointment does not belong to this patient.");
        }
        if (a.getStatus() == AppointmentStatus.CANCEL_REQUESTED) {
            throw new BusinessRuleException("A cancel request is already pending for this visit.");
        }
        if (a.getStartAt() != null && a.getStartAt().isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("This visit has already started or passed.");
        }
        if (a.getStatus() == AppointmentStatus.REQUESTED) {
            a.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(a);
            return;
        }
        if (a.getStatus() == AppointmentStatus.BOOKED) {
            a.setStatus(AppointmentStatus.CANCEL_REQUESTED);
            appointmentRepository.save(a);
            return;
        }
        throw new BusinessRuleException("Only a booked visit or a pending request can be cancelled this way.");
    }

    public void requestReschedule(long patientId, long appointmentId, LocalDateTime proposedStartAt) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (!a.getPatientId().equals(patientId)) {
            throw new BusinessRuleException("Appointment does not belong to this patient.");
        }
        if (a.getStatus() != AppointmentStatus.BOOKED) {
            throw new BusinessRuleException("Only booked appointments can be rescheduled.");
        }
        a.setProposedStartAt(proposedStartAt);
        a.setStatus(AppointmentStatus.RESCHEDULE_REQUESTED);
        appointmentRepository.save(a);
    }

    public AppointmentDetailResponse confirmReschedule(long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (a.getStatus() != AppointmentStatus.RESCHEDULE_REQUESTED || a.getProposedStartAt() == null) {
            throw new BusinessRuleException("No pending reschedule request for this appointment.");
        }
        LocalDateTime newSlot = a.getProposedStartAt();
        assertWeekCapacity(a.getDentistId(), newSlot, a.getAppointmentId());
        a.setStartAt(newSlot);
        a.setProposedStartAt(null);
        a.setStatus(AppointmentStatus.BOOKED);
        Appointment saved = appointmentRepository.save(a);
        Patient patient = patientRepository.findById(a.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        emailNotificationService.sendAppointmentConfirmation(saved, patient.getEmail());
        return toDetail(saved);
    }

    public void confirmCancel(long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (a.getStatus() != AppointmentStatus.CANCEL_REQUESTED) {
            throw new BusinessRuleException("No pending cancel request.");
        }
        a.setStatus(AppointmentStatus.CANCELLED);
        appointmentRepository.save(a);
    }

    public List<AppointmentDetailResponse> listForPatient(long patientId) {
        return appointmentRepository.findByPatientId(patientId).stream()
                .sorted(Comparator.comparing(Appointment::getStartAt))
                .map(this::toDetail)
                .toList();
    }

    public List<AppointmentDetailResponse> listForDentist(long dentistId) {
        return appointmentRepository.findByDentistId(dentistId).stream()
                .sorted(Comparator.comparing(Appointment::getStartAt))
                .map(this::toDetail)
                .toList();
    }

    public List<AppointmentDetailResponse> listAllAppointments() {
        return appointmentRepository.findAll().stream()
                .sorted(Comparator.comparing(Appointment::getStartAt, Comparator.nullsLast(Comparator.naturalOrder())))
                .map(this::toDetail)
                .toList();
    }

    /** Office manager: reject request or cancel a booked visit (same rules as dentist, no ownership check). */
    public void officeCancelVisitOrRejectRequest(long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (a.getStatus() == AppointmentStatus.REQUESTED || a.getStatus() == AppointmentStatus.BOOKED) {
            a.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(a);
            return;
        }
        throw new BusinessRuleException("Cannot cancel in state: " + a.getStatus());
    }

    public void officeRejectRescheduleRequest(long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (a.getStatus() != AppointmentStatus.RESCHEDULE_REQUESTED) {
            throw new BusinessRuleException("No pending reschedule to reject.");
        }
        a.setProposedStartAt(null);
        a.setStatus(AppointmentStatus.BOOKED);
        appointmentRepository.save(a);
    }

    public AppointmentDetailResponse dentistConfirmBooking(long dentistId, long appointmentId) {
        assertAppointmentAssignedToDentist(dentistId, appointmentId);
        return bookAppointment(appointmentId);
    }

    public void dentistApprovePatientCancel(long dentistId, long appointmentId) {
        assertAppointmentAssignedToDentist(dentistId, appointmentId);
        confirmCancel(appointmentId);
    }

    public AppointmentDetailResponse dentistApproveReschedule(long dentistId, long appointmentId) {
        assertAppointmentAssignedToDentist(dentistId, appointmentId);
        return confirmReschedule(appointmentId);
    }

    /** Cancels a booked visit or rejects an online request ({@code REQUESTED} → cancelled). */
    public void dentistCancelVisitOrRejectRequest(long dentistId, long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (!a.getDentistId().equals(dentistId)) {
            throw new BusinessRuleException("This appointment is not assigned to you.");
        }
        if (a.getStatus() == AppointmentStatus.REQUESTED || a.getStatus() == AppointmentStatus.BOOKED) {
            a.setStatus(AppointmentStatus.CANCELLED);
            appointmentRepository.save(a);
            return;
        }
        throw new BusinessRuleException("Cannot cancel in state: " + a.getStatus());
    }

    public void dentistRejectRescheduleRequest(long dentistId, long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (!a.getDentistId().equals(dentistId)) {
            throw new BusinessRuleException("This appointment is not assigned to you.");
        }
        if (a.getStatus() != AppointmentStatus.RESCHEDULE_REQUESTED) {
            throw new BusinessRuleException("No pending reschedule to reject.");
        }
        a.setProposedStartAt(null);
        a.setStatus(AppointmentStatus.BOOKED);
        appointmentRepository.save(a);
    }

    private void assertAppointmentAssignedToDentist(long dentistId, long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (!a.getDentistId().equals(dentistId)) {
            throw new BusinessRuleException("This appointment is not assigned to you.");
        }
    }

    public List<DentistResponse> listDentistsForBooking() {
        return dentistRepository.findAll().stream()
                .sorted(Comparator.comparing(Dentist::getLastName).thenComparing(Dentist::getFirstName))
                .map(ResponseDtoMapper::toDentistResponse)
                .toList();
    }

    public List<SurgeryResponse> listSurgeriesForBooking() {
        return surgeryRepository.findAll().stream()
                .sorted(Comparator.comparing(Surgery::getName))
                .map(ResponseDtoMapper::toSurgeryResponse)
                .toList();
    }

    /**
     * Calendar month view: appointments per day with time, status, and surgery name.
     * {@link MonthScheduleKind#FOR_DENTIST_SELF} adds patient names and includes cancelled visits.
     */
    public List<DayScheduleResponse> dentistMonthSchedule(long dentistId, YearMonth yearMonth, MonthScheduleKind kind) {
        ensureDentistExists(dentistId);
        LocalDate first = yearMonth.atDay(1);
        LocalDate last = yearMonth.atEndOfMonth();
        Map<LocalDate, List<CalendarAppointmentEntry>> byDay = new TreeMap<>();
        for (Appointment a : appointmentRepository.findByDentistId(dentistId)) {
            if (a.getStartAt() == null || !includeInMonthSchedule(a.getStatus(), kind)) {
                continue;
            }
            LocalDate d = a.getStartAt().toLocalDate();
            if (d.isBefore(first) || d.isAfter(last)) {
                continue;
            }
            Surgery s = surgeryRepository.findById(a.getSurgeryId()).orElse(null);
            String surgeryName = s != null ? s.getName() : "?";
            String patientName = null;
            if (kind == MonthScheduleKind.FOR_DENTIST_SELF) {
                Patient p = patientRepository.findById(a.getPatientId()).orElse(null);
                patientName = p == null ? "?" : p.getFirstName() + " " + p.getLastName();
            }
            byDay.computeIfAbsent(d, k -> new ArrayList<>())
                    .add(new CalendarAppointmentEntry(a.getStartAt(), a.getStatus(), a.getSurgeryId(), surgeryName,
                            patientName));
        }
        for (List<CalendarAppointmentEntry> list : byDay.values()) {
            list.sort(Comparator.comparing(CalendarAppointmentEntry::startAt));
        }
        return byDay.entrySet().stream()
                .map(e -> new DayScheduleResponse(e.getKey().toString(), e.getValue()))
                .toList();
    }

    /**
     * Half-hour slots on {@code date} where the dentist is not already busy and weekly BOOKED capacity allows
     * another booking. {@code surgeryId} is validated when provided (same id used when requesting an appointment).
     */
    public List<AvailableSlotResponse> findAvailableSlots(long dentistId, LocalDate date, Long surgeryId) {
        ensureDentistExists(dentistId);
        if (surgeryId != null) {
            ensureSurgeryExists(surgeryId);
        }
        List<Appointment> byDentist = appointmentRepository.findByDentistId(dentistId);
        List<Appointment> all = appointmentRepository.findAll();

        List<AvailableSlotResponse> slots = new ArrayList<>();
        LocalDateTime cursor = date.atTime(DAY_START);
        LocalDateTime lastStart = date.atTime(DAY_END).minusMinutes(SLOT_MINUTES);
        while (!cursor.isAfter(lastStart)) {
            if (!cursor.isBefore(LocalDateTime.now()) && !overlapsBusyAppointment(cursor, byDentist)) {
                long bookedSameWeek = DentistSchedulePolicy.countBookedInSameWeek(all, dentistId, cursor);
                if (bookedSameWeek < DentistSchedulePolicy.MAX_APPOINTMENTS_PER_WEEK) {
                    slots.add(new AvailableSlotResponse(cursor));
                }
            }
            cursor = cursor.plusMinutes(SLOT_MINUTES);
        }
        return slots;
    }

    private static boolean overlapsBusyAppointment(LocalDateTime slotStart, List<Appointment> byDentist) {
        LocalDateTime slotEnd = slotStart.plusMinutes(SLOT_MINUTES);
        for (Appointment a : byDentist) {
            if (a.getStartAt() == null || a.getStatus() == AppointmentStatus.CANCELLED) {
                continue;
            }
            if (!blocksCalendarSlot(a.getStatus())) {
                continue;
            }
            LocalDateTime apptStart = a.getStartAt();
            LocalDateTime apptEnd = apptStart.plusMinutes(SLOT_MINUTES);
            if (slotStart.isBefore(apptEnd) && slotEnd.isAfter(apptStart)) {
                return true;
            }
        }
        return false;
    }

    private static boolean blocksCalendarSlot(AppointmentStatus status) {
        return status == AppointmentStatus.BOOKED
                || status == AppointmentStatus.REQUESTED
                || status == AppointmentStatus.RESCHEDULE_REQUESTED
                || status == AppointmentStatus.CANCEL_REQUESTED;
    }

    private static boolean includeInMonthSchedule(AppointmentStatus status, MonthScheduleKind kind) {
        if (status == null) {
            return false;
        }
        if (kind == MonthScheduleKind.FOR_DENTIST_SELF) {
            return true;
        }
        return blocksCalendarSlot(status);
    }

    private void assertWeekCapacity(long dentistId, LocalDateTime slot, Long excludeAppointmentId) {
        List<Appointment> all = appointmentRepository.findAll();
        long count;
        if (excludeAppointmentId == null) {
            count = DentistSchedulePolicy.countBookedInSameWeek(all, dentistId, slot);
        } else {
            count = DentistSchedulePolicy.countBookedInSameWeekExcluding(all, dentistId, slot, excludeAppointmentId);
        }
        if (count >= DentistSchedulePolicy.MAX_APPOINTMENTS_PER_WEEK) {
            throw new BusinessRuleException("Dentist already has " + DentistSchedulePolicy.MAX_APPOINTMENTS_PER_WEEK
                    + " appointments in that week.");
        }
    }

    private void ensurePatientExists(long id) {
        patientRepository.findById(id).orElseThrow(() -> new NotFoundException("Patient not found: " + id));
    }

    private void ensureDentistExists(long id) {
        dentistRepository.findById(id).orElseThrow(() -> new NotFoundException("Dentist not found: " + id));
    }

    private void ensureSurgeryExists(long id) {
        surgeryRepository.findById(id).orElseThrow(() -> new NotFoundException("Surgery not found: " + id));
    }

    private AppointmentDetailResponse toDetail(Appointment a) {
        Dentist d = dentistRepository.findById(a.getDentistId())
                .orElseThrow(() -> new NotFoundException("Dentist not found"));
        Patient p = patientRepository.findById(a.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found"));
        Surgery s = surgeryRepository.findById(a.getSurgeryId())
                .orElseThrow(() -> new NotFoundException("Surgery not found"));
        return new AppointmentDetailResponse(
                a.getAppointmentId(),
                a.getStatus(),
                a.getChannel(),
                a.getStartAt(),
                a.getProposedStartAt(),
                ResponseDtoMapper.toDentistResponse(d),
                ResponseDtoMapper.toPatientResponse(p),
                ResponseDtoMapper.toSurgeryResponse(s)
        );
    }
}
