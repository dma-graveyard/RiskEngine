package dk.sfs.riskengine.persistence.typeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;

import dk.sfs.riskengine.persistence.domain.Vessel.ShipTypeIwrap;

public class ShipTypeTypeHandler implements TypeHandler<ShipTypeIwrap> {
	
	public void setParameter(PreparedStatement ps, int i, ShipTypeIwrap parameter, JdbcType jdbcType) throws SQLException {
		if (parameter == null) {
			ps.setString(i, null);
		} else {
			ps.setString(i, parameter.getIwrapName());
		}
		
	}

	public ShipTypeIwrap getResult(ResultSet rs, String columnName) throws SQLException {
		return ShipTypeIwrap.getShipTypeFromIwrapName(rs.getString(columnName));
	}

	public ShipTypeIwrap getResult(CallableStatement cs, int columnIndex) throws SQLException {
		return ShipTypeIwrap.getShipTypeFromIwrapName(cs.getString(columnIndex));
	}
}