package com.pdd.redcurrant.domain.data.request.common;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.pdd.redcurrant.domain.annotations.StandardJson;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@StandardJson
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
