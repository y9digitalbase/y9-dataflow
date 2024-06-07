package risesoft.data.transfer.stream.rdbms.in.columns.impl;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Types;

import risesoft.data.transfer.core.column.Column;
import risesoft.data.transfer.core.column.impl.DateColumn;
import risesoft.data.transfer.core.column.impl.LongColumn;
import risesoft.data.transfer.stream.rdbms.in.columns.CreateColumnHandle;

/**
 * Date 类型处理
 * 
 * @typeName DateCreateColumnHandle
 * @date 2024年1月25日
 * @author lb
 */
public class DateCreateColumnHandle implements CreateColumnHandle {

	@Override
	public boolean isHandle(int type) {
		return Types.DATE == type;

	}

	@Override
	public Column getColumn(ResultSet rs, ResultSetMetaData metaData, int index, String mandatoryEncoding)
			throws Exception {
		if (metaData.getColumnTypeName(index).equalsIgnoreCase("year")) {
           return new LongColumn(rs.getInt(index),metaData.getColumnLabel(index));
        } else {
        	return new DateColumn(rs.getDate(index),metaData.getColumnLabel(index));
        }
	}

}
