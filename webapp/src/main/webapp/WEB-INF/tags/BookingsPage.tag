<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="comp" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ tag body-content="empty" %>

<%@ tag import="ar.edu.itba.paw.models.BookingState" %>

<!DOCTYPE html>
<html>
<comp:Head titleCode="components.header.bookings" tooltips="true">
    <c:url value="/js/PaginationTag.js" var="js"/>
    <script type="module" src="${js}"></script>
    <c:url value="/js/Tabs.js" var="js"/>
    <script type="module" src="${js}"></script>
</comp:Head>

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
            <c:set value="${loggedUser.type}" var="userType"/>
            <comp:BookingCardList id="${BookingState.PENDING}" tabId="pending-tab"
                                  active="${activeTab eq BookingState.PENDING}"
                                  callToActionCode="call_to_action.${userType}_bookings.pending"
                                  bookings="${pendingBookings}" currentDate="${currentDate}"
                                  paramName="pendingPage" totalPages="${totPendingPages}"
                                  currentPage="${pendingPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.ACCEPTED}" tabId="accepted-tab"
                                  active="${activeTab eq BookingState.ACCEPTED}"
                                  callToActionCode="call_to_action.${userType}_bookings.accepted"
                                  bookings="${acceptedBookings}" currentDate="${currentDate}"
                                  paramName="acceptedPage" totalPages="${totAcceptedPages}"
                                  currentPage="${acceptedPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.FINISHED}" tabId="finished-tab"
                                  active="${activeTab eq BookingState.FINISHED}"
                                  callToActionCode="call_to_action.${userType}_bookings.finished"
                                  bookings="${finishedBookings}" currentDate="${currentDate}"
                                  paramName="finishedPage" totalPages="${totFinishedPages}"
                                  currentPage="${finishedPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.REJECTED}" tabId="rejected-tab"
                                  active="${activeTab eq BookingState.REJECTED}"
                                  callToActionCode="call_to_action.${userType}_bookings.rejected"
                                  bookings="${rejectedBookings}" currentDate="${currentDate}"
                                  paramName="rejectedPage" totalPages="${totRejectedPages}"
                                  currentPage="${rejectedPage}" loggedUser="${loggedUser}"/>
            <comp:BookingCardList id="${BookingState.CANCELED}" tabId="canceled-tab"
                                  active="${activeTab eq BookingState.CANCELED}"
                                  callToActionCode="call_to_action.${userType}_bookings.canceled"
                                  bookings="${canceledBookings}" currentDate="${currentDate}"
                                  paramName="canceledPage" totalPages="${totCanceledPages}"
                                  currentPage="${canceledPage}" loggedUser="${loggedUser}"/>
        </div>
    </div>

</main>

<comp:Footer toasts="${toasts}"/>

</body>
</html>
