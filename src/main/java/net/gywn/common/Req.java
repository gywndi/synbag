package net.gywn.common;

import java.util.HashMap;
import java.util.Map;
import lombok.Data;

@Data
public class Req {
    private ReqType type;
    private String query;

    public Req(final String query) {
        this.query = query;
        this.type = ReqType.QUERY;
    }
}
