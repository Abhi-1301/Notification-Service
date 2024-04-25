package com.NotificationService.Service.Impl;

import com.NotificationService.Model.BlackListNumber;
import com.NotificationService.Repository.BlackListRepository;
import com.NotificationService.Service.BlackListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BlackListServiceImpl implements BlackListService {
  @Autowired private BlackListRepository blackListRepository;

  @Cacheable(value = "blackListNumberCache", key = "#blackListNumber.phoneNumber")
  @Override
  public void addBlackList(BlackListNumber blackListNumber) throws Exception {
    try {
      blackListRepository.save(blackListNumber);
    } catch (Exception e) {
      throw new Exception();
    }
  }

  @Override
  public Iterable<BlackListNumber> fetchAll() throws Exception{
    try {
      return blackListRepository.findAll();
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @CacheEvict(value = "blackListNumberCache", key = "#blackListNumber.phoneNumber")
  @Override
  public void deleteBlackList(BlackListNumber blackListNumber) throws Exception{
    try {
      blackListRepository.delete(blackListNumber);
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }

  @Override
  public boolean isBlackListed(BlackListNumber blackListNumber) throws Exception{
    try {
      return blackListRepository.existsById(blackListNumber.getPhoneNumber());
    } catch (Exception e) {
      throw new RuntimeException();
    }
  }
}
