import { Navigate, useLocation } from "@solidjs/router";
import { ParentComponent, Show } from "solid-js";
import { useAppState } from "@/app-context/AppContext";
import { UserType } from "@/api/jwt";
import { stripBase } from "@/utils/routing";

const ProtectedRoute: ParentComponent<{ userType: UserType }> = (props) => {
  const [appState] = useAppState();
  const location = useLocation();

  return (
    <Show
      when={appState.user?.type === props.userType}
      fallback={
        appState.user ? (
          <Navigate href={`/${appState.user.type}`} />
        ) : (
          <Navigate href="/login" state={{ from: stripBase(location.pathname) }} />
        )
      }
    >
      {props.children}
    </Show>
  );
};

export const DriverRoute: ParentComponent = (props) => {
  return <ProtectedRoute userType="driver">{props.children}</ProtectedRoute>;
};

export const ClientRoute: ParentComponent = (props) => {
  return <ProtectedRoute userType="client">{props.children}</ProtectedRoute>;
};

export const AnonRoute: ParentComponent = (props) => {
  const [appState] = useAppState();

  return (
    <Show when={!appState.user} fallback={<Navigate href={`/${appState.user!.type}`} />}>
      {props.children}
    </Show>
  );
};
