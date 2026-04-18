package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LoggingEmailNotificationService implements EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(LoggingEmailNotificationService.class);

    @Override
    public void sendAppointmentConfirmation(Appointment appointment, String patientEmail) {
        log.info("[EMAIL] Confirmation to {} for appointment {} at {}",
                patientEmail,
                appointment.getAppointmentId(),
                appointment.getStartAt());
    }
}
