package com.NotificationService.Repository;

import com.NotificationService.Model.SmsRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RequestRepository extends JpaRepository<SmsRequest, Long> {
	Optional<SmsRequest>findByCorrelationID(UUID correlationId);
}
