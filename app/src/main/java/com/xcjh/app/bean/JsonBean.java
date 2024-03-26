package com.xcjh.app.bean;


import java.util.List;

import ando.widget.wheelview.IPickerViewData;

/**
 *
 *
 * @author: 小嵩
 * @date: 2017/3/16 15:36
 */

public class JsonBean implements IPickerViewData {


    public JsonBean(String name,List<CityBean> city){
        this.name= name;
        this.city=city;
    }

    /**
     * name : 2024
     * city : [{"name":"3","area":["21","22","23","24","25"]}]
     */

    private String name;
    private List<CityBean> city;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<CityBean> getCityList() {
        return city;
    }

    public void setCityList(List<CityBean> city) {
        this.city = city;
    }

    // 实现 IPickerViewData 接口，
    // 这个用来显示在PickerView上面的字符串，
    // PickerView会通过IPickerViewData获取getPickerViewText方法显示出来。
    @Override
    public String getPickerViewText() {
        return this.name;
    }


    public static class CityBean {

        public CityBean(String name,List<String> area){
            this.name= name;
            this.area=area;
        }

        /**
         * name : 3
         * area : ["12","13","14","15"]
         */

        private String name;
        private List<String> area;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<String> getArea() {
            return area;
        }

        public void setArea(List<String> area) {
            this.area = area;
        }
    }
}
