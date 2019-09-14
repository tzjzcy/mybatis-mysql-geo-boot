package com.carson.pojo;

import com.carson.common.mybatis.GeoPoint;
import com.carson.common.mybatis.VirtualGenerated;

import javax.persistence.Column;
import javax.persistence.Table;

@Table(name = "t_user")
public class User {
    private String id;
    private String name;
    @Column
    private GeoPoint gis;
    @VirtualGenerated
    private String geohash;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoPoint getGis() {
        return gis;
    }

    public void setGis(GeoPoint gis) {
        this.gis = gis;
    }

    public String getGeohash() {
        return geohash;
    }

    public void setGeohash(String geohash) {
        this.geohash = geohash;
    }
}
