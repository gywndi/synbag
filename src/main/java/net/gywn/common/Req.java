package net.gywn.common;

import java.util.Map;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class Req {
    private static final Logger logger = LoggerFactory.getLogger(Req.class);

    private ReqType type;
    private String query;
    private Extra extra;

    public Req(final String query){
        this.query = query;
        this.type = ReqType.QUERY;
    }

    @Data
    public class Extra{
        private Map<String, String> where;
        private Map<String, String> value;
        private String clause;
        private String orderBy;
        private Integer limit;
        private Integer offset;
    }
}
