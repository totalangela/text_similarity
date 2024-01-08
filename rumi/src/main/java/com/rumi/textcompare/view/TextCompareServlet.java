package com.rumi.textcompare.view;

import com.google.gson.Gson;
import com.rumi.textcompare.controller.TextComparator;
import com.rumi.textcompare.model.TextSimilarity;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class TextCompareServlet extends HttpServlet {

    private final TextComparator textComparator = new TextComparator();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        resp.setContentType("application/json");
        List<TextSimilarity> result;
        try {
            result = textComparator.compare(requestBody.toString());
        } catch (Exception e) {
            throw new ServletException(e.getMessage());
        }

        if (result == null) {
            throw new ServletException("Internal Server Error");
        }

        resp.getWriter().println(new Gson().toJson(result));
    }
}
