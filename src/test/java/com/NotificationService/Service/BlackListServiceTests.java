package com.NotificationService.Service;

import com.NotificationService.Model.BlackListNumber;
import com.NotificationService.Repository.BlackListRepository;
import com.NotificationService.Service.Impl.BlackListServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BlackListServiceTests {
  @Mock private BlackListRepository blackListRepository;

  @InjectMocks private BlackListServiceImpl blackListService;

  @BeforeEach
  void setUp() {
    reset(blackListRepository);
  }

  @Test
  void addBlackListTest() throws Exception {
    BlackListNumber blackListNumber = new BlackListNumber("+911234567891");
    blackListService.addBlackList(blackListNumber);
    verify(blackListRepository, times(1)).save(blackListNumber);
  }

  @Test
  void fetchAllBlackListTest() throws Exception {
    BlackListNumber blackListNumber1 = new BlackListNumber("+911234567891");
    BlackListNumber blackListNumber2 = new BlackListNumber("+911221221221");

    List<BlackListNumber> expected = new ArrayList<>();
    expected.add(blackListNumber1);
    expected.add(blackListNumber2);

    when(blackListRepository.findAll()).thenReturn(expected);

    Iterable<BlackListNumber> actualList = blackListRepository.findAll();
    assertAll(
        () -> {
          for (BlackListNumber blackListNumber : actualList) {
            assertTrue(expected.contains(blackListNumber));
          }
        });
  }

  @Test
  void deleteBlacklistTest() throws Exception {
    BlackListNumber blackListNumber = new BlackListNumber("+911234567891");
    blackListService.deleteBlackList(blackListNumber);
    verify(blackListRepository, times(1)).delete(blackListNumber);
  }
  
  @Test
  void isBlacklistedTest() throws Exception{
    BlackListNumber blackListNumber = new BlackListNumber("+911234567891");
    when (blackListRepository.existsById (blackListNumber.getPhoneNumber ())).thenReturn (true);
    assertTrue (blackListService.isBlackListed (blackListNumber));
  }
}
