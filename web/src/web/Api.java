package web;

import dao.*;
import model.*;
import web.WebServer.Ctx;
import web.WebServer.HttpError;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

import static web.WebServer.*;

/** All JSON endpoints. Every endpoint delegates to the existing DAO classes. */
public final class Api {
    private Api() {}

    private static final UserDAO users = new UserDAO();
    private static final DonationDAO donations = new DonationDAO();
    private static final RequestDAO requests = new RequestDAO();
    private static final EmergencyAidDAO emergencies = new EmergencyAidDAO();
    private static final NotificationDAO notifications = new NotificationDAO();
    private static final DistributionPlaceDAO places = new DistributionPlaceDAO();
    private static final DistributionScheduleDAO schedule = new DistributionScheduleDAO();

    private static final List<String> SOURCE_TYPES = Arrays.asList("individual", "organization", "government");
    private static final List<String> CATEGORIES = Arrays.asList("cooked", "raw", "packaged", "beverages", "dog_food", "other");
    private static final List<String> DAYS = Arrays.asList("monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday");
    private static final List<String> SLOTS = Arrays.asList("morning", "afternoon", "evening");

    // ---------- small helpers ----------
    private static Map<String, Object> m(Object... kv) {
        Map<String, Object> map = new LinkedHashMap<>();
        for (int i = 0; i < kv.length; i += 2) map.put(String.valueOf(kv[i]), kv[i + 1]);
        return map;
    }

    private static HttpError bad(String msg) { return new HttpError(400, msg); }
    private static HttpError notFound(String what) { return new HttpError(404, what + " not found"); }

    private static Map<String, Object> userMap(User u) {
        return m("id", u.getId(), "name", u.getName(), "email", u.getEmail(), "role", u.getRole(),
                "phone", u.getPhone(), "address", u.getAddress(), "active", u.isActive(),
                "createdAt", String.valueOf(u.getCreatedAt()));
    }

    private static Map<String, Object> donationMap(Donation d) {
        LocalDateTime now = LocalDateTime.now();
        boolean soon = d.getExpiryDate() != null && d.getExpiryDate().isAfter(now)
                && d.getExpiryDate().isBefore(now.plusHours(24));
        return m("id", d.getId(), "donorId", d.getDonorId(), "title", d.getTitle(), "description", d.getDescription(),
                "category", d.getCategory(), "quantity", d.getQuantity(), "unit", d.getUnit(),
                "expiryDate", d.getExpiryDate() == null ? null : d.getExpiryDate().toString(),
                "pickupAddress", d.getPickupAddress(), "sourceType", d.getSourceType(), "sourceName", d.getSourceName(),
                "status", d.getStatus(), "createdAt", String.valueOf(d.getCreatedAt()), "expiringSoon", soon);
    }

    private static Map<String, Object> requestMap(Request r) {
        return m("id", r.getId(), "donationId", r.getDonationId(), "recipientId", r.getRecipientId(),
                "status", r.getStatus(), "message", r.getMessage(), "createdAt", String.valueOf(r.getCreatedAt()),
                "donationTitle", r.getDonationTitle(), "donorName", r.getDonorName());
    }

    private static Map<String, Object> aidMap(EmergencyAid a) {
        return m("id", a.getId(), "recipientId", a.getRecipientId(), "recipientName", a.getRecipientName(),
                "title", a.getTitle(), "description", a.getDescription(), "emergencyType", a.getEmergencyType(),
                "urgency", a.getUrgency(), "peopleAffected", a.getPeopleAffected(), "location", a.getLocation(),
                "requiredItems", a.getRequiredItems(), "requiredQuantity", a.getRequiredQuantity(),
                "contactPhone", a.getContactPhone(), "status", a.getStatus(), "createdAt", String.valueOf(a.getCreatedAt()));
    }

    private static Map<String, Object> notifMap(Notification n) {
        return m("id", n.getId(), "message", n.getMessage(), "type", n.getType(), "read", n.isRead(),
                "relatedId", n.getRelatedId(), "createdAt", String.valueOf(n.getCreatedAt()));
    }

    private static Map<String, Object> placeMap(DistributionPlace p) {
        return m("id", p.getId(), "name", p.getName(), "category", p.getCategory(), "address", p.getAddress(),
                "contactPerson", p.getContactPerson(), "contactPhone", p.getContactPhone(),
                "operatingHours", p.getOperatingHours(), "capacity", p.getCapacity(), "notes", p.getNotes(),
                "active", p.isActive());
    }

