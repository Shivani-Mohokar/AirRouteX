package com.airroutex.servlets;

import com.airroutex.controllers.BookingController;
import com.airroutex.controllers.RouteController;
import com.airroutex.models.Booking;
import com.airroutex.models.RouteResult;
import com.airroutex.models.User;
import com.airroutex.utils.JsonArray;
import com.airroutex.utils.JsonObject;
import com.airroutex.utils.SessionUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * BookingServlet.java
 * ------------------------------------------------------------------
 * GET  /AirRouteX/bookings  -> the logged-in user's booking history
 * POST /AirRouteX/bookings  -> create a new booking
 *
 * IMPORTANT SECURITY NOTE: when creating a booking, this servlet
 * does NOT trust any price/route/flight data the client might send.
 * Instead it re-runs the EXACT SAME search (source, destination,
 * optimizationType) server-side via RouteController, and books
 * whatever route that authoritative search returns. This prevents a
 * user from tampering with the request to book a route at a price
 * that was never actually offered.
 * ------------------------------------------------------------------
 */
@WebServlet("/bookings")
public class BookingServlet extends HttpServlet {

    private final RouteController routeController = new RouteController();
    private final BookingController bookingController = new BookingController();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
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

        BookingController.Result result = bookingController.getBookingHistory(user.getUserId());

        try (PrintWriter out = response.getWriter()) {
            if (!result.success) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JsonObject().put("error", result.errorMessage).toJson());
                return;
            }

            @SuppressWarnings("unchecked")
            List<Booking> bookings = (List<Booking>) result.data;

            JsonArray json = new JsonArray();
            for (Booking b : bookings) {
                json.add(JsonMappers.booking(b));
            }
            out.print(json.toJson());
        }
    }

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

        String source = request.getParameter("source");
        String destination = request.getParameter("destination");
        String optimizationType = request.getParameter("optimizationType");

        RouteController.SearchResult searchResult = routeController.search(source, destination, optimizationType);

        try (PrintWriter out = response.getWriter()) {
            if (!searchResult.success) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                out.print(new JsonObject().put("error", searchResult.errorMessage).toJson());
                return;
            }

            RouteResult routeResult = searchResult.routeResult;
            Booking.OptimizationType type = Booking.OptimizationType.valueOf(optimizationType.toUpperCase());

            BookingController.Result bookingResult = bookingController.createBooking(user.getUserId(), type, routeResult);

            if (!bookingResult.success) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.print(new JsonObject().put("error", bookingResult.errorMessage).toJson());
                return;
            }

            out.print(new JsonObject()
                    .put("message", "Booking confirmed")
                    .put("bookingId", (Integer) bookingResult.data)
                    .toJson());
        }
    }
}
