package com.pdd.redcurrant.domain.utils;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import lombok.experimental.UtilityClass;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

@UtilityClass
public class CommonUtils {

    public String mapCountryNameToIso3(String countryName) {
        for (String iso : Locale.getISOCountries()) {
            Locale locale = new Locale("", iso);
            if (locale.getDisplayCountry(Locale.ENGLISH).equalsIgnoreCase(countryName)) {
                return locale.getISO3Country();
            }
        }
        throw new RuntimeException("Unknown country name: " + countryName);
    }

    public String mapIso2CountryCodeToIso3(String iso2CountryCode) {
        return Locale.of("", iso2CountryCode).getISO3Country();
    }

    public String convertDateTimeFormat(String dateTime, String source, String target) {
        try {
            DateTimeFormatter formattedSource = DateTimeFormatter.ofPattern(source);
            DateTimeFormatter formattedTarget = DateTimeFormatter.ofPattern(target);
            return LocalDateTime.parse(dateTime, formattedSource).format(formattedTarget);
        }
        catch (DateTimeParseException ex) {
            throw new RuntimeException("Invalid date time format: " + dateTime);
        }
    }

    public String parsePhoneCountryCode(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsedPhoneNumber = PhoneNumberUtil.getInstance().parse(phoneNumber, null);
            return String.valueOf(parsedPhoneNumber.getCountryCode());
        }
        catch (NumberParseException ex) {
            throw new RuntimeException("Invalid phone number format: " + phoneNumber);
        }
    }

    public String parsePhoneNationalNumber(String phoneNumber) {
        try {
            Phonenumber.PhoneNumber parsedPhoneNumber = PhoneNumberUtil.getInstance().parse(phoneNumber, null);
            return String.valueOf(parsedPhoneNumber.getNationalNumber());
        }
        catch (NumberParseException ex) {
            throw new RuntimeException("Invalid phone number format: " + phoneNumber);
        }
    }

}
