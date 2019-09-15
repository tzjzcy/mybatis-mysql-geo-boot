package com.carson.common.mybatis;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import com.vividsolutions.jts.io.WKBReader;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedTypes;

import java.math.BigDecimal;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/*
 * mybatis查询结果集中 mysql的geometry类型映射到GeoPoint对象
 */
@MappedTypes(value = {GeoPoint.class})
public class MysqlGeoPointTypeHandler extends BaseTypeHandler<GeoPoint> {

    private int _srid = 0;
    private WKBReader _wkbReader;

    public MysqlGeoPointTypeHandler() {
        GeometryFactory _geometryFactory = new GeometryFactory(new PrecisionModel(), _srid);
        _wkbReader = new WKBReader(_geometryFactory);
    }

    public MysqlGeoPointTypeHandler(int srid) {
        this._srid=srid;
        GeometryFactory _geometryFactory = new GeometryFactory(new PrecisionModel(), _srid);
        _wkbReader = new WKBReader(_geometryFactory);
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, GeoPoint parameter, JdbcType jdbcType) {
        //因为GeoPoint对象里包含经度和纬度两个值，无法直接适配到一个参数，所以也不会使用到这个方法
    }

    @Override
    public GeoPoint getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return fromMysqlWkb(rs.getBytes(columnName));
    }

    @Override
    public GeoPoint getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return fromMysqlWkb(rs.getBytes(columnIndex));
    }

    @Override
    public GeoPoint getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return fromMysqlWkb(cs.getBytes(columnIndex));
    }

    /*
     * bytes转GeoPoint对象
     */
    private GeoPoint fromMysqlWkb(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        try {
            byte[] geomBytes = ByteBuffer.allocate(bytes.length - 4).order(ByteOrder.LITTLE_ENDIAN)
                    .put(bytes, 4, bytes.length - 4).array();
            Geometry geometry = _wkbReader.read(geomBytes);
            Point point = (Point) geometry;
            return new GeoPoint(new BigDecimal(String.valueOf(point.getX())), new BigDecimal(String.valueOf(point.getY())));
        } catch (Exception e) {
        }
        return null;
    }
}
