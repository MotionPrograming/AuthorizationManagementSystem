package com.ams.modules.report.repository.impl;

import java.sql.*;
import java.time.LocalDate;
import com.ams.config.DBConnection;
import com.ams.modules.report.entity.SystemReportSummary;
import com.ams.modules.report.repository.ReportRepository;

public class ReportRepositoryImpl implements ReportRepository {
    @Override public SystemReportSummary getSystemSummary(LocalDate startDate, LocalDate endDate) {
        StringBuilder sql=new StringBuilder("SELECT (SELECT COUNT(*) FROM USERS) total_users, "
            + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE 1=1) total_requests, "
            + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.REQUEST_STATUS='PENDING') pending_requests, "
            + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.REQUEST_STATUS='APPROVED') approved_requests, "
            + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.REQUEST_STATUS='REJECTED') rejected_requests, "
            + "(SELECT COUNT(*) FROM AUDIT_LOG al WHERE 1=1) total_audits FROM DUAL");
        // Date filters are applied to request/audit counts when supplied.
        if(startDate!=null || endDate!=null){
            String from="?";
            String to="?";
            sql=new StringBuilder("SELECT (SELECT COUNT(*) FROM USERS) total_users, "
                + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.CREATED_AT >= "+from+" AND ar.CREATED_AT < "+to+") total_requests, "
                + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.REQUEST_STATUS='PENDING' AND ar.CREATED_AT >= "+from+" AND ar.CREATED_AT < "+to+") pending_requests, "
                + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.REQUEST_STATUS='APPROVED' AND ar.CREATED_AT >= "+from+" AND ar.CREATED_AT < "+to+") approved_requests, "
                + "(SELECT COUNT(*) FROM ACCESS_REQUEST ar WHERE ar.REQUEST_STATUS='REJECTED' AND ar.CREATED_AT >= "+from+" AND ar.CREATED_AT < "+to+") rejected_requests, "
                + "(SELECT COUNT(*) FROM AUDIT_LOG al WHERE al.CREATED_AT >= "+from+" AND al.CREATED_AT < "+to+") total_audits FROM DUAL");
        }
        try(Connection c=DBConnection.getConnection();PreparedStatement s=c.prepareStatement(sql.toString())){
            if(startDate!=null || endDate!=null){
                Timestamp from=Timestamp.valueOf((startDate==null?LocalDate.of(1,1,1):startDate).atStartOfDay());
                Timestamp to=Timestamp.valueOf((endDate==null?LocalDate.of(9999,12,31):endDate.plusDays(1)).atStartOfDay());
                int index=1;
                for(int i=0;i<5;i++){s.setTimestamp(index++,from);s.setTimestamp(index++,to);}
            }
            try(ResultSet r=s.executeQuery()){if(r.next())return new SystemReportSummary(r.getLong("total_users"),r.getLong("total_requests"),r.getLong("pending_requests"),r.getLong("approved_requests"),r.getLong("rejected_requests"),r.getLong("total_audits"));}
        }catch(SQLException e){throw new RuntimeException("Failed to generate report",e);}
        return new SystemReportSummary();
    }
}
