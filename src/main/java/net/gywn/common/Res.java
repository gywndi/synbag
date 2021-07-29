package net.gywn.common;

import java.util.List;
import java.util.Map;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Res {
    private static final Logger logger = LoggerFactory.getLogger(Res.class);

    private int rowCount;
    private String message;
    private List<Map<String,String>> rows;

    public void setRows(List<Map<String,String>> rows){
        this.rowCount = rows.size();
        this.rows = rows;
    }
}
