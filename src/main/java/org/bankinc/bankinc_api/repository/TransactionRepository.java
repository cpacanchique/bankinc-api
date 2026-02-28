package org.bankinc.bankinc_api.repository;

import org.bankinc.bankinc_api.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}