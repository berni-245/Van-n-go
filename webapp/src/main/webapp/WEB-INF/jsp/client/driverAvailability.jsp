<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<comp:Head titleCode="components.header.availability" bootstrapjs="true">
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
    <%-- Move to css file--%>
    <style>
        .calendar-container {
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
        }

        #confirmForm {
            max-width: 600px;
            margin: 50px auto;
            padding: 20px;
            border: 1px solid #ddd;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
        }

        #confirmForm textarea {
            width: 100%;
            padding: 10px;
            margin-top: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            resize: none;
        }

        .fc .fc-daygrid-day.active-cell {
            background-color: var(--bs-primary-bg-subtle)
        }

        .fc-day:not(.fc-day-disabled):not(.active-cell):hover {
            background-color: var(--bs-tertiary-bg)
        }
    </style>
</comp:Head>
<body>
<comp:Header/>
<h1 class="text-center"><c:out value="${driver.username}"/></h1>

<div class="calendar-container mt-5">
    <div id="calendar"></div>
</div>

<div id="confirmForm" class="form-control mt-5">
    <form action="${pageContext.request.contextPath}/availability/contact"
          method="post">
        <input type="hidden" name="clientName" value="${loggedUser.username}">
        <input type="hidden" name="clientMail" value="${loggedUser.mail}">
        <label for="jobDescription"><spring:message code="form.jobDescription"/></label>
        <textarea id="jobDescription" name="jobDescription" rows="4"
                  cols="50" required></textarea>
        <input type="hidden" name="driverMail" value="${driver.mail}"/>
        <input type="hidden" name="driverName" value="${driver.username}"/>
        <input id="bookingDate" type="hidden" name="bookingDate" value=""/>
        <button type="submit" class="btn btn-primary mt-2">
            <spring:message code="components.availability.Reserve"/>
        </button>
    </form>
</div>

<script type="text/javascript">
    const reservedDates = [
        <c:forEach var="booking" items="${bookings}">
        "<c:out value='${booking.date}'/>",
        </c:forEach>
    ];

    const daysOfWeekMap = {
        'Sunday': 0,
        'Monday': 1,
        'Tuesday': 2,
        'Wednesday': 3,
        'Thursday': 4,
        'Friday': 5,
        'Saturday': 6
    };

    let workingDays = [
        <c:forEach var="workDay" items="${workingDays}">
        "<c:out value='${workDay}'/>",
        </c:forEach>
    ];
    workingDays = [...new Set(workingDays)];
    const visibleDays = workingDays.map(function (day) {
        return daysOfWeekMap[day];
    });
    const allDays = [0, 1, 2, 3, 4, 5, 6];
    const hiddenDays = allDays.filter(function (dayIndex) {
        return !visibleDays.includes(dayIndex);
    });

    let selectedDate = null;
    document.addEventListener('DOMContentLoaded', function () {
        const calendarEl = document.getElementById('calendar');
        const today = new Date();

        const maxDate = new Date();
        maxDate.setMonth(today.getMonth() + 2);

        calendar = new FullCalendar.Calendar(calendarEl, {
            themeSystem: 'bootstrap5',
            fixedWeekCount: false,
            showNonCurrentDates: false,
            initialView: 'dayGridMonth',
            validRange: {
                start: today.toISOString().split('T')[0],
                end: maxDate.toISOString().split('T')[0]
            },
            headerToolbar: {
                left: 'prev,next',
                center: 'title',
                right: 'dayGridMonth'
            },
            hiddenDays,

            datesSet: function (dateInfo) {
                const prevButton = document.querySelector('.fc-prev-button');
                const currentDate = new Date();

                prevButton.disabled = dateInfo.start <= currentDate;
            },

            events: reservedDates.map(function (date) {
                return {
                    start: date,
                    display: 'background',
                    backgroundColor: 'red'
                }
            }),

            dateClick: function (info) {
                if (reservedDates.includes(info.dateStr)) {
                    alert("Este día ya está reservado. Por favor, selecciona otro día.");
                    document.getElementById('confirmForm').style.display = 'none';
                    return;
                }

                updateSelected(info.dayEl)

                document.getElementById('confirmForm').style.display = 'block';
                document.getElementById('bookingDate').value = info.dateStr;
            }

        });

        calendar.render();
    });

    function updateSelected(newDay) {
        if (selectedDate != null) {
            selectedDate.classList.remove('active-cell')
        }
        newDay.classList.add('active-cell')
        selectedDate = newDay;
    }
</script>

</body>
</html>
