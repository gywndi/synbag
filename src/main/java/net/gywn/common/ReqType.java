package net.gywn.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.gywn.SynbagServer;

public enum ReqType {
    QUERY {
        @Override
        public Res getRes(final String tb, final Req req) {
            Res res = new Res();
            try (final Connection conn = SynbagServer.DS.getConnection()) {
                PreparedStatement pstmt = conn.prepareStatement(req.getQuery());
                ResultSet rs = pstmt.executeQuery();
                ResultSetMetaData rsMeta = rs.getMetaData();
                List<Map<String, String>> rows = new ArrayList<Map<String, String>>();
                while (rs.next()) {
                    Map<String, String> row = new HashMap<String, String>();
                    for (int i = 1;; i++) {
                        try {
                            row.put(rsMeta.getColumnLabel(i), rs.getString(i));
                        } catch (Exception e) {
                            break;
                        }
                    }
                    rows.add(row);
                }
                res.setRows(rows);
                rs.close();
                pstmt.close();
                conn.close();
            } catch (Exception e) {
                res.setMessage(e.toString());
            }
            return res;
        }
    };

    public abstract Res getRes(final String tb, final Req req);
}