package com.carson.common.mybatis;

import org.apache.ibatis.mapping.MappedStatement;
import tk.mybatis.mapper.LogicDeleteException;
import tk.mybatis.mapper.annotation.LogicDelete;
import tk.mybatis.mapper.annotation.Version;
import tk.mybatis.mapper.entity.EntityColumn;
import tk.mybatis.mapper.mapperhelper.EntityHelper;
import tk.mybatis.mapper.mapperhelper.MapperHelper;
import tk.mybatis.mapper.mapperhelper.MapperTemplate;
import tk.mybatis.mapper.mapperhelper.SqlHelper;
import tk.mybatis.mapper.util.StringUtil;
import tk.mybatis.mapper.version.VersionException;

import java.util.Set;


/**
 * BaseUpdateProvider实现类，基础方法实现类
 *
 * @author liuzh
 */
public class GeoBaseUpdateProvider extends MapperTemplate {

    public GeoBaseUpdateProvider(Class<?> mapperClass, MapperHelper mapperHelper) {
        super(mapperClass, mapperHelper);
    }

    /**
     * 通过主键更新全部字段
     *
     * @param ms
     */
    public String updateByPrimaryKey(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(updateSetColumns(entityClass, null, false, false));
        sql.append(SqlHelper.wherePKColumns(entityClass, true));
        return sql.toString();
    }

    /**
     * 通过主键更新不为null的字段
     *
     * @param ms
     * @return
     */
    public String updateByPrimaryKeySelective(MappedStatement ms) {
        Class<?> entityClass = getEntityClass(ms);
        StringBuilder sql = new StringBuilder();
        sql.append(SqlHelper.updateTable(entityClass, tableName(entityClass)));
        sql.append(updateSetColumns(entityClass, null, true, isNotEmpty()));
        sql.append(SqlHelper.wherePKColumns(entityClass, true));
        return sql.toString();
    }

    /**
     * update set列
     *
     * @param entityClass
     * @param entityName  实体映射名
     * @param notNull     是否判断!=null
     * @param notEmpty    是否判断String类型!=''
     * @return
     */
    public String updateSetColumns(Class<?> entityClass, String entityName, boolean notNull, boolean notEmpty) {
        StringBuilder sql = new StringBuilder();
        sql.append("<set>");
        //获取全部列
        Set<EntityColumn> columnSet = EntityHelper.getColumns(entityClass);
        //对乐观锁的支持
        EntityColumn versionColumn = null;
        // 逻辑删除列
        EntityColumn logicDeleteColumn = null;
        //当某个列有主键策略时，不需要考虑他的属性是否为空，因为如果为空，一定会根据主键策略给他生成一个值
        for (EntityColumn column : columnSet) {
            if (column.getEntityField().isAnnotationPresent(VirtualGenerated.class)) {
                continue;
            }
            if (column.getEntityField().isAnnotationPresent(Version.class)) {
                if (versionColumn != null) {
                    throw new VersionException(entityClass.getCanonicalName() + " 中包含多个带有 @Version 注解的字段，一个类中只能存在一个带有 @Version 注解的字段!");
                }
                versionColumn = column;
            }
            if (column.getEntityField().isAnnotationPresent(LogicDelete.class)) {
                if (logicDeleteColumn != null) {
                    throw new LogicDeleteException(entityClass.getCanonicalName() + " 中包含多个带有 @LogicDelete 注解的字段，一个类中只能存在一个带有 @LogicDelete 注解的字段!");
                }
                logicDeleteColumn = column;
            }
            if (!column.isId() && column.isUpdatable()) {
                if (column == versionColumn) {
                    Version version = versionColumn.getEntityField().getAnnotation(Version.class);
                    String versionClass = version.nextVersion().getCanonicalName();
                    sql.append("<bind name=\"").append(column.getProperty()).append("Version\" value=\"");
                    //version = ${@tk.mybatis.mapper.version@nextVersionClass("versionClass", version)}
                    sql.append("@tk.mybatis.mapper.version.VersionUtil@nextVersion(")
                            .append("@").append(versionClass).append("@class, ");
                    if (StringUtil.isNotEmpty(entityName)) {
                        sql.append(entityName).append(".");
                    }
                    sql.append(column.getProperty()).append(")\"/>");
                    sql.append(column.getColumn()).append(" = #{").append(column.getProperty()).append("Version},");
                } else if (column == logicDeleteColumn) {
                    sql.append(SqlHelper.logicDeleteColumnEqualsValue(column, false)).append(",");
                } else if (notNull) {
                    if (column.getJavaType() == GeoPoint.class) {
                        //geo字段更新
                        sql.append(SqlHelper.getIfNotNull(entityName, column, getGeoColumnEqualsHolder(column) + ",", notEmpty));
                    } else {
                        sql.append(SqlHelper.getIfNotNull(entityName, column, column.getColumnEqualsHolder(entityName) + ",", notEmpty));
                    }
                } else {
                    if (column.getJavaType() == GeoPoint.class) {
                        //geo字段更新
                        sql.append(getGeoColumnEqualsHolder(column) + ",");
                    } else {
                        sql.append(column.getColumnEqualsHolder(entityName) + ",");
                    }
                }
            }
        }
        sql.append("</set>");
        return sql.toString();
    }

    /*
     * update GEO字段占位符
     */
    private String getGeoColumnEqualsHolder(EntityColumn column){
        return column.getColumn()+ " = " + String.format("geomfromtext('point(${%s.lng} ${%s.lat})')",column.getProperty(),column.getProperty());
    }
}
