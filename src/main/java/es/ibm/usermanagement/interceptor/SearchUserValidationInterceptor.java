package es.ibm.usermanagement.interceptor;

import es.ibm.usermanagement.exception.custom.InvalidParamException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Pattern;

@Component
public class SearchUserValidationInterceptor implements HandlerInterceptor {

    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+([\\s'-][A-ZÁÉÍÓÚÑa-záéíóúñ]+)*$");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        if (request.getRequestURI().contains("/api/v1/users/search") && request.getMethod().equals("GET")) {
            String ageParam = request.getParameter("age");
            String nameParam = request.getParameter("name");

            if (ageParam != null) {
                try {
                    int age = Integer.parseInt(ageParam);
                    if (age < 1 || age > 150) {
                        throw new InvalidParamException("Age must be between 1 and 150", "age");
                    }
                } catch (NumberFormatException e) {
                    throw new InvalidParamException( "Age must be a number","age");
                }
            }
            if (nameParam != null && !NAME_PATTERN.matcher(nameParam).matches()) {
                throw new InvalidParamException("Invalid name format", "name");
            }
        }
        return true;
    }
}
