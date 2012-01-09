package dk.sfs.riskengine.persistence.mapper;

import dk.sfs.riskengine.index.IncidentType;

public interface RiskMapper {
  
    int insert(IncidentType record);

}