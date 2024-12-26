package com.pdd.redcurrant.domain.data.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceInfoDto {

    private String txnType;

    private String beneficiaryBankRoutingCode;

    private String beneficiaryBankName;

    private String beneficiaryBankBranch;

    private String beneficiaryBankBranchAddress;

    private String ifscCode;

    private String swiftCode;

    private String swiftCode2;

    @JsonAlias("beneficiaryAcctNum")
    private String beneficiaryAccountNumber;

    private String accountType;

    private String bankAccountType;

    private String beneficiaryBankId;

    private String beneficiaryBranchId;

    private String draweeBankBranchID;

    private String beneficiaryBank;

    private String receivingBranchID;

    private String beneficiaryBranchCode;

    private String operatorCode;

    private String pickupLocation;

}
