package io.github.company.servlets;

import java.io.IOException;
import java.util.Arrays;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.company.Database;
import io.github.mapepire_ibmi.types.QueryResult;

public class QueryServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private String sql;

    public QueryServlet(String sql) {
        super();
        this.sql = sql;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectNode jsonResponse = objectMapper.createObjectNode();

        try {
            if (!Database.getReadyJob()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.put("error", "No connection.");
                resp.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
                return;
            }

            String pathInfo = req.getPathInfo();

            QueryResult<Object> result = pathInfo == null ? Database.execute(this.sql).get()
                    : Database.prepareAndExecute(this.sql, Arrays.asList(pathInfo.substring(1))).get();

            if (result.getSuccess()) {
                resp.setStatus(HttpServletResponse.SC_OK);
                jsonResponse.set("message", objectMapper.valueToTree(result.getData()));
            } else {
                resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                jsonResponse.put("error", "No data found.");
            }
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Internal server error. Reason: " + e.toString());
        }

        resp.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
