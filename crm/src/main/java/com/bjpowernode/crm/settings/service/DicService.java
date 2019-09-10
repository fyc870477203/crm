package com.bjpowernode.crm.settings.service;

import com.bjpowernode.crm.exception.LoginException;
import com.bjpowernode.crm.settings.domain.DicValue;
import com.bjpowernode.crm.settings.domain.User;

import java.util.List;
import java.util.Map;

/**
 * 作者:fyc
 * 2019/8/29
 */
public interface DicService {

    Map<String, List<DicValue>> getAll();
}
