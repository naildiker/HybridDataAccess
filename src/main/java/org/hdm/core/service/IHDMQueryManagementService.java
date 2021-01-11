package org.hdm.core.service;

import org.hdm.core.objects.IEntityOccurence;

import java.util.List;

public interface IHDMQueryManagementService {
    List<IEntityOccurence> get(String entityName, String keyword);
}
