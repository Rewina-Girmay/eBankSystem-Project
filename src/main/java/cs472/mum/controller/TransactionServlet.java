package cs472.mum.controller;

import com.google.gson.Gson;
import cs472.mum.model.Account;
import cs472.mum.model.Transaction;
import cs472.mum.service.TransactionService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;

@WebServlet(name = "cs472.mum.controller.TransactionServlet")
public class TransactionServlet extends HttpServlet {
    private Gson gson = null;
    private TransactionService transactionService=null;

    @Override
    public void init() throws ServletException {
        super.init();
        gson= new Gson();
        transactionService= new TransactionService();
        transactionService.createTransaction(1,"acc1", 2.0);
        transactionService.createTransaction(12,"acc1", 3.0);
        transactionService.createTransaction(1234,"acc3", 23.0);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//        String pathInfo = request.getPathInfo();
        doGet(request,response);

    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pathInfo = request.getPathInfo();
        //displays all transactions in the entire
        if (pathInfo == null || pathInfo.equals("/")) {
            HashMap<Integer, Transaction> models = transactionService.getAllTransactions();
            sendAsJson(response, models);
            return;
        }
        //all transactions of that account
        String[] splits = pathInfo.split("/");

//        if(splits.length != 2) {
//            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
//            return;
//        }

        if(splits.length == 2) {
            String modelId = splits[1];
            if (transactionService.readAllTransaction(modelId) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            sendAsJson(response, transactionService.readAllTransaction(modelId));
            return;
        }

        //All transactions made on that day
        if(splits.length==4){

            String modelId = splits[1];
            String date= splits[3];
            if (transactionService.readAllTransaction(modelId) == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            if(transactionService.transactionsFromDate(date,modelId)==null)
            {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            sendAsJson(response, transactionService.transactionsFromDate(date,modelId));
            return;
        }

        //all transactions made starting from that day

    }


    private void sendAsJson(HttpServletResponse response, Object obj) throws IOException {
            response.setContentType("application/json");
            String res = gson.toJson(obj);
            PrintWriter out = response.getWriter();
            System.out.println(res);
            out.print(res);
            out.flush();
        }
    }