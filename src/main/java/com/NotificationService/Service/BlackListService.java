package com.NotificationService.Service;

import com.NotificationService.Model.BlackListNumber;


public interface BlackListService {
	void addBlackList(BlackListNumber blackListNumber) throws Exception;
	Iterable<BlackListNumber> fetchAll() throws Exception;
	void deleteBlackList(BlackListNumber blackListNumber) throws Exception;
	boolean isBlackListed(BlackListNumber blackListNumber) throws Exception;
}
