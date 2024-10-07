<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<comp:Head titleCode="components.header.availability">
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
    <%--TODO: Move to css file--%>
    <c:url value="/css/weekdaySelector.css" var="css"/>
    <link rel="stylesheet" href="${css}">
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

        .reserved {
            background: red;
            pointer-events: none;
        }

        .available {
            cursor: pointer;
        }

        .accordion-button:disabled {
            opacity: 0.5;
        }
    </style>
</comp:Head>
<body>
<comp:Header/>
<h1 class="text-center mb-5">
    <spring:message code="generic.word.${sizeLowerCase}" var="sizeMsg"/>
    <spring:message
            code="generic.phrase.userAvailability"
            arguments="${driver.username},${zone.neighborhoodName},${sizeMsg}"
    />
</h1>

<div class="row">
    <div class="calendar-container">
        <div id="calendar"></div>
    </div>

    <div id="confirmForm" class="form-control">
        <div class="accordion" id="accordionExample">
            <c:forEach var="v" items="${vehicles}">
                <div class="accordion-item">
                    <h2 class="accordion-header">
                        <button id="ab-${v.plateNumber}"
                                class="accordion-button collapsed"
                                type="button" data-bs-toggle="collapse"
                                data-bs-target="#${v.plateNumber}" aria-expanded="false"
                                aria-controls="${v.plateNumber}"
                                vehicleId="${v.id}" plateNumber="${v.plateNumber}"
                                disabled
                        >
                            <strong><c:out value="${v.plateNumber}"/></strong> -
                            <c:out value="${v.volume}"/>m&sup3
                        </button>
                    </h2>
                    <div id="${v.plateNumber}" class="accordion-collapse collapse"
                         data-bs-parent="#accordionExample">
                        <div class="accordion-body">
                            <figure class="figure">
                                <c:if test="${v.img <= 0}">
                                    <c:url value='/images/defaultVehicle.png' var="imgUrl"/>
                                    <img id="vehicleImagePreview" src="${imgUrl}"
                                         alt=" Vehicle Image Preview" class="card-img-top"/>
                                </c:if>
                                <c:if test="${v.img > 0}">
                                    <c:url value='/vehicle/image?vehicleId=${v.id}' var="imgUrl"/>
                                    <img id="vehicleImagePreview" src="${imgUrl}"
                                         alt="Vehicle Image Preview" class="card-img-top"/>
                                </c:if>
                                <figcaption class="figure-caption">
                                    <c:out value="${v.description}"/>
                                </figcaption>
                            </figure>
                            <c:url var="postUrl"
                                   value="/availability/${driverId}?size=${size}"/>
                            <form:form action="${postUrl}" method="post" modelAttribute="bookingForm">
                                <input type="hidden" name="vehicleId" value="${v.id}">
                                <input type="hidden" name="zoneId" value="${zone.id}">
                                <input id="bookingDate" type="hidden" name="date" value=""/>
                                <%--                                <input type="hidden" name="timeStart" value=""/>--%>
                                <%--                                <input type="hidden" name="timeEnd" value=""/>--%>


                                <div>
                                    <label for="hb-availability">
                                        <spring:message code="components.header.availability"/>
                                    </label>
                                    <div id="hb-availability" class="row weekday-toggle-group mt-3 fs-6">
                                        <c:forEach var="i" begin="0" end="23">
                                            <comp:SquareToggleButton
                                                    path="timeStart"
                                                    radio="true"
                                                    id="hb-${i}-${v.plateNumber}"
                                                    labelClass="fs-6"
                                                    inputClass="${v.plateNumber}"
                                                    content="${i} - ${i < 23 ? i + 1 : 0}"
                                                    value="${i < 10 ? 0 : \"\"}${i}:00:00"
                                            />
                                        </c:forEach>
                                    </div>
                                </div>

                                <div class="mt-4">
                                    <label for="jobDescription">
                                        <spring:message code="form.jobDescription"/>
                                    </label>
                                    <textarea id="jobDescription" name="jobDescription"
                                              rows="4" cols="50" required></textarea>
                                </div>
                                <button type="submit" class="btn btn-primary mt-2">
                                    <spring:message code="components.availability.Reserve"/>
                                </button>
                            </form:form>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </div>
    </div>
    <comp:ToastManager toasts="${toasts}"/>
