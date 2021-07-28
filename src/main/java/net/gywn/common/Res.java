package net.gywn.common;

import java.util.List;
import java.util.Map;

import lombok.Data;

@Data
public class Res {
    private int rowCount;
    private String message;
    private List<Map<String,String>> rows;

    public void setRows(List<Map<String,String>> rows){
        this.rowCount = rows.size();
        this.rows = rows;
    }
}
