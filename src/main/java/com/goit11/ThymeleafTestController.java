package com.goit11;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

@WebServlet("/time")
public class ThymeleafTestController extends HttpServlet {
    private TemplateEngine engine;
    Cookie cookie;
    int utc;
    String valueCookie;

    @Override
    public void init() throws ServletException {
        engine = new TemplateEngine();
        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix("C:\\Users\\skin\\IdeaProjects\\GoIt11\\Gradle\\Thym\\templates\\");

        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        Map<String, String[]> parameterMap = req.getParameterMap();
        Map<String, Object> params = new LinkedHashMap<>();
        for (Map.Entry<String, String[]> keyValue : parameterMap.entrySet()) {
            params.put(keyValue.getKey(), keyValue.getValue()[0]);
        }
        String initTime;
        if (!params.isEmpty()){
            String encode = params.get("timezone").toString();
            valueCookie = URLEncoder.encode(encode, StandardCharsets.UTF_8);
            cookie = new Cookie("lastTimezone",valueCookie);
            resp.addCookie(cookie);
            utc = Integer.parseInt(valueCookie.substring(3));
                initTime = LocalDateTime.now()
                        .plusHours(utc)
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss ")) + valueCookie;
        } else if (cookie!=null){
                initTime = LocalDateTime.now()
                        .plusHours(utc)
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss ")) + valueCookie;
        }else{
            initTime = LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("dd-MM-yyyy, HH:mm:ss ")) + "UTC";
        }

        Context simpleContext = new Context(
                    req.getLocale(),
                    Map.of("queryParams", initTime)
            );
            engine.process("test", simpleContext, resp.getWriter());
            resp.getWriter().close();
    }
}
