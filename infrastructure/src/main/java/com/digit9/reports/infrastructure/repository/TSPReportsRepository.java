package com.digit9.reports.infrastructure.repository;

import com.digit9.reports.infrastructure.entity.TSPReports;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface TSPReportsRepository extends JpaRepository<TSPReports, Long>, JpaSpecificationExecutor<TSPReports> {

}
