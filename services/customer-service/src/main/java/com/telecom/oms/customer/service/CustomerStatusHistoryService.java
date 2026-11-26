package com.telecom.oms.customer.service;

import com.telecom.oms.customer.dto.CustomerStatusHistoryResponse;
import com.telecom.oms.customer.entity.Customer;
import com.telecom.oms.customer.entity.CustomerStatus;
import com.telecom.oms.customer.entity.CustomerStatusHistory;
import com.telecom.oms.customer.repository.CustomerStatusHistoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerStatusHistoryService {

    private final CustomerStatusHistoryRepository historyRepository;

    public CustomerStatusHistoryService(
            CustomerStatusHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Transactional
    public void recordStatusChange(
            Customer customer,
            CustomerStatus previousStatus,
            CustomerStatus newStatus) {

        CustomerStatusHistory history = new CustomerStatusHistory();

        history.setCustomer(customer);
        history.setPreviousStatus(previousStatus);
        history.setNewStatus(newStatus);
        history.setChangedBy("SYSTEM");

        historyRepository.save(history);
    }

    @Transactional(readOnly = true)
    public List<CustomerStatusHistoryResponse> getStatusHistory(
            String accountNumber) {

        List<CustomerStatusHistory> historyRecords =
                historyRepository.findByCustomerAccountNumberOrderByChangedAtAsc(
                        accountNumber
                );

        return historyRecords.stream()
                .map(history -> new CustomerStatusHistoryResponse(
                        history.getId(),
                        history.getPreviousStatus(),
                        history.getNewStatus(),
                        history.getChangedBy(),
                        history.getChangedAt()
                ))
                .toList();
    }
}