    private static Map<String, Object> scheduleMap(DistributionSchedule s) {
        return m("id", s.getId(), "placeId", s.getPlaceId(), "placeName", s.getPlaceName(),
                "placeCategory", s.getPlaceCategory(), "dayOfWeek", s.getDayOfWeek(), "timeSlot", s.getTimeSlot(),
                "foodType", s.getFoodType(), "quantityTarget", s.getQuantityTarget(), "unit", s.getUnit(),
                "assignedVolunteer", s.getAssignedVolunteer(), "volunteerPhone", s.getVolunteerPhone(),
                "status", s.getStatus(), "notes", s.getNotes(), "recurring", s.isRecurring());
    }

    private static <T> List<Map<String, Object>> list(List<T> in, java.util.function.Function<T, Map<String, Object>> f) {
        List<Map<String, Object>> out = new ArrayList<>();
        for (T t : in) out.add(f.apply(t));
        return out;
    }

    private static Donation requireDonation(int id) {
        Donation d = donations.findById(id);
        if (d == null) throw notFound("Donation");
        return d;
    }

    private static void requireOwnerOrAdmin(Ctx c, int ownerId) {
        if (!"admin".equalsIgnoreCase(c.user.getRole()) && c.user.getId() != ownerId) {
            throw new HttpError(403, "This item belongs to another user");
        }
    }

    // ---------- routes ----------
    public static void register() {
        registerAuth();
        registerDonations();
        registerEmergency();
        registerNotifications();
        registerAdmin();
        registerDistribution();
    }

    private static void registerAuth() {
        pub("POST", "auth/login", c -> {
            String email = c.str("email"), password = c.str("password");
            if (email.isEmpty() || password.isEmpty()) throw bad("Please enter both email and password.");
            User u = users.login(email, password);
            if (u == null) throw new HttpError(401, "Invalid email or password.");
            if (!u.isActive()) throw new HttpError(403, "Your account is deactivated. Please contact admin.");
            String r = u.getRole().toLowerCase();
            if (!Arrays.asList("donor", "recipient", "admin").contains(r)) throw new HttpError(403, "Unknown role.");
            String sid = newSession(u);
            c.ex.getResponseHeaders().add("Set-Cookie", "sid=" + sid + "; Path=/; HttpOnly; SameSite=Lax");
            return m("user", userMap(u), "redirect", r + ".html");
        });

        pub("POST", "auth/register", c -> {
            String name = c.str("name"), email = c.str("email"), password = c.str("password");
            String role = c.str("role").toLowerCase();
            if (name.isEmpty() || email.isEmpty() || password.isEmpty()) throw bad("Please fill in all required fields.");
            if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) throw bad("Please enter a valid email address.");
            if (password.length() < 6) throw bad("Password must be at least 6 characters.");
            if (!role.equals("donor") && !role.equals("recipient")) throw bad("Role must be donor or recipient.");
            User u = new User();
            u.setName(name);
            u.setEmail(email);
            u.setPasswordHash(password); // UserDAO.register() hashes it with BCrypt
            u.setRole(role);
            u.setPhone(c.str("phone"));
            u.setAddress(c.str("address"));
            u.setActive(true);
            u.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            if (!users.register(u)) throw bad("Registration failed. Email might already be in use.");
            return m("ok", true);
        });

        pub("POST", "auth/logout", c -> {
            endSession(c.sid);
            c.ex.getResponseHeaders().add("Set-Cookie", "sid=; Path=/; Max-Age=0");
            return m("ok", true);
        });

