/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.servlet;

import com.nikita.generator.*;
import com.nikita.generator.connection.NikitaConnection;
import com.nikita.generator.ui.Image;
import com.nikita.generator.ui.Label;
import com.nikita.generator.ui.Textsmart;
import com.nikita.generator.ui.layout.DivLayout;
import com.nikita.generator.ui.layout.NikitaForm;
import com.rkrzmail.nikita.data.Nikitaset;

/**
 *
 * @author user
 */
public class test1 extends NikitaServlet{

    @Override
    public void OnCreate(NikitaRequest request,final NikitaResponse response, NikitaLogic logic) {
//        nikitaConnection = response.getConnection(NikitaConnection.LOGIC);
        NikitaConnection nc = response.getConnection(NikitaConnection.LOGIC);


        Nikitaset ns = nc.Query("select * from master_salary_type");
        if (ns.getRows()>=1) {

        }

    }

    
}
