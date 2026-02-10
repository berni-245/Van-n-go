import { Navigate, useLocation } from "@solidjs/router";
import { ParentComponent } from "solid-js";
import { useAppState } from "@/app-context/AppContext";
import { UserType } from "@/api/jwt";

const ProtectedRoute: ParentComponent<{ userType: UserType }> = (props) => {
  const [appState] = useAppState();
  const location = useLocation();

  if (appState.user?.type !== props.userType) {
    console.log(`No '${props.userType}' detected for protected route, redirecting to '/login'`);
    return <Navigate href="/login" state={{ from: location.pathname }} />;
  }

  return <>{props.children}</>;
};

export const DriverRoute: ParentComponent = (props) => {
  return <ProtectedRoute userType="driver">{props.children}</ProtectedRoute>;
};

export const ClientRoute: ParentComponent = (props) => {
  return <ProtectedRoute userType="client">{props.children}</ProtectedRoute>;
};

export const AnonRoute: ParentComponent = (props) => {
  const [appState] = useAppState();

  if (appState.user) {
    console.log(`Logged user detected for anon route, redirecting to '/${appState.user.type}'`);
    return <Navigate href={`/${appState.user.type}`} />;
  }

  return <>{props.children}</>;
};
