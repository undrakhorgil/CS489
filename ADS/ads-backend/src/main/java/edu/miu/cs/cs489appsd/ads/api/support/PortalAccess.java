package edu.miu.cs.cs489appsd.ads.api.support;

import edu.miu.cs.cs489appsd.ads.exception.BusinessRuleException;
import edu.miu.cs.cs489appsd.ads.security.AdsUserDetails;

/**
 * Validates {@link AdsUserDetails} for role-specific portal APIs (dentist vs patient scope).
 */
public final class PortalAccess {

    private PortalAccess() {
    }

    public static long requireDentistId(AdsUserDetails user) {
        Long id = user.getDentistId();
        if (id == null) {
            throw new BusinessRuleException("This account is not linked to a dentist profile.");
        }
        return id;
    }

    public static long requirePatientId(AdsUserDetails user) {
        Long id = user.getPatientId();
        if (id == null) {
            throw new BusinessRuleException("This account is not linked to a patient profile.");
        }
        return id;
    }
}
