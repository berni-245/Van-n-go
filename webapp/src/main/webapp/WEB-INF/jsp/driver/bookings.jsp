<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%@ page import="ar.edu.itba.paw.models.BookingState" %>

<html>
<comp:Head titleCode="siteName" bsIcons="true"/>

<body class="d-flex flex-column min-vh-100">
<comp:Header inHome="true"/>
<main>
    <div class="container mt-4">
        <div class="row">
            <div class="col-12">
                <h1 class="text-left"><spring:message code="driver.home.yourBookings"/></h1>
            </div>
        </div>
        <ul class="nav nav-tabs" id="statusTabs" role="tablist">
            <comp:TabButton id="pending-tab" targetId="${BookingState.PENDING}"
                            code="generic.word.pending.bookings"
                            active="${activeTab eq BookingState.PENDING}"/>
            <comp:TabButton id="accepted-tab" targetId="${BookingState.ACCEPTED}"
                            code="generic.word.accepted.bookings"
                            active="${activeTab eq BookingState.ACCEPTED}"/>
            <comp:TabButton id="finished-tab" targetId="${BookingState.FINISHED}"
                            code="generic.word.finished.bookings"
                            active="${activeTab eq BookingState.FINISHED}"/>
            <comp:TabButton id="rejected-tab" targetId="${BookingState.REJECTED}"
                            code="generic.word.rejected.bookings"
                            active="${activeTab eq BookingState.REJECTED}"/>
            <comp:TabButton id="canceled-tab" targetId="${BookingState.CANCELED}"
                            code="generic.word.canceled.bookings"
                            active="${activeTab eq BookingState.CANCELED}"/>
        </ul>

        <div class="tab-content mt-3" id="statusTabsContent">
            <comp:ToastManager toasts="${toasts}"/>
            <comp:BookingCardList id="${BookingState.PENDING}" tabId="pending-tab"
                                  active="${activeTab eq BookingState.PENDING}"
                                  callToActionCode="call_to_action.driver_bookings"
                                  bookings="${pendingBookings}" currentDate="${currentDate}"
                                  paramName="pendingPage" totalPages="${totPendingPages}"
                                  currentPage="${pendingPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.ACCEPTED}" tabId="accepted-tab"
                                  active="${activeTab eq BookingState.ACCEPTED}"
                                  callToActionCode="call_to_action.driver_bookings.accepted"
                                  bookings="${acceptedBookings}" currentDate="${currentDate}"
                                  paramName="acceptedPage" totalPages="${totAcceptedPages}"
                                  currentPage="${acceptedPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.FINISHED}" tabId="finished-tab"
                                  active="${activeTab eq BookingState.FINISHED}"
                                  callToActionCode="call_to_action.driver_bookings.finished"
                                  bookings="${finishedBookings}" currentDate="${currentDate}"
                                  paramName="finishedPage" totalPages="${totFinishedPages}"
                                  currentPage="${finishedPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.REJECTED}" tabId="rejected-tab"
                                  active="${activeTab eq BookingState.REJECTED}"
                                  callToActionCode="call_to_action.driver_bookings.rejected"
                                  bookings="${rejectedBookings}" currentDate="${currentDate}"
                                  paramName="rejectedPage" totalPages="${totRejectedPages}"
                                  currentPage="${rejectedPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.CANCELED}" tabId="canceled-tab"
                                  active="${activeTab eq BookingState.CANCELED}"
                                  callToActionCode="call_to_action.driver_bookings.canceled"
                                  bookings="${canceledBookings}" currentDate="${currentDate}"
                                  paramName="canceledPage" totalPages="${totCanceledPages}"
                                  currentPage="${canceledPage}" loggedUser="${loggedUser}"/>
        </div>
    </div>


</main>

<footer class="mt-auto">
    <div class="container">
        <p class="float-end mb-1">
            <a href="#"><spring:message code="public.home.backToTop"/></a>
        </p>
        <p class="mb-1">&copy; PAW 2024B G1</p>
    </div>
</footer>

</body>
<script>
    document.addEventListener('DOMContentLoaded', () => {
        /**
         * @param {Array<{name: string, value: string}>} params
         */
        function setParamsAndNavigate(params) {
            const url = new URL(window.location.href);
            for (const p of params) url.searchParams.set(p.name, p.value);
            window.location.href = url.toString();
        }

        function setPaginationAndTabParam(paginationParamName, page) {
            const activeTab = document.querySelector('button.active[role="tab"]').getAttribute('aria-controls');
            setParamsAndNavigate([
                {name: paginationParamName, value: page},
                {name: 'activeTab', value: activeTab}
            ]);
        }

        document.querySelectorAll('.pagination').forEach(pagination => {
            const paramName = pagination.getAttribute('param-name');
            const currentPage = parseInt(pagination.getAttribute('current-page'));
            pagination.querySelector('.pagination-prev').addEventListener(
                'click',
                () => setPaginationAndTabParam(paramName, currentPage - 1)
            );
            pagination.querySelector('.pagination-next').addEventListener(
                'click',
                () => setPaginationAndTabParam(paramName, currentPage + 1)
            );
            pagination.querySelectorAll('.pagination-page').forEach(p => {
                p.addEventListener('click', () => setPaginationAndTabParam(paramName, p.textContent));
            })
        });
    })
</script>
</html>
