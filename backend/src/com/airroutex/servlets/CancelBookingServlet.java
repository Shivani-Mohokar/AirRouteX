package com.airroutex.servlets;

import com.airroutex.controllers.BookingController;
import com.airroutex.models.User;
import com.airroutex.utils.JsonObject;
import com.airroutex.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * CancelBookingServlet.java
 * ------------------------------------------------------------------
 * POST /AirRouteX/bookings/cancel
 *   params: bookingId
 *
 * BookingDAO.cancelBooking() filters by BOTH booking_id AND user_id,
 * so a user can never cancel someone else's booking even by guessing
 * an ID - this check happens at the SQL level, not just in Java.
 * ------------------------------------------------------------------
 */
@WebServlet("/bookings/cancel")
public class CancelBookingServlet extends HttpServlet {

    private final BookingController bookingController = new BookingController();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = SessionUtil.getLoggedInUser(request);
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            try (PrintWriter out = response.getWriter()) {
                out.print(new JsonObject().put("error", "Please log in first").toJson());
            }
            return;
        }

        try (PrintWriter out = response.getWriter()) {
            String bookingIdParam = request.getParameter("bookingId");
            if (bookingIdParam == null) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", "bookingId is required").toJson());
                return;
            }

            int bookingId = Integer.parseInt(bookingIdParam);
            BookingController.Result result = bookingController.cancelBooking(bookingId, user.getUserId());

            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }

            out.print(new JsonObject().put("message", "Booking cancelled").toJson());
        }
    }
}
