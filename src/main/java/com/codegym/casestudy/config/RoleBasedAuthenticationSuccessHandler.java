package com.codegym.casestudy.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Class này xử lý logic sau khi người dùng đăng nhập thành công.
 * Chức năng chính: Chuyển hướng người dùng đến trang phù hợp dựa trên vai trò (Role) của họ.
 * - ROLE_ADMIN -> /admin/dashboard (hoặc /admin/products)
 * - ROLE_USER  -> /home
 */
@Component
public class RoleBasedAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Không chuyển hướng nếu response đã được commit
        if (response.isCommitted()) {
            return;
        }

        // Xác định URL đích dựa trên vai trò
        String targetUrl = determineTargetUrl(authentication);

        // Thực hiện chuyển hướng
        redirectStrategy.sendRedirect(request, response, targetUrl);

        // Dọn dẹp session
        clearAuthenticationAttributes(request);
    }

    /**
     * Xác định URL chuyển hướng dựa trên vai trò của người dùng.
     * Sử dụng Java Stream để code ngắn gọn và dễ đọc hơn.
     */
    protected String determineTargetUrl(Authentication authentication) {
        // Kiểm tra xem người dùng có vai trò 'ROLE_ADMIN' hay không
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));

        if (isAdmin) {
            return "/admin/product-list"; // Trang quản trị cho Admin
        } else {
            return "/index"; // Trang chủ cho User
        }
    }

    /**
     * Xóa các thuộc tính xác thực tạm thời khỏi session.
     * (ví dụ: thông báo lỗi từ lần đăng nhập thất bại trước đó)
     */
    protected void clearAuthenticationAttributes(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
        }
    }
}