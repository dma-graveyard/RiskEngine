package dk.sfs.riskengine.index;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import dk.sfs.riskengine.persistence.domain.DepthPoint;
import dk.sfs.riskengine.persistence.mapper.DepthPointMapper;

public class Test {

	public static void main(String[] args) throws IOException {
		String resource = "dk/sfs/riskengine/persistence/xml/Configuration.xml";
		Reader reader = Resources.getResourceAsReader(resource);
		SqlSession session = new SqlSessionFactoryBuilder().build(reader).openSession();
		DepthPointMapper mapper= session.getMapper(DepthPointMapper.class);
		
		Map<String, Object> map = new HashMap<String, Object>();
		double cog = 200.0; 	
		double angle = Math.PI*(0.5 - cog/180);
		
		if(angle <0){
			angle+=Math.PI*2;
		}
		System.out.println(angle);
		Long ratioM = Math.round(Math.sin(angle)*10l);  
		Long ratioN = Math.round(Math.cos(angle)*10l);
		System.out.println(ratioM);
		System.out.println(ratioN);
		
		map.put("lat", 55.45);
		map.put("lon", 12.45);
		
		DepthPoint p = mapper.findClosestDeepPoint(map);
		//DepthPoint param = new DepthPoint();
		map.put("m", p.getM());
		map.put("n", p.getN());
		map.put("depth", -5);
		map.put("ratioM", ratioM);
		map.put("ratioN", ratioN);
		
		DepthPoint o = mapper.findGroundingPointIndices(map);
		p = mapper.selectByIndices(o);
		System.out.println("m "+p.getLat());
		System.out.println("n "+p.getLon());
		System.out.println("depth "+p.getDepth());
		 
	}
}
