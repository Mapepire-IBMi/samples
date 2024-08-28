package io.github.company.servlets;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.github.company.Database;
import io.github.mapapire.types.ServerTraceLevel;

public class TracingServlet extends HttpServlet {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        ObjectNode jsonResponse = objectMapper.createObjectNode();

        try {
            String level = req.getParameter("level");
            Database.setTraceLevel(ServerTraceLevel.fromValue(level));

            resp.setStatus(HttpServletResponse.SC_OK);
            jsonResponse.put("message", "Successfully set server trace level.");
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            jsonResponse.put("error", "Internal server error. Reason: " + e.toString());
        }

        resp.getWriter().write(objectMapper.writeValueAsString(jsonResponse));
    }
}
