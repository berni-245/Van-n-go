<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.availability">
    <script src='https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js'></script>
    <%--TODO: Move to css file--%>
    <style>
        .calendar-container {
            max-width: 800px;
            overflow: hidden;
            padding-bottom: 1rem;
            margin: 0 auto;
            border-radius: 8px;
            box-sizing: border-box;
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

        .fc-daygrid-day {
            cursor: pointer;
        }

        .fc .fc-daygrid-day.active-cell {
            background-color: var(--bs-primary-bg-subtle);
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

        .shift-period-button {
            width: 100px;
            padding: 10px;
            text-align: center;
        }
    </style>
</comp:Head>
<body>
<comp:Header/>
<div class="d-flex justify-content-between align-items-center mb-5">
    <a href="${pageContext.request.contextPath}/client/availability?zoneId=${originZone.id}&size=${size}&priceMin=${priceMin}&priceMax=${priceMax}&weekday=${weekday}&rating=${rating}&order=${order}&page=${page}"
       class="btn">
        <svg xmlns="http://www.w3.org/2000/svg" width="32" height="32" fill="currentColor" class="bi bi-arrow-left"
             viewBox="0 0 16 16">
            <path fill-rule="evenodd"
                  d="M5.854 4.146a.5.5 0 0 1 0 .708L3.707 7H13.5a.5.5 0 0 1 0 1H3.707l2.147 2.146a.5.5 0 0 1-.708.708l-3-3a.5.5 0 0 1 0-.708l3-3a.5.5 0 0 1 .708 0z"></path>
        </svg>
    </a>
    <h1 class="text-center mb-0 flex-grow-1">
        <spring:message code="generic.phrase.userAvailability" arguments="${driver.username},${originZone}"/>
    </h1>
    <div style="width: 50px;"></div>
</div>

<div class="row mx-0 mb-6 pb-4">
    <div class="col">
    <label class="form-label h4">
        <spring:message code="client.search.selectDate"/>
    </label>
    <div class="calendar-container">
        <div id="calendar"></div>
    </div>
    </div>

    <div class="col">
    <label class="form-label h4">
        <spring:message code="client.search.selectVehicle"/>
    </label>
    <div id="confirmForm" class="form-control">
        <div class="accordion" id="accordionExample">
            <c:forEach var="v" items="${vehicles}">
                <div class="accordion-item">
                    <h2 class="accordion-header">
                        <button id="ab-${v.plateNumber}"
                                class="accordion-button collapsed  d-flex align-items-center"
                                type="button" data-bs-toggle="collapse"
                                data-bs-target="#${v.plateNumber}" aria-expanded="false"
                                aria-controls="${v.plateNumber}"
                                onclick="updateSelectedVehicle('${v.plateNumber}')"
                                disabled
                        >
                            <span class="flex-grow-1">
                                <strong><c:out value="${v.plateNumber}"/></strong> -
                                <c:out value="${v.volume}"/>m&sup3
                            </span>
                            <span class="text-end me-3">
                                $<c:out value="${v.hourlyRate}"/>/h
                            </span>
                        </button>
                    </h2>
                    <div id="${v.plateNumber}" class="accordion-collapse collapse"
                         data-bs-parent="#accordionExample">
                        <div class="accordion-body">
                            <figure class="figure">
                                <c:choose>
                                <c:when test="${v.imgId eq null}">
                                    <c:url value='/images/defaultVehicle.png' var="imgUrl"/>
                                </c:when>
                                <c:otherwise>
                                    <c:url value='/vehicle/image?imgId=${v.imgId}' var="imgUrl"/>
                                </c:otherwise>
                                </c:choose>
                                <img id="vehicleImagePreview" src="${imgUrl}"
                                     alt="Vehicle Image Preview" class="object-fit: cover; card-img-top"/>
                                <figcaption class="figure-caption">
                                    <c:out value="${v.description}"/>
                                </figcaption>
                            </figure>
                            <c:url var="postUrl" value="/client/availability/${driverId}">
                                <c:param name="zoneId" value="${originZone.id}" />
                                <c:param name="size" value="${size}" />
                                <c:param name="priceMin" value="${priceMin}" />
                                <c:param name="priceMax" value="${priceMax}" />
                                <c:param name="weekday" value="${weekday}" />
                                <c:param name="rating" value="${rating}" />
                                <c:param name="page" value="${page}" />
                                <c:param name="order" value="${order}"/>
                            </c:url>
                            <form:form action="${postUrl}" method="post" modelAttribute="bookingForm">
                                <input type="hidden" name="vehicleId" value="${v.id}">
                                <input type="hidden" name="originZoneId" value="${originZone.id}">
                                <input id="bookingDate" type="hidden" name="date" value=""/>
                                <div>
                                    <label for="hb-availability">
                                        <spring:message code="components.header.availability"/>
                                    </label>
                                    <div id="hb-availability" class="mt-3 fs-6 d-flex justify-content-around">
                                        <c:forEach var="sp" items="${shiftPeriods}">
                                            <div class="position-relative">
                                                <form:radiobutton path="shiftPeriod"
                                                                  value="${sp}"
                                                                  id="sp-${sp}-${v.plateNumber}"
                                                                  cssClass="btn-check bottom-0"
                                                                  element="div"
                                                                  required="true"
                                                />
                                                <label class="btn btn-primary"
                                                       for="sp-${sp}-${v.plateNumber}">
                                                    <c:out value="${sp}"/>
                                                </label>
                                            </div>
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
                                <div class="mt-3">
                                    <label for="destinationZoneId">
                                        <spring:message code="driver.availability.selectDestinationZone"/>
                                    </label>
                                    <spring:bind path="destinationZoneId">
                                        <form:select path="destinationZoneId" id="select-zones" multiple="false"
                                                     autocomplete="off"
                                                     cssClass="form-select ${status.error ? 'is-invalid' : ''}">
                                            <form:options items="${zones}" itemValue="id"/>
                                        </form:select>
                                    </spring:bind>
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
    </div>

<script type="text/javascript">
    let workingDays = [
        <c:forEach var="workDay" items="${workingDays}">
        ${workDay.value % 7},
        </c:forEach>
    ];


    const allDays = [0, 1, 2, 3, 4, 5, 6];
    const hiddenDays = allDays.filter((dayIndex) => !workingDays.includes(dayIndex));
    const shiftPeriods = ['MORNING', 'AFTERNOON', 'EVENING'];

    let selectedDate = null;
    let selectedDateString = null;
    let selectedIntDayOfWeek = null;
    let selectedVehiclePlateNumber = null;
    let selectedShiftPeriod = null;
    document.addEventListener('DOMContentLoaded', function () {
        const calendarEl = document.getElementById('calendar');
        const today = new Date();

        const maxDate = new Date();
        maxDate.setMonth(today.getMonth() + 6);

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
                right: ''
            },
            hiddenDays,

            datesSet: function (dateInfo) {
                const prevButton = document.querySelector('.fc-prev-button');
                const currentDate = new Date();

                prevButton.disabled = dateInfo.start <= currentDate;
            },

            dateClick: function (info) {

                updateSelectedDay(info);

                document.getElementById('confirmForm').style.display = 'block';
                document.querySelectorAll('input[name="date"]').forEach(
                    input => input.value = info.dateStr
                );
            }

        });

        calendar.render();
    });

    const vehiclesData = [
        <c:forEach var="v" items="${vehicles}">
        {
            "plateNumber": "${v.plateNumber}",
            "availabilityDays": [
                <c:forEach var="av" items="${v.availability}">
                {
                    "weekDay": "${av.weekDay.value % 7}",
                    "shiftPeriod": "${av.shiftPeriod}"
                },
                </c:forEach>
            ],
            "acceptedBookings": [
                <c:forEach var="b" items="${v.acceptedBookings}">
                {
                    "bookingDay": "${b.date}",
                    "bookingShiftPeriod": "${b.shiftPeriod}"
                },
                </c:forEach>
            ]
        },
        </c:forEach>
    ];
    const accordionButtons = document.querySelectorAll('.accordion-button');

    function updateSelectedDay(newDay) {
        if (selectedDate != null) {
            selectedDate.classList.remove('active-cell')
        }
        selectedVehiclePlateNumber = null;
        selectedShiftPeriod = null;
        newDay.dayEl.classList.add('active-cell')
        selectedDate = newDay.dayEl;
        selectedDateString = newDay.date.toISOString().slice(0, 10);
        selectedIntDayOfWeek = newDay.date.getDay();
        updateVehicleInfo(selectedIntDayOfWeek);
    }

    function updateVehicleInfo(weekDay) {
        vehiclesData.forEach(v => {
            const accordionButton = document.getElementById('ab-' + v.plateNumber);
            const accordionBody = document.getElementById(v.plateNumber);
            if (v.availabilityDays.some(av => av.weekDay === weekDay.toString())) {
                accordionButton.disabled = false;
                updateShiftPeriodButtons(v, weekDay, selectedDateString)
            } else {
                accordionButton.classList.add('collapsed');
                accordionBody.classList.remove('show');
                accordionButton.disabled = true;
            }
        });
    }

    function updateShiftPeriodButtons(vehicle, weekDay, selectedDateString) {
        shiftPeriods.forEach(sp => {
            const spButton = document.getElementById('sp-' + sp + '-' + vehicle.plateNumber);
            spButton.checked = false;
            spButton.disabled = !vehicle.availabilityDays.some(av =>
                av.weekDay === weekDay.toString() && av.shiftPeriod === sp
            );
        })
        vehicle.acceptedBookings.forEach(b => {
            if (b.bookingDay === selectedDateString) {
                ``
                const spButton = document.getElementById('sp-' + b.bookingShiftPeriod + '-' + vehicle.plateNumber);
                spButton.disabled = true;
            }
        })
    }

    function updateSelectedVehicle(vehiclePlate) {
        selectedVehiclePlateNumber = vehiclePlate;
        selectedShiftPeriod = null;
        vehiclesData.forEach(v => {
            if (v.plateNumber === vehiclePlate) {
                updateShiftPeriodButtons(v, selectedIntDayOfWeek, selectedDateString);
            }
        })
    }

</script>

</body>
</html>