        auth("GET", "auth/me", c -> m("user", userMap(c.user), "unread", notifications.countUnread(c.user.getId())));
    }

    private static void registerDonations() {
        auth("GET", "donations/available", c -> list(donations.findAvailable(), Api::donationMap));

        role("GET", "donations/mine", c -> list(donations.findByDonor(c.user.getId()), Api::donationMap), "donor");

        role("POST", "donations", c -> {
            String title = c.str("title"), cat = c.str("category"), unit = c.str("unit"), src = c.str("sourceType");
            if (title.isEmpty() || cat.isEmpty() || unit.isEmpty() || c.str("expiryDate").isEmpty() || src.isEmpty()) {
                throw bad("Please fill all required fields.");
            }
            if (!CATEGORIES.contains(cat)) throw bad("Unknown category.");
            if (!SOURCE_TYPES.contains(src)) throw bad("Unknown source type.");
            int qty = c.integer("quantity", 0);
            if (qty <= 0) throw bad("Quantity must be a positive number.");
            LocalDate day;
            try {
                day = LocalDate.parse(c.str("expiryDate"));
            } catch (DateTimeParseException e) {
                throw bad("Expiry date is invalid.");
            }
            Donation d = new Donation();
            d.setDonorId(c.user.getId());
            d.setTitle(title);
            d.setDescription(c.str("description"));
            d.setCategory(cat);
            d.setQuantity(qty);
            d.setUnit(unit);
            d.setExpiryDate(LocalDateTime.of(day, LocalTime.of(23, 59)));
            d.setPickupAddress(c.str("pickupAddress"));
            d.setSourceType(src);
            d.setSourceName(c.str("sourceName"));
            d.setStatus("available");
            d.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            if (!donations.create(d)) throw new HttpError(500, "Failed to create donation.");
            return m("ok", true);
        }, "donor");

        auth("GET", "donations/{id}", c -> {
            Donation d = requireDonation(c.id("id"));
            Map<String, Object> out = donationMap(d);
            String role = c.user.getRole().toLowerCase();
            if (role.equals("admin") || (role.equals("donor") && d.getDonorId() == c.user.getId())) {
                out.put("requests", Db.query("SELECT r.*, u.name AS recipient_name, u.phone AS recipient_phone "
                        + "FROM requests r JOIN users u ON r.recipient_id = u.id WHERE r.donation_id = ? "
                        + "ORDER BY r.created_at DESC", d.getId()));
            } else if (role.equals("recipient")) {
                out.put("myRequest", Db.one("SELECT id, status, message FROM requests WHERE donation_id = ? AND recipient_id = ? "
                        + "ORDER BY created_at DESC LIMIT 1", d.getId(), c.user.getId()));
            }
            return out;
        });

        role("POST", "donations/{id}/claim", c -> {
            Donation d = requireDonation(c.id("id"));
            if (!"available".equals(d.getStatus())) throw bad("This donation is no longer available.");
            if (d.getExpiryDate() != null && d.getExpiryDate().isBefore(LocalDateTime.now())) throw bad("This donation has expired.");
            Request r = new Request();
            r.setDonationId(d.getId());
            r.setRecipientId(c.user.getId());
            r.setStatus("pending");
            r.setMessage(c.str("message"));
            r.setCreatedAt(new Timestamp(System.currentTimeMillis()));
            if (!requests.create(r)) throw new HttpError(500, "Could not submit claim request. Please try again.");
            donations.updateStatus(d.getId(), "claimed");
            notifications.create(d.getDonorId(), c.user.getName() + " has requested your donation: " + d.getTitle(), "claim_received", d.getId());
            return m("ok", true);
        }, "recipient");

        role("POST", "donations/{id}/status", c -> {
            Donation d = requireDonation(c.id("id"));
            requireOwnerOrAdmin(c, d.getDonorId());
            String status = c.str("status");
            if (!Arrays.asList("picked_up", "completed", "cancelled").contains(status)) throw bad("Invalid status.");
            donations.updateStatus(d.getId(), status);
            Map<String, Object> acc = Db.one("SELECT recipient_id FROM requests WHERE donation_id = ? AND status = 'accepted' LIMIT 1", d.getId());
            if (acc != null) {
                notifications.create(((Number) acc.get("recipientId")).intValue(),
                        "Donation \"" + d.getTitle() + "\" is now " + status.replace('_', ' ') + ".", "donation_" + status, d.getId());
            }
            return m("ok", true);
        }, "donor", "admin");

        role("POST", "requests/{id}/accept", c -> decideRequest(c, true), "donor", "admin");
        role("POST", "requests/{id}/reject", c -> decideRequest(c, false), "donor", "admin");

        role("GET", "my/claims", c -> list(requests.findByRecipient(c.user.getId()), Api::requestMap), "recipient");
    }

    private static Object decideRequest(Ctx c, boolean accept) {
        Map<String, Object> r = Db.one("SELECT r.id, r.status, r.recipient_id, r.donation_id, d.donor_id, d.title, d.status AS donation_status "
                + "FROM requests r JOIN donations d ON r.donation_id = d.id WHERE r.id = ?", c.id("id"));
        if (r == null) throw notFound("Request");
        requireOwnerOrAdmin(c, ((Number) r.get("donorId")).intValue());
        if (!"pending".equals(r.get("status"))) throw bad("This request has already been handled.");
        int reqId = ((Number) r.get("id")).intValue();
        int donationId = ((Number) r.get("donationId")).intValue();
        int recipientId = ((Number) r.get("recipientId")).intValue();
        String title = String.valueOf(r.get("title"));
        if (accept) {
            requests.updateStatus(reqId, "accepted");
            donations.updateStatus(donationId, "accepted");
            notifications.create(recipientId, "Your request for \"" + title + "\" was accepted. Please arrange pickup.", "request_accepted", donationId);
        } else {
            requests.updateStatus(reqId, "rejected");
            if ("claimed".equals(r.get("donationStatus"))) donations.updateStatus(donationId, "available");
            notifications.create(recipientId, "Your request for \"" + title + "\" was declined.", "request_rejected", donationId);
        }
        return m("ok", true);
    }

    private static void registerEmergency() {
        auth("GET", "emergencies", c -> list(emergencies.findActive(), Api::aidMap));

        role("GET", "emergencies/mine", c -> list(emergencies.findByRecipient(c.user.getId()), Api::aidMap), "recipient");

        role("POST", "emergencies", c -> {
            String title = c.str("title"), type = c.str("emergencyType"), urgency = c.str("urgency");
            if (title.isEmpty() || type.isEmpty() || urgency.isEmpty()) throw bad("Please fill required fields.");
            if (!Arrays.asList("critical", "high", "medium").contains(urgency)) throw bad("Invalid urgency.");
            EmergencyAid a = new EmergencyAid();
            a.setRecipientId(c.user.getId());
            a.setTitle(title);
            a.setDescription(c.str("description"));
            a.setEmergencyType(type);
            a.setUrgency(urgency);
            a.setPeopleAffected(Math.max(1, c.integer("peopleAffected", 1)));
            a.setLocation(c.str("location"));
            a.setRequiredItems(c.str("requiredItems"));
            a.setRequiredQuantity(Math.max(0, c.integer("requiredQuantity", 0)));
            a.setContactPhone(c.str("contactPhone"));
            a.setStatus("open");
            if (!emergencies.create(a)) throw new HttpError(500, "Failed to submit request.");
            return m("ok", true);
        }, "recipient");

        // Donors / admins pledge help. This implements the "Respond" button that was a stub in the desktop app.
        role("POST", "emergencies/{id}/respond", c -> {
            Map<String, Object> a = Db.one("SELECT * FROM emergency_aids WHERE id = ?", c.id("id"));
            if (a == null) throw notFound("Emergency request");
            String st = String.valueOf(a.get("status"));
            if (!Arrays.asList("open", "responding", "partially_fulfilled").contains(st)) throw bad("This request is no longer active.");
            if ("open".equals(st)) emergencies.updateStatus(c.id("id"), "responding");
            String note = c.str("message");
            notifications.create(((Number) a.get("recipientId")).intValue(),
                    c.user.getName() + " has pledged help for \"" + a.get("title") + "\"" + (note.isEmpty() ? "." : ": " + note),
                    "emergency_response", c.id("id"));
            return m("ok", true);
        }, "donor", "admin");

        role("POST", "emergencies/{id}/status", c -> {
            Map<String, Object> a = Db.one("SELECT * FROM emergency_aids WHERE id = ?", c.id("id"));
            if (a == null) throw notFound("Emergency request");
            requireOwnerOrAdmin(c, ((Number) a.get("recipientId")).intValue());
            String status = c.str("status");
            if (!Arrays.asList("open", "responding", "partially_fulfilled", "fulfilled", "closed").contains(status)) throw bad("Invalid status.");
            emergencies.updateStatus(c.id("id"), status);
            return m("ok", true);
        }, "recipient", "admin");
    }

    private static void registerNotifications() {
        auth("GET", "notifications", c -> list(notifications.findByUser(c.user.getId()), Api::notifMap));

        auth("POST", "notifications/read-all", c -> {
            notifications.markAllRead(c.user.getId());
            return m("ok", true);
        });

        auth("POST", "notifications/{id}/read", c -> {
            Map<String, Object> n = Db.one("SELECT user_id FROM notifications WHERE id = ?", c.id("id"));
            if (n == null || ((Number) n.get("userId")).intValue() != c.user.getId()) throw notFound("Notification");
            notifications.markRead(c.id("id"));
            return m("ok", true);
        });
    }

    private static void registerAdmin() {
        role("GET", "analytics", c -> {
            Map<String, Integer> counts = donations.countBySourceType();
            int total = 0;
            for (int v : counts.values()) total += v;
            List<Map<String, Object>> bySource = new ArrayList<>();
            Set<String> order = new LinkedHashSet<>(SOURCE_TYPES);
            order.addAll(counts.keySet());
            for (String s : order) {
                int n = counts.getOrDefault(s, 0);
                bySource.add(m("source", s == null ? "unknown" : s, "count", n, "percent", total == 0 ? 0 : Math.round(n * 1000.0 / total) / 10.0));
            }
            return m("total", total, "bySource", bySource, "monthly", donations.countMonthlyBySource());
        }, "admin");

        role("GET", "admin/stats", c -> m(
                "users", users.findAll().size(),
                "donations", donations.countAll(),
                "requests", requests.countAll(),
                "activeEmergencies", emergencies.findActive().size(),
                "usersByRole", users.countByRole()), "admin");

        role("GET", "admin/users", c -> list(users.findAll(), Api::userMap), "admin");

        role("POST", "admin/users/{id}/active", c -> {
            int id = c.id("id");
            if (id == c.user.getId()) throw bad("You cannot deactivate your own account.");
            if (users.findById(id) == null) throw notFound("User");
            users.updateActive(id, c.bool("active", true));
            return m("ok", true);
        }, "admin");

        role("GET", "admin/donations", c -> Db.query("SELECT d.*, u.name AS donor_name FROM donations d "
                + "LEFT JOIN users u ON d.donor_id = u.id ORDER BY d.created_at DESC"), "admin");

        role("GET", "admin/requests", c -> Db.query("SELECT r.*, d.title AS donation_title, u.name AS recipient_name "
                + "FROM requests r JOIN donations d ON r.donation_id = d.id JOIN users u ON r.recipient_id = u.id "
                + "ORDER BY r.created_at DESC"), "admin");
    }

    private static void registerDistribution() {
        auth("GET", "places", c -> list(places.findAll(), Api::placeMap));

        role("POST", "places", c -> {
            if (c.str("name").isEmpty() || c.str("category").isEmpty()) throw bad("Name and category are required.");
            DistributionPlace p = new DistributionPlace();
            p.setName(c.str("name"));
            p.setCategory(c.str("category"));
            p.setAddress(c.str("address"));
            p.setContactPerson(c.str("contactPerson"));
            p.setContactPhone(c.str("contactPhone"));
            p.setOperatingHours(c.str("operatingHours"));
            p.setCapacity(Math.max(0, c.integer("capacity", 0)));
            p.setNotes(c.str("notes"));
            p.setActive(true);
            if (!places.create(p)) throw new HttpError(500, "Could not save place.");
            return m("ok", true);
        }, "admin");

        role("POST", "places/{id}/active", c -> {
            DistributionPlace p = places.findById(c.id("id"));
            if (p == null) throw notFound("Place");
            p.setActive(c.bool("active", true));
            places.update(p);
            return m("ok", true);
        }, "admin");

        auth("GET", "schedule", c -> list(schedule.findAll(), Api::scheduleMap));
        auth("GET", "schedule/today", c -> list(schedule.findToday(), Api::scheduleMap));

        role("POST", "schedule", c -> {
            int placeId = c.integer("placeId", 0);
            String day = c.str("dayOfWeek").toLowerCase(), slot = c.str("timeSlot").toLowerCase();
            if (places.findById(placeId) == null) throw bad("Please choose a place.");
            if (!DAYS.contains(day) || !SLOTS.contains(slot)) throw bad("Please choose a valid day and time slot.");
            if (c.str("foodType").isEmpty()) throw bad("Food type is required.");
            DistributionSchedule s = new DistributionSchedule();
            s.setPlaceId(placeId);
            s.setDayOfWeek(day);
            s.setTimeSlot(slot);
            s.setFoodType(c.str("foodType"));
            s.setQuantityTarget(Math.max(0, c.integer("quantityTarget", 0)));
            s.setUnit(c.str("unit"));
            s.setAssignedVolunteer(c.str("assignedVolunteer"));
            s.setVolunteerPhone(c.str("volunteerPhone"));
            s.setNotes(c.str("notes"));
            s.setStatus("scheduled");
            s.setRecurring(true);
            if (!schedule.create(s)) throw new HttpError(500, "Could not save schedule.");
            return m("ok", true);
        }, "admin");

        role("POST", "schedule/{id}/status", c -> {
            String st = c.str("status");
            if (!Arrays.asList("scheduled", "assigned", "in_transit", "delivered", "completed").contains(st)) throw bad("Invalid status.");
            schedule.updateStatus(c.id("id"), st);
            return m("ok", true);
        }, "admin");

        role("DELETE", "schedule/{id}", c -> {
            schedule.delete(c.id("id"));
            return m("ok", true);
        }, "admin");
    }
}
