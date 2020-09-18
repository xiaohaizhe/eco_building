package com.giot.eco_building.utils;

import com.opencsv.bean.CsvToBeanFilter;

/**
 * Created with IntelliJ IDEA.
 * Description:
 * User: Ting
 * Date: 2020-09-12
 * Time: 23:22
 */
public class ProjectCsvToBeanFilter implements CsvToBeanFilter {
    @Override
    public boolean allowLine(String[] strings) {
        if (strings[0].equals("")) {
            return false;
        }else{
            return true;
        }
    }
}
