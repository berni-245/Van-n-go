<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Driver Availability</title>
    <%@include file="../lib/bootstrap_css.jsp" %>
    <%@include file="../lib/bootstrap_js.jsp" %>
    <jsp:include page="../lib/tom_select.jsp"/>
    <c:url value="/js/availability.js" var="js"/>
    <c:url value="/css/availability_styles.css" var="css"/>
    <script src="${js}"></script>
    <link rel="stylesheet" href="${css}">
    <script src="https://code.jquery.com/jquery-3.5.1.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.bundle.min.js"></script>
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</head>
<body>

<div class="header-button">
    <a href="${pageContext.request.contextPath}/availability/" class="btn btn-primary">Atrás</a>
</div>


<h1 class="page-title"><c:out value="${driver.username}"/></h1>


<div class="calendar-container mt-5">
    <div id="calendar"></div>
</div>

<div id="confirmForm">
    <div class="form-control mt-5">
        <form action="${pageContext.request.contextPath}/availability/contact"
              method="post">
            <input type="hidden" name="clientName" value="${loggedUser.username}">
            <input type="hidden" name="clientMail" value="${loggedUser.mail}">
            <label for="jobDescription"><spring:message code="form.jobDescription"/></label>
            <textarea id="jobDescription" name="jobDescription" rows="4"
                      cols="50" required></textarea>
            <input type="hidden" name="driverMail" value="${driver.mail}"/>
            <input type="hidden" name="driverName" value="${driver.username}"/>
            <input  id="bookingDate" type="hidden" name="bookingDate" value=""/>
            <button type="submit" class="btn btn-primary mt-2">Reservar</button>
        </form>
    </div>
</div>



<script type="text/javascript">
    var reservedDates = [
        <c:forEach var="booking" items="${bookings}">
        "<c:out value='${booking.date}'/>",
        </c:forEach>
    ];

    var daysOfWeekMap = {
        'Sunday': 0,
        'Monday': 1,
        'Tuesday': 2,
        'Wednesday': 3,
        'Thursday': 4,
        'Friday': 5,
        'Saturday': 6
    };


    var workingDays = [
        <c:forEach var="workDay" items="${workingDays}">
        "<c:out value='${workDay}'/>",
        </c:forEach>
    ]
    workingDays = [...new Set(workingDays)];
    var visibleDays = workingDays.map(function(day) {
        return daysOfWeekMap[day];
    });
    var allDays = [0, 1, 2, 3, 4, 5, 6];
    var hiddenDays = allDays.filter(function(dayIndex) {
        return !visibleDays.includes(dayIndex);
    });


    let selectedDate = null;
    document.addEventListener('DOMContentLoaded', function () {
        var calendarEl = document.getElementById('calendar');
        var today = new Date();


        var maxDate = new Date();
        maxDate.setMonth(today.getMonth() + 2);

        var daysOfWeekMap = {
            'Sunday': 0,
            'Monday': 1,
            'Tuesday': 2,
            'Wednesday': 3,
            'Thursday': 4,
            'Friday': 5,
            'Saturday': 6
        };



        calendar = new FullCalendar.Calendar(calendarEl, {
            themeSystem: 'bootstrap5',
            fixedWeekCount: false,
            showNonCurrentDates: false,
            initialView: 'dayGridMonth', // Vista mensual
            validRange: {
                start: today.toISOString().split('T')[0],
                end: maxDate.toISOString().split('T')[0]
            },
            headerToolbar: {
                left: 'prev,next',
                center: 'title',
                right: 'dayGridMonth'
            },
            hiddenDays: hiddenDays,



            datesSet: function(dateInfo) {
                var prevButton = document.querySelector('.fc-prev-button');
                var currentDate = new Date();

                prevButton.disabled = dateInfo.start <= currentDate;
            },

            events: reservedDates.map(function (date){
                return{
                    start: date,
                    display: 'background',
                    backgroundColor: 'red'
                }
            }),

            dateClick: function(info) {
                if (reservedDates.includes(info.dateStr)) {
                    alert("Este día ya está reservado. Por favor, selecciona otro día.");
                    document.getElementById('confirmForm').style.display = 'none';
                    return; ///hay que cambiarlo pr un modal!!!
                }

                updateSelected(info.dayEl)

                document.getElementById('confirmForm').style.display = 'block';
                document.getElementById('bookingDate').value = info.dateStr;
            }

        });

        calendar.render();
    });

        function updateSelected(newDay){
            if(selectedDate != null ){
                selectedDate.style.backgroundColor = 'white';
            }
            newDay.style.backgroundColor = 'blue';
            selectedDate = newDay;
        }
</script>




</body>
</html>
