package com.digit9.reports.infrastructure.entity;

import com.digit9.commons.core.data.enums.TransactionCategory;
import com.digit9.commons.infra.entity.BaseEntity;
import com.digit9.reports.infrastructure.contants.EntityConstants;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigInteger;

@Getter
@Setter
@Entity
@Table(name = "tsp_reports", schema = EntityConstants.SCHEMA_NAME)
public class TSPReports extends BaseEntity {

    @Id
    private Long id;

    @Column(columnDefinition = "numeric(20)")
    private BigInteger transactionId;

    @Enumerated(EnumType.STRING)
    private TransactionCategory category;

    private String tspId;

    private Long walletId;

}
