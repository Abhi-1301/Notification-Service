package com.NotificationService.Repository;

import com.NotificationService.Model.BlackListNumber;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlackListRepository extends CrudRepository<BlackListNumber, String> {}
