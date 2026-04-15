package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import edu.miu.cs.cs489appsd.ads.domain.AppointmentStatus;
import edu.miu.cs.cs489appsd.ads.domain.Dentist;
import edu.miu.cs.cs489appsd.ads.domain.Patient;
import edu.miu.cs.cs489appsd.ads.domain.Surgery;
import edu.miu.cs.cs489appsd.ads.exception.BusinessRuleException;
import edu.miu.cs.cs489appsd.ads.exception.NotFoundException;
import edu.miu.cs.cs489appsd.ads.repository.AppointmentRepository;
import edu.miu.cs.cs489appsd.ads.repository.BillRepository;
import edu.miu.cs.cs489appsd.ads.repository.DentistRepository;
import edu.miu.cs.cs489appsd.ads.repository.PatientRepository;
import edu.miu.cs.cs489appsd.ads.repository.SurgeryRepository;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentDetailResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.AppointmentRequestDto;
import edu.miu.cs.cs489appsd.ads.web.dto.DentistResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.PatientResponse;
import edu.miu.cs.cs489appsd.ads.web.dto.SurgeryResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DentistRepository dentistRepository;
    private final PatientRepository patientRepository;
    private final SurgeryRepository surgeryRepository;
    private final BillRepository billRepository;
    private final EmailNotificationService emailNotificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DentistRepository dentistRepository,
                              PatientRepository patientRepository,
                              SurgeryRepository surgeryRepository,
                              BillRepository billRepository,
                              EmailNotificationService emailNotificationService) {
        this.appointmentRepository = appointmentRepository;
        this.dentistRepository = dentistRepository;
        this.patientRepository = patientRepository;
        this.surgeryRepository = surgeryRepository;
        this.billRepository = billRepository;
        this.emailNotificationService = emailNotificationService;
    }

    public AppointmentDetailResponse requestAppointment(long patientId, AppointmentRequestDto dto) {
        ensurePatientExists(patientId);
        if (billRepository.existsByPatientIdAndPaidFalse(patientId)) {
            throw new BusinessRuleException("Cannot request a new appointment while an unpaid bill exists.");
        }
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

    public void requestCancel(long patientId, long appointmentId) {
        Appointment a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Appointment not found: " + appointmentId));
        if (!a.getPatientId().equals(patientId)) {
            throw new BusinessRuleException("Appointment does not belong to this patient.");
        }
        if (a.getStatus() != AppointmentStatus.BOOKED && a.getStatus() != AppointmentStatus.REQUESTED) {
            throw new BusinessRuleException("Only active appointments can be cancelled.");
        }
        a.setStatus(AppointmentStatus.CANCEL_REQUESTED);
        appointmentRepository.save(a);
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
                toDentist(d),
                toPatient(p),
                toSurgery(s)
        );
    }

    private static DentistResponse toDentist(Dentist d) {
        return new DentistResponse(
                d.getDentistId(),
                d.getFirstName(),
                d.getLastName(),
                d.getContactPhoneNumber(),
                d.getEmail(),
                d.getSpecialization()
        );
    }

    private static PatientResponse toPatient(Patient p) {
        return new PatientResponse(
                p.getPatientId(),
                p.getFirstName(),
                p.getLastName(),
                p.getContactPhoneNumber(),
                p.getEmail(),
                p.getMailingAddress(),
                p.getDateOfBirth()
        );
    }

    private static SurgeryResponse toSurgery(Surgery s) {
        return new SurgeryResponse(
                s.getSurgeryId(),
                s.getName(),
                s.getLocationAddress(),
                s.getTelephoneNumber()
        );
    }
}
