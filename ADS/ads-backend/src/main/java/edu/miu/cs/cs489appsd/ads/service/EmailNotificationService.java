package edu.miu.cs.cs489appsd.ads.service;

import edu.miu.cs.cs489appsd.ads.domain.Appointment;

public interface EmailNotificationService {

    void sendAppointmentConfirmation(Appointment appointment, String patientEmail);
}
