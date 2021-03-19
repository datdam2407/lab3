/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import datdt.dao.LoginDAO;
import datdt.dto.LoginCreateError;
import datdt.dto.Tool;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.NamingException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


/**
 *
 * @author MSI-PC
 */
public class SignupController extends HttpServlet {
       private final String INVALID_PAGE = "signup.jsp";
    private final String LOGIN_PAGE = "login.jsp";
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        String url = INVALID_PAGE;
        try {
            HttpSession session = request.getSession();
            String email = request.getParameter("txtUsername");
            String password = request.getParameter("txtPassword");
            String confirm = request.getParameter("txtConfirm");
            String name = request.getParameter("txtFullname");
            LoginCreateError errors = new LoginCreateError();
            session.setAttribute("txtPassword", password);
            session.setAttribute("txtUsername", email);
            session.setAttribute("txtConfirm", confirm);
            session.setAttribute("txtFullname", name);
            try{
                boolean foundErr = false;
            if (email.trim().length() < 6 || email.trim().length() > 20) {
                foundErr = true;
                errors.setUsernameLengthErr("Length must from 6 to 30");
            }
             if (email.contains("duplicate")) {
                    errors.setUsernameIsExisted(email + " is existed");
                    
                }
            if (password.trim().length() < 6 || password.trim().length() > 30) {
                foundErr = true;
                errors.setPassordLengthErr("Length must from 6 to 30");
            } else if (!password.equals(confirm)) {
                foundErr = true;
                errors.setConfirmNotMatched("Not match with password");
            }
            if (name.trim().length() < 2 || name.trim().length() > 50) {
                foundErr = true;
                errors.setFullNameLengthErr("Length must from 2 to 50");
            }
            if (foundErr) {
                //2.store faults into scope
                request.setAttribute("CREATEERROR", errors);
            } 
            else {
                //2.2 call DAO to insert to DB
                LoginDAO dao = new LoginDAO();
                boolean result = dao.createAccount(email, name, Tool.sha256(password), "Student", "new");    
                if (result) {
                    url = LOGIN_PAGE;
                }//end if insert is ok
            }//end if found err
        } 
       catch (SQLException e) {
                String errMgs = e.getMessage();
                log("SignUpServlet_SQL " + errMgs);
                if (errMgs.contains("duplicate")) {
                    errors.setUsernameIsExisted(email + " is existed");
                    request.setAttribute("CREATEERROR", errors);
                }
            } catch (NamingException ex) {
                log("SignUpServlet_Naming " + ex.getMessage());
            }
          
        
              }  finally {
            RequestDispatcher rd = request.getRequestDispatcher(url);
            rd.forward(request, response);
            out.close();
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
           try {
               processRequest(request, response);
           } catch (Exception ex) {
               Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
           }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
           try {
               processRequest(request, response);
           } catch (Exception ex) {
               Logger.getLogger(SignupController.class.getName()).log(Level.SEVERE, null, ex);
           }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
