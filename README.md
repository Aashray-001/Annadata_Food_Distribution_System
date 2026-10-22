# Annadata - Food Donation & Distribution System

A web application (HTML + CSS + JavaScript front end, Java back end, MySQL database) for managing
food donations, recipient claims, emergency aid requests and daily food distribution services.

## Features
- **Role-based login** - Donor, Recipient (NGO / shelter / individual) and Admin, BCrypt password hashing.
- **Donation lifecycle** - available > claimed > accepted > picked up > completed, with expiry warnings (24 h).
- **Source tracking & analytics** - every donation records Individual / Organization / Government; admin sees bar charts and a monthly trend.
- **Emergency aid** - recipients post critical / high / medium requests, donors and admins pledge help.
- **Distribution services** - places (dog food, old age homes, temples, orphanages, kitchens, hospitals, schools) and a Monday-Sunday x Morning/Afternoon/Evening weekly schedule with today's panel.
- **Notifications** - unread badge, click to mark read, mark all read.

## Project structure
```
food-donation-system/
|-- compile.bat / run.bat / setup.bat
|-- lib/                     jbcrypt, mysql-connector-j (no JavaFX)
|-- sql/                     schema.sql + dumps/ (live data, per table)
|-- src/
|   |-- model/               data classes (User, Donation, Request, ...)
|   |-- dao/                 JDBC data access (one DAO per table)
|   |-- db/                  DatabaseConnection, DatabaseInitializer
|   `-- util/PasswordUtil    BCrypt helper
`-- web/
    |-- src/web/             WebServer (HTTP + sessions), Api (JSON endpoints), Json, Db
    `-- public/              index.html, donor/recipient/admin.html, css/style.css, js/*.js
```

## How it works
Browser (HTML/CSS/JS) -> `fetch('/api/...')` -> `web.Api` handlers -> DAO classes -> MySQL.
`WebServer` also serves the static pages and keeps a login session per browser (cookie).

## Setup
1. Install JDK 11+ and MySQL 8 (port 3306, user `root`, password `root`).
2. Double-click `setup.bat` - creates `food_donation_db`, imports `sql/dumps`, compiles the project.
   (Different credentials: `set DB_URL=...`, `set DB_USER=...`, `set DB_PASSWORD=...` first.)
3. Double-click `run.bat` and open **http://localhost:8080** (`set PORT=8081` to change the port).

After changing Java code run `compile.bat` again. HTML/CSS/JS changes only need a browser refresh.

## Ready-to-use accounts
| Role | Email | Password |
|---|---|---|
| Admin | `admin@foodshare.com` | `admin123` |
| Donor | `donor@foodshare.com` | `donor123` |
| Recipient | `recipient@foodshare.com` | `recipient123` |

You can also register new donor / recipient accounts on the login page.

## Manual import of the database (alternative to setup.bat)
```cmd
mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS food_donation_db;"
mysql -u root -proot food_donation_db < sql\dumps\food_donation_db_users.sql
```
...repeat for each file in `sql\dumps\` (users, distribution_places, donations, requests, emergency_aids, notifications, distribution_schedule, routines).

## Notes
- The earlier JavaFX desktop version was removed from the project and kept in `_javafx_removed/` (safe to delete).
- Requests are processed one at a time because the DAOs share one JDBC connection.
"# Annadata_Food_Distribution_System" 
