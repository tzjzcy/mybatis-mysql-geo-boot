# mybatis-mysql-geo-boot
mybatis-mysql-geo-boot是基于 mybatis 和 tk通用mapper 扩展出对 mysql数据库（5.7以上版本） 地理位置geometry 类型字段和 虚拟列(Virtual Generated Column) 的支持，主要功能如下：
- 增加TypeHandler支持查询geometry字段，映射到GeoPoint类型
- 使tk通用mapper的insert/insertSelective方法支持GeoPoint类型的属性，插入到geometry类型字段
- 使tk通用mapper的updateByPrimaryKey/updateByPrimaryKeySelective方法支持GeoPoint类型的属性，更新到geometry类型字段
- 增加@VirtualGenerated注解，使tk通用mapper的insert/insertSelective/updateByPrimaryKey/updateByPrimaryKeySelective方法忽略虚拟列

## 如何使用
该项目基于spring boot2.0，下载代码，运行mybatis-mysql-geo-boot，开箱即用，你可以自由的修改代码，做任何你需要的定制。

## 案例演示
下载代码，运行carson-web-mvc-bootdemo，即可看到使用案例
- 调用/userList接口演示select案例
- 调用/addUser接口演示insert案例

## 版本更新
- 2019-09-15 项目发布第一版

## 使用指南
1. 实体类中，为geometry类型的字段使用GeoPoint类型，并加上@Column注解，以支持新增、修改
1. 实体类中，为mysql虚拟列加上@VirtualGenerated注解，使新增、修改时忽略该字段
1. 为mybatis配置增加MysqlGeoPointTypeHandler,可参照MybatisConfig，或使用xml配置，使查询时能够映射geometry类型的字段
1. 让你的Mapper接口继承GeoBaseInsertMapper<T>, GeoBaseUpdateMapper<T>，以支持geometry类型的新增、修改

### 实体类demo
```java
@Table(name = "t_user")
public class User {
    private String id;
    private String name;
    @Column
    private GeoPoint gis;
    @VirtualGenerated
    private String geohash;
    
    //getter、setter省略
}
```

### mybatis配置
这里使用java代码配置，也可以使用xml配置
```java
@Configuration
@MapperScan(basePackages = {"com.carson.**.mapper"}, sqlSessionTemplateRef = "sqlSessionTemplate")
public class MybatisConfig {

    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) {
        SqlSessionFactoryBean bean = new SqlSessionFactoryBean();
        bean.setDataSource(dataSource);
        bean.setVfs(SpringBootVFS.class);
        //添加XML目录
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        try {
            bean.setMapperLocations(resolver.getResources("classpath:mybatis/**/*Mapper.xml"));
            bean.setTypeAliasesPackage("com.carson.pojo");
            //为mybatis配置增加MysqlGeoPointTypeHandler,使查询时能够映射geometry类型的字段
            bean.setTypeHandlers(new TypeHandler[]{new MysqlGeoPointTypeHandler(0)});
            bean.getObject().getConfiguration().setMapUnderscoreToCamelCase(true);
            return bean.getObject();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
    //...
}
```

### mapper接口
```java
@Repository
public interface UserMapper extends GeoBaseInsertMapper<User>, 
                                    GeoBaseUpdateMapper<User>, 
                                    BaseSelectMapper<User> {
}
```

## Prerequisites
[JDK 8 update 20 or later][JDK8 build]

## Check out sources
github:
`git clone https://github.com/tzjzcy/mybatis-mysql-geo-boot.git`

gitee:
`git clone https://gitee.com/tzjzcy/mybatis-mysql-geo-boot.git`

## License
The mybatis-mysql-geo-boot is released under [MIT License][].

[JDK8 build]: http://www.oracle.com/technetwork/java/javase/downloads
[MIT License]: https://github.com/tzjzcy/mybatis-mysql-geo-boot/blob/master/LICENSE