package com.pdd.redcurrant.domain.constants;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SourceOfIncomeEnum {

    S01("SALARY"), S02("GIFT"), S03("CR / DR OR LOAN"), S04("BUSINESS"), S05("OTHER INCOME"), S06("TOURIST EXPENSES"),
    S07("BONUS/COMMISSIONS"), S08("COMPENSATION FROM COURT"), S09("FAMILY INCOME"), S10("FINAL SETTLEMENT / INDEMNITY"),
    S11("INCOME FROM BUSINESS OR PROFESSION"), S12("INCOME FROM RENTAL PROPERTY"),
    S13("INCOME FROM SALE OF CAR / VEHICLE"), S14("INCOME FROM SALE OF PROPERTY"), S15("INCOME FROM SHARES"),
    S16("INCOME GENERATED FROM SALE OF GOODS AND SERVICES"), S17("INHERITANCE INCOME"), S18("INVESTMENT INCOME"),
    S19("LOAN FROM BANK"), S20("LOAN FROM EMPLOYER"), S21("LOTTERY INCOME"), S22("PENSION FUNDS"),
    S23("RETIRED PENSION FUND"), S24("ROYAL COURT SAVINGS"), S25("WINNINGS FROM CONTEST / COMPETITION"),
    S26("INSURANCE CLAIM"), S27("SAVINGS"), S28("EDUCATION LOAN FROM BANK"),
    S29("EDUCATION LOAN FROM OTHER FINANCIAL INSTITUTION");

    private final String description;

    public static SourceOfIncomeEnum fromCodeOrDefault(String code) {
        try {
            return SourceOfIncomeEnum.valueOf(code);
        }
        catch (IllegalArgumentException | NullPointerException ex) {
            return S01;
        }
    }

}
