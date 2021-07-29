package net.gywn.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum ReqType {
    INS {
        @Override
        public Res getRes(final DataSource ds, final String tb, final Req req) {
            Res res = new Res();
            try (final Connection conn = ds.getConnection()) {
                List<String> params = new ArrayList<String>();
                StringBuffer sbSQL = new StringBuffer();
                String cols = "", vals = "";

                // Generate insert query
                for (Entry<String, String> entry : req.getExtra().getValue().entrySet()) {
                    if (params.size() > 0) {
                        cols += ",";
                        vals += ",";
                    }
                    cols += entry.getKey();
                    vals += "?";
                    params.add(entry.getValue());
                }
                sbSQL.append("insert into ").append(tb);
                sbSQL.append("(").append(cols).append(")");
                sbSQL.append(" values ");
                sbSQL.append("(").append(vals).append(")");

                // Bind parameters preparedStatement
                int seq = 1;
                PreparedStatement pstmt = conn.prepareStatement(sbSQL.toString());
                for (String param : params) {
                    pstmt.setString(seq++, param);
                }
                int r = pstmt.executeUpdate();
                res.setRowCount(r);
                pstmt.close();
            } catch (Exception e) {
                res.setMessage(e.toString());
            }
            return res;
        }
    },
    UPD {
        @Override
        public Res getRes(final DataSource ds, final String tb, final Req req) {
            Res res = new Res();
            try (final Connection conn = ds.getConnection()) {
                List<String> params = new ArrayList<String>();
                StringBuffer sbSQL = new StringBuffer();

                // Generate update query
                sbSQL.append("update ").append(tb).append(" set ");

                // set
                for (Entry<String, String> entry : req.getExtra().getValue().entrySet()) {
                    if (params.size() > 0) {
                        sbSQL.append(", ");
                    }
                    sbSQL.append(entry.getKey()).append(" = ?");
                    params.add(entry.getValue());
                }

                // where
                sbSQL.append(" where 1 = 1");
                for (Entry<String, String> entry : req.getExtra().getWhere().entrySet()) {
                    sbSQL.append(" and ").append(entry.getKey()).append(" = ?");
                    params.add(entry.getValue());
                }

                // Bind parameters preparedStatement
                int seq = 1;
                PreparedStatement pstmt = conn.prepareStatement(sbSQL.toString());
                for (String param : params) {
                    pstmt.setString(seq++, param);
                }
                int r = pstmt.executeUpdate();
                res.setRowCount(r);
                pstmt.close();
            } catch (Exception e) {
                res.setMessage(e.toString());
            }
            return res;
        }
    },
    DEL {
        @Override
        public Res getRes(final DataSource ds, final String tb, final Req req) {
            Res res = new Res();
            try (final Connection conn = ds.getConnection()) {
                List<String> params = new ArrayList<String>();
                StringBuffer sbSQL = new StringBuffer();

                // Generate delete query
                sbSQL.append("delete from ").append(tb);
                
                // where
                sbSQL.append(" where 1 = 1");
                for (Entry<String, String> entry : req.getExtra().getWhere().entrySet()) {
                    sbSQL.append(" and ").append(entry.getKey()).append(" = ?");
                    params.add(entry.getValue());
                }

                // Bind parameters preparedStatement
                int seq = 1;
                PreparedStatement pstmt = conn.prepareStatement(sbSQL.toString());
                for (String param : params) {
                    pstmt.setString(seq++, param);
                }
                int r = pstmt.executeUpdate();
                res.setRowCount(r);
                pstmt.close();
            } catch (Exception e) {
                res.setMessage(e.toString());
            }
            return res;
        }
    },
    GET {
        @Override
        public Res getRes(final DataSource ds, final String tb, final Req req) {
            Res res = new Res();
            try (final Connection conn = ds.getConnection()) {
                List<String> params = new ArrayList<String>();
                StringBuffer sbSQL = new StringBuffer();

                // Initial select query
                sbSQL.append("select * from ").append(tb).append(" where 1 = 1 ");

                if (req.getExtra() != null) {
                    // Add equal clause from before map
                    if (req.getExtra().getWhere() != null) {
                        for (Entry<String, String> entry : req.getExtra().getWhere().entrySet()) {
                            sbSQL.append(" and ").append(entry.getKey()).append(" = ?");
                            params.add(entry.getValue());
                        }
                    }

                    // Add extra where clause
                    if (req.getExtra().getClause() != null) {
                        sbSQL.append(" and (").append(req.getExtra().getClause()).append(")");
                    }

                    // order by
                    if (req.getExtra().getOrderBy() != null) {
                        sbSQL.append(" order by ").append(req.getExtra().getOrderBy());
                    }

                    // limit
                    if (req.getExtra().getLimit() != null) {
                        sbSQL.append(" limit ").append(req.getExtra().getLimit());
                    }

                    // offset
                    if (req.getExtra().getOffset() != null) {
                        sbSQL.append(" offset ").append(req.getExtra().getOffset());
                    }
                }

                System.out.println(sbSQL.toString());

                // Bind parameters preparedStatement
                int seq = 1;
                PreparedStatement pstmt = conn.prepareStatement(sbSQL.toString());
                for (String param : params) {
                    pstmt.setString(seq++, param);
                }

                // Get result set
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
    },
    QUERY {
        @Override
        public Res getRes(final DataSource ds, final String tb, final Req req) {
            Res res = new Res();
            try (final Connection conn = ds.getConnection()) {
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
    public abstract Res getRes(final DataSource ds, final String tb, final Req req);
}