</div>

<script type="text/javascript">
    <%--const reservedDates = [--%>
    <%--    <c:forEach var="booking" items="${bookings}">--%>
    <%--    <c:if test="${booking.confirmed}">--%>
    <%--    "<c:out value='${booking.date}'/>",--%>
    <%--    </c:if>--%>
    <%--    </c:forEach>--%>
    <%--];--%>

    let workingDays = [
        <c:forEach var="workDay" items="${workingDays}">
        ${workDay},
        </c:forEach>
    ];
    const allDays = [0, 1, 2, 3, 4, 5, 6];
    const hiddenDays = allDays.filter((dayIndex) => !workingDays.includes(dayIndex));

    let selectedDate = null;
    let selectedDateString = null;
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

            // events: reservedDates.map(function (date) {
            //     return {
            //         start: date,
            //         display: 'background',
            //         backgroundColor: 'red'
            //     }
            // }),

            dateClick: function (info) {
                // if (reservedDates.includes(info.dateStr)) {
                //     // alert("Este día ya está reservado. Por favor, selecciona otro día.");
                //     // document.getElementById('confirmForm').style.display = 'none';
                //     return;
                // }

                updateSelected(info);

                document.getElementById('confirmForm').style.display = 'block';
                document.querySelectorAll('input[name="date"]').forEach(
                    input => input.value = info.dateStr
                );
            }

        });

        calendar.render();
    });

    const vehicles = [
        <c:forEach var="v" items="${vehicles}">
        ${v.toJson()},
        </c:forEach>
    ]
    const accordionButtons = document.querySelectorAll('.accordion-button');

    function updateSelected(newDay) {
        if (selectedDate != null) {
            selectedDate.classList.remove('active-cell')
        }
        newDay.dayEl.classList.add('active-cell')
        selectedDate = newDay.dayEl;
        selectedDateString = newDay.date.toISOString().slice(0, 10);
        const weekDay = newDay.date.getDay();
        vehicles.forEach(v => {
            const accordionButton = document.getElementById('ab-' + v.plateNumber);
            const accordionBody = document.getElementById(v.plateNumber);
            if (v.weeklyAvailability.some(wa => wa.weekDay === weekDay)) {
                accordionButton.disabled = false;
            } else {
                accordionButton.disabled = true;
                accordionButton.classList.add('collapsed');
                accordionBody.classList.remove('show');
            }
        });
        updateActiveAvailability();
    }

    function updateActiveAvailability() {
        const activeVehicleBtns = document.querySelectorAll('button.accordion-button:not(:disabled)')
        activeVehicleBtns.forEach(btn => {
            const plateNumber = btn.getAttribute('plateNumber');
            const vehicleId = btn.getAttribute('vehicleId');
            const path = '/availability/active';
            const url = new URL(path, window.location.origin);
            url.searchParams.append('vehicleId', vehicleId);
            url.searchParams.append('zoneId', ${zone.id});
            url.searchParams.append('date', selectedDateString);
            fetch(url.toString()).then(res => res.json()).then(activeAv => {
                const hourBlockInputs = Array.from(
                    document.querySelectorAll('input[name="timeStart"]' + '.' + plateNumber)
                );
                hourBlockInputs.forEach(input => input.disabled = true);
                activeAv.forEach(av => {
                    const input = hourBlockInputs.find(input => input.value === av.hourInterval.startHourString);
                    if (input) input.disabled = false;
                })
            })
        })
    }
</script>

</body>
</html>
