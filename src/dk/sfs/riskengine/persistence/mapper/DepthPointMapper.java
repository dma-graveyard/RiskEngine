package dk.sfs.riskengine.persistence.mapper;

import java.util.Map;

import dk.sfs.riskengine.persistence.domain.DepthPoint;

public interface DepthPointMapper {
    
	DepthPoint findGroundingPointIndices(Map<String, Object> map);
    
    DepthPoint findClosestDeepPoint(Map<String, Object> map);
    
    DepthPoint selectByIndices(DepthPoint o);
    
}