package dk.sfs.riskengine.persistence.mapper;

import java.util.List;

import dk.sfs.riskengine.persistence.domain.RiskIndexes;

public interface RiskIndexesMapper {
       int insert(RiskIndexes record);

       RiskIndexes selectByPrimaryKey(Integer id);
       
       int updateByPrimaryKey(RiskIndexes record);
        List<RiskIndexes> selectLatest();
}