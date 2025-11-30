package com.emicalculator.web;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import com.emicalculator.database.DatabaseManager;
import com.emicalculator.model.EMICalculation;
import com.emicalculator.service.EMICalculatorService;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EMIWebServer {
    private static final EMICalculatorService calculatorService = new EMICalculatorService();
    private static final DatabaseManager databaseManager = new DatabaseManager();
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException {
        System.setProperty("java.net.preferIPv4Stack", "true");
        HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 8080), 0);
        
        server.createContext("/", new HomeHandler());
        server.createContext("/calculate", new CalculateHandler());
        server.createContext("/history", new HistoryHandler());
        server.createContext("/api/calculate", new ApiCalculateHandler());
        server.createContext("/api/history", new ApiHistoryHandler());
        
        server.setExecutor(null);
        server.start();
        
        System.out.println("EMI Calculator Web Server started on http://localhost:8080");
        System.out.println("Press Ctrl+C to stop the server");
    }

    static class HomeHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getHomePageHtml();
            sendResponse(exchange, 200, html, "text/html");
        }
    }

    static class CalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getCalculatePageHtml();
            sendResponse(exchange, 200, html, "text/html");
        }
    }

    static class HistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String html = getHistoryPageHtml();
            sendResponse(exchange, 200, html, "text/html");
        }
    }

    static class ApiCalculateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                Map<String, String> params = parseFormData(exchange);
                
                try {
                    double principal = Double.parseDouble(params.get("principal"));
                    double rate = Double.parseDouble(params.get("rate"));
                    int tenure = Integer.parseInt(params.get("tenure"));
                    
                    EMICalculation calc = calculatorService.calculateEMI(principal, rate, tenure);
                    databaseManager.saveCalculation(calc);
                    
                    String json = String.format(
                        "{\"success\":true,\"emi\":%.2f,\"totalAmount\":%.2f,\"totalInterest\":%.2f}",
                        calc.getEmiAmount(), calc.getTotalAmount(), calc.getTotalInterest()
                    );
                    
                    sendResponse(exchange, 200, json, "application/json");
                } catch (Exception e) {
                    String json = "{\"success\":false,\"error\":\"" + e.getMessage() + "\"}";
                    sendResponse(exchange, 400, json, "application/json");
                }
            } else {
                sendResponse(exchange, 405, "Method Not Allowed", "text/plain");
            }
        }
    }

    static class ApiHistoryHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            List<EMICalculation> history = databaseManager.getCalculationHistory();
            
            StringBuilder json = new StringBuilder("[");
            for (int i = 0; i < history.size(); i++) {
                EMICalculation calc = history.get(i);
                if (i > 0) json.append(",");
                json.append(String.format(
                    "{\"id\":%d,\"principal\":%.2f,\"rate\":%.2f,\"tenure\":%d," +
                    "\"emi\":%.2f,\"totalAmount\":%.2f,\"totalInterest\":%.2f,\"date\":\"%s\"}",
                    calc.getId(), calc.getPrincipal(), calc.getRateOfInterest(),
                    calc.getTenureMonths(), calc.getEmiAmount(), calc.getTotalAmount(),
                    calc.getTotalInterest(), calc.getCalculatedAt().format(formatter)
                ));
            }
            json.append("]");
            
            sendResponse(exchange, 200, json.toString(), "application/json");
        }
    }

    private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String formData = br.readLine();
        
        Map<String, String> params = new HashMap<>();
        if (formData != null) {
            for (String param : formData.split("&")) {
                String[] pair = param.split("=");
                if (pair.length == 2) {
                    params.put(pair[0], pair[1]);
                }
            }
        }
        return params;
    }

    private static void sendResponse(HttpExchange exchange, int statusCode, String response, String contentType) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", contentType + "; charset=UTF-8");
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }

    private static String getHomePageHtml() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>EMI Calculator</title>" +
            "<style>body{font-family:Arial,sans-serif;max-width:800px;margin:50px auto;padding:20px;background:#f5f5f5}" +
            ".container{background:white;padding:30px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}" +
            "h1{color:#333;text-align:center}nav{text-align:center;margin:30px 0}" +
            "a{display:inline-block;padding:15px 30px;margin:10px;background:#007bff;color:white;text-decoration:none;border-radius:5px}" +
            "a:hover{background:#0056b3}</style></head><body><div class='container'>" +
            "<h1>&#127974; Loan EMI Calculator</h1><nav>" +
            "<a href='/calculate'>Calculate EMI</a>" +
            "<a href='/history'>View History</a></nav></div></body></html>";
    }

    private static String getCalculatePageHtml() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Calculate EMI</title>" +
            "<style>body{font-family:Arial,sans-serif;max-width:600px;margin:50px auto;padding:20px;background:#f5f5f5}" +
            ".container{background:white;padding:30px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}" +
            "h1{color:#333;text-align:center}.form-group{margin:20px 0}label{display:block;margin-bottom:5px;font-weight:bold}" +
            "input{width:100%;padding:10px;border:1px solid #ddd;border-radius:5px;box-sizing:border-box}" +
            "button{width:100%;padding:15px;background:#28a745;color:white;border:none;border-radius:5px;cursor:pointer;font-size:16px}" +
            "button:hover{background:#218838}.result{margin-top:20px;padding:20px;background:#e7f3ff;border-radius:5px;display:none}" +
            ".back{display:inline-block;margin-top:20px;color:#007bff;text-decoration:none}" +
            ".result-item{margin:10px 0;font-size:18px}</style></head><body><div class='container'>" +
            "<h1>Calculate EMI</h1><form id='emiForm'>" +
            "<div class='form-group'><label>Loan Amount (&#8377;):</label><input type='number' id='principal' required></div>" +
            "<div class='form-group'><label>Interest Rate (% per annum):</label><input type='number' step='0.01' id='rate' required></div>" +
            "<div class='form-group'><label>Tenure (months):</label><input type='number' id='tenure' required></div>" +
            "<button type='submit'>Calculate</button></form>" +
            "<div id='result' class='result'><h3>Result:</h3>" +
            "<div class='result-item'>Monthly EMI: &#8377;<span id='emi'></span></div>" +
            "<div class='result-item'>Total Amount: &#8377;<span id='total'></span></div>" +
            "<div class='result-item'>Total Interest: &#8377;<span id='interest'></span></div></div>" +
            "<a href='/' class='back'>&#8592; Back to Home</a></div>" +
            "<script>document.getElementById('emiForm').addEventListener('submit',async(e)=>{e.preventDefault();" +
            "const data=new URLSearchParams({principal:document.getElementById('principal').value," +
            "rate:document.getElementById('rate').value,tenure:document.getElementById('tenure').value});" +
            "const res=await fetch('/api/calculate',{method:'POST',body:data});" +
            "const json=await res.json();if(json.success){document.getElementById('emi').textContent=json.emi.toFixed(2);" +
            "document.getElementById('total').textContent=json.totalAmount.toFixed(2);" +
            "document.getElementById('interest').textContent=json.totalInterest.toFixed(2);" +
            "document.getElementById('result').style.display='block';}});</script></body></html>";
    }

    private static String getHistoryPageHtml() {
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>Calculation History</title>" +
            "<style>body{font-family:Arial,sans-serif;max-width:1200px;margin:50px auto;padding:20px;background:#f5f5f5}" +
            ".container{background:white;padding:30px;border-radius:10px;box-shadow:0 2px 10px rgba(0,0,0,0.1)}" +
            "h1{color:#333;text-align:center}table{width:100%;border-collapse:collapse;margin:20px 0}" +
            "th,td{padding:12px;text-align:left;border-bottom:1px solid #ddd}" +
            "th{background:#007bff;color:white}.back{display:inline-block;margin-top:20px;color:#007bff;text-decoration:none}" +
            ".empty{text-align:center;padding:40px;color:#666}</style></head><body><div class='container'>" +
            "<h1>Calculation History</h1><div id='historyTable'></div>" +
            "<a href='/' class='back'>&#8592; Back to Home</a></div>" +
            "<script>fetch('/api/history').then(r=>r.json()).then(data=>{const div=document.getElementById('historyTable');" +
            "if(data.length===0){div.innerHTML='<div class=\"empty\">No calculation history found.</div>';return;}" +
            "let html='<table><tr><th>ID</th><th>Principal</th><th>Rate %</th><th>Tenure</th><th>EMI</th><th>Total Amount</th><th>Interest</th><th>Date</th></tr>';" +
            "data.forEach(c=>{html+=`<tr><td>${c.id}</td><td>&#8377;${c.principal.toFixed(2)}</td><td>${c.rate}%</td><td>${c.tenure}</td>" +
            "<td>&#8377;${c.emi.toFixed(2)}</td><td>&#8377;${c.totalAmount.toFixed(2)}</td><td>&#8377;${c.totalInterest.toFixed(2)}</td><td>${c.date}</td></tr>`;});" +
            "html+='</table>';div.innerHTML=html;});</script></body></html>";
    }
}
