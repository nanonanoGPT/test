package com.servlet;

import com.nikita.generator.NikitaLogic;
import com.nikita.generator.NikitaRequest;
import com.nikita.generator.NikitaResponse;
import com.nikita.generator.NikitaServlet;
import com.nikita.generator.connection.NikitaConnection;
import com.rkrzmail.nikita.data.Log;
import com.rkrzmail.nikita.data.Nikitaset;
import com.rkrzmail.nikita.data.Nset;

public class HitungPayroll extends NikitaServlet {
    public void OnRun(NikitaRequest request, NikitaResponse response, NikitaLogic logic) {
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);
//        String a;
        Nikitaset ns = nc.Query("call selectPayroll");
        for (int i = 0; i < ns.getRows(); i++) {
//            nc.Query("update payroll_employee set result = 'update' where nik = ?",ns.getText(i,"nik"));
            String  result;
            float gapok = 0, type = 0;
            int tunjangan = Integer.parseInt(ns.getText(i,"tunjangan_supervisor") +
                    ns.getText(i,"tunjangan_jabatan") +
                    ns.getText(i,"skill_allowance") +
                    ns.getText(i,"grading_allowance") +
                    ns.getText(i,"montly_allowance") +
                    ns.getText(i,"performance_allowance") +
                    ns.getText(i,"position_allowance") +
                    ns.getText(i,"bbm") +
                    ns.getText(i,"komunikasi") +
                    ns.getText(i,"transportasi") +
                    ns.getText(i,"productivity") +
                    ns.getText(i,"khusus") +
                    ns.getText(i,"sewa_laptop") +
                    ns.getText(i,"makan_meal") +
                    ns.getText(i,"jabatan") +
                    ns.getText(i,"career_allowence") +
                    ns.getText(i,"tunjangan_premium") +
                    ns.getText(i,"kinerja") +
                    ns.getText(i,"tunjangan_parkir"));

            int absen = Integer.parseInt(ns.getText(i,"absen"));
            int jmlHariKerja = Integer.parseInt(ns.getText(i,"total_works"));
            String payroll_Mode = ns.getText(i,"payroll_mode");

            if (payroll_Mode.equals("MODE1")){
//                ((ABSEN/JML ABSEN * VARIABLE) + TUNJANGAN)
                type = Integer.parseInt(ns.getText(i,"variable"));
                gapok = (absen * 100) / jmlHariKerja ;
                gapok = gapok/100;
                gapok = gapok * type;
            } else if(payroll_Mode.equals("MODE2")){
//                ((FIX) + TUNJANGAN)
                 gapok = Integer.parseInt(ns.getText(i,"fix"));
            } else if(payroll_Mode.equals("MODE3")){
//                ((ABSEN * daily) + TUNJANGAN)
                gapok = (absen) * Integer.parseInt(ns.getText(i,"daily"));
            } else if(payroll_Mode.equals("MODE4")){
//                ((ABSEN * value) + TUNJANGAN)
                gapok = (absen) *  Integer.parseInt(ns.getText(i,"allowence"));
            } else if(payroll_Mode.equals("MODE5")){
//                ((ABSEN * value) + TUNJANGAN)
                gapok = (absen) *  Integer.parseInt(ns.getText(i,"submitted_user"));
            } else if(payroll_Mode.equals("MODE6")){
//                ((ABSEN * value) + TUNJANGAN)
                gapok = (absen) *  Integer.parseInt(ns.getText(i,"cl_first_transaction"));
            } else if(payroll_Mode.equals("MODE6")){
//                ((ABSEN * value) + TUNJANGAN)
                gapok = (absen) *  Integer.parseInt(ns.getText(i,"cl_retention"));
            }
//            gapok + tunjangan
            float gapokTunjangan = gapok + tunjangan;
            float gapokTanpaTunjangan = gapok;
            result=String.valueOf(gapokTunjangan);

            nc.Query("call insertHistoryPayroll(?,?)",ns.getText(i,"id"), result);
        }
    }
}
        //nc.closeConnection();
